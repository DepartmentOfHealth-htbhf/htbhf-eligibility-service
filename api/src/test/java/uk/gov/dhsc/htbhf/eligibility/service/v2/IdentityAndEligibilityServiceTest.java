package uk.gov.dhsc.htbhf.eligibility.service.v2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.IdentityAndEligibilityResponseTestDataFactory;
import uk.gov.dhsc.htbhf.dwp.testhelper.v2.PersonDTOV2TestDataFactory;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.testhelper.v2.CombinedIdAndEligibilityResponseTestDataFactory;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.dwp.testhelper.v2.DWPEligibilityRequestV2TestDataFactory.aValidDWPEligibilityRequestV2WithEligibilityEndDate;

@ExtendWith(MockitoExtension.class)
class IdentityAndEligibilityServiceTest {

    private static final int UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE = 40800;
    @Mock
    private DWPClientV2 client;

    private IdentityAndEligibilityService service;

    @BeforeEach
    void setUp() {
        service = new IdentityAndEligibilityService(UC_MONTHLY_INCOME_THRESHOLD_IN_PENCE, client);
    }

    @Test
    void shouldSuccessfullyCheckIdentityAndEligibility() {
        //Given
        PersonDTOV2 person = PersonDTOV2TestDataFactory.aValidPersonDTOV2();
        IdentityAndEligibilityResponse identityAndEligibilityResponse = IdentityAndEligibilityResponseTestDataFactory
                .anAllMatchedEligibilityConfirmedUCResponseWithHouseholdIdentifier();
        given(client.checkIdentityAndEligibility(any())).willReturn(identityAndEligibilityResponse);

        //When
        CombinedIdentityAndEligibilityResponse response = service.checkIdentityAndEligibility(person);

        //Then
        assertThat(response).isEqualTo(CombinedIdAndEligibilityResponseTestDataFactory
                .anIdMatchedEligibilityConfirmedResponseWithNoHmrcHouseholdIdentifier());
        verify(client).checkIdentityAndEligibility(aValidDWPEligibilityRequestV2WithEligibilityEndDate(LocalDate.now()));
    }
}
