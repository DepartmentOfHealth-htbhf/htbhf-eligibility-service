package uk.gov.dhsc.htbhf.eligibility.model.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.dhsc.htbhf.assertions.AbstractValidationTest;

import java.util.Set;
import javax.validation.ConstraintViolation;

import static uk.gov.dhsc.htbhf.assertions.ConstraintViolationAssert.assertThat;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.v1.PersonDTOTestDataFactory.*;

class PersonDTOTest extends AbstractValidationTest {

    @Test
    void shouldSuccessfullyValidateAValidPerson() {
        //Given
        PersonDTO person = aPerson();
        //When
        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);
        //Then
        assertThat(violations).hasNoViolations();
    }

    @Test
    void shouldFailValidationWithMissingNino() {
        PersonDTO person = aPersonWithNoNino();

        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);

        assertThat(violations).hasSingleConstraintViolation("must not be null", "nino");
    }

    @ParameterizedTest
    @ValueSource(strings = {"YYHU456781", "888888888", "ABCDEFGHI", "ZQQ123456CZ", "QQ123456T"})
    void shouldFailValidationForInvalidNino(String nino) {
        PersonDTO person = buildDefaultPerson().nino(nino).build();

        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);

        assertThat(violations).hasSingleConstraintViolation("must match \"[a-zA-Z]{2}\\d{6}[a-dA-D]\"", "nino");
    }

    @Test
    void shouldFailValidationForMissingDateOfBirth() {
        PersonDTO person = aPersonWithNoDateOfBirth();

        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);

        assertThat(violations).hasSingleConstraintViolation("must not be null", "dateOfBirth");
    }

    @Test
    void shouldFailValidationForFutureDateOfBirth() {
        PersonDTO person = aPersonWithDateOfBirthInFuture();

        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);

        assertThat(violations).hasSingleConstraintViolation("must be a past date", "dateOfBirth");
    }

    @Test
    void shouldFailValidationForMissingAddress() {
        PersonDTO person = aPersonWithNoAddress();

        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);

        assertThat(violations).hasSingleConstraintViolation("must not be null", "address");
    }

    @Test
    void shouldFailValidationForInvalidAddress() {
        PersonDTO person = aPersonWithPostcode("11AA21");

        Set<ConstraintViolation<PersonDTO>> violations = validator.validate(person);

        assertThat(violations).hasSingleConstraintViolation("invalid postcode format", "address.postcode");
    }
}
