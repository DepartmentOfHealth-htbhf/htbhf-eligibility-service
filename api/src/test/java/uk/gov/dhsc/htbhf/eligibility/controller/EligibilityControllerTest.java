package uk.gov.dhsc.htbhf.eligibility.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonTestFactory.aPerson;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityControllerTest {

    private static final String ENDPOINT_URL = "/eligibility";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetEligibility() {
        PersonDTO person = aPerson();

        var response = restTemplate.postForEntity(ENDPOINT_URL, person, EligibilityResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
    }
}
