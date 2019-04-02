package uk.gov.dhsc.htbhf.eligibility.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.EligibilityResponseTestFactory.aNonMatchingEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.EligibilityResponseTestFactory.anEligibleEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPersonWithNoNino;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityControllerTest {

    private static final String ENDPOINT_URL = "/v1/eligibility";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private EligibilityService eligibilityService;

    @Test
    void shouldGetEligibility() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        EligibilityResponse eligibilityResponse = anEligibleEligibilityResponse();
        given(eligibilityService.checkEligibility(person)).willReturn(eligibilityResponse);

        ResponseEntity<EligibilityResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(eligibilityResponse);
    }

    @Test
    void shouldReturnNotFoundForNonMatchingNino() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        EligibilityResponse eligibilityResponse = aNonMatchingEligibilityResponse();
        given(eligibilityService.checkEligibility(person)).willReturn(eligibilityResponse);

        ResponseEntity<EligibilityResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(eligibilityResponse);
    }

    @Test
    void shouldReturnBadRequestForMissingNino() {
        PersonDTO person = aPersonWithNoNino();

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity(ENDPOINT_URL, person, ErrorResponse.class);

        assertValidationErrorInResponse(response, "nino", "must not be null");
    }
}
