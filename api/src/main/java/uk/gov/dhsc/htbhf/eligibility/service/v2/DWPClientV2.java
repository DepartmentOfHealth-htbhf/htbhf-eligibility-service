package uk.gov.dhsc.htbhf.eligibility.service.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.dwp.model.v2.DWPEligibilityRequestV2;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;

@Service
@Slf4j
public class DWPClientV2 {

    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";
    private final String uri;
    private final RestTemplate restTemplate;

    public DWPClientV2(@Value("${dwp.base-uri}") String baseUri,
                       RestTemplate restTemplate) {
        this.uri = baseUri + DWP_ENDPOINT;
        this.restTemplate = restTemplate;
    }

    public IdentityAndEligibilityResponse checkIdentityAndEligibility(DWPEligibilityRequestV2 request) {
        log.debug("Checking DWP eligibility V2");
        ResponseEntity<IdentityAndEligibilityResponse> response = restTemplate.postForEntity(uri, request, IdentityAndEligibilityResponse.class);
        return response.getBody();
    }
}
