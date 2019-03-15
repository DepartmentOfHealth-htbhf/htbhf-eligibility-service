package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.AddressDTO;
import uk.gov.dhsc.htbhf.eligibility.model.DWPPersonDTO;

import java.time.LocalDate;

public class DWPPersonTestFactory {

    private static final LocalDate DOB = LocalDate.parse("1985-12-31");
    private static final String ADDRESS_LINE_1 = "Flat b";
    private static final String ADDRESS_LINE_2 = "123 Fake street";
    private static final String TOWN_OR_CITY = "Springfield";
    private static final String POSTCODE = "AA1 1AA";
    private static final String NINO = "EB123456C";
    private static final String FORENAME = "Lisa";
    private static final String SURNAME = "Simpson";

    public static DWPPersonDTO aDWPPerson() {
        String nino = "IA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    private static DWPPersonDTO.DWPPersonDTOBuilder buildDefaultPerson() {
        return DWPPersonDTO.builder()
                .dateOfBirth(DOB)
                .nino(NINO)
                .address(aValidAddress())
                .forename(FORENAME)
                .surname(SURNAME);
    }

    private static AddressDTO aValidAddress() {
        return AddressDTO.builder()
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .townOrCity(TOWN_OR_CITY)
                .postcode(POSTCODE)
                .build();
    }
}
