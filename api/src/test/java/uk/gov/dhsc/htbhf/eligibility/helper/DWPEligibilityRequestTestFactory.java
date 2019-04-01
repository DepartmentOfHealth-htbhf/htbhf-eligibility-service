package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;

public class DWPEligibilityRequestTestFactory {

    private static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    private static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    private static final BigDecimal UC_MONTHLY_INCOME_THRESHOLD = BigDecimal.valueOf(408);

    public static DWPEligibilityRequest aDWPEligibilityRequest() {
        return buildDefaultRequest().build();
    }

    public static DWPEligibilityRequest.DWPEligibilityRequestBuilder buildDefaultRequest() {
        return DWPEligibilityRequest.builder()
                .person(aPerson())
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ucMonthlyIncomeThreshold(UC_MONTHLY_INCOME_THRESHOLD);
    }
}
