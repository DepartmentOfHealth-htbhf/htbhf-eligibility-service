package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.HMRCEligibilityRequestTestDataFactory.anHMRCEligibilityRequest;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponse;

@ExtendWith(MockitoExtension.class)
class HMRCClientTest {

    @Mock
    private RestTemplate restTemplate;
    private String uri = "http://localhost:8130";

    private HMRCClient hmrcClient;

    /**
     * HMRC client contains async methods which will require the request context and MDC to have non null values
     * in order to work.
     */
    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(new MockHttpServletRequest()));
        MDC.setContextMap(new HashMap<>());
        hmrcClient = new HMRCClient(uri, restTemplate);
    }

    @Test
    void shouldSendRequest() throws ExecutionException, InterruptedException {
        HMRCEligibilityRequest request = anHMRCEligibilityRequest();
        HMRCEligibilityResponse eligibilityResponse = anHMRCEligibilityResponse();
        given(restTemplate.postForEntity(anyString(), any(), eq(HMRCEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(eligibilityResponse, OK));

        CompletableFuture<HMRCEligibilityResponse> response = hmrcClient.checkEligibility(request);

        assertThat(response.get()).isEqualTo(eligibilityResponse);
        verify(restTemplate).postForEntity(uri + "/v1/hmrc/eligibility", request, HMRCEligibilityResponse.class);
    }
}
