package uk.gov.dhsc.htbhf.eligibility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.dwp.http.HeaderName;
import uk.gov.dhsc.htbhf.dwp.model.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;

import java.net.URI;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.TestConstants.*;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertInternalServerErrorResponse;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.dwp.model.VerificationOutcome.NOT_SUPPLIED;
import static uk.gov.dhsc.htbhf.dwp.testhelper.IdAndEligibilityResponseTestDataFactory.anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId;
import static uk.gov.dhsc.htbhf.dwp.testhelper.PersonDTOTestDataFactory.aPersonDTOWithNino;
import static uk.gov.dhsc.htbhf.dwp.testhelper.PersonDTOTestDataFactory.aPersonDTOWithSurname;
import static uk.gov.dhsc.htbhf.dwp.testhelper.PersonDTOTestDataFactory.aValidPersonDTO;
import static uk.gov.dhsc.htbhf.eligibility.model.testhelper.CombinedIdAndEligibilityResponseTestDataFactory.anIdMatchedEligibilityConfirmedUCResponseWithAllMatchesAndHmrcHouseIdentifier;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8110)
class EligibilityServiceIntegrationTests {

    private static final URI ENDPOINT = URI.create("/v2/eligibility");
    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnEligibleResponseGivenEligibleResponseReturnedFromDwp() throws JsonProcessingException {
        //Given
        PersonDTO person = aValidPersonDTO();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId();
        stubDWPEndpointWithSuccessfulResponse(identityAndEligibilityResponse);
        //When
        ResponseEntity<CombinedIdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person),
                CombinedIdentityAndEligibilityResponse.class);
        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getBody()).isEqualTo(anIdMatchedEligibilityConfirmedUCResponseWithAllMatchesAndHmrcHouseIdentifier(null));
        verifyDWPEndpointCalled();
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() {
        //Given
        PersonDTO person = aPersonDTOWithSurname(null);
        //When
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        assertValidationErrorInResponse(responseEntity, "surname", "must not be null");
    }

    @Test
    void shouldValidateForNullNino() throws JsonProcessingException {
        //Given
        PersonDTO person = aPersonDTOWithNino(null);
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId()
                .toBuilder()
                .emailAddressMatch(NOT_SUPPLIED)
                .mobilePhoneMatch(NOT_SUPPLIED)
                .build();
        stubDWPEndpointWithSuccessfulResponse(identityAndEligibilityResponse);
        //When
        ResponseEntity<CombinedIdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person),
                CombinedIdentityAndEligibilityResponse.class);

        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(responseEntity.getBody().getEmailAddressMatch()).isEqualTo(NOT_SUPPLIED);
        assertThat(responseEntity.getBody().getMobilePhoneMatch()).isEqualTo(NOT_SUPPLIED);
        verifyDWPEndpointCalled();
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionFromDwpRestCall() {
        //Given
        PersonDTO person = aValidPersonDTO();
        stubDWPEndpointWithInternalServerError();

        //When
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        //Then
        assertThat(response).isNotNull();
        assertInternalServerErrorResponse(response);
        verifyDWPEndpointCalled();
    }

    private void stubDWPEndpointWithSuccessfulResponse(IdentityAndEligibilityResponse identityAndEligibilityResponse) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(identityAndEligibilityResponse);
        stubFor(get(urlEqualTo(DWP_ENDPOINT)).willReturn(okJson(json)));
    }

    private void stubDWPEndpointWithInternalServerError() {
        stubFor(get(urlEqualTo(DWP_ENDPOINT)).willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR.value())));
    }

    private void verifyDWPEndpointCalled() {
        verify(getRequestedFor(urlEqualTo(DWP_ENDPOINT))
                .withHeader(HeaderName.ADDRESS_LINE_1.getHeader(), equalTo(SIMPSONS_ADDRESS_LINE_1))
                .withHeader(HeaderName.SURNAME.getHeader(), equalTo(SIMPSON_SURNAME))
                .withHeader(HeaderName.NINO.getHeader(), equalTo(HOMER_NINO))
                .withHeader(HeaderName.DATE_OF_BIRTH.getHeader(), equalTo(HOMER_DATE_OF_BIRTH_STRING))
                .withHeader(HeaderName.ELIGIBILITY_END_DATE.getHeader(), equalTo(ISO_LOCAL_DATE.format(LocalDate.now())))
                .withHeader(HeaderName.ADDRESS_LINE_1.getHeader(), equalTo(SIMPSONS_ADDRESS_LINE_1))
                .withHeader(HeaderName.POSTCODE.getHeader(), equalTo(SIMPSONS_POSTCODE))
                .withHeader(HeaderName.EMAIL_ADDRESS.getHeader(), equalTo(HOMER_EMAIL))
                .withHeader(HeaderName.MOBILE_PHONE_NUMBER.getHeader(), equalTo(HOMER_MOBILE))
                .withHeader(HeaderName.PREGNANT_DEPENDENT_DOB.getHeader(), equalTo(MAGGIE_DATE_OF_BIRTH_STRING))
                .withHeader(HeaderName.UC_MONTHLY_INCOME_THRESHOLD.getHeader(), equalTo(String.valueOf(UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE)))
        );
    }

    private RequestEntity buildRequestEntity(PersonDTO personDTO) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RequestEntity<>(personDTO, headers, POST, ENDPOINT);
    }

}
