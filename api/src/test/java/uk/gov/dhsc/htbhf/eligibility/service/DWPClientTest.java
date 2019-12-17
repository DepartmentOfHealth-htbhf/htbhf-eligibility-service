package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.dwp.http.GetRequestBuilder;
import uk.gov.dhsc.htbhf.dwp.model.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.dwp.model.IdentityAndEligibilityResponse;

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
import static uk.gov.dhsc.htbhf.dwp.testhelper.DWPEligibilityRequestTestDataFactory.aValidDWPEligibilityRequest;
import static uk.gov.dhsc.htbhf.dwp.testhelper.HttpRequestTestDataFactory.aValidEligibilityHttpEntity;
import static uk.gov.dhsc.htbhf.dwp.testhelper.IdAndEligibilityResponseTestDataFactory.anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId;
import static uk.gov.dhsc.htbhf.dwp.testhelper.IdAndEligibilityResponseTestDataFactory.anIdMatchedEligibilityConfirmedFullAddressNotMatchedResponse;

@ExtendWith(MockitoExtension.class)
class DWPClientTest {

    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private GetRequestBuilder getRequestBuilder;

    private String uri = "http://localhost:8110";

    private DWPClient dwpClient;

    @BeforeEach
    void setUp() {
        dwpClient = new DWPClient(uri, restTemplate, getRequestBuilder);
    }

    @Test
    void shouldSendRequestAndReturnResponseWithHouseholdIdentifierFromDWP() {
        //Given
        DWPEligibilityRequest request = aValidDWPEligibilityRequest();
        String existingHouseholdIdentifier = "anExistingId";
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(MAGGIE_AND_LISA_DOBS,
                existingHouseholdIdentifier);
        given(restTemplate.exchange(anyString(), any(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        HttpEntity httpEntity = aValidEligibilityHttpEntity();
        given(getRequestBuilder.buildRequestWithHeaders(any())).willReturn(httpEntity);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        assertThat(response).isEqualTo(identityAndEligibilityResponse);
        assertThat(response.getHouseholdIdentifier()).isEqualTo(existingHouseholdIdentifier);
        verify(restTemplate).exchange(uri + DWP_ENDPOINT, HttpMethod.GET, httpEntity, IdentityAndEligibilityResponse.class);
        verify(getRequestBuilder).buildRequestWithHeaders(request);
    }

    @Test
    void shouldCreateSyntheticIdentifierForHouseholdWithChildren() {
        //Given
        DWPEligibilityRequest request = aValidDWPEligibilityRequest();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(MAGGIE_AND_LISA_DOBS,
                NO_HOUSEHOLD_IDENTIFIER_PROVIDED);
        setupRequestMocks(identityAndEligibilityResponse);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        String syntheticIdComponents = "AA11AA[" + LISA_DATE_OF_BIRTH_STRING + "," + MAGGIE_DATE_OF_BIRTH_STRING + "]";
        String expectedHouseholdId = Base64.getEncoder().encodeToString(syntheticIdComponents.getBytes(UTF_8));
        assertThat(response.getHouseholdIdentifier()).isEqualTo(expectedHouseholdId);
    }

    @Test
    void shouldNotCreateSyntheticIdentifierForResponseWithUnmatchedAddress() {
        //Given
        DWPEligibilityRequest request = aValidDWPEligibilityRequest();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anIdMatchedEligibilityConfirmedFullAddressNotMatchedResponse();
        setupRequestMocks(identityAndEligibilityResponse);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        assertThat(response.getHouseholdIdentifier()).isEqualTo(NO_HOUSEHOLD_IDENTIFIER_PROVIDED);
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
        DWPEligibilityRequest request = aValidDWPEligibilityRequest();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId(dobOfChildrenUnder4,
                NO_HOUSEHOLD_IDENTIFIER_PROVIDED);
        setupRequestMocks(identityAndEligibilityResponse);
        //When
        IdentityAndEligibilityResponse response = dwpClient.checkIdentityAndEligibility(request);
        //Then
        String expectedHouseholdId = Base64.getEncoder().encodeToString(HOMER_NINO.getBytes(UTF_8));
        assertThat(response.getHouseholdIdentifier()).isEqualTo(expectedHouseholdId);
    }

    private void setupRequestMocks(IdentityAndEligibilityResponse identityAndEligibilityResponse) {
        given(restTemplate.exchange(anyString(), any(), any(), eq(IdentityAndEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(identityAndEligibilityResponse, OK));
        HttpEntity httpEntity = aValidEligibilityHttpEntity();
        given(getRequestBuilder.buildRequestWithHeaders(any())).willReturn(httpEntity);
    }
}
