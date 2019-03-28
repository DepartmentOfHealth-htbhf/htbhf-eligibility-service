package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

public class DWPEligibilityResponseTestFactory {

    public static DWPEligibilityResponse aDWPEligibilityResponse() {
        return DWPEligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .householdIdentifier("dwpHousehold1")
                .build();
    }
}
