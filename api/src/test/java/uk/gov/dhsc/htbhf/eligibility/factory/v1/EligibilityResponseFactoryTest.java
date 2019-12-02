package uk.gov.dhsc.htbhf.eligibility.factory.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.ChildDTO;
import uk.gov.dhsc.htbhf.eligibility.model.v1.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.service.v1.EligibilityStatusCalculator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static uk.gov.dhsc.htbhf.TestConstants.DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.TestConstants.HMRC_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.INELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.PENDING;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.DWPEligibilityResponseTestDataFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.DWPEligibilityResponseTestDataFactory.aDwpEligibilityResponseBuilder;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.DWPEligibilityResponseTestDataFactory.createChildren;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseBuilder;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.HMRCEligibilityResponseTestDataFactory.anHMRCEligibilityResponseWithStatus;

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

        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo(DWP_HOUSEHOLD_IDENTIFIER);
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo(HMRC_HOUSEHOLD_IDENTIFIER);
    }

    @Test
    void shouldSetHouseholdIdentifierFromDwpOnly() {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(ELIGIBLE);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(INELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getDwpHouseholdIdentifier()).isEqualTo(DWP_HOUSEHOLD_IDENTIFIER);
        assertThat(response.getHmrcHouseholdIdentifier()).isNull();
    }

    @Test
    void shouldSetHouseholdIdentifierFromHmrcOnly() {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(PENDING);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(ELIGIBLE);
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getDwpHouseholdIdentifier()).isNull();
        assertThat(response.getHmrcHouseholdIdentifier()).isEqualTo(HMRC_HOUSEHOLD_IDENTIFIER);
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
        List<ChildDTO> children = createChildren(1, 2);
        DWPEligibilityResponse dwpEligibilityResponse = aDwpEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(1)
                .numberOfChildrenUnderFour(2)
                .children(children)
                .build();
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(0)
                .numberOfChildrenUnderFour(0)
                .children(null)
                .build();
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getNumberOfChildrenUnderOne()).isEqualTo(1);
        assertThat(response.getNumberOfChildrenUnderFour()).isEqualTo(2);
        assertThat(response.getChildren()).isEqualTo(children);
    }

    @Test
    void shouldHandleNullChildCounts() {
        DWPEligibilityResponse dwpEligibilityResponse = aDwpEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(null)
                .numberOfChildrenUnderFour(null)
                .children(null)
                .build();
        List<ChildDTO> children = createChildren(1, 2);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(1)
                .numberOfChildrenUnderFour(2)
                .children(children)
                .build();
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getNumberOfChildrenUnderOne()).isEqualTo(1);
        assertThat(response.getNumberOfChildrenUnderFour()).isEqualTo(2);
        assertThat(response.getChildren()).isEqualTo(children);

    }

    @Test
    void shouldReturnNullChildCounts() {
        DWPEligibilityResponse dwpEligibilityResponse = aDwpEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(null)
                .numberOfChildrenUnderFour(null)
                .children(null)
                .build();
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .numberOfChildrenUnderOne(null)
                .numberOfChildrenUnderFour(null)
                .children(null)
                .build();
        given(statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse)).willReturn(ELIGIBLE);

        EligibilityResponse response = factory.createEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(response.getNumberOfChildrenUnderOne()).isNull();
        assertThat(response.getNumberOfChildrenUnderFour()).isNull();
        assertThat(response.getChildren()).isNull();
    }
}
