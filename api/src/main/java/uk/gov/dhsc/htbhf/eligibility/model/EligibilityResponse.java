package uk.gov.dhsc.htbhf.eligibility.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class EligibilityResponse {
    private Decision decision;
    private List<String> reasons;
}
