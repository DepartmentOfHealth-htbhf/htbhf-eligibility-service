package uk.gov.dhsc.htbhf.eligibility.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.dhsc.htbhf.dwp.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.service.IdentityAndEligibilityService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Responsible for obtaining a decision on identity matching and eligibility from downstream services, sending the response
 * back to the claimant service and returning the result.
 */
@RequestMapping("/v2/eligibility")
@Controller
@Slf4j
@AllArgsConstructor
public class EligibilityController {

    private IdentityAndEligibilityService identityAndEligibilityService;

    /**
     * Invokes downstream services to obtain a decision on the identity and eligibility for the given person.
     *
     * @param person the {@link PersonDTO}
     * @return the {@link CombinedIdentityAndEligibilityResponse}
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public CombinedIdentityAndEligibilityResponse getIdentityAndEligibilityDecision(@RequestBody @Valid PersonDTO person) {
        log.debug("Received eligibility request V2");

        CombinedIdentityAndEligibilityResponse response = identityAndEligibilityService.checkIdentityAndEligibility(person);

        logResponse(response);
        return response;
    }

    private void logResponse(CombinedIdentityAndEligibilityResponse response) {
        log.debug("Returning identity status: {}, eligibility status: {}, qualifying benefits: {}, addressLine1: {}, postcode: {}, mobile: {}, email: {}",
                response.getIdentityStatus(), response.getEligibilityStatus(), response.getQualifyingBenefits(),
                response.getAddressLine1Match(), response.getPostcodeMatch(), response.getMobilePhoneMatch(), response.getEmailAddressMatch());
    }

}
