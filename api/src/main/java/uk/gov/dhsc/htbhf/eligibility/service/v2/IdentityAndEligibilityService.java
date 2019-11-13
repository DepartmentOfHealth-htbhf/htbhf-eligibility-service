package uk.gov.dhsc.htbhf.eligibility.service.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.dwp.model.v2.DWPEligibilityRequestV2;
import uk.gov.dhsc.htbhf.dwp.model.v2.IdentityAndEligibilityResponse;
import uk.gov.dhsc.htbhf.dwp.model.v2.PersonDTOV2;

import java.time.LocalDate;

@Service
@Slf4j
public class IdentityAndEligibilityService {

    private final DWPClientV2 dwpClient;
    private final int ucMonthlyIncomeThresholdInPence;

    public IdentityAndEligibilityService(@Value("${dwp.uc-monthly-income-threshold-in-pence}") int ucMonthlyIncomeThresholdInPence,
                                         DWPClientV2 dwpClient) {
        this.dwpClient = dwpClient;
        this.ucMonthlyIncomeThresholdInPence = ucMonthlyIncomeThresholdInPence;
    }

    /**
     * Checks the identity and eligibility of a given person by calling DWP.
     *
     * @param person The person to check
     * @return The identity and eligibility response
     */
    public IdentityAndEligibilityResponse checkIdentityAndEligibility(PersonDTOV2 person) {
        log.debug("Checking identity and eligibility");
        DWPEligibilityRequestV2 dwpEligibilityRequest = createDWPRequest(person);

        return dwpClient.checkIdentityAndEligibility(dwpEligibilityRequest);
    }

    private DWPEligibilityRequestV2 createDWPRequest(PersonDTOV2 person) {
        return DWPEligibilityRequestV2.builder()
                .person(person)
                .eligibilityEndDate(LocalDate.now())
                .ucMonthlyIncomeThresholdInPence(ucMonthlyIncomeThresholdInPence)
                .build();
    }

}
