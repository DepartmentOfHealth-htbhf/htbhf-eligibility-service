package uk.gov.dhsc.htbhf.eligibility.testhelper.v1;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.EligibilityResponse;

import static uk.gov.dhsc.htbhf.TestConstants.DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.TestConstants.HMRC_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;

public class EligibilityResponseTestDataFactory {

    public static EligibilityResponse anEligibleEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .dwpHouseholdIdentifier(DWP_HOUSEHOLD_IDENTIFIER)
                .hmrcHouseholdIdentifier(HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }

    public static EligibilityResponse anEligibilityResponseWithDwpHouseholdIdentifier(EligibilityStatus eligibilityStatus, String householdIdentifier) {
        return EligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .dwpHouseholdIdentifier(householdIdentifier)
                .build();
    }

    public static EligibilityResponse anEligibilityResponseWithHmrcHouseholdIdentifier(EligibilityStatus eligibilityStatus, String householdIdentifier) {
        return EligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .hmrcHouseholdIdentifier(householdIdentifier)
                .build();
    }

    public static EligibilityResponse anEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return EligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .build();
    }

    public static EligibilityResponse aNonMatchingEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(NO_MATCH)
                .dwpHouseholdIdentifier(DWP_HOUSEHOLD_IDENTIFIER)
                .hmrcHouseholdIdentifier(HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }
}
