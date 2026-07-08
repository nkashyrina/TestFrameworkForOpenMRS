# Why the Search/Register UI Tests Are Flaky

_Analysis of `SearchPatientTest`, `RegisterPatientTest`, and supporting page objects._

The flakiness comes from **three distinct sources**, only one of which is a real
product bug. The other two are test-design races that the recent "fixes" in git
history did not actually address — they renamed locators and removed `@Skip`
annotations, but the underlying timing problems remain.

---

## 1. Core flake: async search race with no "results settled" wait ⭐ (Search tests)

This is the big one. The sequence in `searchDropdownShowsCorrectResultsTest`
(`SearchPatientTest.java:40-57`):

```java
AdminSteps.createPatientsForSearch(4, true, generatedPartOfTheName);   // create via API
...
searchPatients.open().header.populateSearchPatientString(generatedPartOfTheName);
int countFromUIDropDown = searchPatients.header.getSearchResultsCount();              // read #1
List<...> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults(); // read #2
softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);            // can mismatch
```

There are **two compounding races**:

### (a) The UI dropdown is read mid-render
`populateSearchPatientString` (`Header.java:62-69`) types the query and then only
waits for `searchResultsContainer.shouldBe(visible)`. The container turns visible
the moment the *first* result (or a spinner) paints — not when the debounced
search settles on its final result set. The test then does two separate,
non-atomic DOM reads: `getSearchResultsCount()` reads the `resultsText` label,
then `getSearchDropdownResults()` re-queries all `<a>` elements. If React
re-renders between those two reads (results trickling in), the count text and the
link count disagree -> `hasSize(countFromUIDropDown)` fails. **There is no wait
that the result count has stabilized before reading.**

### (b) OpenMRS search indexing is eventually consistent
`createPatientsForSearch` creates 4 patients via REST. Both the UI search *and*
`searchPatientsByString` (`AdminSteps.java:435`) hit the patient search index,
which OpenMRS updates **asynchronously** (Lucene). Immediately after creation, the
index may have 0-4 of them. So the three-way assertion
`UI dropdown size == count == API size` (lines 50, 53) compares three values that
are each independently converging toward 4. Run it fast enough and you get 3 vs 4,
or 4 vs 2.

### Smoking gun
You have `RetryUtils.retryStable()` — a utility literally built to "wait for a
value to stabilize" — and it is **used nowhere in the UI or search code**. The
right tool exists; it was never applied to the problem it was made for.

---

## 2. Fragile focus-hack clicks on re-rendering elements (Search -> results page)

`pressEnterButton()` and `clickSearchButton()` (`Header.java:114-128`) both do
`searchResultsCount.click()` to move focus before submitting:

```java
public SearchResultsPage clickSearchButton() {
    searchResultsCount.click();                 // clicking a live-updating text node
    searchButton.shouldBe(visible).click();
    ...
}
```

Clicking the `resultsText` element — which is being re-rendered as results stream
in — is inherently racy. Selenide can throw stale-element / click-intercepted if
the node is replaced between locate and click. This is a workaround for focus
behavior, not a deterministic interaction.

---

## 3. The Register `@Skip` is a genuine product bug, not a timing flake (Register test)

`RegisterPatientTest.java:108`:

```java
@Skip(reason = "Bug(Inconsistency): when aprox. date is fewer than 6 only year is shown,
        when greater month and year")
```

This is correctly diagnosed and is **not** flakiness — it is a real OpenMRS
display inconsistency in how approximate (estimated) birthdates render. The test
was skipped rather than made tolerant of both formats. Separately,
`PatientSearchResultParser.parseAge/isEstimated` (`parser.java:68-79`) is brittle
around exactly this estimated-age formatting (`yrs`/`wks`/`days`), so the same
product behavior would make result-page parsing flaky too.

---

## Secondary issues that amplify the flake

- **No stabilization wait anywhere**, yet `VisitPage.java` uses hard
  `Selenide.sleep(1000/1500/700)` (lines 283, 310, 331, 343) — the opposite
  anti-pattern. The framework swings between "no wait" (search -> flaky) and
  "fixed sleep" (visit -> slow + still flaky).
- **Cleanup regression:** the `feb47f1` "flaky Search UI tests fix" commit
  *deleted* the `createdUuids` tracking and the `@AfterAll` purge from
  `SearchPatientTest`. Unless `@AutoCleanup` is active on this class (it is not
  annotated), every search test now **leaks 4 patients per run** into the shared
  OpenMRS instance. Accumulated patients make search slower and the `q=` count
  comparisons more fragile over time.
- **Parser order-dependence:** `getSearchDropdownResults` maps every `<a>` through
  a parser that infers fields by regex/line-position (`parser.java:32-66`). Any
  extra link or layout shift in the dropdown corrupts a row silently.

---

## Recommended fixes (priority order)

1. **Add a "results settled" wait before reading.** In
   `populateSearchPatientString`, after the container is visible, wait until the
   count label matches the number of rendered `<a>` rows *and* equals the expected
   count — e.g. wrap the read in `RetryUtils.retryStable(...)` so you poll until
   two consecutive reads agree. This single change kills race (a).
2. **Poll the API index until it's consistent before asserting.** After
   `createPatientsForSearch(4, ...)`, call `searchPatientsByString` in a retry loop
   until it returns the expected count before driving the UI. Removes race (b).
3. **Replace the `searchResultsCount.click()` focus hacks** with
   `searchTextInputField.click()` / explicit focus on a stable element, then submit.
4. **Restore cleanup** — re-add the `@AfterAll` purge or annotate
   `SearchPatientTest` with `@AutoCleanup` so search patients don't leak.
5. **Register test:** instead of `@Skip`, make the birthdate assertion accept both
   the year-only and month-year formats (matching documented product behavior), or
   keep it `@Skip` with a tracking bug ID rather than a freeform note.

---

## Highest-leverage next step

Fixes #1 and #2 (the `retryStable`-based settle wait + API index poll) remove the
two real races and would let you un-skip the remaining flaky tests.
