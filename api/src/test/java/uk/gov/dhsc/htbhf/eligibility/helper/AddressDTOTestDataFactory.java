package uk.gov.dhsc.htbhf.eligibility.helper;

import uk.gov.dhsc.htbhf.eligibility.model.AddressDTO;

public class AddressDTOTestDataFactory {

    private static final String ADDRESS_LINE_1 = "742 Evergreen Terrace";
    private static final String ADDRESS_LINE_2 = "123 Fake street";
    private static final String TOWN_OR_CITY = "Springfield";
    private static final String POSTCODE = "AA1 1AA";

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
