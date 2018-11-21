package uk.gov.dhsc.htbhf.eligibility.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EligibilityResponse {
    private Decision decision;
    private List<String> reasons;
}
