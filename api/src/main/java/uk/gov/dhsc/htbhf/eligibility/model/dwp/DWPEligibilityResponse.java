package uk.gov.dhsc.htbhf.eligibility.model.dwp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.dhsc.htbhf.eligibility.model.ChildDTO;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class DWPEligibilityResponse {

    @JsonProperty("eligibilityStatus")
    private EligibilityStatus eligibilityStatus;

    @JsonProperty("householdIdentifier")
    private String householdIdentifier;

    @JsonProperty("numberOfChildrenUnderOne")
    private final Integer numberOfChildrenUnderOne;

    @JsonProperty("numberOfChildrenUnderFour")
    private final Integer numberOfChildrenUnderFour;

    @JsonProperty("children")
    private final List<ChildDTO> children;

}
