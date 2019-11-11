package uk.gov.dhsc.htbhf.eligibility.testhelper.v1;

import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.v1.ChildDTO;
import uk.gov.dhsc.htbhf.eligibility.model.v1.dwp.DWPEligibilityResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.nCopies;
import static uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants.LISA_DOB;
import static uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants.MAGGIE_DATE_OF_BIRTH;
import static uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus.ELIGIBLE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.SIMPSON_DWP_HOUSEHOLD_IDENTIFIER;

public class DWPEligibilityResponseTestDataFactory {

    public static DWPEligibilityResponse aDWPEligibilityResponse() {
        return aDwpEligibilityResponseBuilder(ELIGIBLE)
                .build();
    }

    public static DWPEligibilityResponse aDWPEligibilityResponseWithStatus(EligibilityStatus eligibilityStatus) {
        return aDwpEligibilityResponseBuilder(eligibilityStatus)
                .build();
    }

    public static DWPEligibilityResponse.DWPEligibilityResponseBuilder aDwpEligibilityResponseBuilder(EligibilityStatus eligibilityStatus) {
        return DWPEligibilityResponse.builder()
                .eligibilityStatus(eligibilityStatus)
                .householdIdentifier(SIMPSON_DWP_HOUSEHOLD_IDENTIFIER);
    }

    public static List<ChildDTO> createChildren(Integer numberOfChildrenUnderOne, Integer numberOfChildrenUnderFour) {
        List<ChildDTO> childrenUnderOne = nCopies(numberOfChildrenUnderOne, new ChildDTO(MAGGIE_DATE_OF_BIRTH));
        List<ChildDTO> childrenBetweenOneAndFour = nCopies(numberOfChildrenUnderFour - numberOfChildrenUnderOne, new ChildDTO(LISA_DOB));
        return Stream.concat(childrenUnderOne.stream(), childrenBetweenOneAndFour.stream()).collect(Collectors.toList());
    }
}
