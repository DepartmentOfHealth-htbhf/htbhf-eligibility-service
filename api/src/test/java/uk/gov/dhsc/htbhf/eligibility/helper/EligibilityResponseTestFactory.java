package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NOMATCH;

public class EligibilityResponseTestFactory {

    public static EligibilityResponse anEligibleEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .dwpHouseholdIdentifier("dwpHousehold1")
                .hmrcHouseholdIdentifier("hmrcHousehold1")
                .build();
    }

    public static EligibilityResponse aNonMatchingEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(NOMATCH)
                .dwpHouseholdIdentifier("dwpHousehold1")
                .hmrcHouseholdIdentifier("hmrcHousehold1")
                .build();
    }
}
