package uk.gov.dhsc.htbhf.eligibility.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EligibilityService {

    private final DWPClient dwpClient;
    private final BigDecimal ucMonthlyIncomeThreshold;
    private final Integer eligibilityCheckFrequencyInWeeks;


    public EligibilityService(@Value("${dwp.eligibility-check-frequency-in-weeks}") Integer eligibilityCheckFrequencyInWeeks,
                              @Value("${dwp.uc-monthly-income-threshold}") BigDecimal ucMonthlyIncomeThreshold,
                              DWPClient dwpClient) {
        this.dwpClient = dwpClient;
        this.ucMonthlyIncomeThreshold = ucMonthlyIncomeThreshold;
        this.eligibilityCheckFrequencyInWeeks = eligibilityCheckFrequencyInWeeks;
    }

    /**
     * Checks the eligibility of a given person.
     * Build the DWP Eligibility Request and send to to DWP as an Async call.
     *
     * @param person The person to check
     * @return The eligibility response
     */
    public EligibilityResponse checkEligibility(PersonDTO person) {
        LocalDate currentDate = LocalDate.now();

        EligibilityRequest request = EligibilityRequest.builder()
                .person(person)
                .eligibleEndDate(currentDate)
                .eligibleStartDate(currentDate.minusWeeks(eligibilityCheckFrequencyInWeeks))
                .ucMonthlyIncomeThreshold(ucMonthlyIncomeThreshold)
                .build();

        DWPEligibilityResponse dwpEligibilityResponse = dwpClient.checkEligibility(request);
        return buildEligibilityResponse(dwpEligibilityResponse);
    }

    private EligibilityResponse buildEligibilityResponse(DWPEligibilityResponse dwpEligibilityResponse) {
        return EligibilityResponse.builder()
                .eligibilityStatus(determineEligibilityStatus(dwpEligibilityResponse))
                .dwpHouseholdIdentifier(dwpEligibilityResponse.getHouseholdIdentifier())
                .build();
    }

    private EligibilityStatus determineEligibilityStatus(DWPEligibilityResponse dwpEligibilityResponse) {
        return dwpEligibilityResponse.getEligibilityStatus();
    }
}
