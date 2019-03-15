package uk.gov.dhsc.htbhf.eligibility.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

@Service
public class DWPClient {

    private final String uri;
    private final RestTemplate restTemplate;

    public DWPClient(@Value("${dwp.uri}") String uri,
                     RestTemplate restTemplate) {
        this.uri = uri;
        this.restTemplate = restTemplate;
    }

    public EligibilityResponse checkEligibility(EligibilityRequest request) {
        ResponseEntity<EligibilityResponse> response = restTemplate.postForEntity(uri, request, EligibilityResponse.class);
        return response.getBody();
    }
}
