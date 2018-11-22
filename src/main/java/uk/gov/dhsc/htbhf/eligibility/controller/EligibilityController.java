package uk.gov.dhsc.htbhf.eligibility.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.dhsc.htbhf.eligibility.model.Decision;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.Decision.ELIGIBLE;

@Controller
public class EligibilityController {

    @GetMapping("/eligibility")
    @ResponseBody
    public EligibilityResponse getDecision() {
        return EligibilityResponse.builder()
                .decision(ELIGIBLE)
                .build();
    }

}
