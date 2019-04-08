package uk.gov.dhsc.htbhf.eligibility.testhelper;

import uk.gov.dhsc.htbhf.eligibility.model.AddressDTO;

import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.ADDRESS_LINE_1;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.ADDRESS_LINE_2;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.POSTCODE;
import static uk.gov.dhsc.htbhf.eligibility.testhelper.TestConstants.TOWN_OR_CITY;

public class AddressDTOTestDataFactory {

    public static AddressDTO aValidAddress() {
        return aValidAddressBuilder().build();
    }

    public static AddressDTO anAddressWithPostcode(String postcode) {
        return aValidAddressBuilder().postcode(postcode).build();
    }

    public static AddressDTO anAddressWithAddressLine1(String addressLine1) {
        return aValidAddressBuilder().addressLine1(addressLine1).build();
    }

    public static AddressDTO anAddressWithAddressLine2(String addressLine2) {
        return aValidAddressBuilder().addressLine2(addressLine2).build();
    }

    public static AddressDTO anAddressWithTownOrCity(String townOrCity) {
        return aValidAddressBuilder().townOrCity(townOrCity).build();
    }

    private static AddressDTO.AddressDTOBuilder aValidAddressBuilder() {
        return AddressDTO.builder()
                .addressLine1(ADDRESS_LINE_1)
                .addressLine2(ADDRESS_LINE_2)
                .townOrCity(TOWN_OR_CITY)
                .postcode(POSTCODE);
    }
}
