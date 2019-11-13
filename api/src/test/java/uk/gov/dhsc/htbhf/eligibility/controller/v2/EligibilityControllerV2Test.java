package uk.gov.dhsc.htbhf.eligibility.controller.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory;
import uk.gov.dhsc.htbhf.eligibility.service.v2.IdentityAndEligibilityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.IdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches;

@ExtendWith(MockitoExtension.class)
class EligibilityControllerV2Test {

    @Mock
    private IdentityAndEligibilityService service;
    @InjectMocks
    private EligibilityControllerV2 controller;

    @Test
    void shouldSuccessfullyGetIdentityAndEligibilityDecision() {
        //Given
        PersonDTOV2 person = PersonDTOV2TestDataFactory.aValidPersonDTOV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches();
        given(service.checkIdentityAndEligibility(any())).willReturn(identityAndEligibilityResponse);
        //When
        IdentityAndEligibilityResponse response = controller.getIdentityAndEligibilityDecision(person);
        //Then
        assertThat(response).isEqualTo(identityAndEligibilityResponse);
        verify(service).checkIdentityAndEligibility(person);
    }
}
