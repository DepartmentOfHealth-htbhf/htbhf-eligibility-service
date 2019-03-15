package uk.gov.dhsc.htbhf.eligibility.service;

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


    public EligibilityService(PersonDTOToDWPPersonConverter converter,
                              DWPClient dwpClient) {
        this.converter = converter;
        this.dwpClient = dwpClient;
    }

    public EligibilityResponse checkEligibility(PersonDTO person) {
        DWPPersonDTO dwpPerson = converter.convert(person);
        EligibilityRequest request = EligibilityRequest.builder()
                .person(dwpPerson)
                // TODO fetch below values from configuration
                .eligibleEndDate(LocalDate.now())
                .eligibleStartDate(LocalDate.now())
                .ucMonthlyIncomeThreshold(BigDecimal.ONE)
                .build();

        return dwpClient.checkEligibility(request);
    }
}
