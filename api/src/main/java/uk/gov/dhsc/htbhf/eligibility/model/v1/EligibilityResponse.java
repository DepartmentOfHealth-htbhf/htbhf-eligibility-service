package uk.gov.dhsc.htbhf.eligibility.model.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class EligibilityResponse {

    @JsonProperty("eligibilityStatus")
    private EligibilityStatus eligibilityStatus;

    @JsonProperty("dwpHouseholdIdentifier")
    private String dwpHouseholdIdentifier;

    @JsonProperty("hmrcHouseholdIdentifier")
    private String hmrcHouseholdIdentifier;

    @JsonProperty("numberOfChildrenUnderOne")
    @ApiModelProperty(notes = "The number of children under 1 that the person has", example = "1")
    private final Integer numberOfChildrenUnderOne;

    @JsonProperty("numberOfChildrenUnderFour")
    @ApiModelProperty(notes = "The number of children under 4 that the person has (which will include the number of children under 1)", example = "1")
    private final Integer numberOfChildrenUnderFour;

    @JsonProperty("children")
    private final List<ChildDTO> children;
}
