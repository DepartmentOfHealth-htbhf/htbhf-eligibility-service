package uk.gov.dhsc.htbhf.eligibility.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.dwp.model.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.dwp.model.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.CombinedIdentityAndEligibilityResponse;

import java.time.LocalDate;

@Service
@Slf4j
public class IdentityAndEligibilityService {

    private final DWPClient dwpClient;
    private final int ucMonthlyIncomeThresholdInPence;

    public IdentityAndEligibilityService(@Value("${dwp.uc-monthly-income-threshold-in-pence}") int ucMonthlyIncomeThresholdInPence,
                                         DWPClient dwpClient) {
        this.dwpClient = dwpClient;
        this.ucMonthlyIncomeThresholdInPence = ucMonthlyIncomeThresholdInPence;
    }

    /**
     * Checks the identity and eligibility of a given person by calling DWP.
     *
     * @param person The person to check
     * @return The combined identity and eligibility response
     */
    public CombinedIdentityAndEligibilityResponse checkIdentityAndEligibility(PersonDTO person) {
        log.debug("Checking identity and eligibility");
        DWPEligibilityRequest dwpEligibilityRequest = createDWPRequest(person);

        IdentityAndEligibilityResponse dwpIdentityAndEligibilityResponse = dwpClient.checkIdentityAndEligibility(dwpEligibilityRequest);
        return buildCombinedResponse(dwpIdentityAndEligibilityResponse);
    }

    private DWPEligibilityRequest createDWPRequest(PersonDTO person) {
        return DWPEligibilityRequest.builder()
                .person(person)
                .eligibilityEndDate(LocalDate.now())
                .ucMonthlyIncomeThresholdInPence(ucMonthlyIncomeThresholdInPence)
                .build();
    }

    private CombinedIdentityAndEligibilityResponse buildCombinedResponse(IdentityAndEligibilityResponse dwpResponse) {
        return CombinedIdentityAndEligibilityResponse.builder()
                .identityStatus(dwpResponse.getIdentityStatus())
                .eligibilityStatus(dwpResponse.getEligibilityStatus())
                .qualifyingBenefits(dwpResponse.getQualifyingBenefits())
                .mobilePhoneMatch(dwpResponse.getMobilePhoneMatch())
                .emailAddressMatch(dwpResponse.getEmailAddressMatch())
                .addressLine1Match(dwpResponse.getAddressLine1Match())
                .postcodeMatch(dwpResponse.getPostcodeMatch())
                .pregnantChildDOBMatch(dwpResponse.getPregnantChildDOBMatch())
                .dwpHouseholdIdentifier(dwpResponse.getHouseholdIdentifier())
                .deathVerificationFlag(dwpResponse.getDeathVerificationFlag())
                .dobOfChildrenUnder4(dwpResponse.getDobOfChildrenUnder4())
                //TODO 02/12/2019: Specifically set to null for now, HTBHF-2410 will set this value when integrated with HMRC
                .hmrcHouseholdIdentifier(null)
                .build();
    }

}
