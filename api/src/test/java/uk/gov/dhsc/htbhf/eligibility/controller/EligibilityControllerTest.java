package uk.gov.dhsc.htbhf.eligibility.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.dwp.model.PersonDTO;
import uk.gov.dhsc.htbhf.dwp.testhelper.PersonDTOTestDataFactory;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.service.IdentityAndEligibilityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.eligibility.model.testhelper.CombinedIdAndEligibilityResponseTestDataFactory.anIdMatchedEligibilityConfirmedUCResponseWithAllMatchesAndHmrcHouseIdentifier;

@ExtendWith(MockitoExtension.class)
class EligibilityControllerTest {

    @Mock
    private IdentityAndEligibilityService service;
    @InjectMocks
    private EligibilityController controller;

    @Test
    void shouldSuccessfullyGetIdentityAndEligibilityDecision() {
        //Given
        PersonDTO person = PersonDTOTestDataFactory.aValidPersonDTO();
        CombinedIdentityAndEligibilityResponse identityAndEligibilityResponse =
                anIdMatchedEligibilityConfirmedUCResponseWithAllMatchesAndHmrcHouseIdentifier(null);
        given(service.checkIdentityAndEligibility(any())).willReturn(identityAndEligibilityResponse);
        //When
        CombinedIdentityAndEligibilityResponse response = controller.getIdentityAndEligibilityDecision(person);
        //Then
        assertThat(response).isEqualTo(identityAndEligibilityResponse);
        verify(service).checkIdentityAndEligibility(person);
    }
}
