package uk.gov.dhsc.htbhf.eligibility.service.v2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.dwp.model.v2.DWPEligibilityRequestV2;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.DWPEligibilityRequestV2TestDataFactory;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.IdentityAndEligibilityResponseTestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(MockitoExtension.class)
class DWPClientV2Test {

    public static final String DWP_ENDPOINT = "/v2/dwp/eligibility";
    @Mock
    private RestTemplate restTemplate;
    private String uri = "http://localhost:8110";

    private DWPClientV2 dwpClient;

    @BeforeEach
    void setUp() {
        dwpClient = new DWPClientV2(uri, restTemplate);
    }

    @Test
    void shouldSendRequest() {
        //Given
        DWPEligibilityRequestV2 request = DWPEligibilityRequestV2TestDataFactory.aValidDWPEligibilityRequestV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse =
                IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches();
        given(restTemplate.postForEntity(anyString(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        assertThat(response).isEqualTo(identityAndEligibilityResponse);
        verify(restTemplate).postForEntity(uri + DWP_ENDPOINT, request, IdentityAndEligibilityResponse.class);
    }
}
