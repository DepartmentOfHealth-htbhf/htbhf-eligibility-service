package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static uk.gov.dhsc.htbhf.eligibility.helper.PersonDTOTestFactory.aPerson;

public class HMRCEligibilityRequestTestFactory {

    private static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    private static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    private static final BigDecimal CTC_ANNUAL_INCOME_THRESHOLD = BigDecimal.valueOf(16190.00);

    public static HMRCEligibilityRequest anHMRCEligibilityRequest() {
        return buildDefaultRequest().build();
    }

    public static HMRCEligibilityRequest.HMRCEligibilityRequestBuilder buildDefaultRequest() {
        return HMRCEligibilityRequest.builder()
                .person(aPerson())
                .eligibleStartDate(ELIGIBLE_START_DATE)
                .eligibleEndDate(ELIGIBLE_END_DATE)
                .ctcAnnualIncomeThreshold(CTC_ANNUAL_INCOME_THRESHOLD);
    }
}
