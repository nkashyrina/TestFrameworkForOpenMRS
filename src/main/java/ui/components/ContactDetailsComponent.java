package ui.components;

import com.codeborne.selenide.SelenideElement;
import ui.pages.PatientSummaryPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContactDetailsComponent extends BaseComponent {
    private final boolean isEmpty;
    private final String telephoneNumber;

    public ContactDetailsComponent(SelenideElement self) {
        super(self);

        List<String> items = self.findAll("li").texts();

        this.isEmpty = items.size() == 1 && items.getFirst().equals("--");
        this.telephoneNumber = extractValue(items, "Telephone Number");
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

    public PatientSummaryPage checkTelephoneNumberIsEqualTo(String telephoneNumber) {
        assertThat(this.telephoneNumber).isEqualTo(telephoneNumber);

        return new PatientSummaryPage();
    }

    public PatientSummaryPage checkContactDetailsSectionIsEmpty() {
        assertThat(isEmpty).isTrue();

        return getPage(PatientSummaryPage.class);
    }
}
