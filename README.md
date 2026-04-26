# Customer Management System

![Java](https://img.shields.io/badge/Java-8-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen?logo=springboot)
![React](https://img.shields.io/badge/React-18-blue?logo=react)
![MariaDB](https://img.shields.io/badge/MariaDB-10.11-blue?logo=mariadb)
![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-yellow)

A full-stack Customer Management System built with **Spring Boot** and **React**. Supports complete customer lifecycle management including creation, editing, viewing, bulk Excel uploads, multiple phone numbers, multiple addresses, and family member linking.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Database Setup](#database-setup)
- [Backend Setup](#backend-setup)
- [Frontend Setup](#frontend-setup)
- [API Reference](#api-reference)
- [Bulk Upload Guide](#bulk-upload-guide)
- [Running Tests](#running-tests)
- [Design Decisions](#design-decisions)

---

## Features

- **Customer CRUD** — Create, view, edit, and delete customers
- **Paginated Table View** — Searchable and paginated customer list
- **Multiple Phone Numbers** — Add unlimited phone numbers per customer
- **Multiple Addresses** — Add unlimited addresses with city and country selection
- **Family Member Linking** — Link existing customers as family members
- **Bulk Excel Upload** — Upload `.xlsx` files with up to 1,000,000 rows
  - Async processing with real-time progress tracking
  - Automatic upsert (create new or update existing by NIC)
  - Handles timeouts and memory constraints via batch processing
- **Master Data** — Cities and countries managed server-side, no frontend exposure needed
- **Input Validation** — Both client-side and server-side validation
- **Error Handling** — Global exception handler with meaningful error responses

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 8 | Core language |
| Spring Boot | 2.7.18 | Application framework |
| Spring Data JPA | 2.7.18 | ORM and repository layer |
| Hibernate | 5.6.15 | JPA implementation |
| MariaDB Java Client | 2.7.12 | Database driver |
| Apache POI | 5.2.3 | Excel file parsing |
| Lombok | Latest | Boilerplate reduction |
| Maven | 3.9.x | Build and dependency management |
| JUnit 5 | 5.x | Unit testing |
| Mockito | 4.x | Mocking framework |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 18 | UI framework |
| React Router DOM | 6 | Client-side routing |
| Axios | Latest | HTTP client |
| React Hook Form | Latest | Form state management |
| React Select | Latest | Dropdown with search |
| React DatePicker | Latest | Date of birth picker |

### Database
| Technology | Version | Purpose |
|---|---|---|
| MariaDB | 10.11+ | Primary database |
| H2 (test scope) | Latest | In-memory DB for unit tests |

---

## Project Structure

```
customer-management/
├── backend/                                    # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/cms/backend/
│   │   │   │   ├── BackendApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── AsyncConfig.java        # Thread pool for bulk upload
│   │   │   │   │   └── CorsConfig.java         # CORS for React dev server
│   │   │   │   ├── controller/
│   │   │   │   │   ├── CustomerController.java
│   │   │   │   │   ├── BulkUploadController.java
│   │   │   │   │   └── MasterDataController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── CustomerRequestDto.java
│   │   │   │   │   ├── CustomerResponseDto.java
│   │   │   │   │   ├── CustomerListDto.java
│   │   │   │   │   ├── AddressDto.java
│   │   │   │   │   ├── MasterDataDto.java
│   │   │   │   │   └── BulkUploadStatusDto.java
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   └── DuplicateNicException.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Customer.java
│   │   │   │   │   ├── CustomerPhone.java
│   │   │   │   │   ├── CustomerAddress.java
│   │   │   │   │   ├── Country.java
│   │   │   │   │   ├── City.java
│   │   │   │   │   └── BulkUploadJob.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── CustomerRepository.java
│   │   │   │   │   ├── CityRepository.java
│   │   │   │   │   ├── CountryRepository.java
│   │   │   │   │   └── BulkUploadJobRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── CustomerService.java
│   │   │   │       ├── BulkUploadService.java
│   │   │   │       └── MasterDataService.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       ├── java/com/cms/backend/
│   │       │   ├── controller/
│   │       │   │   ├── CustomerControllerTest.java
│   │       │   │   └── MasterDataControllerTest.java
│   │       │   └── service/
│   │       │       └── CustomerServiceTest.java
│   │       └── resources/
│   │           └── application.properties      # H2 test config
│   └── pom.xml
│
└── frontend/                                   # React application
    ├── src/
    │   ├── api/
    │   │   └── api.js                          # Axios API calls
    │   ├── context/
    │   │   └── MasterDataContext.jsx           # Global countries/cities state
    │   ├── pages/
    │   │   ├── CustomerListPage.jsx            # Paginated table view
    │   │   ├── CustomerFormPage.jsx            # Create / Edit form
    │   │   ├── CustomerDetailPage.jsx          # View customer detail
    │   │   └── BulkUploadPage.jsx              # Excel upload with progress
    │   ├── App.js
    │   ├── App.css
    │   └── index.js
    └── package.json
```

---

## Prerequisites

Make sure the following are installed before running the project:

| Tool | Version | Download |
|---|---|---|
| Java JDK | 8+ | [adoptium.net](https://adoptium.net) |
| Apache Maven | 3.9+ | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Node.js & npm | 16+ | [nodejs.org](https://nodejs.org) |
| MariaDB | 10.11+ | [mariadb.org](https://mariadb.org/download) |

---

## Database Setup

### 1. Create the database and user

Open the MariaDB client and run:

```sql
CREATE DATABASE IF NOT EXISTS customer_mgmt
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'cms_user'@'localhost'
  IDENTIFIED BY 'cms_pass123';

GRANT ALL PRIVILEGES ON customer_mgmt.* TO 'cms_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Run the DDL (Schema)

```sql
USE customer_mgmt;

CREATE TABLE IF NOT EXISTS countries (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cities (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    country_id BIGINT NOT NULL,
    FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE IF NOT EXISTS customers (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    date_of_birth DATE         NOT NULL,
    nic_number    VARCHAR(50)  NOT NULL UNIQUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS customer_phones (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id  BIGINT      NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS customer_addresses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id   BIGINT NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city_id       BIGINT,
    country_id    BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (city_id)     REFERENCES cities(id),
    FOREIGN KEY (country_id)  REFERENCES countries(id)
);

CREATE TABLE IF NOT EXISTS family_members (
    customer_id BIGINT NOT NULL,
    member_id   BIGINT NOT NULL,
    PRIMARY KEY (customer_id, member_id),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id)   REFERENCES customers(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bulk_upload_jobs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name      VARCHAR(255),
    status         ENUM('PENDING','PROCESSING','DONE','FAILED') DEFAULT 'PENDING',
    total_rows     INT DEFAULT 0,
    processed_rows INT DEFAULT 0,
    failed_rows    INT DEFAULT 0,
    error_message  TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. Run the DML (Seed master data)

```sql
INSERT INTO countries (name) VALUES
  ('Sri Lanka'), ('India'), ('United States'), ('United Kingdom'), ('Australia');

INSERT INTO cities (name, country_id) VALUES
  ('Colombo', 1), ('Kandy', 1), ('Galle', 1), ('Jaffna', 1), ('Negombo', 1),
  ('Mumbai', 2), ('Delhi', 2), ('Bangalore', 2), ('Chennai', 2),
  ('New York', 3), ('Los Angeles', 3), ('Chicago', 3),
  ('London', 4), ('Manchester', 4), ('Birmingham', 4),
  ('Sydney', 5), ('Melbourne', 5), ('Brisbane', 5);
```
### 4. Viewing the Database

#### Option 1 — MariaDB Command Line (all platforms)
```bash
mysql -u cms_user -p
# password: cms_pass123
USE customer_mgmt;
SHOW TABLES;
SELECT * FROM customers;
```

#### Option 2 — HeidiSQL (Windows only, free)
HeidiSQL comes bundled with the MariaDB Windows installer.
1. Open **HeidiSQL** from Start Menu
2. Click **New** → enter these details:
   - Host: `127.0.0.1`
   - User: `cms_user`
   - Password: `cms_pass123`
   - Port: `3306`
3. Click **Open** → select `customer_mgmt` database on the left panel

#### Option 3 — DBeaver (Windows / Mac / Linux, free)
A universal DB GUI that works on all platforms.
Download from [dbeaver.io](https://dbeaver.io/download/)
1. New Connection → select **MariaDB**
2. Host: `localhost`, Port: `3306`
3. Database: `customer_mgmt`
4. Username: `cms_user`, Password: `cms_pass123`
5. Click **Finish**
---

## Backend Setup

```bash
cd backend

# Build the project
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

### Configuration

Edit `src/main/resources/application.properties` to change database credentials:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/customer_mgmt
spring.datasource.username=cms_user
spring.datasource.password=cms_pass123
```

---

## Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

The app will be available at: `http://localhost:3000`

> **Note:** The backend must be running before starting the frontend.

---

## API Reference

### Customers

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/customers` | Get paginated customer list |
| `GET` | `/api/customers?search=john&page=0&size=20` | Search customers by name or NIC |
| `GET` | `/api/customers/{id}` | Get single customer detail |
| `POST` | `/api/customers` | Create a new customer |
| `PUT` | `/api/customers/{id}` | Update an existing customer |
| `DELETE` | `/api/customers/{id}` | Delete a customer |

### Master Data

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/master-data` | Get all countries and cities |

### Bulk Upload

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/bulk-upload` | Upload Excel file (returns job ID immediately) |
| `GET` | `/api/bulk-upload/status/{jobId}` | Poll upload job status |

### Request Body — Create / Update Customer

```json
{
  "name": "John Silva",
  "dateOfBirth": "1990-05-15",
  "nicNumber": "199005150123",
  "phoneNumbers": ["0771234567", "0112345678"],
  "addresses": [
    {
      "addressLine1": "123 Main Street",
      "addressLine2": "Apt 4B",
      "cityId": 1,
      "countryId": 1
    }
  ],
  "familyMemberIds": [2, 5]
}
```

### Response — Customer Detail

```json
{
  "id": 1,
  "name": "John Silva",
  "dateOfBirth": "1990-05-15",
  "nicNumber": "199005150123",
  "phoneNumbers": ["0771234567"],
  "addresses": [
    {
      "id": 1,
      "addressLine1": "123 Main Street",
      "addressLine2": "Apt 4B",
      "cityName": "Colombo",
      "countryName": "Sri Lanka"
    }
  ],
  "familyMembers": [
    { "id": 2, "name": "Jane Silva", "nicNumber": "199208101234" }
  ]
}
```

### HTTP Status Codes

| Code | Meaning |
|---|---|
| `200 OK` | Successful GET or PUT |
| `201 Created` | Customer created successfully |
| `204 No Content` | Customer deleted successfully |
| `400 Bad Request` | Validation failed — response includes field-level errors |
| `404 Not Found` | Customer not found |
| `409 Conflict` | Duplicate NIC number |
| `500 Internal Server Error` | Unexpected server error |

---

## Bulk Upload Guide

The bulk upload feature supports creating or updating up to **1,000,000 customers** from a single `.xlsx` file.

### Excel File Format

| Column A | Column B | Column C |
|---|---|---|
| Name | Date of Birth | NIC Number |
| John Silva | 1990-05-15 | 199005150123 |
| Jane Perera | 1985-08-22 | 198508221234 |

### Rules

- **Row 1** can optionally be a header row — it is automatically detected and skipped
- **Date format** — `yyyy-MM-dd` is preferred. Also accepts `dd/MM/yyyy`, `MM/dd/yyyy`, `dd-MM-yyyy`
- **NIC must be unique** — if a NIC already exists, the record is **updated** instead of created
- **Name and NIC are mandatory** — rows missing these are counted as failed
- Blank rows are automatically skipped

### How it works

1. Upload the `.xlsx` file via the Bulk Upload page
2. The server immediately returns a **Job ID** and begins async processing
3. The frontend polls `/api/bulk-upload/status/{jobId}` every 2 seconds
4. A progress bar shows rows processed in real time
5. On completion, status shows `DONE` with processed and failed row counts

### Performance

- Rows are processed in **batches of 500** using `saveAll()` with JDBC batch inserts
- File is parsed using **Apache POI** — the full workbook is never unnecessarily held in memory
- A dedicated **async thread pool** (`BulkUpload-` prefixed threads) handles processing so the HTTP response returns instantly
- Multipart upload configured for files up to **100MB**

---

## Running Tests

```bash
cd backend
mvn test
```

### Test Coverage

| Test Class | Tests | What it covers |
|---|---|---|
| `CustomerServiceTest` | 10 | Create, read, update, delete, duplicate NIC, search, pagination |
| `CustomerControllerTest` | 10 | All REST endpoints, HTTP status codes, validation errors |
| `MasterDataControllerTest` | 1 | Master data endpoint response structure |

### Test Strategy

- **Service tests** use `@ExtendWith(MockitoExtension.class)` with mocked repositories — no database required
- **Controller tests** use `@WebMvcTest` with `MockMvc` — tests the full HTTP layer in isolation
- **Test database** uses H2 in-memory — configured separately in `src/test/resources/application.properties`

---

## Design Decisions

### Minimal DB Calls
- Master data (cities and countries) is loaded **once on app startup** via React context and cached for the session — no repeated API calls per form render
- The customer list uses a **lightweight DTO** (`CustomerListDto`) with only the fields needed for the table view — avoids loading phones, addresses, and family members unnecessarily
- Full detail is only fetched when viewing or editing a single customer

### Family Members
- Implemented as a **self-referencing many-to-many** join on the `customers` table via a `family_members` join table
- Both sides of the relationship are full customers — no separate "person" concept needed

### Bulk Upload Architecture
- Uses `@Async` with a dedicated `ThreadPoolTaskExecutor` so large file processing never blocks the HTTP thread
- The `bulk_upload_jobs` table tracks status, progress, and errors persistently — the frontend polls this rather than holding an open HTTP connection
- Supports **upsert by NIC** — existing customers are updated, new NICs are inserted

### Cascade Deletes
- `customer_phones` and `customer_addresses` use `ON DELETE CASCADE` — deleting a customer automatically removes all related phones and addresses
- `family_members` join table also cascades — no orphan records left behind

---

## License

This project is licensed under the MIT License.
