package uk.gov.dhsc.htbhf.eligibility.service.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.factory.v1.EligibilityResponseFactory;
import uk.gov.dhsc.htbhf.eligibility.model.v1.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.v1.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.v1.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.testhelper.v1.EligibilityResponseTestDataFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.DWPEligibilityResponseTestDataFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.PersonDTOTestDataFactory.aPerson;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EligibilityServiceTest {

    @Autowired
    private EligibilityService eligibilityService;

    @MockBean
    private DWPClient dwpClient;
    @MockBean
    private HMRCClient hmrcClient;
    @MockBean
    private EligibilityResponseFactory responseFactory;

    @Test
    void shouldCheckEligibilityWithEligibleResponseFromBothDwpAndHmrc() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        given(dwpClient.checkEligibility(any())).willReturn(completedFuture(dwpEligibilityResponse));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(hmrcClient.checkEligibility(any())).willReturn(completedFuture(hmrcEligibilityResponse));
        EligibilityResponse expectedResponse = EligibilityResponseTestDataFactory.anEligibleEligibilityResponse();
        given(responseFactory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(expectedResponse);

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response).isEqualTo(expectedResponse);
        verifyDWPRequestSent(person);
        verifyHMRCRequestSent(person);
    }

    private void verifyDWPRequestSent(PersonDTO person) {
        ArgumentCaptor<DWPEligibilityRequest> argumentCaptor = ArgumentCaptor.forClass(DWPEligibilityRequest.class);
        verify(dwpClient).checkEligibility(argumentCaptor.capture());
        DWPEligibilityRequest sentRequest = argumentCaptor.getValue();
        assertThat(sentRequest.getPerson()).isEqualTo(person);
        // Below values match those in test/resources/application.yml
        BigDecimal expectedThresholdInPounds = BigDecimal.valueOf(408.00).setScale(2, RoundingMode.HALF_UP);
        assertThat(sentRequest.getUcMonthlyIncomeThreshold()).isEqualTo(expectedThresholdInPounds);
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
