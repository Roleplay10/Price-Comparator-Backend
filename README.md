# Price Comparator Backend

A Spring Boot application that helps users compare product prices across multiple grocery stores, track price history, manage shopping carts, and receive custom price alerts.

## Project Description

**Price Comparator Backend** processes CSV data for products and discounts, stores it in PostgreSQL, and exposes REST APIs for:

* **User Authentication**: Register & login with JWT.
* **Shopping Cart**: Single cart per user to add/remove items.
* **Price Optimization**: Assign each cart item to the store offering the lowest price.
* **Best & New Discounts**: Query top percentage discounts and those added in the last 24h.
* **Price History**: Retrieve time-series price points for products, filterable by store/category/brand.
* **Recommendations**: Compute price-per-unit (e.g., RON/kg) to highlight best buys.
* **Custom Price Alerts**: Users set target prices and receive email notifications when met.

## Architecture & Modules

```
src/
├── controller/            # REST controllers
├── service/               # Business logic interfaces & implementations
├── repository/            # Spring Data JPA repositories
├── domain/                # JPA entities
├── dto/                   # Request & response DTOs
├── scheduler/             # Scheduled tasks (alerts)
├── security/              # JWT & Spring Security config
├── config/                # App configuration
└── resources/
    └── application.yml    # DB, mail, scheduler settings (see below)
```

## Configuration Example

Rename `application.yml` to `application.yaml` (or `.yml`) and **do not commit** with secrets. Create an `application.yaml` (or override via `application-{profile}.yaml` or env vars):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/price_comparator
    username: your_db_user
    password: your_db_pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

mail:
  host: smtp.gmail.com
  port: 587
  username: your_gmail_email
  password: your_gmail_password
  protocol: smtp
  properties:
    mail:
      smtp:
        auth: true
        starttls.enable: true

alerts:
  check:
    interval: 300000
```

## Sample CSV Files

Products CSV (`lidl_2025-05-01.csv`):

```csv
product_id;product_name;product_category;brand;package_quantity;package_unit;price;currency
P001;Lapte Zuzu;lactate;Zuzu;1.0;l;9.90;RON
P002;Iaurt Grecesc;lactate;Lidl;0.4;kg;11.50;RON
P003;Ouă Mărimea M;ouă;Lidl;10;buc;13.20;RON
```

Discounts CSV (`lidl_discount_2025-05-01.csv`):

```csv
product_id;product_name;product_category;brand;package_quantity;package_unit;from_date;to_date;percentage_of_discount
P002;Iaurt Grecesc;lactate;Lidl;0.4;kg;2025-05-01;2025-05-07;15
P003;Ouă Mărimea M;ouă;Lidl;10;buc;2025-05-01;2025-05-05;10
```

## Setup & Run

1. **Clone & Build**

```bash
git clone [https://github.com/your-org/price-comparator-backend.git](https://github.com/your-org/price-comparator-backend.git)
cd price-comparator-backend
mvn clean package
```

2. **Configure**
   - Create `application.yaml` as above.
   - Set environment variables for mail.

3. **Run**
   ```bash
mvn spring-boot:run
````

4. **Import Data**

```bash
# Products
curl -X POST http://localhost:8080/import/prices \
  -H "Authorization: Bearer $TOKEN" \
  -F "files=@lidl_2025-05-01.csv"

# Discounts
curl -X POST http://localhost:8080/import/discounts \
  -H "Authorization: Bearer $TOKEN" \
  -F "files=@lidl_discount_2025-05-01.csv"
```

## Key Endpoints

### Auth

* `POST /auth/register`
* `POST /auth/login`

### Cart

* `GET /cart`
* `POST /cart/items`
* `DELETE /cart/items/{id}`
* `DELETE /cart`
* `GET /cart/optimize`

### Discounts

* `GET /discounts/best?limit=N`
* `GET /discounts/new`

### Price History

* `GET /products/{id}/history?store=&category=&brand=`

### Recommendations

* `GET /products/recommendations?category=&brand=`

### Alerts

* `POST /alerts`
* `GET /alerts`
* `DELETE /alerts/{id}`

## Scheduler

Price alerts are checked every 5 minutes (`alerts.check.interval`).

---

Feel free to tweak the configuration samples and CSV examples as needed.
