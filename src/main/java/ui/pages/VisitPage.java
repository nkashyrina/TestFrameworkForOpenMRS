package ui.pages;

import api.models.ui.VisitMessage;
import api.models.ui.VisitTab;
import api.models.enums.VisitTypeEnum;
import com.codeborne.selenide.*;
import org.openqa.selenium.By;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static org.assertj.core.api.Assertions.assertThat;

public class VisitPage extends BasePage<VisitPage> {
    @Override
    public String url() {
        return "/patient/%s/chart/Patient%%20Summary";
    }

    @Override
    public VisitPage checkItIsCorrectPage() {
        startVisitHeader.shouldBe(Condition.visible);

        return this;
    }

    private final ElementsCollection actionsMenuItems = $$("div[role='menu'] button[role='menuitem']");
    private final SelenideElement actionButton = $("button.cds--overflow-menu__trigger");
    private final SelenideElement discardButton = $("button.cds--btn--secondary");
    private final SelenideElement discardChangesButton = $x("//button[contains(text(), 'Discard') or contains(text(), 'discard changes')]");
    private final SelenideElement cancelButtonInStartVisitForm = $x("//button[text()='Cancel' and contains(@class, 'cds--btn--secondary')]");
    private final SelenideElement showMoreButton = $(By.xpath("//button[text()='Show more']"));
    private final SelenideElement startVisitHeader = $(By.xpath("//header[@aria-label='Workspace header']//span[contains(text(),'Start a visit')]"));
    private final SelenideElement newTab = $(By.xpath("//button[.//span[text()='New']]"));
    private final SelenideElement ongoingTab = $(By.xpath("//button[.//span[text()='Ongoing']]"));
    private final SelenideElement inThePastTab = $(By.xpath("//button[.//span[text()='In the past']]"));
    private final SelenideElement startVisitSubmitButton = $(By.xpath("//button[@type='submit']//span[text()='Start visit']"));
    private final SelenideElement activeVisitTag = $(By.xpath("//div[contains(@class,'cds--tag') and .//span[contains(text(),'Active Visit')]]"));
    private final SelenideElement locationInput = $(By.xpath("//input[@id='location']"));
    private final SelenideElement ubuntuHospitalLocation = $(By.xpath("(//div[text()='Ubuntu Hospital'])[1]"));
    private final SelenideElement searchVisitTypeInput = $(By.xpath("//input[@placeholder='Search for a visit type']"));
    private final SelenideElement facilityVisitOption = $(By.xpath("//label[.//span[text()='Facility Visit']]"));
    private final SelenideElement homeVisitOption = $(By.xpath("//label[.//span[text()='Home Visit']]"));
    private final SelenideElement opdVisitOption = $(By.xpath("//label[.//span[text()='OPD Visit']]"));
    private final SelenideElement offlineVisitOption = $(By.xpath("//label[.//span[text()='Offline Visit']]"));
    private final SelenideElement groupSessionOption = $(By.xpath("//span[text()='Group Session']/parent::label"));
    private final SelenideElement upcomingAppointmentsMessage = $(By.xpath("//div[contains(@class, 'cds--inline-notification') and contains(., 'Upcoming appointments')]"));
    private final SelenideElement errorMessage = $x("//div[contains(@class, 'cds--form-requirement') or contains(@class, 'error')]");
    private final SelenideElement deleteVisitConfirmButton = $x("//button[contains(@class, 'cds--btn--danger') and contains(text(), 'Delete visit')]");
    private final SelenideElement deleteActiveVisitOption = $x("//div[@data-extension-id='delete-visit-button']//button");

    public VisitPage waitPatientSummaryLoaded() {
        $("body")
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage openActionsMenu() {
        actionButton
                .shouldBe(Condition.visible)
                .click();

        $("div[role='menu']")
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage clickShowMore() {
        showMoreButton
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage selectAddVisit() {
        actionsMenuItems
                .shouldBe(CollectionCondition.sizeGreaterThan(0));

        for (SelenideElement item : actionsMenuItems) {

            if (item.getText().equalsIgnoreCase("Add visit")) {

                item.shouldBe(Condition.visible).click();

                return this;
            }
        }

        throw new AssertionError("Add visit not found in Actions menu");
    }

    public VisitPage waitStartVisitModal() {
        startVisitHeader
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage selectVisitTab(VisitTab tab) {
        switch (tab) {
            case NEW:
                newTab.shouldBe(Condition.visible).click();
                break;

            case ONGOING:
                ongoingTab.shouldBe(Condition.visible).click();
                break;

            case IN_THE_PAST:
                inThePastTab.shouldBe(Condition.visible).click();
                break;

            default:
                throw new IllegalArgumentException("Unknown tab: " + tab.getTabName());
        }

        return this;
    }

    public VisitPage selectUbuntuHospitalLocation() {
        locationInput
                .shouldBe(Condition.visible)
                .click();

        ubuntuHospitalLocation
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage selectVisitType(VisitTypeEnum visitType) {
        String visitTypeDisplayName = visitType.getDisplayName();

        searchVisitTypeInput
                .shouldBe(Condition.visible)
                .sendKeys(visitTypeDisplayName);

        switch (visitType) {
            case FACILITY_VISIT:
                facilityVisitOption.shouldBe(Condition.visible).click();
                break;

            case HOME_VISIT:
                homeVisitOption.shouldBe(Condition.visible).click();
                break;

            case OPD_VISIT:
                opdVisitOption.shouldBe(Condition.visible).click();
                break;

            case OFFLINE_VISIT:
                offlineVisitOption.shouldBe(Condition.visible).click();
                break;

            case GROUP_SESSION:
                groupSessionOption.shouldBe(Condition.visible).click();
                break;

            default:
                throw new IllegalArgumentException("Unknown visit type: " + visitType);
        }

        return this;
    }

    public VisitPage confirmStartVisit() {
        startVisitSubmitButton
                .shouldBe(Condition.visible)
                .shouldBe(Condition.enabled)
                .click();

        return this;
    }

    public VisitPage confirmStartVisitAndWaitForClose() {
        confirmStartVisit();
        startVisitHeader
                .shouldBe(Condition.disappear);

        return this;
    }

    public VisitPage checkActiveVisitIsStarted() {
        activeVisitTag.shouldBe(Condition.visible);

        assertThat(activeVisitTag.getText())
                .as("Active Visit tag text should be correct")
                .contains("Active Visit");

        return this;
    }

    public VisitPage selectEndActiveVisit() {
        actionsMenuItems
                .shouldBe(CollectionCondition.sizeGreaterThan(0))
                .findBy(Condition.text("End active visit"))
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage waitEndVisitConfirmationModal() {
        $x("//div[contains(text(), 'Are you sure you want to end this active visit')]")
                .shouldBe(Condition.visible, Duration.ofSeconds(10));

        return this;
    }

    public VisitPage confirmEndVisit() {
        $("button")
                .shouldHave(Condition.text("End Visit"))
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage checkVisitEndedSuccessfully() {
        $("div[role='alert']")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .shouldHave(Condition.text("Visit ended"))
                .shouldHave(Condition.text("Ended current visit successfully"));

        return this;
    }

    public VisitPage checkNoActiveVisitTag() {
        activeVisitTag.shouldNotBe(Condition.visible);
        return this;
    }

    public VisitPage checkNoUpcomingAppointmentsMessage() {
        upcomingAppointmentsMessage
                .shouldBe(Condition.visible, Duration.ofSeconds(10));

        assertThat(VisitMessage.NO_UPCOMING_APPOINTMENTS.isPresentIn(upcomingAppointmentsMessage.getText()))
                .as("No upcoming appointments message should be displayed")
                .isTrue();

        return this;
    }

    public VisitPage checkStartVisitErrorIsDisplayed() {
        errorMessage
                .shouldBe(Condition.visible, Duration.ofSeconds(10));

        assertThat(VisitMessage.START_VISIT_ERROR_REQUIRED.isPresentIn(errorMessage.getText()))
                .as("Start visit error message should contain required/visit type/mandatory/select")
                .isTrue();

        return this;
    }

    public VisitPage refresh() {
        String currentUrl = WebDriverRunner.url();
        open(currentUrl);
        waitPatientSummaryLoaded();
        return this;
    }

    public VisitPage waitAfterVisitStarted() {
        waitPatientSummaryLoaded();

        activeVisitTag
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Active Visit"));

        actionButton.shouldBe(Condition.visible);

        return this;
    }

    public VisitPage clickDiscard() {
        discardButton
                .shouldBe(Condition.visible)
                .click();

        Selenide.sleep(1000);

        return this;
    }

    public VisitPage checkDiscardModalOrFormClosed() {
        startVisitHeader
                .shouldBe(Condition.disappear);

        waitPatientSummaryLoaded();

        return this;
    }

    public VisitPage confirmDiscard() {
        discardChangesButton
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage clickCancel() {
        cancelButtonInStartVisitForm
                .shouldBe(Condition.visible)
                .click();

        Selenide.sleep(1000);

        return this;
    }

    public VisitPage checkStartVisitModalStillOpen() {
        startVisitHeader
                .shouldBe(Condition.visible);

        return this;
    }

    public VisitPage confirmDeleteVisit() {
        deleteVisitConfirmButton
                .shouldBe(Condition.visible)
                .click();

        return this;
    }

    public VisitPage deleteActiveVisit() {
        Selenide.sleep(1500);

        Selenide.executeJavaScript("""
            const btn = document.querySelector("button[aria-controls*='patient-actions-menu']");
            if (btn) btn.click();
        """);

        deleteActiveVisitOption
                .shouldBe(Condition.visible)
                .scrollIntoView(true)
                .hover();

        Selenide.sleep(700);

        deleteActiveVisitOption.click();

        return this;
    }

    public VisitPage waitDeleteVisitConfirmationModal() {
        $x("//*[contains(text(), 'Are you sure you want to delete this visit')]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));

        return this;
    }

    public VisitPage checkVisitDeletedSuccessfully() {
        SelenideElement successMsg = $x("//*[contains(text(), 'Facility Visit deleted')]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15));

        assertThat(VisitMessage.VISIT_DELETED_SUCCESSFULLY.isPresentIn(successMsg.getText()))
                .as("Visit deleted success message should be displayed")
                .isTrue();

        return this;
    }
}
