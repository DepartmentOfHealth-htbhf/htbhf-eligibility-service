package uk.gov.dhsc.htbhf.eligibility.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class EligibilityRequest {

    @JsonProperty("person")
    private DWPPersonDTO person;

    @JsonProperty("ucMonthlyIncomeThreshold")
    private final BigDecimal ucMonthlyIncomeThreshold;

    @JsonProperty("eligibleStartDate")
    private final LocalDate eligibleStartDate;

    @JsonProperty("eligibleEndDate")
    private final LocalDate eligibleEndDate;
}
