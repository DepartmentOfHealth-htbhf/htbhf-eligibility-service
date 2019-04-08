package uk.gov.dhsc.htbhf.eligibility.model;

import org.junit.jupiter.api.Test;
import uk.gov.dhsc.htbhf.assertions.AbstractValidationTest;

import java.util.Set;
import javax.validation.ConstraintViolation;

import static uk.gov.dhsc.htbhf.assertions.ConstraintViolationAssert.assertThat;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.AddressDTOTestDataFactory.*;

class AddressDTOTest extends AbstractValidationTest {

    @Test
    void shouldValidateAddressSuccessfully() {
        //Given
        var address = aValidAddress();
        //When
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        //Then
        assertThat(violations).hasNoViolations();
    }

    @Test
    void shouldFailToValidateAddressWithNoAddressLine1() {
        //Given
        var address = anAddressWithAddressLine1(null);
        //When
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        //Then
        assertThat(violations).hasSingleConstraintViolation("must not be null", "addressLine1");
    }

    @Test
    void shouldValidateAddressSuccessfullyWithNoAddressLine2() {
        //Given
        var address = anAddressWithAddressLine2(null);
        //When
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        //Then
        assertThat(violations).hasNoViolations();
    }

    @Test
    void shouldFailToValidateAddressWithNoTownOrCity() {
        //Given
        var address = anAddressWithTownOrCity(null);
        //When
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        //Then
        assertThat(violations).hasSingleConstraintViolation("must not be null", "townOrCity");
    }

    @Test
    void shouldFailToValidateAddressWithNoPostcode() {
        //Given
        var address = anAddressWithPostcode(null);
        //When
        Set<ConstraintViolation<AddressDTO>> violations = validator.validate(address);
        //Then
        assertThat(violations).hasSingleConstraintViolation("must not be null", "postcode");
    }
}
