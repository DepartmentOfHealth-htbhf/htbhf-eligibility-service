package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.dwp.model.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.PersonDTO;
import uk.gov.dhsc.htbhf.dwp.testhelper.PersonDTOTestDataFactory;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.dwp.testhelper.DWPEligibilityRequestTestDataFactory.aValidDWPEligibilityRequestWithEligibilityEndDate;
import static uk.gov.dhsc.htbhf.dwp.testhelper.IdAndEligibilityResponseTestDataFactory.anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId;
import static uk.gov.dhsc.htbhf.eligibility.model.testhelper.CombinedIdAndEligibilityResponseTestDataFactory.anIdMatchedEligibilityConfirmedUCResponseWithAllMatchesAndHmrcHouseIdentifier;

@ExtendWith(MockitoExtension.class)
class IdentityAndEligibilityServiceTest {

    private static final int UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE = 40800;
    @Mock
    private DWPClient client;

    private IdentityAndEligibilityService service;

    @BeforeEach
    void setUp() {
        service = new IdentityAndEligibilityService(UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE, client);
    }

    @Test
    void shouldSuccessfullyCheckIdentityAndEligibility() {
        //Given
        PersonDTO person = PersonDTOTestDataFactory.aValidPersonDTO();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = anAllMatchedEligibilityConfirmedUCResponseWithHouseholdId();
        given(client.checkIdentityAndEligibility(any())).willReturn(identityAndEligibilityResponse);

        //When
        CombinedIdentityAndEligibilityResponse response = service.checkIdentityAndEligibility(person);

        //Then
        assertThat(response).isEqualTo(anIdMatchedEligibilityConfirmedUCResponseWithAllMatchesAndHmrcHouseIdentifier(null));
        verify(client).checkIdentityAndEligibility(aValidDWPEligibilityRequestWithEligibilityEndDate(LocalDate.now()));
    }
}
