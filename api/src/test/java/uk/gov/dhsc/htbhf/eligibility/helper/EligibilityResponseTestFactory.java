package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

public class EligibilityResponseTestFactory {

    public static EligibilityResponse anEligibilityResponse() {
        return EligibilityResponse.builder()
                .eligibilityStatus(ELIGIBLE)
                .build();
    }
}
