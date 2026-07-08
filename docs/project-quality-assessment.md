# TestFrameworkForOpenMRS — Code Quality & Maturity Assessment

## What this project is

An end-to-end test automation framework for **OpenMRS 3.x** (an open-source
medical records system). It tests two layers:

- **API layer** (REST Assured) — patient CRUD, visits, persons, authentication
- **UI layer** (Selenide/Selenium) — login, patient registration, search, visits

**Stack:** Java 21, Maven, JUnit 5, Selenide 7, REST Assured 6, AssertJ, Lombok,
Allure reporting, Swagger-coverage. Full Docker Compose infra (OpenMRS + MariaDB +
Selenoid browser grid) driven by a GitHub Actions pipeline.

---

## Skill level: **Mid-to-Senior (solidly above junior)**

Upper-middle / junior-senior boundary, leaning senior on architecture.

### What makes it look senior

Things juniors almost never do:

1. **Custom JUnit 5 extensions for cross-cutting concerns** — `@AutoCleanup`
   automatically deletes every entity a test created via API afterward
   (`CleanupExtension`), `@InjectAdmin` does parameter-level dependency injection,
   `@AdminSession` pre-authenticates the browser. This is framework-author
   thinking, not test-writer thinking.
2. **Config-driven model comparison** — assertion field-mappings live in
   `model-comparison.properties` instead of being hardcoded. Decoupling assertion
   logic from test code is a senior instinct.
3. **Layered config resolution** — `Config.java` resolves system props -> env vars
   -> property file, so the same code runs locally and in CI without edits.
4. **Proper abstraction layers** — generic typed Page Object base (`BasePage<T>`),
   Component Object Model, a `CrudRequester`/`ValidatedCrudRequester` HTTP layer,
   request/response specs, and an `AdminSteps` facade. Clean separation, very
   little duplication.
5. **ThreadLocal entity storage** designed for parallel safety, plus Swagger
   coverage gating in CI.

### What holds it back from "clearly senior"

1. **Flaky UI tests are skipped, not fixed** — e.g. `RegisterPatientTest.java:108`
   has `@Skip(reason="Bug(Inconsistency)...")`, and recent git history is full of
   *"flaky Search UI tests fix"* and *"checking, if swagger is running"* commits.
   A senior fixes root causes or quarantines deliberately; working around timing
   issues with skips is a tech-debt signal.
2. **Hardcoded credentials in the repo** — `admin.password=Admin123` and a base64
   token sit in `config.properties`. They're harmless OpenMRS demo defaults, but
   committing them is a habit seniors avoid.
3. **The framework has no tests of itself** — the generators, extensions, and
   model comparator (the most complex code) are untested, which makes them risky
   to refactor.
4. **No static analysis / formatting** — pre-commit only strips whitespace; no
   Checkstyle/Spotless/Spotbugs.
5. **Parallelization is built but disabled** (commented out in
   `junit-platform.properties`) — the capability exists but isn't realized, and the
   Swagger coverage gate is set to a very permissive 4%.

---

## Bottom line

The **architecture is senior-level**; the **operational discipline is
middle-level**. Whoever wrote this clearly understands enterprise test-automation
patterns and applied them deliberately — but the recurring flaky-test workarounds,
committed credentials, lack of self-tests, and unused parallelization are the gaps
between "designs good frameworks" and "runs them at production rigor."

### Highest-leverage moves to push it firmly into senior territory

1. Fix the flaky tests' root causes instead of `@Skip`
   (see `flaky-ui-tests-analysis.md`).
2. Move credentials to CI secrets.
3. Add Spotless + a static analyzer.
4. Actually enable parallel execution.
