package uk.gov.dhsc.htbhf.eligibility.service.v1;

import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.eligibility.exception.v1.NoEligibilityStatusProvidedException;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.v1.hmrc.HMRCEligibilityResponse;

import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.*;

@Service
public class EligibilityStatusCalculator {

    /**
     * Determines the eligibility base on a DWP {@link DWPEligibilityResponse} and HMRC response {@link HMRCEligibilityResponse}.
     * The rules to determine eligibility are as follows (first matching rule takes priority) :
     * If either is ELIGIBLE, then ELIGIBLE
     * If either is PENDING then PENDING
     * If there was an error connecting to either service, then ERROR
     * If either is INELIGIBLE then INELIGIBLE
     * Otherwise NO_MATCH
     *
     * @param dwpEligibilityResponse  dwp response
     * @param hmrcEligibilityResponse hmrc response
     * @return the calculated eligibility status
     * @throws NoEligibilityStatusProvidedException if neither HMRC nor DWP provide an EligibilityStatus
     */
    public EligibilityStatus determineStatus(DWPEligibilityResponse dwpEligibilityResponse,
                                             HMRCEligibilityResponse hmrcEligibilityResponse) {

        assertResponsesContainEligibilityStatus(dwpEligibilityResponse, hmrcEligibilityResponse);

        EligibilityStatus dwpStatus = dwpEligibilityResponse.getEligibilityStatus();
        EligibilityStatus hmrcStatus = hmrcEligibilityResponse.getEligibilityStatus();

        if (dwpStatus == ELIGIBLE || hmrcStatus == ELIGIBLE) {
            return ELIGIBLE;
        } else if (dwpStatus == PENDING || hmrcStatus == PENDING) {
            return PENDING;
        } else if (dwpStatus == ERROR || hmrcStatus == ERROR) {
            return ERROR;
        } else if (dwpStatus == INELIGIBLE || hmrcStatus == INELIGIBLE) {
            return INELIGIBLE;
        }

        return NO_MATCH;
    }


    private void assertResponsesContainEligibilityStatus(DWPEligibilityResponse dwpEligibilityResponse,
                                                         HMRCEligibilityResponse hmrcEligibilityResponse) {
        if (dwpEligibilityResponse.getEligibilityStatus() == null) {
            throw new NoEligibilityStatusProvidedException("No eligibilityStatus returned by DWP: " + dwpEligibilityResponse);
        }
        if (hmrcEligibilityResponse.getEligibilityStatus() == null) {
            throw new NoEligibilityStatusProvidedException("No eligibilityStatus returned by HMRC: " + hmrcEligibilityResponse);
        }
    }

}
