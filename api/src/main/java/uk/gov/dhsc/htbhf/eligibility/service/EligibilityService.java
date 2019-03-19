package uk.gov.dhsc.htbhf.eligibility.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.dhsc.htbhf.eligibility.converter.PersonDTOToDWPPersonConverter;
import uk.gov.dhsc.htbhf.eligibility.model.DWPPersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityRequest;
import uk.gov.dhsc.htbhf.eligibility.model.EligibilityResponse;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EligibilityService {

    private final PersonDTOToDWPPersonConverter converter;
    private final DWPClient dwpClient;
    private final BigDecimal ucMonthlyIncomeThreshold;
    private final Integer eligibilityCheckFrequencyInWeeks;


    public EligibilityService(@Value("${dwp.eligibility-check-frequency-in-weeks}") Integer eligibilityCheckFrequencyInWeeks,
                              @Value("${dwp.uc-monthly-income-threshold}") BigDecimal ucMonthlyIncomeThreshold,
                              PersonDTOToDWPPersonConverter converter,
                              DWPClient dwpClient) {
        this.converter = converter;
        this.dwpClient = dwpClient;
        this.ucMonthlyIncomeThreshold = ucMonthlyIncomeThreshold;
        this.eligibilityCheckFrequencyInWeeks = eligibilityCheckFrequencyInWeeks;
    }

    /**
     * Checks the eligibility of a given person.
     */
    public EligibilityResponse checkEligibility(PersonDTO person) {
        DWPPersonDTO dwpPerson = converter.convert(person);
        LocalDate currentDate = LocalDate.now();

        EligibilityRequest request = EligibilityRequest.builder()
                .person(dwpPerson)
                .eligibleEndDate(currentDate)
                .eligibleStartDate(currentDate.minusWeeks(eligibilityCheckFrequencyInWeeks))
                .ucMonthlyIncomeThreshold(ucMonthlyIncomeThreshold)
                .build();

        return dwpClient.checkEligibility(request);
    }
}
