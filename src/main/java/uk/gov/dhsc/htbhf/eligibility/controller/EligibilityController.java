package uk.gov.dhsc.htbhf.eligibility.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;

@Controller
public class EligibilityController {

    @GetMapping("/eligibility")
    @ResponseBody
    public EligibilityResponse getDecision() {
        return EligibilityResponse.builder().build();
    }

}
