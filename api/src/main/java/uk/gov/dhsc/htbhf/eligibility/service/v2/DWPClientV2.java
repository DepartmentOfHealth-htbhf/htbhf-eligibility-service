package uk.gov.dhsc.htbhf.eligibility.service.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.dwp.http.v2.GetRequestBuilder;
import uk.gov.dhsc.htbhf.dwp.model.v2.DWPEligibilityRequestV2;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
public class DWPClientV2 {

    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";
    private final String uri;
    private final RestTemplate restTemplate;
    private final GetRequestBuilder getRequestBuilder;

    public DWPClientV2(@Value("${dwp.base-uri}") String baseUri,
                       RestTemplate restTemplate, GetRequestBuilder getRequestBuilder) {
        this.uri = baseUri + DWP_ENDPOINT;
        this.restTemplate = restTemplate;
        this.getRequestBuilder = getRequestBuilder;
    }

    public IdentityAndEligibilityResponse checkIdentityAndEligibility(DWPEligibilityRequestV2 request) {
        log.debug("Checking DWP eligibility V2");
        HttpEntity httpEntity = getRequestBuilder.buildRequestWithHeaders(request);
        ResponseEntity<IdentityAndEligibilityResponse> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, IdentityAndEligibilityResponse.class);
        IdentityAndEligibilityResponse body = response.getBody();
        if (body == null) { // why, spotBugs, why? (This fixes a false positive Null Pointer dereference reported by spotBugs)
            throw new RestClientException("DWP response body was null");
        }
        if (!StringUtils.isEmpty(body.getHouseholdIdentifier())) {
            return body;
        }
        return addHouseholdIdentifierToResponse(request, body);
    }

    private IdentityAndEligibilityResponse addHouseholdIdentifierToResponse(DWPEligibilityRequestV2 request, IdentityAndEligibilityResponse response) {
        String idComponents = getHouseholdIdComponents(request, response);
        String householdId = encodeHouseholdId(idComponents);
        return response.toBuilder().householdIdentifier(householdId).build();
    }

    private String getHouseholdIdComponents(DWPEligibilityRequestV2 request, IdentityAndEligibilityResponse response) {
        List<LocalDate> dobOfChildrenUnder4 = response.getDobOfChildrenUnder4();
        if (CollectionUtils.isEmpty(dobOfChildrenUnder4)) {
            log.debug("Creating synthetic household id using NINO");
            return request.getPerson().getNino();
        }
        log.debug("Creating synthetic household id using postcode and children's dates of birth");
        return request.getPerson().getPostcode() + dobOfChildrenUnder4.stream().sorted().collect(Collectors.toList());
    }

    private String encodeHouseholdId(String idString) {
        return Base64.getEncoder().encodeToString(idString.replace(" ", "").toUpperCase(Locale.UK).getBytes(UTF_8));
    }
}
