package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.testhelper.CombinedIdAndEligibilityResponseTestDataFactory;

public class CombinedIdAndEligibilityTestDataFactory {

    public static CombinedIdentityAndEligibilityResponse anIdMatchedEligibilityConfirmedResponseWithNoHmrcHouseholdIdentifier() {
        return CombinedIdAndEligibilityResponseTestDataFactory.anIdMatchedEligibilityConfirmedUCResponseWithAllMatches()
                .toBuilder()
                .hmrcHouseholdIdentifier(null)
                .build();
    }
}
