package uk.gov.dhsc.htbhf.eligibility.converter;

import org.junit.jupiter.api.Test;
import uk.gov.dhsc.htbhf.eligibility.model.DWPPersonDTO;
import uk.gov.dhsc.htbhf.eligibility.model.PersonDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.gov.dhsc.htbhf.eligibility.helper.PersonTestFactory.aPerson;

class PersonDTOToDWPPersonConverterTest {

    private PersonDTOToDWPPersonConverter converter = new PersonDTOToDWPPersonConverter();

    @Test
    void shouldConvertPerson() {
        PersonDTO person = aPerson();

        DWPPersonDTO result = converter.convert(person);

        assertThat(result).isNotNull();
        assertThat(result.getAddress()).isEqualTo(person.getAddress());
        assertThat(result.getDateOfBirth()).isEqualTo(person.getDateOfBirth());
        assertThat(result.getNino()).isEqualTo(person.getNino());
        assertThat(result.getForename()).isEqualTo(person.getFirstName());
        assertThat(result.getSurname()).isEqualTo(person.getLastName());
    }
}
