package uk.gov.dhsc.htbhf.eligibility.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DWPClient {

    private static final String DWP_ENDPOINT = "/v1/dwp/eligibility";
    private final String uri;
    private final RestTemplate restTemplate;

    public DWPClient(@Value("${dwp.base-uri}") String baseUri,
                     RestTemplate restTemplate) {
        this.uri = baseUri + DWP_ENDPOINT;
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<DWPEligibilityResponse> checkEligibility(DWPEligibilityRequest request) {
        log.debug("Checking DWP eligibility");
        ResponseEntity<DWPEligibilityResponse> response = restTemplate.postForEntity(uri, request, DWPEligibilityResponse.class);
        return CompletableFuture.completedFuture(response.getBody());
    }
}
