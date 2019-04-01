package uk.gov.dhsc.htbhf.eligibility.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.service.EligibilityService;

import java.util.concurrent.ExecutionException;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Responsible for obtaining a decision on eligibility from downstream services, sending the application to the claimant service
 * and returning the eligibility result.
 */
@Controller
@Slf4j
@AllArgsConstructor
public class EligibilityController {

    private EligibilityService eligibilityService;

    /**
     * Invokes downstream services to obtain a decision on eligibility for the given person,
     * persists the request and decision with the claimant service.
     *
     * @param person the {@link PersonDTO}
     * @return the {@link EligibilityResponse}
     */
    @PostMapping(path = "/v1/eligibility", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public EligibilityResponse getDecision(@RequestBody @Valid PersonDTO person) throws ExecutionException, InterruptedException {
        log.debug("Received eligibility request");
        return eligibilityService.checkEligibility(person);
    }

}
