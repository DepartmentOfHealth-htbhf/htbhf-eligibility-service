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

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.TestConstants.*;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.HttpRequestTestDataFactory.aValidEligibilityHttpEntity;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.IdAndEligibilityResponseTestDataFactory.anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId;

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
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(MAGGIE_AND_LISA_DOBS,
                "anExistingId");
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

    @Test
    void shouldCreateSyntheticIdentifierForHouseholdWithChildren() {
        //Given
        DWPEligibilityRequestV2 request = DWPEligibilityRequestV2TestDataFactory.aValidDWPEligibilityRequestV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(MAGGIE_AND_LISA_DOBS,
                NO_HOUSEHOLD_IDENTIFIER_PROVIDED);
        given(restTemplate.exchange(anyString(), any(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        HttpEntity httpEntity = aValidEligibilityHttpEntity();
        given(getRequestBuilder.buildRequestWithHeaders(any())).willReturn(httpEntity);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        String syntheticIdComponents = "AA11AA[" + LISA_DATE_OF_BIRTH_STRING + "," + MAGGIE_DATE_OF_BIRTH_STRING + "]";
        String expectedHouseholdId = Base64.getEncoder().encodeToString(syntheticIdComponents.getBytes(UTF_8));
        assertThat(response.getHouseholdIdentifier()).isEqualTo(expectedHouseholdId);
    }

    @Test
    void shouldCreateSyntheticIdentifierForHouseholdWithNoChildren() {
        testSyntheticIdentifierForHouseholdWithNoChildren(emptyList());
    }

    @Test
    void shouldCreateSyntheticIdentifierForHouseholdWithNullChildren() {
        testSyntheticIdentifierForHouseholdWithNoChildren(null);
    }

    private void testSyntheticIdentifierForHouseholdWithNoChildren(List<LocalDate> dobOfChildrenUnder4) {
        //Given
        DWPEligibilityRequestV2 request = DWPEligibilityRequestV2TestDataFactory.aValidDWPEligibilityRequestV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(dobOfChildrenUnder4,
                NO_HOUSEHOLD_IDENTIFIER_PROVIDED);
        given(restTemplate.exchange(anyString(), any(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        HttpEntity httpEntity = aValidEligibilityHttpEntity();
        given(getRequestBuilder.buildRequestWithHeaders(any())).willReturn(httpEntity);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        String expectedHouseholdId = Base64.getEncoder().encodeToString(HOMER_NINO_V2.getBytes(UTF_8));
        assertThat(response.getHouseholdIdentifier()).isEqualTo(expectedHouseholdId);
    }

    @Test
    void shouldNotCreateSyntheticIdentifierForHouseholdWithIdentifier() {
        //Given
        DWPEligibilityRequestV2 request = DWPEligibilityRequestV2TestDataFactory.aValidDWPEligibilityRequestV2();
        String existingHouseholdIdentifier = "AnExistingHouseholdIdentifier";
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(MAGGIE_AND_LISA_DOBS,
                existingHouseholdIdentifier);
        given(restTemplate.exchange(anyString(), any(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        HttpEntity httpEntity = aValidEligibilityHttpEntity();
        given(getRequestBuilder.buildRequestWithHeaders(any())).willReturn(httpEntity);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        assertThat(response.getHouseholdIdentifier()).isEqualTo(existingHouseholdIdentifier);
    }
}
