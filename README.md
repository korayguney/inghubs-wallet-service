# Inghubs Wallet Service

## 1. Project Purpose
- The Inghubs Wallet Service is a Spring Boot-based application designed to manage wallets for customers and employees. 
- It provides functionalities such as wallet creation, deposit, withdrawal, and listing wallets based on filters. 
- The service also includes basic authentication for secure access and supports role-based operations.

---

## 2. Required Technologies and Versions
To run this project, ensure the following technologies are installed:

- **Java**: Version 21
- **Maven**: Version 3.8 or higher
- **Spring Boot**: Version 3.5.3
- **Database**: H2 (embedded database)

To run the project: `mvn clean spring-boot:run`
To test the project: `mvn clean test`

---

## 3. Database Configuration
The application uses an embedded H2 database for development and testing purposes. Below are the database details:

- **URL**: `jdbc:h2:mem:testdb`
- **Username**: `inghubs`
- **Password**: (empty)

To access the H2 console, navigate to: `http://localhost:8080/h2-console`

Ensure the following settings in the H2 console:
- **Driver Class**: `org.h2.Driver`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User Name**: `inghubs`
- **Password**: (leave blank)

Initial schema and sample data are automatically loaded via `schema.sql` and `data.sql` when the application starts.

---

## 4. Basic Authentication
The application uses Basic Authentication for secure access. Below are the credentials for testing:

### Customer Credentials
- **Username**: `customer1`
- **Password**: `password`


- **Username**: `customer2`
- **Password**: `password`

### Employee Credentials
- **Username**: `admin`
- **Password**: `password`


- EMPLOYEE can perform all operations for any customer.
- CUSTOMER can only operate on their own wallets and transactions.
- To use Basic Authentication, include the credentials in the `Authorization` header of your HTTP requests. Find examples at the next part of this document.

---

## 5. API Testing with cURL
Below are example cURL requests for testing the APIs:

### Create Wallet
```bash
curl --location 'http://localhost:8080/api/wallets' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--data '{
    "walletName": "My Wallet",
    "currency": "TRY",
    "activeForShopping": true,
    "activeForWithdraw": true,
    "customerId": 1
}'
```

### List Wallets
```bash
curl --location 'http://localhost:8080/api/wallets' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
```

- This request lists all wallets. You can filter by `customerId` , `currency`, `minAmount` or `maxAmount` by adding query parameters like `?customerId=1&currency=TRY`.

### Deposit Money
```bash
curl --location 'http://localhost:8080/api/wallets/deposit' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--data '{
  "walletId": 1,
  "amount": 1400.50,
  "source": "IBAN",
  "oppositeParty": "TR1234567890987654321"
}'
```

### Withdraw Money
```bash
curl --location 'http://localhost:8080/api/wallets/withdraw' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--data '{
  "walletId": 1,
  "amount": 1123.50,
  "source": "IBAN",
  "oppositeParty": "TR1234567890987654321"
}'
```

### List Transactions
```bash
curl --location 'http://localhost:8080/api/transactions?walletId=1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
```

### Approve Transactions
```bash
curl --location 'http://localhost:8080/api/transactions/approve' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46cGFzc3dvcmQ=' \
--data '{
  "transactionId": 1,
  "status": "APPROVED"
}'
```