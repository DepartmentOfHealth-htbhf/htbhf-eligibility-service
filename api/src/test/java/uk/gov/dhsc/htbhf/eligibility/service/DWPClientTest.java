package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPEligibilityRequestTestFactory.aDWPEligibilityRequest;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPEligibilityResponseTestFactory.aDWPEligibilityResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DWPClientTest {

    @MockBean
    private RestTemplate restTemplate;
    @Value("${dwp.base-uri}")
    private String uri;

    @Autowired
    private DWPClient dwpClient;

    /**
     * DWP client contains async methods which will require the request context and MDC to have non null values
     * in order to work.
     */
    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(new MockHttpServletRequest()));
        MDC.setContextMap(new HashMap<>());
    }

    @Test
    void shouldSendRequest() throws ExecutionException, InterruptedException {
        DWPEligibilityRequest request = aDWPEligibilityRequest();
        DWPEligibilityResponse eligibilityResponse = aDWPEligibilityResponse();
        given(restTemplate.postForEntity(anyString(), any(), eq(DWPEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(eligibilityResponse, OK));

        CompletableFuture<DWPEligibilityResponse> response = dwpClient.checkEligibility(request);

        assertThat(response.get()).isEqualTo(eligibilityResponse);
        verify(restTemplate).postForEntity(uri + "/v1/dwp/eligibility", request, DWPEligibilityResponse.class);
    }
}
