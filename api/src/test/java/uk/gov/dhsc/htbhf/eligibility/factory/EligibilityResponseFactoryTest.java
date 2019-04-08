package uk.gov.dhsc.htbhf.eligibility.factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.service.EligibilityStatusCalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.INELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.PENDING;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.DWPEligibilityResponseTestDataFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.DWPEligibilityResponseTestDataFactory.aDwpEligibilityResponseBuilder;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseBuilder;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER;

@ExtendWith(MockitoExtension.class)
class EligibilityResponseFactoryTest {

    @Mock
    EligibilityStatusCalculator statusCalculator;

    @InjectMocks
    EligibilityResponseFactory factory;


    @ParameterizedTest
    @EnumSource(EligibilityStatus.class)
    void shouldCreateEligibleResponseWithCorrectStatus(EligibilityStatus status) {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(status);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getEligibilityStatus()).isEqualTo(status);
    }

    @Test
    void shouldSetHouseholdIdentifiersFromDwpAndHMRC() {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo(SIMPSON_DWP_HOUSEHOLD_IDENTIFIER);
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo(SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER);
    }

    @Test
    void shouldSetHouseholdIdentifierFromDwpOnly() {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(INELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo(SIMPSON_DWP_HOUSEHOLD_IDENTIFIER);
        assertThat(response.getHmrcHouseholdIdentifier()).isNull();
    }

    @Test
    void shouldSetHouseholdIdentifierFromHmrcOnly() {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(PENDING);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getDwpHouseholdIdentifier()).isNull();
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo(SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER);
    }

    @Test
    void shouldNotSetHouseholdIdentifiers() {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(INELIGIBLE);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(INELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(INELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getDwpHouseholdIdentifier()).isNull();
        assertThat(response.getHmrcHouseholdIdentifier()).isNull();
    }

    @Test
    void shouldUseMaximumCountOfChildren() {
        DWPEligibilityResponse dwpEligibilityResponse = aDwpEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(1)
                .numberOfChildrenUnderFour(2)
                .build();
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(0)
                .numberOfChildrenUnderFour(0)
                .build();
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getNumberOfChildrenUnderOne()).isEqualTo(1);
        assertThat(response.getNumberOfChildrenUnderFour()).isEqualTo(2);

    }

    @Test
    void shouldHandleNullChildCounts() {
        DWPEligibilityResponse dwpEligibilityResponse = aDwpEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(null)
                .numberOfChildrenUnderFour(null)
                .build();
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(1)
                .numberOfChildrenUnderFour(2)
                .build();
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getNumberOfChildrenUnderOne()).isEqualTo(1);
        assertThat(response.getNumberOfChildrenUnderFour()).isEqualTo(2);

    }

    @Test
    void shouldReturnNullChildCounts() {
        DWPEligibilityResponse dwpEligibilityResponse = aDwpEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(null)
                .numberOfChildrenUnderFour(null)
                .build();
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(null)
                .numberOfChildrenUnderFour(null)
                .build();
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getNumberOfChildrenUnderOne()).isNull();
        assertThat(response.getNumberOfChildrenUnderFour()).isNull();

    }
}