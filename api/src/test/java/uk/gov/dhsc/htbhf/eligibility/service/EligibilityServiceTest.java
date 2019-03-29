package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPEligibilityResponseTestFactory.aDWPEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.HMRCEligibilityResponseTestFactory.anHMRCEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityServiceTest {

    @Autowired
    private EligibilityService eligibilityService;
    @MockBean
    private DWPClient dwpClient;
    @MockBean
    private HMRCClient hmrcClient;

    @Test
    void shouldCreateRequestWithValuesFromConfig() {
        PersonDTO person = aPerson();
        given(dwpClient.checkEligibility(any())).willReturn(aDWPEligibilityResponse());
        given(hmrcClient.checkEligibility(any())).willReturn(anHMRCEligibilityResponse());

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(ELIGIBLE);
        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo("dwpHousehold1");
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo("hmrcHousehold1");
        verifyDWPRequestSent(person);
        verifyHMRCRequestSent(person);
    }

    private void verifyDWPRequestSent(PersonDTO person) {
        ArgumentCaptor<DWPEligibilityRequest> argumentCaptor = ArgumentCaptor.forClass(DWPEligibilityRequest.class);
        verify(dwpClient).checkEligibility(argumentCaptor.capture());
        DWPEligibilityRequest sentRequest = argumentCaptor.getValue();
        assertThat(sentRequest.getPerson()).isEqualTo(person);
        // Below values match those in test/resources/application.yml
        assertThat(sentRequest.getUcMonthlyIncomeThreshold()).isEqualTo(BigDecimal.valueOf(408.0));
        assertThat(sentRequest.getEligibleStartDate()).isEqualTo(sentRequest.getEligibleEndDate().minusWeeks(4));
        assertThat(sentRequest.getEligibleEndDate()).isNotNull();
    }

    private void verifyHMRCRequestSent(PersonDTO person) {
        ArgumentCaptor<HMRCEligibilityRequest> argumentCaptor = ArgumentCaptor.forClass(HMRCEligibilityRequest.class);
        verify(hmrcClient).checkEligibility(argumentCaptor.capture());
        HMRCEligibilityRequest sentRequest = argumentCaptor.getValue();
        assertThat(sentRequest.getPerson()).isEqualTo(person);
        // Below values match those in test/resources/application.yml
        assertThat(sentRequest.getCtcAnnualIncomeThreshold()).isEqualTo(BigDecimal.valueOf(16190.00));
        assertThat(sentRequest.getEligibleStartDate()).isEqualTo(sentRequest.getEligibleEndDate().minusWeeks(4));
        assertThat(sentRequest.getEligibleEndDate()).isNotNull();
    }


}
