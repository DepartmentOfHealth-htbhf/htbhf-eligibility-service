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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonTestFactory.aPerson;
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
    void shouldCreateRequest() {
        PersonDTO person = aPerson();
        DWPPersonDTO dwpPerson = DWPPersonDTO.builder().build();
        given(converter.convert(person)).willReturn(dwpPerson);
        EligibilityResponse eligibilityResponse = EligibilityResponse.builder().eligibilityStatus(ELIGIBLE).build();
        given(dwpClient.checkEligibility(any())).willReturn(eligibilityResponse);

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(ELIGIBLE);
        verify(converter).convert(person);
        verifyRequestSent(dwpPerson);
    }

    private void verifyRequestSent(DWPPersonDTO dwpPerson) {
        ArgumentCaptor<EligibilityRequest> argumentCaptor = ArgumentCaptor.forClass(EligibilityRequest.class);
        verify(dwpClient).checkEligibility(argumentCaptor.capture());
        EligibilityRequest sentRequest = argumentCaptor.getValue();
        assertThat(sentRequest.getPerson()).isEqualTo(dwpPerson);
    }
}
