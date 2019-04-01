package uk.gov.dhsc.htbhf.eligibility.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.dhsc.htbhf.eligibility.helper.DWPEligibilityResponseTestFactory.aDWPEligibilityResponseWithStatus;
import static uk.gov.dhsc.htbhf.eligibility.helper.HMRCEligibilityResponseTestFactory.anHMRCEligibilityResponseWithStatus;

public class EligibilityStatusCalculatorTest {

    private final EligibilityStatusCalculator statusCalculator = new EligibilityStatusCalculator();

    @ParameterizedTest(name = "Should return {2} when DWP status is {0} and HMRC status is {1}")
    @CsvSource({
            "ELIGIBLE, ELIGIBLE, ELIGIBLE",
            "ELIGIBLE, INELIGIBLE, ELIGIBLE",
            "ELIGIBLE, PENDING, ELIGIBLE",
            "ELIGIBLE, ERROR, ELIGIBLE",
            "ELIGIBLE, NOMATCH, ELIGIBLE",
            "PENDING, PENDING, PENDING",
            "PENDING, INELIGIBLE, PENDING",
            "PENDING, ELIGIBLE, ELIGIBLE",
            "PENDING, ERROR, PENDING",
            "PENDING, NOMATCH, PENDING",
            "ERROR, ERROR, ERROR",
            "ERROR, INELIGIBLE, ERROR",
            "ERROR, ELIGIBLE, ELIGIBLE",
            "ERROR, PENDING, PENDING",
            "ERROR, NOMATCH, ERROR",
            "INELIGIBLE, ELIGIBLE, ELIGIBLE",
            "INELIGIBLE, PENDING, PENDING",
            "INELIGIBLE, INELIGIBLE, INELIGIBLE",
            "INELIGIBLE, NOMATCH, INELIGIBLE",
            "INELIGIBLE, ERROR, ERROR",
            "NOMATCH, INELIGIBLE, INELIGIBLE",
            "NOMATCH, ELIGIBLE, ELIGIBLE",
            "NOMATCH, PENDING, PENDING",
            "NOMATCH, ERROR, ERROR",
            "NOMATCH, NOMATCH, NOMATCH"
    })
    void shouldReturnCorrectEligibilityStatus(EligibilityStatus dwpStatus, EligibilityStatus hmrcStatus, EligibilityStatus responseStatus) {
        DWPEligibilityResponse dwpEligibilityResponse = aDWPEligibilityResponseWithStatus(dwpStatus);
        HMRCEligibilityResponse hmrcEligibilityResponse = anHMRCEligibilityResponseWithStatus(hmrcStatus);

        EligibilityStatus result = statusCalculator.determineStatus(dwpEligibilityResponse, hmrcEligibilityResponse);

        assertThat(result).isEqualTo(responseStatus);
    }
}
