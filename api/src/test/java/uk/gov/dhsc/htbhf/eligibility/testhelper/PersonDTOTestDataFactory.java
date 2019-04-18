package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import static uk.gov.dhsc.htbhf.eligibility.testhelper.AddressDTOTestDataFactory.aValidAddress;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.AddressDTOTestDataFactory.anAddressWithPostcode;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.*;

public class PersonDTOTestDataFactory {

    public static PersonDTO aPerson() {
        return buildDefaultPerson().build();
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
                anAddressWithPostcode(postcode))
                .build();
    }

    public static PersonDTO.PersonDTOBuilder buildDefaultPerson() {
        return PersonDTO.builder()
                .dateOfBirth(HOMER_DATE_OF_BIRTH)
                .nino(HOMER_NINO)
                .address(aValidAddress())
                .firstName(HOMER_FIRST_NAME)
                .lastName(SIMPSON_LAST_NAME);
    }
}
