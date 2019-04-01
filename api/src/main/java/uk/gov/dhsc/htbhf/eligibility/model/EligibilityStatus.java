package uk.gov.dhsc.htbhf.eligibility.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;

/**
 * The possible states that a claim can be in according to the DWP.
 */
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum EligibilityStatus {

    ELIGIBLE,
    INELIGIBLE,
    PENDING,
    NOMATCH,
    ERROR
}
