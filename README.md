# Price Comparator Backend

A Spring Boot application that helps users compare product prices across multiple grocery stores, track price history, manage shopping carts, and receive custom price alerts. This README provides an overview of the project, key features, architecture, setup instructions, and API reference.

## Project Description

Price Comparator Backend ingests product and discount data from CSV files, stores it in a PostgreSQL database, and exposes RESTful APIs for:

* **User Authentication**: Register and login using JWT-based security.
* **Shopping Cart**: Add/remove items in a single cart scoped to each user.
* **Price Optimization**: Determine the lowest price per item across stores and group results by store.
* **Best & New Discounts**: Query current top percentage discounts and those added in the past 24 hours.
* **Price History**: Retrieve time-series price data for products, filterable by store, category, or brand.
* **Recommendations**: Compute and compare value-per-unit (e.g., RON/kg or RON/l) to highlight best buys.
* **Custom Price Alerts**: Users set target prices for products and receive email notifications when prices fall below thresholds.

## Architecture & Modules

```
src/
├── controller/            # REST controllers
├── service/               # Business logic interfaces & implementations
├── repository/            # Spring Data JPA repositories
├── domain/                # JPA entity models
├── dto/                   # Request and response DTOs
├── scheduler/             # Scheduled tasks (price-alert checks)
├── security/              # JWT and Spring Security configuration
├── config/                # Application configuration (e.g. mail)
└── resources/
    └── application.yml    # Data source, mail, and scheduler settings
```

## Setup Instructions

1. **Clone & Build**

   ```bash
   git clone https://github.com/your-org/price-comparator-backend.git
   cd price-comparator-backend
   mvn clean package
   ```

2. **Configure Database**

    * Create a PostgreSQL database (e.g., `price_comparator`).
    * In `src/main/resources/application.yml`, set `spring.datasource.url`, `username`, and `password`.

3. **Configure Email**

    * Obtain a Gmail App Password and set environment variables:

      ```bash
      export GMAIL_USER=youremail@gmail.com
      export GMAIL_APP_PASSWORD=your_app_password
      ```
    * Or update `spring.mail` properties in `application.yml`.

4. **Run the Application**

   ```bash
   mvn spring-boot:run
   ```

5. **Import Sample Data**

    * Upload product CSVs:

      ```bash
      curl -X POST http://localhost:8080/import/prices \
        -H "Authorization: Bearer $TOKEN" \
        -F "files=@lidl_2025-05-01.csv" \
        -F "files=@kaufland_2025-05-02.csv" \
        -F "files=@profi_2025-05-03.csv"
      ```
    * Upload discount CSVs:

      ```bash
      curl -X POST http://localhost:8080/import/discounts \
        -H "Authorization: Bearer $TOKEN" \
        -F "files=@lidl_discount_2025-05-01.csv" \
        -F "files=@kaufland_discount_2025-05-02.csv"
      ```

## Key API Endpoints

### Authentication

* `POST /auth/register` – user signup
* `POST /auth/login` – returns JWT

### Shopping Cart

* `GET /cart` – view or create user cart
* `POST /cart/items` – add item
* `DELETE /cart/items/{itemId}` – remove item
* `DELETE /cart` – clear cart
* `GET /cart/optimize` – split cart by lowest price per store

### Discounts

* `GET /discounts/best?limit=N` – top N active discounts
* `GET /discounts/new` – discounts added in last 24h

### Price History

* `GET /products/{id}/history?store=&category=&brand=` – time-series points

### Recommendations

* `GET /products/recommendations?category=&brand=` – value-per-unit comparisons

### Price Alerts

* `POST /alerts` – create or reactivate alert
* `GET /alerts` – list user alerts
* `DELETE /alerts/{id}` – disable alert

## Scheduled Jobs

* **Price Alert Checker** runs every 5 minutes (configurable via `alerts.check.interval`), compares effective prices (including discounts) against active alerts, sends email notifications, and deactivates triggered alerts.

---

For detailed examples and code snippets, refer to the Javadoc and inline comments in each package. Enjoy optimizing your shopping costs!
