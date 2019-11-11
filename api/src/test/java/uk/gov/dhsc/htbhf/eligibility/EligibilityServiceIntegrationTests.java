package uk.gov.dhsc.htbhf.eligibility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.v1.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityResponse;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;

import java.net.URI;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertInternalServerErrorResponse;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.DWPEligibilityResponseTestDataFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.EligibilityResponseTestDataFactory.anEligibilityResponseWithDwpHouseholdIdentifier;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.EligibilityResponseTestDataFactory.anEligibilityResponseWithHmrcHouseholdIdentifier;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.EligibilityResponseTestDataFactory.anEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.EligibilityResponseTestDataFactory.anEligibleEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.PersonDTOTestDataFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.PersonDTOTestDataFactory.aPersonWithNoNino;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8110)
class EligibilityServiceIntegrationTests {

    private static final URI ENDPOINT = URI.create("/v1/eligibility");
    private static final String DWP_ENDPOINT = "/v1/dwp/eligibility";
    private static final String HMRC_ENDPOINT = "/v1/hmrc/eligibility";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }

    @Test
    void shouldReturnEligibleResponseGivenEligibleResponseReturnedFromBothDwpAndHmrc() throws JsonProcessingException {
        runIntegrationTestReturningEligibilityResponse(ELIGIBLE, ELIGIBLE, anEligibleEligibilityResponse());
    }

    @Test
    void shouldReturnBadRequestForMissingNino() {
        PersonDTO person = aPersonWithNoNino();

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        assertValidationErrorInResponse(response, "nino", "must not be null");
    }

    @ParameterizedTest(name = "Should return ELIGIBLE response from eligibility-service when DWP status returned is ELIGIBLE and HMRC is {0}")
    @ValueSource(strings = {
            "INELIGIBLE",
            "PENDING",
            "ERROR",
            "NO_MATCH"
    })
    void shouldReturnEligibleResponseGivenEligibleResponseFromDwp(EligibilityStatus hmrcResponseEligibilityStatus) throws JsonProcessingException {
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
                                                                                     EligibilityStatus expectedStatus) throws JsonProcessingException {
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
    void shouldReturnEligibleResponseGivenEligibleResponseFromHmrc(EligibilityStatus dwpResponseEligibilityStatus) throws JsonProcessingException {
        runIntegrationTestReturningEligibilityResponse(dwpResponseEligibilityStatus,
                ELIGIBLE,
                anEligibilityResponseWithHmrcHouseholdIdentifier(ELIGIBLE, SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER));
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionFromDwpRestCall() throws JsonProcessingException {
        //Given
        PersonDTO person = aPerson();
        stubDWPEndpointWithInternalServerError();
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        stubHMRCEndpointWithSuccessfulResponse(hmrcEligibilityResponse);

        //When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        //Then
        assertThat(response).isNotNull();
        assertInternalServerErrorResponse(response);
        verifyDWPAndHMRCEndpointsCalled();
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionFromHmrcRestCall() throws JsonProcessingException {
        //Given
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        stubDWPEndpointWithSuccessfulResponse(dwpEligibilityResponse);
        stubHMRCEndpointWithInternalServerError();

        //When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        //Then
        assertThat(response).isNotNull();
        assertInternalServerErrorResponse(response);
        verifyDWPAndHMRCEndpointsCalled();
    }

    private void runIntegrationTestReturningEligibilityResponse(EligibilityStatus dwpResponseEligibilityStatus,
                                                                EligibilityStatus hmrcResponseEligibilityStatus,
                                                                EligibilityResponse expectedResponse) throws JsonProcessingException {
        //Given
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(dwpResponseEligibilityStatus);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(hmrcResponseEligibilityStatus);
        stubDWPEndpointWithSuccessfulResponse(dwpEligibilityResponse);
        stubHMRCEndpointWithSuccessfulResponse(hmrcEligibilityResponse);

        //When
        ResponseEntity<EligibilityResponse> response = callService(person);

        //Then
        assertThat(response).isNotNull();
        HttpStatus httpStatus = (expectedResponse.getEligibilityStatus() == NO_MATCH) ? NOT_FOUND : OK;
        assertThat(response.getStatusCode()).isEqualTo(httpStatus);
        assertResponseCorrect(response, expectedResponse);
        verifyDWPAndHMRCEndpointsCalled();
    }

    private void stubDWPEndpointWithSuccessfulResponse(DWPEligibilityResponse dwpEligibilityResponse) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(dwpEligibilityResponse);
        stubFor(post(urlEqualTo(DWP_ENDPOINT)).willReturn(okJson(json)));
    }

    private void stubHMRCEndpointWithSuccessfulResponse(HMRCEligibilityResponse hmrcEligibilityResponse) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(hmrcEligibilityResponse);
        stubFor(post(urlEqualTo(HMRC_ENDPOINT)).willReturn(okJson(json)));
    }

    private void stubDWPEndpointWithInternalServerError() {
        stubFor(post(urlEqualTo(DWP_ENDPOINT)).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR.value())));
    }

    private void stubHMRCEndpointWithInternalServerError() {
        stubFor(post(urlEqualTo(HMRC_ENDPOINT)).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR.value())));
    }

    private void verifyDWPAndHMRCEndpointsCalled() {
        verify(postRequestedFor(urlEqualTo(DWP_ENDPOINT)));
        verify(postRequestedFor(urlEqualTo(HMRC_ENDPOINT)));
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
}
