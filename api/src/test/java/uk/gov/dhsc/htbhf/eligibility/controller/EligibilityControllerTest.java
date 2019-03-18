package uk.gov.dhsc.htbhf.eligibility.controller;

import org.assertj.core.api.Assertions;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.helper.EligibilityResponseTestFactory.anEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPersonWithAnInvalidNino;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPersonWithNoAddress;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPersonWithNoDateOfBirth;
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

        var benefit = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        Assertions.assertThat(benefit.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForInvalidNino() {
        PersonDTO person = aPersonWithAnInvalidNino();

        var benefit = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        Assertions.assertThat(benefit.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForMissingDateOfBirth() {
        PersonDTO person = aPersonWithNoDateOfBirth();

        var benefit = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        Assertions.assertThat(benefit.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForMissingAddress() {
        PersonDTO person = aPersonWithNoAddress();

        var benefit = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        Assertions.assertThat(benefit.getStatusCode()).isEqualTo(BAD_REQUEST);
    }

}
