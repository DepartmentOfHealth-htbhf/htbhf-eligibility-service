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
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.helper.EligibilityRequestTestFactory.anEligibilityRequest;
import static uk.gov.dhsc.htbhf.eligibility.helper.EligibilityResponseTestFactory.anEligibilityResponse;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DWPClientTest {

    @MockBean
    private RestTemplate restTemplate;
    @Value("${dwp.uri}")
    private String uri;

    @Autowired
    private DWPClient dwpClient;

    @Test
    void shouldSendRequest() {
        EligibilityRequest request = anEligibilityRequest();
        EligibilityResponse eligibilityResponse = anEligibilityResponse();
        given(restTemplate.postForEntity(anyString(), any(), eq(EligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(eligibilityResponse, OK));

        EligibilityResponse response = dwpClient.checkEligibility(request);

        assertThat(response).isEqualTo(eligibilityResponse);
        verify(restTemplate).postForEntity(uri, request, EligibilityResponse.class);
    }
}
