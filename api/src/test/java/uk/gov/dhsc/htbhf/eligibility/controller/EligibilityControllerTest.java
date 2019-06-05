package uk.gov.dhsc.htbhf.eligibility.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.service.EligibilityService;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.EligibilityResponseTestDataFactory.aNonMatchingEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.EligibilityResponseTestDataFactory.anEligibleEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.PersonDTOTestDataFactory.aPerson;

@ExtendWith(MockitoExtension.class)
class EligibilityControllerTest {

    @InjectMocks
    private EligibilityController eligibilityController;

    @Mock
    private EligibilityService eligibilityService;

    @Test
    void shouldGetEligibility() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        EligibilityResponse eligibilityResponse = anEligibleEligibilityResponse();
        given(eligibilityService.checkEligibility(any())).willReturn(eligibilityResponse);

        ResponseEntity<EligibilityResponse> response = eligibilityController.getDecision(person);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(eligibilityResponse);
        verify(eligibilityService).checkEligibility(person);
    }

    @Test
    void shouldReturnNotFoundForNonMatchingNino() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        EligibilityResponse eligibilityResponse = aNonMatchingEligibilityResponse();
        given(eligibilityService.checkEligibility(any())).willReturn(eligibilityResponse);

        ResponseEntity<EligibilityResponse> response = eligibilityController.getDecision(person);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(eligibilityResponse);
        verify(eligibilityService).checkEligibility(person);
    }
}
