package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.AddressDTO;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import java.time.LocalDate;

public class PersonDTOTestFactory {

    private static final LocalDate DOB = LocalDate.parse("1985-12-31");
    private static final String ADDRESS_LINE_1 = "Flat b";
    private static final String ADDRESS_LINE_2 = "123 Fake street";
    private static final String TOWN_OR_CITY = "Springfield";
    private static final String POSTCODE = "AA1 1AA";
    private static final String NINO = "EB123456C";
    private static final String FIRST_NAME = "Lisa";
    private static final String LAST_NAME = "Simpson";
    private static final LocalDate FUTURE_DATE = LocalDate.now().plusMonths(1);

    public static PersonDTO aPerson() {
        String nino = "IA000000C";
        return buildDefaultPerson().nino(nino).build();
    }

    public static PersonDTO aPersonWithNoNino() {
        return buildDefaultPerson().nino(null).build();
    }

    public static PersonDTO aPersonWithNoDateOfBirth() {
        return buildDefaultPerson().dateOfBirth(null).build();
    }

    public static PersonDTO aPersonWithDateOfBirthInFuture() {
        return buildDefaultPerson().dateOfBirth(FUTURE_DATE).build();
    }

    public static PersonDTO aPersonWithNoAddress() {
        return buildDefaultPerson().address(null).build();
    }

    public static PersonDTO aPersonWithPostcode(String postcode) {
        return buildDefaultPerson().address(
                buildDefaultAddress().postcode(postcode).build())
                .build();
    }

    public static PersonDTO.PersonDTOBuilder buildDefaultPerson() {
        return PersonDTO.builder()
                .dateOfBirth(DOB)
                .nino(NINO)
                .address(buildDefaultAddress().build())
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME);
    }

    private static AddressDTO.AddressDTOBuilder buildDefaultAddress() {
        return AddressDTO.builder()
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .townOrCity(TOWN_OR_CITY)
                .postcode(POSTCODE);
    }
}
