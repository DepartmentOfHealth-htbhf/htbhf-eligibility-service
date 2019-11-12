package uk.gov.dhsc.htbhf.eligibility.service.v2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.dwp.http.v2.GetRequestBuilder;
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
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.HttpRequestTestDataFactory.aValidEligibilityHttpEntity;

@ExtendWith(MockitoExtension.class)
class DWPClientV2Test {

    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private GetRequestBuilder getRequestBuilder;

    private String uri = "http://localhost:8110";

    private DWPClientV2 dwpClient;

    @BeforeEach
    void setUp() {
        dwpClient = new DWPClientV2(uri, restTemplate, getRequestBuilder);
    }

    @Test
    void shouldSendRequest() {
        //Given
        DWPEligibilityRequestV2 request = DWPEligibilityRequestV2TestDataFactory.aValidDWPEligibilityRequestV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse =
                IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches();
        given(restTemplate.exchange(anyString(), any(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        HttpEntity httpEntity = aValidEligibilityHttpEntity();
        given(getRequestBuilder.buildRequestWithHeaders(any())).willReturn(httpEntity);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        assertThat(response).isEqualTo(identityAndEligibilityResponse);
        verify(restTemplate).exchange(uri + DWP_ENDPOINT, HttpMethod.GET, httpEntity, IdentityAndEligibilityResponse.class);
        verify(getRequestBuilder).buildRequestWithHeaders(request);
    }
}
