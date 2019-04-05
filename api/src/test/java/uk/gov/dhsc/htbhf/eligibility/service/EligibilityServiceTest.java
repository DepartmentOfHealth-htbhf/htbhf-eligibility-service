package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.dhsc.htbhf.eligibility.exception.NoEligibilityStatusProvidedException;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPEligibilityResponseTestFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.helper.HMRCEligibilityResponseTestFactory.anHMRCEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.INELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.PENDING;

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
    private EligibilityStatusCalculator statusCalculator;

    @Test
    void shouldCheckEligibilityWithEligibleResponseFromBothDwpAndHmrc() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        given(dwpClient.checkEligibility(any())).willReturn(completedFuture(dwpEligibilityResponse));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(hmrcClient.checkEligibility(any())).willReturn(completedFuture(hmrcEligibilityResponse));
        given(statusCalculator.determineStatus(any(), any())).willReturn(ELIGIBLE);

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(ELIGIBLE);
        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo("dwpHousehold1");
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo("hmrcHousehold1");
        verify(statusCalculator).determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse);
        verifyDWPRequestSent(person);
        verifyHMRCRequestSent(person);
    }

    @Test
    void shouldCheckEligibilityWithEligibleResponseFromDwpOnly() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        given(dwpClient.checkEligibility(any())).willReturn(completedFuture(dwpEligibilityResponse));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(INELIGIBLE);
        given(hmrcClient.checkEligibility(any())).willReturn(completedFuture(hmrcEligibilityResponse));
        given(statusCalculator.determineStatus(any(), any())).willReturn(ELIGIBLE);

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(ELIGIBLE);
        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo("dwpHousehold1");
        assertThat(response.getHmrcHouseholdIdentifier()).isNull();
        verify(statusCalculator).determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse);
        verifyDWPRequestSent(person);
        verifyHMRCRequestSent(person);
    }

    @Test
    void shouldCheckEligibilityWithEligibleResponseFromHmrcOnly() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(PENDING);
        given(dwpClient.checkEligibility(any())).willReturn(completedFuture(dwpEligibilityResponse));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(hmrcClient.checkEligibility(any())).willReturn(completedFuture(hmrcEligibilityResponse));
        given(statusCalculator.determineStatus(any(), any())).willReturn(ELIGIBLE);

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(ELIGIBLE);
        assertThat(response.getDwpHouseholdIdentifier()).isNull();
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo("hmrcHousehold1");
        verify(statusCalculator).determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse);
        verifyDWPRequestSent(person);
        verifyHMRCRequestSent(person);
    }

    @Test
    void shouldCheckEligibilityWithIneligibleResponseFromBothHmrcAndDwpOnly() throws ExecutionException, InterruptedException {
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(INELIGIBLE);
        given(dwpClient.checkEligibility(any())).willReturn(completedFuture(dwpEligibilityResponse));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(INELIGIBLE);
        given(hmrcClient.checkEligibility(any())).willReturn(completedFuture(hmrcEligibilityResponse));
        given(statusCalculator.determineStatus(any(), any())).willReturn(INELIGIBLE);

        EligibilityResponse response = eligibilityService.checkEligibility(person);

        assertThat(response.getEligibilityStatus()).isEqualTo(INELIGIBLE);
        assertThat(response.getDwpHouseholdIdentifier()).isNull();
        assertThat(response.getHmrcHouseholdIdentifier()).isNull();
        verify(statusCalculator).determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse);
        verifyDWPRequestSent(person);
        verifyHMRCRequestSent(person);
    }

    @Test
    void shouldThrowExceptionWhenNotGivenEligibilityStatusInDWPResponse() {
        shouldThrowExceptionWhenNotGivenEligibilityStatusInResponse(null, ELIGIBLE);
    }

    @Test
    void shouldThrowExceptionWhenNotGivenEligibilityStatusInHMRCResponse() {
        shouldThrowExceptionWhenNotGivenEligibilityStatusInResponse(ELIGIBLE, null);
    }

    @Test
    void shouldThrowExceptionWhenNotGivenEligibilityStatusInDWPOrHMRCResponse() {
        shouldThrowExceptionWhenNotGivenEligibilityStatusInResponse(null, null);
    }

    private void shouldThrowExceptionWhenNotGivenEligibilityStatusInResponse(EligibilityStatus dwpStatus, EligibilityStatus hmrcStatus) {
        PersonDTO person = aPerson();
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(dwpStatus);
        given(dwpClient.checkEligibility(any())).willReturn(completedFuture(dwpEligibilityResponse));
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(hmrcStatus);
        given(hmrcClient.checkEligibility(any())).willReturn(completedFuture(hmrcEligibilityResponse));

        Throwable thrown = catchThrowable(() -> eligibilityService.checkEligibility(person));

        assertThat(thrown).isInstanceOf(NoEligibilityStatusProvidedException.class);
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
