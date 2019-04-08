package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.HMRC_HOUSEHOLD_IDENTIFIER;

public class HMRCEligibilityResponseTestDataFactory {

    public static HMRCEligibilityResponse anHMRCEligibilityResponse() {
        return HMRCEligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .householdIdentifier(HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }

    public static HMRCEligibilityResponse anHMRCEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return HMRCEligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .householdIdentifier(HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }
}
