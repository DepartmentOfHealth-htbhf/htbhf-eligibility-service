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
import uk.gov.dhsc.htbhf.dwp.http.v2.HeaderName;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;
import uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;

import java.net.URI;
import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertInternalServerErrorResponse;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory.aPersonDTOV2WithNino;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory.aValidPersonDTOV2;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8110)
class EligibilityServiceIntegrationTestsV2 {

    private static final URI ENDPOINT = URI.create("/v2/eligibility");
    private static final String DWP_ENDPOINT = "/v2/dwp/eligibility";

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnEligibleResponseGivenEligibleResponseReturnedFromDwp() throws JsonProcessingException {
        //Given
        PersonDTOV2 person = aValidPersonDTOV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches();
        stubDWPEndpointWithSuccessfulResponse(identityAndEligibilityResponse);
        //When
        ResponseEntity<IdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person), IdentityAndEligibilityResponse.class);
        //Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        assertThat(responseEntity.hasBody()).isTrue();
        assertThat(identityAndEligibilityResponse).isEqualTo(identityAndEligibilityResponse);
        verifyDWPEndpointCalled();
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() {
        //Given
        PersonDTOV2 person = aPersonDTOV2WithNino(null);
        //When
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        assertValidationErrorInResponse(responseEntity, "nino", "must not be null");
    }

    @Test
    void shouldReturnInternalServerErrorWhenExceptionFromDwpRestCall() {
        //Given
        PersonDTOV2 person = aValidPersonDTOV2();
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
                .withHeader(HeaderName.ADDRESS_LINE_1.getHeader(), equalTo(TestConstants.SIMPSONS_ADDRESS_LINE_1))
                .withHeader(HeaderName.SURNAME.getHeader(), equalTo(TestConstants.SIMPSON_SURNAME))
                .withHeader(HeaderName.NINO.getHeader(), equalTo(TestConstants.HOMER_NINO_V2))
                .withHeader(HeaderName.DATE_OF_BIRTH.getHeader(), equalTo(TestConstants.HOMER_DATE_OF_BIRTH_STRING))
                .withHeader(HeaderName.ELIGIBILITY_END_DATE.getHeader(), equalTo(ISO_LOCAL_DATE.format(LocalDate.now())))
                .withHeader(HeaderName.ADDRESS_LINE_1.getHeader(), equalTo(TestConstants.SIMPSONS_ADDRESS_LINE_1))
                .withHeader(HeaderName.POSTCODE.getHeader(), equalTo(TestConstants.SIMPSONS_POSTCODE))
                .withHeader(HeaderName.EMAIL_ADDRESS.getHeader(), equalTo(TestConstants.HOMER_EMAIL))
                .withHeader(HeaderName.MOBILE_PHONE_NUMBER.getHeader(), equalTo(TestConstants.HOMER_MOBILE))
                .withHeader(HeaderName.PREGNANT_DEPENDENT_DOB.getHeader(), equalTo(TestConstants.MAGGIE_DATE_OF_BIRTH_STRING))
                .withHeader(HeaderName.UC_MONTHLY_INCOME_THRESHOLD.getHeader(), equalTo(String.valueOf(TestConstants.UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE)))
        );
    }

    private RequestEntity buildRequestEntity(PersonDTOV2 personDTO) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RequestEntity<>(personDTO, headers, POST, ENDPOINT);
    }

}
