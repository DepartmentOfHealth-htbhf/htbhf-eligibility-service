package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.converter.PersonDTOToDWPPersonConverter;
import uk.gov.dhsc.htbhf.eligibility.model.DWPPersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPEligibilityResponseTestFactory.aDWPEligibilityResponse;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPPersonTestFactory.aDWPPerson;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityServiceTest {

    @Autowired
    private EligibilityService eligibilityService;
    @MockBean
    private PersonDTOToDWPPersonConverter converter;
    @MockBean
    private DWPClient dwpClient;

    @Test
    void shouldCreateRequestWithValuesFromConfig() {
        PersonDTO person = aPerson();
        DWPPersonDTO dwpPerson = aDWPPerson();
        given(converter.convert(any())).willReturn(dwpPerson);
        given(dwpClient.checkEligibility(any())).willReturn(aDWPEligibilityResponse());

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(ELIGIBLE);
        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo("dwpHousehold1");
        assertThat(response.getHmrcHouseholdIdentifier()).isNull();
        verify(converter).convert(person);
        verifyRequestSent(dwpPerson);
    }

    private void verifyRequestSent(DWPPersonDTO dwpPerson) {
        ArgumentCaptor<EligibilityRequest> argumentCaptor = ArgumentCaptor.forClass(EligibilityRequest.class);
        verify(dwpClient).checkEligibility(argumentCaptor.capture());
        EligibilityRequest sentRequest = argumentCaptor.getValue();
        assertThat(sentRequest.getPerson()).isEqualTo(dwpPerson);
        // Below values match those in test/resources/application.yml
        assertThat(sentRequest.getUcMonthlyIncomeThreshold()).isEqualTo(BigDecimal.valueOf(408.0));
        assertThat(sentRequest.getEligibleStartDate()).isEqualTo(sentRequest.getEligibleEndDate().minusWeeks(4));
        assertThat(sentRequest.getEligibleEndDate()).isNotNull();
    }
}
