package uk.gov.dhsc.htbhf.eligibility.controller.v1;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.dhsc.htbhf.eligibility.model.v1.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.service.v1.EligibilityService;

import java.util.concurrent.ExecutionException;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.NO_MATCH;

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
     * @throws ExecutionException   If problems with parallel calls to DWP and HMRC to check eligibility
     * @throws InterruptedException If problems with parallel calls to DWP and HMRC to check eligibility
     */
    @PostMapping(path = "/v1/eligibility", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EligibilityResponse> getDecision(@RequestBody @Valid PersonDTO person) throws ExecutionException, InterruptedException {
        log.debug("Received eligibility request");

        EligibilityResponse eligibilityResponse = eligibilityService.checkEligibility(person);

        HttpStatus httpStatus = determineHttpStatusFromEligibilityStatus(eligibilityResponse);
        return new ResponseEntity<>(eligibilityResponse, httpStatus);
    }

    private HttpStatus determineHttpStatusFromEligibilityStatus(EligibilityResponse eligibilityResponse) {
        return eligibilityResponse.getEligibilityStatus() == NO_MATCH ? HttpStatus.NOT_FOUND : HttpStatus.OK;
    }

}
