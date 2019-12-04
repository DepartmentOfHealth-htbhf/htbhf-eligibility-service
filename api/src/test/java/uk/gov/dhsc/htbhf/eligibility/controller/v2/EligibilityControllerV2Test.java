package uk.gov.dhsc.htbhf.eligibility.controller.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.service.v2.IdentityAndEligibilityService;
import uk.gov.dhsc.htbhf.eligibility.testhelper.v2.CombinedIdAndEligibilityTestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
        CombinedIdentityAndEligibilityResponse identityAndEligibilityResponse = CombinedIdAndEligibilityTestDataFactory
                .anIdMatchedEligibilityConfirmedResponseWithNoHmrcHouseholdIdentifier();
        given(service.checkIdentityAndEligibility(any())).willReturn(identityAndEligibilityResponse);
        //When
        CombinedIdentityAndEligibilityResponse response = controller.getIdentityAndEligibilityDecision(person);
        //Then
        assertThat(response).isEqualTo(identityAndEligibilityResponse);
        verify(service).checkIdentityAndEligibility(person);
    }
}
