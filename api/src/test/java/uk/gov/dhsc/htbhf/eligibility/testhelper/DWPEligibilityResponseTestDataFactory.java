package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_DWP_HOUSEHOLD_IDENTIFIER;

public class DWPEligibilityResponseTestDataFactory {

    public static DWPEligibilityResponse aDWPEligibilityResponse() {
        return aDwpEligibilityResponseBuilder(ELIGIBLE)
                .build();
    }

    public static DWPEligibilityResponse aDWPEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return aDwpEligibilityResponseBuilder(eligibilityStatus)
                .build();
    }

    public static DWPEligibilityResponse.DWPEligibilityResponseBuilder aDwpEligibilityResponseBuilder(EligibilityStatus eligibilityStatus) {
        return DWPEligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .householdIdentifier(SIMPSON_DWP_HOUSEHOLD_IDENTIFIER);
    }
}
