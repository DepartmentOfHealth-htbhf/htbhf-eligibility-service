package uk.gov.dhsc.htbhf.eligibility.service;

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
import uk.gov.dhsc.htbhf.dwp.http.GetRequestBuilder;
import uk.gov.dhsc.htbhf.dwp.model.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.dwp.model.IdentityAndEligibilityResponse;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * REST client for making calls to the DWP eligibility API.
 */
@Service
@Slf4j
public class DWPClient {

    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";
    private final String uri;
    private final RestTemplate restTemplate;
    private final GetRequestBuilder getRequestBuilder;

    public DWPClient(@Value("${dwp.base-uri}") String baseUri,
                     RestTemplate restTemplate, GetRequestBuilder getRequestBuilder) {
        this.uri = baseUri + DWP_ENDPOINT;
        this.restTemplate = restTemplate;
        this.getRequestBuilder = getRequestBuilder;
    }

    public IdentityAndEligibilityResponse checkIdentityAndEligibility(DWPEligibilityRequest request) {
        log.debug("Checking DWP eligibility V2");
        HttpEntity httpEntity = getRequestBuilder.buildRequestWithHeaders(request);
        ResponseEntity<IdentityAndEligibilityResponse> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, IdentityAndEligibilityResponse.class);
        IdentityAndEligibilityResponse identityAndEligibilityResponse = response.getBody();
        if (identityAndEligibilityResponse == null) { // why, spotBugs, why? (This fixes a false positive Null Pointer dereference reported by spotBugs)
            throw new RestClientException("DWP response body was null");
        }
        if (isSyntheticHouseholdIdentifierRequired(identityAndEligibilityResponse)) {
            return addHouseholdIdentifierToResponse(request, identityAndEligibilityResponse);
        }
        return identityAndEligibilityResponse;
    }

    private boolean isSyntheticHouseholdIdentifierRequired(IdentityAndEligibilityResponse identityAndEligibilityResponse) {
        return identityAndEligibilityResponse.isAddressMatched() && StringUtils.isEmpty(identityAndEligibilityResponse.getHouseholdIdentifier());
    }

    private IdentityAndEligibilityResponse addHouseholdIdentifierToResponse(DWPEligibilityRequest request, IdentityAndEligibilityResponse response) {
        String idComponents = getHouseholdIdComponents(request, response);
        String householdId = encodeHouseholdId(idComponents);
        return response.toBuilder().householdIdentifier(householdId).build();
    }

    private String getHouseholdIdComponents(DWPEligibilityRequest request, IdentityAndEligibilityResponse response) {
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
