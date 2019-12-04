package uk.gov.dhsc.htbhf.eligibility.testhelper.v2;

import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.testhelper.CombinedIdentityAndEligibilityResponseTestDataFactory;

public class CombinedIdAndEligibilityResponseTestDataFactory {

    public static CombinedIdentityAndEligibilityResponse anIdMatchedEligibilityConfirmedResponseWithNoHmrcHouseholdIdentifier() {
        return CombinedIdentityAndEligibilityResponseTestDataFactory.anIdentityMatchedEligibilityConfirmedUCResponseWithAllMatches()
                .toBuilder()
                .hmrcHouseholdIdentifier(null)
                .build();
    }
}
