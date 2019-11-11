package uk.gov.dhsc.htbhf.eligibility.testhelper.v1;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER;

public class HMRCEligibilityResponseTestDataFactory {

    public static HMRCEligibilityResponse anHMRCEligibilityResponse() {
        return anHMRCEligibilityResponseBuilder(ELIGIBLE)
                .build();
    }

    public static HMRCEligibilityResponse anHMRCEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return anHMRCEligibilityResponseBuilder(eligibilityStatus)
                .build();
    }

    public static HMRCEligibilityResponse.HMRCEligibilityResponseBuilder anHMRCEligibilityResponseBuilder(EligibilityStatus eligibilityStatus) {
        return HMRCEligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .householdIdentifier(SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER);
    }
}
