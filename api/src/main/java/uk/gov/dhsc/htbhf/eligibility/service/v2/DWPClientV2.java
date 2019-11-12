package uk.gov.dhsc.htbhf.eligibility.service.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.dwp.http.v2.GetRequestBuilder;
import uk.gov.dhsc.htbhf.dwp.model.v2.DWPEligibilityRequestV2;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;

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
        return response.getBody();
    }
}
