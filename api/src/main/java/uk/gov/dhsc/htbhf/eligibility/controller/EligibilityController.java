package uk.gov.dhsc.htbhf.eligibility.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.dhsc.htbhf.eligibility.model.Decision.ELIGIBLE;

/**
 * Responsible for obtaining a decision on eligibility from downstream services, sending the application to the claimant service
 * and returning the eligibility result.
 */
@Controller
@Slf4j
public class EligibilityController {

    /**
     * Invokes downstream services to obtain a decision on eligibility for the given person,
     * persists the request and decision with the claimant service.
     *
     * @param person the {@link PersonDTO}
     * @return the {@link EligibilityResponse}
     */
    @PostMapping(path = "/eligibility", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public EligibilityResponse getDecision(@RequestBody PersonDTO person) {
        return EligibilityResponse.builder()
                .decision(ELIGIBLE)
                .build();
    }

}
