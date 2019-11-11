package uk.gov.dhsc.htbhf.eligibility.model.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static uk.gov.dhsc.htbhf.eligibility.regex.v1.PostcodeRegex.UK_POST_CODE_REGEX;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class AddressDTO {
    @NotNull
    @JsonProperty("addressLine1")
    private String addressLine1;

    @JsonProperty("addressLine2")
    private String addressLine2;

    @NotNull
    @JsonProperty("townOrCity")
    private String townOrCity;

    @NotNull
    @Pattern(regexp = UK_POST_CODE_REGEX, message = "invalid postcode format")
    @JsonProperty("postcode")
    private String postcode;
}
