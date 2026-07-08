package ui.components;

import api.models.Address;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.pages.PatientSummaryPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Getter
public class AddressComponent extends BaseComponent {
    private final boolean isEmpty;
    private final String address1;
    private final String address2;
    private final String city;
    private final String state;
    private final String postalCode;
    private final String country;

    public AddressComponent(SelenideElement self) {
        super(self);

        List<String> items = self.findAll("li").texts();

        this.isEmpty = items.size() == 1 && items.getFirst().equals("--");

        this.address1 = extractValue(items, "Address line 1");
        this.address2 = extractValue(items, "Address line 2");
        this.city = extractValue(items, "City");
        this.state = extractValue(items, "State");
        this.postalCode = extractValue(items, "Postal code");
        this.country = extractValue(items, "Country");
    }

    private String extractValue(List<String> items, String fieldName) {
        return items.stream()
                .filter(item -> item.startsWith(fieldName + ":"))
                .findFirst()
                .map(item -> item.substring(item.indexOf(":") + 1).trim())
                .orElse(null);
    }

    @Override
    protected SelenideElement getSelf() {
        return self;
    }

    public AddressComponent checkAddress1IsEqualTo(String address) {
        assertThat(address1).isEqualTo(address);

        return this;
    }

    public AddressComponent checkAddress2IsEqualTo(String address) {
        assertThat(address2).isEqualTo(address);

        return this;
    }

    public AddressComponent checkCityIsEqualTo(String city) {
        assertThat(this.city).isEqualTo(city);

        return this;
    }

    public AddressComponent checkStateIsEqualTo(String state) {
        assertThat(this.state).isEqualTo(state);

        return this;
    }

    public AddressComponent checkPostalCodeIsEqualTo(String postalCode) {
        assertThat(this.postalCode).isEqualTo(postalCode);

        return this;
    }

    public AddressComponent checkCountryIsEqualTo(String country) {
        assertThat(this.country).isEqualTo(country);

        return this;
    }

    public PatientSummaryPage checkAllAddressFieldsAreCorrect(Address address) {
        checkAddress1IsEqualTo(address.getAddress1());
        checkAddress2IsEqualTo(address.getAddress2());
        checkCountryIsEqualTo(address.getCountry());
        checkStateIsEqualTo(address.getStateProvince());
        checkPostalCodeIsEqualTo(address.getPostalCode());
        checkCityIsEqualTo(address.getCityVillage());

        return getPage(PatientSummaryPage.class);
    }

    public PatientSummaryPage checkAddressSectionIsEmpty() {
        assertThat(isEmpty).isTrue();

        return getPage(PatientSummaryPage.class);
    }
}
