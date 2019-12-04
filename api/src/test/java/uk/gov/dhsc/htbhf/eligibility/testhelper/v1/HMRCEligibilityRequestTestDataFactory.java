package uk.gov.dhsc.htbhf.eligibility.testhelper.v1;

import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityRequest;

import java.time.LocalDate;

import static uk.gov.dhsc.htbhf.TestConstants.ELIGIBLE_END_DATE;
import static uk.gov.dhsc.htbhf.TestConstants.ELIGIBLE_START_DATE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.CTC_ANNUAL_INCOME_THRESHOLD;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.PersonDTOTestDataFactory.aPerson;

public class HMRCEligibilityRequestTestDataFactory {

    public static HMRCEligibilityRequest anHMRCEligibilityRequest() {
        return buildDefaultRequest().build();
    }

    public static HMRCEligibilityRequest anHMRCEligibilityRequestWithEligibilityDates(LocalDate startDate, LocalDate endDate) {
        return buildDefaultRequest()
                .eligibleStartDate(startDate)
                .eligibleEndDate(endDate)
                .build();
    }

    private static HMRCEligibilityRequest.HMRCEligibilityRequestBuilder buildDefaultRequest() {
        return HMRCEligibilityRequest.builder()
                .person(aPerson())
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ctcAnnualIncomeThreshold(CTC_ANNUAL_INCOME_THRESHOLD);
    }
}
