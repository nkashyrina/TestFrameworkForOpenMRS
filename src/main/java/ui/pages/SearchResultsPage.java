package ui.pages;

import api.models.ui.UiPatientMandatoryInfo;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import ui.parsers.PatientSearchResultParser;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

public class SearchResultsPage extends AuthBasePage<SearchResultsPage> {
    private final SelenideElement refineSearchTitle = $(Selectors.byText("Refine search"));
    private final SelenideElement searchResultsCount = $(Selectors.byXpath("//h2[contains(text(), 'search results')]"));
    private final SelenideElement searchResultsList = $("div[data-openmrs-role='Search Results']");

    private final PatientSearchResultParser parser = new PatientSearchResultParser();

    @Override
    public String url() {
        return "/search?query=%s";
    }

    @Override
    public SearchResultsPage checkItIsCorrectPage() {
        refineSearchTitle.shouldBe(Condition.visible);
        searchResultsCount.shouldBe(Condition.visible);

        return this;
    }

    public int getSearchResultsCount() {
        return Integer.parseInt(searchResultsCount.getText().split(" ")[0]);
    }

    public List<UiPatientMandatoryInfo> getSearchResults() {
        return searchResultsList
                .findAll("a")
                .stream()
                .map(parser::parse)
                .collect(Collectors.toList());
    }
}
