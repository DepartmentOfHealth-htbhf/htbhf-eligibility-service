package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.helper.HMRCEligibilityRequestTestFactory.anHMRCEligibilityRequest;
import static uk.gov.dhsc.htbhf.eligibility.helper.HMRCEligibilityResponseTestFactory.anHMRCEligibilityResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class HMRCClientTest {

    @MockBean
    private RestTemplate restTemplate;
    @Value("${hmrc.base-uri}")
    private String uri;

    @Autowired
    private HMRCClient hmrcClient;

    @Test
    void shouldSendRequest() {
        HMRCEligibilityRequest request = anHMRCEligibilityRequest();
        HMRCEligibilityResponse eligibilityResponse = anHMRCEligibilityResponse();
        given(restTemplate.postForEntity(anyString(), any(), eq(HMRCEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(eligibilityResponse, OK));

        HMRCEligibilityResponse response = hmrcClient.checkEligibility(request);

        assertThat(response).isEqualTo(eligibilityResponse);
        verify(restTemplate).postForEntity(uri + "/v1/hmrc/eligibility", request, HMRCEligibilityResponse.class);
    }
}
