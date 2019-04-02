package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

public class DWPEligibilityResponseTestFactory {

    private static final String HOUSEHOLD_IDENTIFIER = "dwpHousehold1";

    public static DWPEligibilityResponse aDWPEligibilityResponse() {
        return DWPEligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .householdIdentifier(HOUSEHOLD_IDENTIFIER)
                .build();
    }

    public static DWPEligibilityResponse aDWPEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return DWPEligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .householdIdentifier(HOUSEHOLD_IDENTIFIER)
                .build();
    }
}
