package uk.gov.dhsc.htbhf.eligibility.testhelper;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class TestConstants {

    public static final LocalDate HOMER_DATE_OF_BIRTH = LocalDate.parse("1985-12-31");
    public static final String HOMER_NINO = "EB123456C";
    public static final String HOMER_FIRST_NAME = "Lisa";
    public static final String SIMPSON_LAST_NAME = "Simpson";
    public static final LocalDate FUTURE_DATE = LocalDate.now().plusMonths(1);

    public static final LocalDate MAGGIE_DOB = LocalDate.now().minusMonths(6);
    public static final LocalDate LISA_DOB = LocalDate.now().minusMonths(24);

    public static final String SIMPSONS_ADDRESS_LINE_1 = "742 Evergreen Terrace";
    public static final String SIMPSONS_ADDRESS_LINE_2 = "Suburb";
    public static final String SIMPSONS_TOWN_OR_CITY = "Springfield";
    public static final String SIMPSONS_POSTCODE = "AA1 1AA";

    public static final LocalDate ELIGIBLE_END_DATE = LocalDate.parse("2019-03-01");
    public static final LocalDate ELIGIBLE_START_DATE = LocalDate.parse("2019-02-14");
    public static final BigDecimal UC_MONTHLY_INCOME_THRESHOLD = BigDecimal.valueOf(408.0);
    public static final BigDecimal CTC_ANNUAL_INCOME_THRESHOLD = BigDecimal.valueOf(16190.00);

    public static final String SIMPSON_DWP_HOUSEHOLD_IDENTIFIER = "dwpHousehold1";
    public static final String SIMPSON_HMRC_HOUSEHOLD_IDENTIFIER = "hmrcHousehold1";
}
