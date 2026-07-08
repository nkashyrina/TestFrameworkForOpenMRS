package ui.pages;

import api.constants.Constants;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class ServiceQueuesPage extends AuthBasePage<ServiceQueuesPage> {
    private static final String EXPECTED_PAGE_NAME = "Service queues";

    private final SelenideElement clinicName = $(Selectors.byXpath("//*[@data-testid='patient-queue-header']//p[text()='Clinic']"));
    private final SelenideElement pageName = $(Selectors.byXpath("//*[@data-testid='patient-queue-header']//p[text()='Service queues']"));

    @Override
    public String url() {
        return "/home/service-queues";
    }

    @Override
    public ServiceQueuesPage checkItIsCorrectPage() {
        pageName.shouldBe(Condition.visible).shouldHave(Condition.exactText(EXPECTED_PAGE_NAME));
        clinicName.shouldBe(Condition.visible).shouldHave(Condition.exactText(Constants.CLINIC_NAME));
        header.getSearchPatientIcon().shouldBe(Condition.visible);

        return this;
    }
}
