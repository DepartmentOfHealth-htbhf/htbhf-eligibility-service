package uk.gov.dhsc.htbhf.eligibility.testhelper;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestConstants {

    public static final LocalDate FUTURE_DATE = LocalDate.now().plusMonths(1);

    public static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    public static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    public static final BigDecimal CTC_ANNUAL_INCOME_THRESHOLD = BigDecimal.valueOf(16190.00);

    public static final String SIMPSON_DWP_HOUSEHOLD_IDENTIFIER = "dwpHousehold1";
    public static final String SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER = "hmrcHousehold1";
}
