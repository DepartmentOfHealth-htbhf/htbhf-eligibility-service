package uk.gov.dhsc.htbhf.eligibility.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

@Service
public class DWPClient {

    private static final String DWP_ENDPOINT = "/v1/dwp/eligibility";
    private final String uri;
    private final RestTemplate restTemplate;

    public DWPClient(@Value("${dwp.base-uri}") String baseUri,
                     RestTemplate restTemplate) {
        this.uri = baseUri + DWP_ENDPOINT;
        this.restTemplate = restTemplate;
    }

    public DWPEligibilityResponse checkEligibility(EligibilityRequest request) {
        ResponseEntity<DWPEligibilityResponse> response = restTemplate.postForEntity(uri, request, DWPEligibilityResponse.class);
        return response.getBody();
    }
}
