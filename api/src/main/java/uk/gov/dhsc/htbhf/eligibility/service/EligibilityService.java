package uk.gov.dhsc.htbhf.eligibility.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityStatus;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.dwp.DWPEligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.hmrc.HMRCEligibilityResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EligibilityService {

    private final DWPClient dwpClient;
    private final HMRCClient hmrcClient;
    private final BigDecimal ucMonthlyIncomeThreshold;
    private final Integer eligibilityCheckFrequencyInWeeks;
    private final BigDecimal ctcAnnualIncomeThreshold;


    public EligibilityService(@Value("${eligibility-check-frequency-in-weeks}") Integer eligibilityCheckFrequencyInWeeks,
                              @Value("${dwp.uc-monthly-income-threshold}") BigDecimal ucMonthlyIncomeThreshold,
                              @Value("${hmrc.ctc-annual-income-threshold}") BigDecimal ctcAnnualIncomeThreshold,
                              DWPClient dwpClient,
                              HMRCClient hmrcClient) {
        this.dwpClient = dwpClient;
        this.ucMonthlyIncomeThreshold = ucMonthlyIncomeThreshold;
        this.eligibilityCheckFrequencyInWeeks = eligibilityCheckFrequencyInWeeks;
        this.hmrcClient = hmrcClient;
        this.ctcAnnualIncomeThreshold = ctcAnnualIncomeThreshold;
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

        DWPEligibilityRequest dwpEligibilityRequest = createDWPRequest(person, currentDate);
        HMRCEligibilityRequest hmrcEligibilityRequest = createHMRCRequest(person, currentDate);

        DWPEligibilityResponse dwpEligibilityResponse = dwpClient.checkEligibility(dwpEligibilityRequest);
        HMRCEligibilityResponse hmrcEligibilityResponse = hmrcClient.checkEligibility(hmrcEligibilityRequest);
        return buildEligibilityResponse(dwpEligibilityResponse, hmrcEligibilityResponse);
    }

    private DWPEligibilityRequest createDWPRequest(PersonDTO person, LocalDate currentDate) {
        return DWPEligibilityRequest.builder()
                .person(person)
                .eligibleEndDate(currentDate)
                .eligibleStartDate(currentDate.minusWeeks(eligibilityCheckFrequencyInWeeks))
                .ucMonthlyIncomeThreshold(ucMonthlyIncomeThreshold)
                .build();
    }

    private HMRCEligibilityRequest createHMRCRequest(PersonDTO person, LocalDate currentDate) {
        return HMRCEligibilityRequest.builder()
                .person(person)
                .eligibleEndDate(currentDate)
                .eligibleStartDate(currentDate.minusWeeks(eligibilityCheckFrequencyInWeeks))
                .ctcAnnualIncomeThreshold(ctcAnnualIncomeThreshold)
                .build();
    }

    private EligibilityResponse buildEligibilityResponse(DWPEligibilityResponse dwpEligibilityResponse,
                                                         HMRCEligibilityResponse hmrcEligibilityResponse) {
        return EligibilityResponse.builder()
                .eligibilityStatus(determineEligibilityStatus(dwpEligibilityResponse))
                .dwpHouseholdIdentifier(dwpEligibilityResponse.getHouseholdIdentifier())
                .hmrcHouseholdIdentifier(hmrcEligibilityResponse.getHouseholdIdentifier())
                .build();
    }

    private EligibilityStatus determineEligibilityStatus(DWPEligibilityResponse dwpEligibilityResponse) {
        return dwpEligibilityResponse.getEligibilityStatus();
    }
}
