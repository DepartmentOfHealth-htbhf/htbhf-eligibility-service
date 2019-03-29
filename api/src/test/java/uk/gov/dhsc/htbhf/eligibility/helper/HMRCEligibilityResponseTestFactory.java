package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

public class HMRCEligibilityResponseTestFactory {

    public static HMRCEligibilityResponse anHMRCEligibilityResponse() {
        return HMRCEligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .householdIdentifier("hmrcHousehold1")
                .build();
    }

    public static HMRCEligibilityResponse anHMRCEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return HMRCEligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .householdIdentifier("hmrcHousehold1")
                .build();
    }
}
