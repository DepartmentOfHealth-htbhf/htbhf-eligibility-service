package uk.gov.dhsc.htbhf.eligibility.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.service.EligibilityService;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.EligibilityResponseTestFactory.anEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityControllerTest {

    private static final String ENDPOINT_URL = "/v1/eligibility";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EligibilityService eligibilityService;

    @Test
    void shouldGetEligibility() {
        PersonDTO person = aPerson();
        EligibilityResponse eligibilityResponse = anEligibilityResponse();
        given(eligibilityService.checkEligibility(person)).willReturn(eligibilityResponse);

        ResponseEntity<EligibilityResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(eligibilityResponse);
    }

    @Test
    void shouldReturnBadRequestForMissingNino() {
        PersonDTO person = aPersonWithNoNino();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "nino", "must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"YYHU456781", "888888888", "ABCDEFGHI", "ZQQ123456CZ", "QQ123456T"})
    void shouldReturnBadRequestForInvalidNino(String nino) {
        PersonDTO person = buildDefaultPerson().nino(nino).build();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "nino", "must match \"[a-zA-Z]{2}\\d{6}[a-dA-D]\"");
    }

    @Test
    void shouldReturnBadRequestForMissingDateOfBirth() {
        PersonDTO person = aPersonWithNoDateOfBirth();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "dateOfBirth", "must not be null");
    }

    @Test
    void shouldReturnBadRequestForInvalidDateOfBirth() {
        PersonDTO person = aPersonWithDateOfBirthInFuture();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "dateOfBirth", "must be a past date");
    }

    @Test
    void shouldReturnBadRequestForMissingAddress() {
        PersonDTO person = aPersonWithNoAddress();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "address", "must not be null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AA1122BB", "11AA21", ""})
    void shouldReturnBadRequestForInvalidPostcode(String postcode) {
        PersonDTO person = aPersonWithPostcode(postcode);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "address.postcode", "invalid postcode format");
    }

}
