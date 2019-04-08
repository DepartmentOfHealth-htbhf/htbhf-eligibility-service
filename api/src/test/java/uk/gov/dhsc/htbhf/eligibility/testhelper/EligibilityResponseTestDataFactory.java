package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NOMATCH;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.HMRC_HOUSEHOLD_IDENTIFIER;

public class EligibilityResponseTestDataFactory {

    public static EligibilityResponse anEligibleEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .dwpHouseholdIdentifier(DWP_HOUSEHOLD_IDENTIFIER)
                .hmrcHouseholdIdentifier(HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }

    public static EligibilityResponse aNonMatchingEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(NOMATCH)
                .dwpHouseholdIdentifier(DWP_HOUSEHOLD_IDENTIFIER)
                .hmrcHouseholdIdentifier(HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }
}
