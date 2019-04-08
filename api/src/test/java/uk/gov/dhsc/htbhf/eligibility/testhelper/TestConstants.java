package uk.gov.dhsc.htbhf.eligibility.testhelper;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestConstants {

    public static final LocalDate DOB = LocalDate.parse("1985-12-31");
    public static final String NINO = "EB123456C";
    public static final String FIRST_NAME = "Lisa";
    public static final String LAST_NAME = "Simpson";
    public static final LocalDate FUTURE_DATE = LocalDate.now().plusMonths(1);

    public static final String ADDRESS_LINE_1 = "742 Evergreen Terrace";
    public static final String ADDRESS_LINE_2 = "123 Fake street";
    public static final String TOWN_OR_CITY = "Springfield";
    public static final String POSTCODE = "AA1 1AA";

    public static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    public static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    public static final BigDecimal UC_MONTHLY_INCOME_THRESHOLD = BigDecimal.valueOf(408);
    public static final BigDecimal CTC_ANNUAL_INCOME_THRESHOLD = BigDecimal.valueOf(16190.00);

    public static final String DWP_HOUSEHOLD_IDENTIFIER = "dwpHousehold1";
    public static final String HMRC_HOUSEHOLD_IDENTIFIER = "hmrcHousehold1";
}
