package uk.gov.dhsc.htbhf.eligibility.factory;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.dhsc.htbhf.eligibility.exception.NoEligibilityStatusProvidedException;
import uk.gov.dhsc.htbhf.eligibility.model.ChildDTO;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.service.EligibilityStatusCalculator;

import java.util.List;

import static com.google.common.base.MoreObjects.firstNonNull;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;

@Component
@AllArgsConstructor
public class EligibilityResponseFactory {

    private final EligibilityStatusCalculator statusCalculator;

    /**
     * Creates an EligibilityResponse by combining data from DWP ({@link DWPEligibilityResponse}) and HMRC ({@link HMRCEligibilityResponse}).
     * The rules to determine eligibility are as follows (first matching rule takes priority) :
     * If either is ELIGIBLE, then ELIGIBLE
     * If either is PENDING then PENDING
     * If there was an error connecting to either service, then ERROR
     * If either is INELIGIBLE then INELIGIBLE
     * Otherwise NOMATCH
     *
     * @param dwpResponse  dwp response
     * @param hmrcResponse hmrc response
     * @return an EligibilityResponse
     * @throws NoEligibilityStatusProvidedException if either DWP or HMRC responses are missing an EligibilityStatus
     */
    public EligibilityResponse createEligibilityResponse(DWPEligibilityResponse dwpResponse,
                                                         HMRCEligibilityResponse hmrcResponse) {

        EligibilityStatus eligibilityStatus = statusCalculator.determineStatus(dwpResponse, hmrcResponse);

        EligibilityResponse.EligibilityResponseBuilder builder = EligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus);

        if (dwpResponse.getEligibilityStatus() == ELIGIBLE) {
            builder.dwpHouseholdIdentifier(dwpResponse.getHouseholdIdentifier());
        }

        if (hmrcResponse.getEligibilityStatus() == ELIGIBLE) {
            builder.hmrcHouseholdIdentifier(hmrcResponse.getHouseholdIdentifier());
        }

        builder.numberOfChildrenUnderOne(nullSafeMax(dwpResponse.getNumberOfChildrenUnderOne(), hmrcResponse.getNumberOfChildrenUnderOne()));
        builder.numberOfChildrenUnderFour(nullSafeMax(dwpResponse.getNumberOfChildrenUnderFour(), hmrcResponse.getNumberOfChildrenUnderFour()));
        builder.children(createChildren(dwpResponse, hmrcResponse));

        return builder.build();
    }

    private List<ChildDTO> createChildren(DWPEligibilityResponse dwpResponse, HMRCEligibilityResponse hmrcResponse) {
        List<ChildDTO> dwpChildren = getListOfEmptyIfNull(dwpResponse.getChildren());
        List<ChildDTO> hmrcChildren = getListOfEmptyIfNull(hmrcResponse.getChildren());

        if (dwpChildren.size() >= hmrcChildren.size()) {
            return dwpResponse.getChildren();
        }

        return hmrcResponse.getChildren();
    }

    // Return the list if it's not null, otherwise return any empty list.
    private List<ChildDTO> getListOfEmptyIfNull(List<ChildDTO> children) {
        return firstNonNull(children, ImmutableList.of());
    }

    private Integer nullSafeMax(Integer count1, Integer count2) {
        if (count1 == null && count2 == null) {
            return null;
        }
        if (count1 == null) {
            return count2;
        }
        if (count2 == null) {
            return count1;
        }
        return Math.max(count1, count2);
    }


}
