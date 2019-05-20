package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_DWP_HOUSEHOLD_IDENTIFIER;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER;

public class EligibilityResponseTestDataFactory {

    public static EligibilityResponse anEligibleEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .dwpHouseholdIdentifier(SIMPSON_DWP_HOUSEHOLD_IDENTIFIER)
                .hmrcHouseholdIdentifier(SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER)
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
                .dwpHouseholdIdentifier(SIMPSON_DWP_HOUSEHOLD_IDENTIFIER)
                .hmrcHouseholdIdentifier(SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER)
                .build();
    }
}