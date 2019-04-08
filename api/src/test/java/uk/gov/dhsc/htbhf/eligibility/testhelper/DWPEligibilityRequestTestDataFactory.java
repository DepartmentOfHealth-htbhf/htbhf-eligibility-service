package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;

import java.time.LocalDate;

import static uk.gov.dhsc.htbhf.eligibility.testhelper.PersonDTOTestDataFactory.aPerson;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.ELIGIBLE_END_DATE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.ELIGIBLE_START_DATE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.UC_MONTHLY_INCOME_THRESHOLD;

public class DWPEligibilityRequestTestDataFactory {

    public static DWPEligibilityRequest aDWPEligibilityRequest() {
        return buildDefaultRequest().build();
    }

    public static DWPEligibilityRequest aDWPEligibilityRequestWithEligibilityDates(LocalDate startDate, LocalDate endDate) {
        return buildDefaultRequest()
                .eligibleStartDate(startDate)
                .eligibleEndDate(endDate)
                .build();
    }

    private static DWPEligibilityRequest.DWPEligibilityRequestBuilder buildDefaultRequest() {
        return DWPEligibilityRequest.builder()
                .person(aPerson())
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ucMonthlyIncomeThreshold(UC_MONTHLY_INCOME_THRESHOLD);
    }
}
