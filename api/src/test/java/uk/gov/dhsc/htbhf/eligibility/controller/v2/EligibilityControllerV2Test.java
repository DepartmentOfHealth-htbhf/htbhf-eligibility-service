package uk.gov.dhsc.htbhf.eligibility.controller.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EligibilityControllerV2Test {

    @InjectMocks
    private EligibilityControllerV2 controller;

    @Test
    void shouldSuccessfullyGetIdentityAndEligibilityDecision() {
        //Given
        PersonDTOV2 person = PersonDTOV2TestDataFactory.aValidPersonDTOV2();
        //When
        IdentityAndEligibilityResponse response = controller.getIdentityAndEligibilityDecision(person);
        //Then
        IdentityAndEligibilityResponse expectedResponse = IdentityAndEligibilityResponse.builder().build();
        assertThat(response).isEqualTo(expectedResponse);
    }
}
