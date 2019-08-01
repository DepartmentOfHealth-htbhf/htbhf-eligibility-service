package uk.gov.dhsc.htbhf.eligibility.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class HMRCClient {

    private static final String HMRC_ENDPOINT = "/v1/hmrc/eligibility";
    private final String uri;
    private final RestTemplate restTemplate;

    public HMRCClient(@Value("${hmrc.base-uri}") String baseUri,
                      RestTemplate restTemplate) {
        this.uri = baseUri + HMRC_ENDPOINT;
        this.restTemplate = restTemplate;
    }

    @Async
    public CompletableFuture<HMRCEligibilityResponse> checkEligibility(HMRCEligibilityRequest request) {
        log.debug("Checking HRMC eligibility");
        ResponseEntity<HMRCEligibilityResponse> response = restTemplate.postForEntity(uri, request, HMRCEligibilityResponse.class);
        return CompletableFuture.completedFuture(response.getBody());
    }
}
