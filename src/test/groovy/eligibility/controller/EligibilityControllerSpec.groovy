package eligibility.controller


import spock.lang.Specification
import uk.gov.dhsc.htbhf.eligibility.controller.EligibilityController
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse

import static uk.gov.dhsc.htbhf.eligibility.model.Decision.ELIGIBLE

class EligibilityControllerSpec extends Specification {
    EligibilityController eligibilityController = new EligibilityController()

    def "empty request should be eligible"() {
        when:
        EligibilityResponse result = eligibilityController.getDecision()

        then:
        result != null
        result.decision == ELIGIBLE
    }
}
