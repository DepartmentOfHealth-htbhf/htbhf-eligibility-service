package uk.gov.dhsc.htbhf.eligibility.model.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor(onConstructor_ = {@JsonCreator})
public class PersonDTO {

    @NotNull
    @JsonProperty("firstName")
    private final String firstName;

    @NotNull
    @JsonProperty("lastName")
    private final String lastName;

    @NotNull
    @Pattern(regexp = "[a-zA-Z]{2}\\d{6}[a-dA-D]")
    @JsonProperty("nino")
    private final String nino;

    @NotNull
    @Past
    @JsonProperty("dateOfBirth")
    private final LocalDate dateOfBirth;

    @NotNull
    @Valid
    @JsonProperty("address")
    private final AddressDTO address;
}
