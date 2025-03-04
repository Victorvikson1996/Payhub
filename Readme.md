# PayHub - Kotlin/Spring Boot Fintech Backend

PayHub is a monolithic fintech backend built with Kotlin and Spring Boot, designed to provide a robust platform for financial operations including user authentication, wallet management, card issuance, bank account generation, savings, and transaction tracking with receipts.

## Features
- **User Authentication**: Register and login with JWT-based authentication.
- **Wallet Management**: Add money, check balance, set spending limits.
- **Card Issuance**: Generate virtual cards with configurable limits.
- **Bank Account Issuance**: Create test IBANs and BIC for SEPA compliance.
- **Savings**: Manage savings accounts with timestamps.
- **Transactions**: Track all operations with states (Pending, Processing, Sent, Received) and timestamps.
- **Receipts**: Generate receipts for transactions and actions.
- **Notifications**: Event-driven messaging via Kafka.

## Architecture
PayHub follows a **hexagonal architecture** (ports and adapters):
- **Domain**: Core entities and exceptions (e.g., `User`, `Transaction`).
- **Application**: Business logic services (e.g., `AuthService`, `WalletService`).
- **Infrastructure**: External system integrations (PostgreSQL, Redis, Kafka).
- **Adapters**: REST controllers for HTTP endpoints.

## Prerequisites
- **Java 17**: Install via Homebrew (`brew install temurin17`) on macOS.
- **Gradle**: Install via Homebrew (`brew install gradle`).
- **Docker**: Install Docker Desktop for M1 Macs from [docker.com](https://www.docker.com/products/docker-desktop/).

## Project Structure