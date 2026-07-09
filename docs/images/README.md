# README screenshots

Drop image assets referenced by the main `README.md` here.

Expected files:

- `allure-report.png` — a screenshot of the Allure report overview
  (the "Overview" dashboard with the pass/fail donut and suite breakdown looks best).

**How to capture it:**

```bash
./mvnw clean test -P all      # run the suite to produce results
./mvnw allure:serve           # opens the interactive report in your browser
```

Take a screenshot of the Overview page, save it as `allure-report.png` in this
folder, and commit it. The main README will render it automatically.
