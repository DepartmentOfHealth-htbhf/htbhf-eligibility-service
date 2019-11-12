package uk.gov.dhsc.htbhf.eligibility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;
import uk.gov.dhsc.htbhf.errorhandler.ErrorResponse;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.assertions.IntegrationTestAssertions.assertValidationErrorInResponse;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory.aPersonDTOV2WithNino;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory.aValidPersonDTOV2;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityServiceIntegrationTestsV2 {

    private static final URI ENDPOINT = URI.create("/v2/eligibility");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnEligibleResponseGivenEligibleResponseReturnedFromBothDwpAndHmrc() {
        //Given
        PersonDTOV2 person = aValidPersonDTOV2();
        //When
        ResponseEntity<IdentityAndEligibilityResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person), IdentityAndEligibilityResponse.class);
        //Then
        IdentityAndEligibilityResponse expectedResponse = IdentityAndEligibilityResponse.builder().build();
        assertThat(responseEntity.getStatusCode()).isEqualTo(OK);
        IdentityAndEligibilityResponse identityAndEligibilityResponse = responseEntity.getBody();
        assertThat(identityAndEligibilityResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() {
        //Given
        PersonDTOV2 person = aPersonDTOV2WithNino(null);
        //When
        ResponseEntity<ErrorResponse> responseEntity = restTemplate.exchange(buildRequestEntity(person), ErrorResponse.class);

        assertValidationErrorInResponse(responseEntity, "nino", "must not be null");
    }

    private RequestEntity buildRequestEntity(PersonDTOV2 personDTO) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new RequestEntity<>(personDTO, headers, POST, ENDPOINT);
    }

}
