# Design & Test-Automation Patterns — TestFrameworkForOpenMRS

This document lists **every design pattern actually used** in the project, with
concrete file/class evidence and a short explanation of *how* each one is
realized. Patterns that are commonly expected but **not** present are listed at
the end so the picture is complete.

All references point to real code under `src/main/java`.

---

## At a glance

| # | Pattern | Category | Where (primary evidence) |
|---|---------|----------|--------------------------|
| 1 | **Singleton** | Creational | `api.configs.Config`, `common.storages.EntityStorage` (ThreadLocal) |
| 2 | **Builder** | Creational | Lombok `@Builder` on ~23 models; `CrudRequester.QueryBuilder` |
| 3 | **Factory Method** (static-factory form) | Creational | `RequestSpecs`, `ResponseSpecs`, generators, `BasePage.getPage` |
| 4 | **Adapter** | Structural | `ui.parsers.PatientSearchResultParser`, `RequestSpecs.setCookieInBrowser` |
| 5 | **Decorator** | Structural | `ValidatedCrudRequester`, `ValidatedAuthRequester` |
| 6 | **Facade** | Structural | `AdminSteps`, `RequestSpecs`/`ResponseSpecs`, Page Objects |
| 7 | **Proxy** | Structural | Selenide lazy element/page proxies; `Validated*` as protection proxies |
| 8 | **Strategy** | Behavioral | request/response specs; `CrudEndpointInterface` implementations; generation rules |
| 9 | **Template Method** | Behavioral | `BasePage.open()` calling abstract `url()`/`checkItIsCorrectPage()` |
| 10 | **Specification** | Behavioral (REST Assured) | `RequestSpecs`, `ResponseSpecs` |
| 11 | **Dependency Injection** | Structural/JUnit | `InjectAdminExtension` (`ParameterResolver`), lifecycle extensions |
| 12 | **Page Object Model** (+ Fluent, Component Object) | Test automation | `ui/pages/*`, `ui/components/*` |

**Not used:** Abstract Factory, Prototype, Iterator (custom), Mediator, Screenplay — see the last section.

---

## Creational Patterns

### 1. Singleton ✅
Exactly one instance, private constructor, global access point.

**`api/configs/Config.java`** — classic eager singleton holding the parsed
`config.properties`:
```java
public final class Config {
    private static final Config INSTANCE = new Config();   // single eager instance
    private Config() { /* loads config.properties once */ }
    public static String getProperty(String key) { ... INSTANCE.properties.getProperty(key); }
}
```

**`common/storages/EntityStorage.java`** — a **thread-scoped** singleton (one
instance *per thread*), the correct variant for parallel test execution: each
thread isolates the entities it created so they can be cleaned up afterwards
without locking:
```java
private static final ThreadLocal<EntityStorage> INSTANCE =
        ThreadLocal.withInitial(EntityStorage::new);
private EntityStorage() { }
public static void add(BaseModel entity) { INSTANCE.get().storage... }
```

### 2. Builder ✅
Separates step-by-step construction of a complex object from its representation.

- **Lombok `@Builder`** on ~23 request/response models, e.g. `CreatePatientRequest`,
  `CreateVisitRequest`, `PersonName`, `AdminLogin`:
  ```java
  CreatePatientRequest.builder().identifiers(List.of(identifiers)).person(person).build();
  ```
- **Hand-written fluent builder** `CrudRequester.QueryBuilder` for query params, with
  domain-specific steps:
  ```java
  new CrudRequester.QueryBuilder().q(name).vEqualsFull().limit(10).build();
  ```

### 3. Factory Method — static-factory form ✅
The pure GoF variant (subclass overrides a method to pick the product) is not
used, but the **static factory method** idiom is used heavily to centralize and
name construction:

- `RequestSpecs.unauthSpec()` / `adminSpec()` / `authWithJSessionId(id)` build configured `RequestSpecification`s.
- `ResponseSpecs.requestReturnsOK()` / `requestReturnsCreated()` / `requestReturnsNotFoundWithMessage(...)` build `ResponseSpecification`s.
- `PartialEntityGenerator.generate(Class, fields...)`, `RandomModelGenerator.generate(Class)`, `RandomDataGenerator.*`, `RandomPasswordGenerator.generate()` produce objects/data.
- `BasePage.getPage(Class)` / `BaseComponent.getPage(Class)` return the requested page product at runtime via `Selenide.page(...)`.

---

## Structural Patterns

### 4. Adapter ✅
Converts one interface/representation into the one the client expects.

- **`ui/parsers/PatientSearchResultParser`** adapts a Selenide `SelenideElement`
  (raw multi-line UI text) into the typed domain model `UiPatientMandatoryInfo`:
  ```java
  public UiPatientMandatoryInfo parse(SelenideElement el) {
      List<String> lines = extractLines(el);
      return UiPatientMandatoryInfo.builder().names(findName(lines))
              .gender(findGender(lines)).age(parseAge(...))...build();
  }
  ```
- **`RequestSpecs.setCookieInBrowser(...)`** adapts a RestAssured cookie into a
  Selenium/Selenide cookie so an API session can be injected into the browser.

### 5. Decorator ✅
Same interface as the wrapped component + added responsibilities.

- **`ValidatedCrudRequester`** implements `CrudEndpointInterface`, wraps a
  `CrudRequester`, and adds response-type validation + entity registration:
  ```java
  public T post(BaseModel model) {
      BaseModel response = crudRequester.post(model).extract().as(endpoint.getResponseModel());
      if (!endpoint.getResponseModel().isInstance(response))            // added: validation
          throw new IllegalStateException("Unexpected response type: " + response.getClass());
      EntityStorage.add(response);                                      // added: track for cleanup
      return (T) response;
  }
  ```
- **`ValidatedAuthRequester`** wraps `AuthRequester` (both implement
  `SessionEndpointInterface`) and adds automatic response extraction/deserialization.

> These `Validated*` classes double as **protection Proxies** (§7); the only
> departure from a textbook Decorator is that they *create* the wrapped requester
> internally instead of receiving it via the constructor.

### 6. Facade ✅
A simple, task-oriented surface over a complex subsystem.

- **`api/requests/steps/AdminSteps`** is the clearest facade: methods like
  `createPatient()`, `createVisit(patient)`, `deletePatientByUuid(uuid)` hide
  specs + endpoints + requesters + generators + builders behind one call.
- **`RequestSpecs` / `ResponseSpecs`** present a small, intention-revealing API over RestAssured's builder machinery.
- **Page Objects** present page-scoped operations over the low-level Selenide API (`BasePage.authAsUser(...)` even hides the open-browser → fetch-API-cookie → inject-cookie flow).

### 7. Proxy ✅
- **Framework-provided (virtual/lazy proxies):** Selenide `SelenideElement`s and
  the page objects returned by `Selenide.page(...)` (used in `BasePage.getPage`)
  are dynamic proxies that resolve and re-locate the real DOM element only when a
  method is invoked — the classic lazy/virtual proxy.
- **Application-level (protection/smart proxy):** the `Validated*` requesters
  stand in front of the raw requesters with the same interface and control access
  by validating the response before returning it.

---

## Behavioral Patterns

### 8. Strategy ✅
Interchangeable, encapsulated behaviors selected at runtime.

- **Request/response specs** — swap `RequestSpecs.unauthSpec()` /
  `adminSpec()` / `authWithJSessionId()` to change the auth strategy, and pass
  different `ResponseSpecs.*` to change the validation strategy — all into the
  same requester.
- **Requester implementations** — `CrudRequester` vs `ValidatedCrudRequester`
  (both `CrudEndpointInterface`) are interchangeable CRUD strategies.
- **Field generation** — in `PartialEntityGenerator`/`RandomModelGenerator`, a
  `@GeneratingRule(regex=...)` selects a regex strategy, otherwise a type-based
  default strategy is used per field.

### 9. Template Method ✅
A base class fixes the skeleton and defers steps to subclasses.

**`ui/pages/BasePage`** — concrete `open()` drives the flow but delegates the
page-specific parts to abstract hooks the subclasses implement:
```java
public abstract String url();                                  // step: subclass supplies
public abstract <T extends BasePage> T checkItIsCorrectPage();  // step: subclass supplies
public T open() { return Selenide.open(url(), (Class<T>) getClass()); }  // skeleton
```
(`BaseComponent.shouldBeLoaded()` + abstract `getSelf()` follow the same shape.)

### 10. Specification ✅ (REST Assured idiom)
`RequestSpecs` and `ResponseSpecs` build composable `RequestSpecification` /
`ResponseSpecification` objects that are combined and reused across requesters —
including `HttpRequest.combine(...)` which merges multiple response specs into one.

### 11. Dependency Injection ✅ (JUnit 5)
`common/extensions/InjectAdminExtension` implements `ParameterResolver` to inject
an `AdminLogin` into any test parameter annotated `@InjectAdmin`:
```java
public boolean supportsParameter(...) {
    return param.getType() == AdminLogin.class && param.isAnnotated(InjectAdmin.class);
}
public AdminLogin resolveParameter(...) { return AdminLogin.getAdmin(); }
// usage: void test(@InjectAdmin AdminLogin admin) { ... }
```
Lifecycle extensions (`AdminSessionExtension`, `CleanupExtension`, `TimingExtension`,
`SkipExtension`) similarly inject cross-cutting behavior via JUnit callbacks —
each `@ExtendWith` on `BaseTest` handles one concern.

---

## Test-Automation Patterns

### 12. Page Object Model (+ Fluent + Component Object) ✅
- **POM:** each page in `ui/pages/*` encapsulates its elements and exposes
  interaction methods (`LoginPage`, `PatientRegistrationPage`, ...).
- **Fluent:** methods return `this` or the next page to allow chaining:
  ```java
  new LoginPage().open()
      .populateUserNameField(admin.getUsername()).clickContinueButton()
      .populatePasswordField(admin.getPassword()).clickLogInButton();
  ```
- **Component Object / Loadable Component:** reusable UI fragments with a
  `shouldBeLoaded()` self-check (`ui/components/BaseComponent`, `Header`,
  `AddressComponent`, `ContactDetailsComponent`).

Generics/parameterized types (`BasePage<T>`, `ValidatedCrudRequester<T extends BaseModel>`)
underpin all of the above as type-safe reusable infrastructure.

---

## Patterns considered but NOT used

| Pattern | Verdict | Why |
|---------|---------|-----|
| **Abstract Factory** | ❌ | No family of related factories selected together; specs/requesters are built with plain static methods and `new`. |
| **Prototype** | ❌ | No `clone()`/`Cloneable`/copy-constructor; objects are built fresh via builders or reflective generation, not cloned. |
| **Iterator** (custom) | ❌ | Only the JDK's built-in iteration (`for-each`, Streams, `List.getFirst()`); no custom `Iterator`/`Iterable` is authored. |
| **Mediator** | ❌ | `AdminSteps` is a one-directional Facade, not a peer-coordinating Mediator; JUnit extensions are interceptors, not mediators. |
| **Screenplay** | ❌ | No Actors/Tasks/Questions/Abilities; the UI layer uses Page Objects + a Steps facade instead. |

---

## Summary

- **Backbone:** Page Object Model (fluent) + Component Objects for UI, and a
  **Facade (`AdminSteps`) + Strategy (specs/requesters) + Builder (models & `QueryBuilder`)**
  for the API layer.
- **Supporting GoF patterns:** Singleton, Factory Method (static-factory form),
  Adapter, Decorator, Proxy, Template Method.
- **Framework idioms:** Specification (REST Assured), Dependency Injection (JUnit 5), Generics.
- **Not used:** Abstract Factory, Prototype, custom Iterator, Mediator, Screenplay.
