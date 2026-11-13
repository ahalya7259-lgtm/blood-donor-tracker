# Blood Donor Tracker

Educational **Java 17+ / Spring Boot 3** app for a small-town blood bank or volunteer group **demo**.

It stores **synthetic** donors and blood stock, finds eligible donors using a **configurable** donation gap, and shows low-stock / near-expiry alerts.

> **Not medical advice.**  
> **Not a real blood bank system.**  
> **Do not store real donor data** without consent, security, and legal review.

## Features

1. Donor CRUD (name, age, blood group, phone, city, last donation, available)
2. Eligibility check using `app.donation.min-gap-days` (default **90**) + available flag; shows next eligible date
3. Search by blood group and city (eligible donors first)
4. Blood stock units with collection and expiry dates
5. Alerts: low stock and units expiring within 7 days
6. Blood group compatibility helper (educational map only)
7. Dashboard with Chart.js, counts, and alerts
8. CSV export for donors and stock
9. Validation (phone pattern, age range settings, no future dates)
10. Admin login via Spring Security (`ADMIN_PASSWORD` environment variable)

## Privacy

- Seed data: **50 synthetic donors**, phones like `90000 00001`
- Rows marked SAMPLE in UI and `sample_data` flag
- Never commit real personal data

## Settings (not medical facts)

| Property | Default |
|----------|--------|
| `app.donation.min-gap-days` | 90 |
| `app.donor.min-age` / `max-age` | 18 / 65 |
| `app.stock.low-threshold` | 3 |
| `app.stock.expiry-warning-days` | 7 |

Confirm real donation intervals with the **National Blood Transfusion Council of India** or a licensed blood bank before any real use.

## How to run

```bash
git clone https://github.com/ahalya7259-lgtm/blood-donor-tracker.git
cd blood-donor-tracker
export ADMIN_PASSWORD=ChangeMe123
mvn test
mvn spring-boot:run
```

Open http://localhost:8080 — user `admin`, password from `ADMIN_PASSWORD`.

## Tech stack

Spring Boot 3 · Spring Data JPA · Spring Security · Thymeleaf · H2 · Chart.js · JUnit 5 · Maven

## Author

**Ahalya** — https://github.com/ahalya7259-lgtm

## License

MIT
