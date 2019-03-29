package uk.gov.dhsc.htbhf.eligibility.model.hmrc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class HMRCEligibilityRequest {

    @JsonProperty("person")
    private PersonDTO person;

    @JsonProperty("ctcAnnualIncomeThreshold")
    private final BigDecimal ctcAnnualIncomeThreshold;

    @JsonProperty("eligibleStartDate")
    private final LocalDate eligibleStartDate;

    @JsonProperty("eligibleEndDate")
    private final LocalDate eligibleEndDate;
}
