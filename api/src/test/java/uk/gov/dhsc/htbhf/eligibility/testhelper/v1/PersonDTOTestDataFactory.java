package uk.gov.dhsc.htbhf.eligibility.testhelper.v1;

import uk.gov.dhsc.htbhf.eligibility.model.v1.PersonDTO;

import static uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants.HOMER_DATE_OF_BIRTH;
import static uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants.HOMER_FORENAME;
import static uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants.HOMER_NINO_V1;
import static uk.gov.dhsc.htbhf.dwp.testhelper.TestConstants.SIMPSON_SURNAME;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.FUTURE_DATE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.AddressDTOTestDataFactory.aValidAddress;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.AddressDTOTestDataFactory.anAddressWithPostcode;

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
        return buildDefaultPerson().address(anAddressWithPostcode(postcode)).build();
    }

    public static PersonDTO.PersonDTOBuilder buildDefaultPerson() {
        return PersonDTO.builder()
                .dateOfBirth(HOMER_DATE_OF_BIRTH)
                .nino(HOMER_NINO_V1)
                .address(aValidAddress())
                .firstName(HOMER_FORENAME)
                .lastName(SIMPSON_SURNAME);
    }
}
