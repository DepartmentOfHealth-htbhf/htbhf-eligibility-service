package uk.gov.dhsc.htbhf.eligibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertInternalServerErrorResponse;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.DWPEligibilityRequestTestDataFactory.aDWPEligibilityRequestWithEligibilityDates;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.DWPEligibilityResponseTestDataFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.EligibilityResponseTestDataFactory.anEligibilityResponseWithDwpHouseholdIdentifier;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.EligibilityResponseTestDataFactory.anEligibilityResponseWithHmrcHouseholdIdentifier;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.EligibilityResponseTestDataFactory.anEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.EligibilityResponseTestDataFactory.anEligibleEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.HMRCEligibilityRequestTestDataFactory.anHMRCEligibilityRequestWithEligibilityDates;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.PersonDTOTestDataFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EligibilityServiceIntegrationTests {

    private static final URI ENDPOINT = URI.create("/v1/eligibility");

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private RestTemplate restTemplateWithIdHeaders;

    @Value("${hmrc.base-uri}")
    private String hmrcUri;

    @Value("${dwp.base-uri}")
    private String dwpUri;

    @Value("${eligibility-check-period-length}")
    private Integer eligibilityCheckPeriodLength;

    @Test
    void shouldReturnEligibleResponseGivenEligibleResponseReturnedFromBothDwpAndHmrc() {
        runIntegrationTestReturningEligibilityResponse(ELIGIBLE, ELIGIBLE, anEligibleEligibilityResponse());
    }

    @ParameterizedTest(name = "Should return ELIGIBLE response from eligibility-service when DWP status returned is ELIGIBLE and HMRC is {0}")
    @ValueSource(strings = {
            "INELIGIBLE",
            "PENDING",
            "ERROR",
            "NO_MATCH"
    })
    void shouldReturnEligibleResponseGivenEligibleResponseFromDwp(EligibilityStatus hmrcResponseEligibilityStatus) {
        runIntegrationTestReturningEligibilityResponse(ELIGIBLE,
                hmrcResponseEligibilityStatus,
                anEligibilityResponseWithDwpHouseholdIdentifier(ELIGIBLE, SIMPSON_DWP_HOUSEHOLD_IDENTIFIER));
    }

    @ParameterizedTest(name = "Should return {2} response from eligibility-service when DWP status returned is {0} and HMRC is {1}")
    @CsvSource({
            "PENDING, PENDING, PENDING",
            "PENDING, INELIGIBLE, PENDING",
            "PENDING, ERROR, PENDING",
            "PENDING, NO_MATCH, PENDING",
            "ERROR, ERROR, ERROR",
            "ERROR, INELIGIBLE, ERROR",
            "ERROR, PENDING, PENDING",
            "ERROR, NO_MATCH, ERROR",
            "INELIGIBLE, PENDING, PENDING",
            "INELIGIBLE, INELIGIBLE, INELIGIBLE",
            "INELIGIBLE, NO_MATCH, INELIGIBLE",
            "INELIGIBLE, ERROR, ERROR",
            "NO_MATCH, INELIGIBLE, INELIGIBLE",
            "NO_MATCH, PENDING, PENDING",
            "NO_MATCH, ERROR, ERROR"
    })
    void shouldReturnEligibilityResponseGivenANonEligibleResponseFromEitherDwpOrHmrc(EligibilityStatus dwpResponseEligibilityStatus,
                                                                                     EligibilityStatus hmrcResponseEligibilityStatus,
                                                                                     EligibilityStatus expectedStatus) {
        runIntegrationTestReturningEligibilityResponse(dwpResponseEligibilityStatus,
                hmrcResponseEligibilityStatus,
                anEligibilityResponseWithStatus(expectedStatus));
    }

    @ParameterizedTest(name = "Should return ELIGIBLE response from eligibility-service when DWP status returned is {0} and HMRC is ELIGIBLE")
    @ValueSource(strings = {
            "PENDING",
            "ERROR",
            "INELIGIBLE",
            "NO_MATCH"
    })
    void shouldReturnEligibleResponseGivenEligibleResponseFromHmrc(EligibilityStatus dwpResponseEligibilityStatus) {
        runIntegrationTestReturningEligibilityResponse(dwpResponseEligibilityStatus,
                ELIGIBLE,
                anEligibilityResponseWithHmrcHouseholdIdentifier(ELIGIBLE, SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER));
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionFromDwpRestCall() {
        //Given
        PersonDTO person = aPerson();
        given(restTemplateWithIdHeaders.postForEntity(anyString(), any(), eq(DWPEligibilityResponse.class)))
                .willThrow(new RestClientException("Testing a failed REST call"));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(restTemplateWithIdHeaders.postForEntity(anyString(), any(), eq(HMRCEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(hmrcEligibilityResponse, OK));

        //When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        //Then
        assertThat(response).isNotNull();
        assertInternalServerErrorResponse(response);
        verify(restTemplateWithIdHeaders).postForEntity(buildDwpUri(), buildDwpExpectedRequest(), DWPEligibilityResponse.class);
        verify(restTemplateWithIdHeaders).postForEntity(buildHmrcUri(), buildHmrcExpectedRequest(), HMRCEligibilityResponse.class);
        verifyNoMoreInteractions(restTemplateWithIdHeaders);
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionFromHmrcRestCall() {
        //Given
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        given(restTemplateWithIdHeaders.postForEntity(anyString(), any(), eq(DWPEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(dwpEligibilityResponse, OK));
        given(restTemplateWithIdHeaders.postForEntity(anyString(), any(), eq(HMRCEligibilityResponse.class)))
                .willThrow(new RestClientException("Testing a failed REST call"));

        //When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        //Then
        assertThat(response).isNotNull();
        assertInternalServerErrorResponse(response);
        verify(restTemplateWithIdHeaders).postForEntity(buildDwpUri(), buildDwpExpectedRequest(), DWPEligibilityResponse.class);
        verify(restTemplateWithIdHeaders).postForEntity(buildHmrcUri(), buildHmrcExpectedRequest(), HMRCEligibilityResponse.class);
        verifyNoMoreInteractions(restTemplateWithIdHeaders);
    }

    private void runIntegrationTestReturningEligibilityResponse(EligibilityStatus dwpResponseEligibilityStatus,
                                                                EligibilityStatus hmrcResponseEligibilityStatus,
                                                                EligibilityResponse expectedResponse) {
        //Given
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(dwpResponseEligibilityStatus);
        given(restTemplateWithIdHeaders.postForEntity(anyString(), any(), eq(DWPEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(dwpEligibilityResponse, OK));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(hmrcResponseEligibilityStatus);
        given(restTemplateWithIdHeaders.postForEntity(anyString(), any(), eq(HMRCEligibilityResponse.class)))
                .willReturn(new ResponseEntity<>(hmrcEligibilityResponse, OK));

        //When
        ResponseEntity<EligibilityResponse> response = callService(person);

        //Then
        assertThat(response).isNotNull();
        HttpStatus httpStatus = (expectedResponse.getEligibilityStatus() == NO_MATCH) ? NOT_FOUND : OK;
        assertThat(response.getStatusCode()).isEqualTo(httpStatus);
        assertResponseCorrect(response, expectedResponse);
        verify(restTemplateWithIdHeaders).postForEntity(buildDwpUri(), buildDwpExpectedRequest(), DWPEligibilityResponse.class);
        verify(restTemplateWithIdHeaders).postForEntity(buildHmrcUri(), buildHmrcExpectedRequest(), HMRCEligibilityResponse.class);
        verifyNoMoreInteractions(restTemplateWithIdHeaders);
    }

    private ResponseEntity<EligibilityResponse> callService(PersonDTO personDTO) {
        return restTemplate.exchange(buildRequestEntity(personDTO), EligibilityResponse.class);
    }

    private RequestEntity buildRequestEntity(PersonDTO personDTO) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RequestEntity<>(personDTO, headers, POST, ENDPOINT);
    }

    private void assertResponseCorrect(ResponseEntity<EligibilityResponse> response, EligibilityResponse expectedResponse) {
        assertThat(response.getStatusCode()).isEqualTo(OK);
        EligibilityResponse eligibilityResponse = response.getBody();
        assertThat(eligibilityResponse).isNotNull();
        assertThat(eligibilityResponse).isEqualTo(expectedResponse);
    }

    private String buildDwpUri() {
        return dwpUri + "/v1/dwp/eligibility";
    }

    private String buildHmrcUri() {
        return hmrcUri + "/v1/hmrc/eligibility";
    }

    private HMRCEligibilityRequest buildHmrcExpectedRequest() {
        return anHMRCEligibilityRequestWithEligibilityDates(getEligibilityStartDate(), getEligibilityEndDate());
    }

    private DWPEligibilityRequest buildDwpExpectedRequest() {
        return aDWPEligibilityRequestWithEligibilityDates(getEligibilityStartDate(), getEligibilityEndDate());
    }

    private LocalDate getEligibilityStartDate() {
        return LocalDate.now().minusDays(eligibilityCheckPeriodLength);
    }

    private LocalDate getEligibilityEndDate() {
        return LocalDate.now();
    }
}
