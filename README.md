# Kotlin Ktor Exposed Starter

This is a starter project for a RESTful API application, implemented in Kotlin using Ktor, Exposed, and an H2 in-memory database.

**Author:** Ebubekir Yiğit

**Objective:** The application serves as a demonstration of a simple financial transfer system, 
allowing customers to transfer funds from their wallet to a restaurant's account upon making a purchase.

---

Features
--------

- The application can transfer funds from a customer's wallet to a restaurant's account.
- The transfer operation validates that the customer has sufficient funds in their wallet and that the transfer amount does not exceed their daily limit.
- The application can display the balances in both customer and restaurant accounts.

Prerequisites
-------------

- You need to have Kotlin and Java installed on your local machine. This guide assumes you have the Java Development
  Kit (JDK) version 11 or higher installed.

---

Getting Started
---------------

### Running the Application

To start the application, run the following command in the root directory of the project:

```bash
./gradlew run
```

### Running the Tests

To run the tests, use the following command:

```bash
./gradlew test
```

### Endpoints


- `POST /transactions`: Takes the TransferRequest object and transfers the amount from the customer's account to the
  restaurant's account.

**Transaction Request:**
```json
{
    "customerId": 2,
    "restaurantId": 1,
    "amount": 3.5
}
```
**Transaction Response:**
```json
{
    "success": true,
    "data": {
        "transactionId": 1,
        "customerId": 2,
        "restaurantId": 1,
        "amount": 3.5
    }
}
```


- `GET /accounts/customer/{id}`: Returns the balance of the customer's account with the given id.
- `GET /accounts/restaurant/{id}`: Returns the balance of the restaurant's account with the given id.

**Account Balance Response:**

```json
{
    "success": true,
    "data": {
        "accountId": 2,
        "balance": 26.5
    }
}
```

---

### Project Structure

```
├── main
│   ├── kotlin
│   │   ├── database       # Database initialization, migration etc.
│   │   ├── models         # Request, Response and DB Models
│   │   ├── repositories   # Database actions, storage layer
│   │   ├── routes         # Endpoint definitions
│   │   └── services       # Business logic
└── test
    └── kotlin
        ├── api            # API tests
        └── service        # Service tests
```

Acknowledgments
---------------

This project uses the following libraries:

- [Ktor](https://ktor.io)
- [Exposed](https://github.com/JetBrains/Exposed)
- [H2 Database Engine](https://github.com/h2database/h2database)
- [HikariCP](https://github.com/brettwooldridge/HikariCP)
- [Flyway](https://flywaydb.org)
- [JUnit](https://junit.org/junit5/)
