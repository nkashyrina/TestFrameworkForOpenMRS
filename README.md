# TestFrameworkForOpenMRS

[![Run tests](https://github.com/nkashyrina/TestFrameworkForOpenMRS/actions/workflows/run_tests_pipeline.yml/badge.svg)](https://github.com/nkashyrina/TestFrameworkForOpenMRS/actions/workflows/run_tests_pipeline.yml)

## Reports

| Report | Link |
|---|---|
| Allure test results | [View](https://nkashyrina.github.io/TestFrameworkForOpenMRS/) |
| Swagger API coverage | [View](https://nkashyrina.github.io/TestFrameworkForOpenMRS/swagger-coverage-report.html) |




# Test Automation Framework for OpenMRS

End-to-end test automation framework for [**OpenMRS 3.x**](https://openmrs.org/) — an
open-source electronic medical records platform. Covers both the **REST API** and the
**web UI** from a single Maven project, with fully containerized infrastructure and a
CI pipeline that provisions the system under test on every push.

[![Run tests](https://github.com/nkashyrina/TestFrameworkForOpenMRS/actions/workflows/run_tests_pipeline.yml/badge.svg)](https://github.com/nkashyrina/TestFrameworkForOpenMRS/actions/workflows/run_tests_pipeline.yml)
![Java](https://img.shields.io/badge/Java-21-orange)
![Build](https://img.shields.io/badge/Build-Maven-blue)
![API](https://img.shields.io/badge/API-REST%20Assured%206-green)
![UI](https://img.shields.io/badge/UI-Selenide%207-brightgreen)
![Reporting](https://img.shields.io/badge/Reporting-Allure-ff69b4)
[![Live Allure Report](https://img.shields.io/badge/Live%20Report-Allure-8A2BE2)](https://nkashyrina.github.io/TestFrameworkForOpenMRS/)

> **Why this project exists:** a portfolio-grade demonstration of enterprise
> test-automation architecture — layered abstractions, custom test-lifecycle
> tooling, containerized environments, and quality gates — rather than a flat
> collection of test scripts.

---

## Test Report

Every CI run publishes an interactive **Allure report** with history, trends, and
per-test detail:

📊 **[View the live test report →](https://nkashyrina.github.io/TestFrameworkForOpenMRS/)**

<!--
  GO-LIVE CHECKLIST for the live report link above:
  1. Ensure the GitHub Actions pipeline has completed a successful run
     (it publishes the report to the `gh-pages` branch automatically).
  2. In the repo: Settings → Pages → Source = "Deploy from a branch",
     Branch = `gh-pages`, folder = `/ (root)`.
  3. The URL https://nkashyrina.github.io/TestFrameworkForOpenMRS/ will then be live.
  Until then, the link will 404 — remove this section or the badge if you prefer.
-->

<p align="center">
  <img src="docs/images/allure-report.png" alt="Allure test report overview" width="800">
</p>

> _Screenshot placeholder — see [`docs/images/README.md`](docs/images/README.md)
> for how to generate `allure-report.png` (`./mvnw allure:serve`, then capture the
> Overview page). Delete this note once the image is added._

---

## Highlights

- **Two test layers, one framework** — API tests (REST Assured) and UI tests (Selenide),
  sharing the same models, data generation, config, and reporting.
- **Custom JUnit 5 extensions** for cross-cutting concerns: automatic post-test cleanup
  of every entity created via API (`@AutoCleanup`), parameter-level dependency injection
  (`@InjectAdmin`), browser pre-authentication (`@AdminSession`), conditional skipping
  (`@Skip`), and per-test timing.
- **Layered API client** — a `CrudRequester` / `ValidatedCrudRequester` stack over
  REST Assured with reusable request/response *specifications*, so tests read as
  intent, not as HTTP plumbing.
- **Config-driven assertions** — field-level model comparison rules live in
  `model-comparison.properties`, decoupling assertion logic from test code.
- **Containerized system under test** — Docker Compose spins up OpenMRS + MariaDB +
  a Selenoid browser grid; the same setup runs locally and in CI.
- **Quality gates in CI** — Swagger/OpenAPI coverage is measured and gated on every run,
  with Allure reports published to GitHub Pages and Telegram build notifications.
- **Thread-safe by design** — `ThreadLocal` entity storage isolates each test's data
  for parallel-safe execution.

---

## Tech Stack

| Area | Technology |
|------|------------|
| Language / Build | Java 21, Maven (wrapper included) |
| Test runner | JUnit 5 (Jupiter) |
| API testing | REST Assured 6 |
| UI testing | Selenide 7 (Selenium) |
| Assertions | AssertJ |
| Boilerplate | Lombok |
| Serialization | Jackson |
| Reporting | Allure |
| API coverage | swagger-coverage |
| Infrastructure | Docker Compose (OpenMRS, MariaDB, Selenoid grid) |
| CI/CD | GitHub Actions |

---

## Architecture

```
src/main/java
├── api/                 # API layer: production-style client, not test code
│   ├── requests/        #   CrudRequester / ValidatedCrudRequester, endpoints,
│   │                    #   request & response specifications, admin steps (facade)
│   ├── models/          #   typed request/response models (Lombok @Builder)
│   ├── configs/         #   layered config resolution (sys props → env → file)
│   └── assertions/      #   reusable / config-driven model comparison
├── ui/                  # UI layer
│   ├── pages/           #   Page Objects (generic typed BasePage<T>, fluent API)
│   └── components/      #   reusable Component Objects (header, address, contacts…)
└── common/              # cross-cutting infrastructure
    ├── extensions/      #   custom JUnit 5 extensions (cleanup, DI, timing, session)
    ├── annotations/     #   @AutoCleanup, @InjectAdmin, @AdminSession, @Skip …
    ├── generators/      #   random / partial test-data generation
    └── storages/        #   ThreadLocal entity storage for parallel safety

src/test/java
├── api/                 # API test suites  (patients, visits, persons, auth, search…)
└── ui/                  # UI test suites   (login, registration, search, visits)
```

The framework leans on well-known design patterns applied deliberately — Page Object
Model, Facade, Strategy, Decorator, Builder, Template Method, and JUnit-based
Dependency Injection. Each is documented with concrete code evidence in
[`docs/design-patterns.md`](docs/design-patterns.md).

---

## Getting Started

### Prerequisites

- **JDK 21+**
- **Docker** and **Docker Compose** (to run the system under test)
- No local Maven install required — use the bundled `./mvnw` wrapper

### 1. Start the environment

```bash
cd infra/docker_compose
bash restart_docker.sh          # brings up OpenMRS + MariaDB + Selenoid grid
```

Wait until OpenMRS is fully initialized (the CI pipeline polls a health endpoint;
locally, the backend is ready when `http://localhost/openmrs` responds).

### 2. Run the tests

```bash
./mvnw clean test -P api        # API suite only
./mvnw clean test -P ui         # UI suite only
./mvnw clean test -P all        # full suite (API + UI)
```

### 3. View the Allure report

```bash
./mvnw allure:serve             # generates and opens the interactive report
```

---

## Configuration

Runtime configuration is resolved in priority order **system property → environment
variable → `config.properties`**, so the same code runs locally and in CI without edits.
Key settings:

| Property | Purpose |
|----------|---------|
| `apiBaseUrl` / `apiFullPrefix` | OpenMRS REST base URL |
| `uiBaseUrl` | OpenMRS 3.x SPA URL |
| `browser`, `browserSize`, `browserRemote` | Selenide / Selenoid grid config |

---

## Continuous Integration

The [GitHub Actions pipeline](.github/workflows/run_tests_pipeline.yml) runs on every
push and pull request and:

1. Provisions the full OpenMRS stack via Docker Compose (ephemeral, per-run).
2. Waits for the backend to become healthy before executing tests.
3. Runs the complete API + UI suite against the live system.
4. Measures **API (Swagger) coverage** and enforces a coverage gate.
5. Publishes the **Allure report** to GitHub Pages and sends a **Telegram**
   pass/fail notification with a link to the run.

---

## About

Built as a demonstration of production-style test-automation engineering:
architecture, tooling, and CI discipline applied to a real, non-trivial application.

**Author:** Nata Kashyrina · [GitHub](https://github.com/nkashyrina)
</content>
</invoke>
