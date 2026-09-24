# Java Bank System

A command-line banking application written in Java. It uses object-oriented design (inheritance) and plain text files for storage, with no database.

## Features

**Accounts and users**
- Register a new account or log in
- Two user roles: **Customer** and **Banker**
- Customers can open a Savings account, a Checking account, or both
- Passwords are hashed with SHA-256

**Banking operations**
- Deposit and withdraw
- Transfer between your own accounts (Savings and Checking)
- Transfer to another customer by ID

**Overdraft protection**
- A $35 fee is charged when a withdrawal leaves the balance negative
- Withdrawals over $100 are blocked while the balance is negative
- The account is deactivated after 2 overdrafts
- The account is reactivated once the balance is back to zero or above

**Card types and daily limits**

| Card       | Withdraw | Transfer | Transfer (own) | Deposit | Deposit (own) |
|------------|----------|----------|----------------|---------|---------------|
| Platinum   | $20,000  | $40,000  | $80,000        | $100,000 | $200,000     |
| Titanium   | $10,000  | $20,000  | $40,000        | $100,000 | $200,000     |
| Mastercard | $5,000   | $10,000  | $20,000        | $100,000 | $200,000     |

**Transaction history**
- Every transaction is logged to a separate file per customer (date, type, balance after)
- Filter history by today, last 7 days, or last 30 days

**Security**
- Fraud detection: 3 failed logins locks the account for 1 minute

**Banker menu**
- View all customers
- Search a customer by ID
- View a customer's transaction history

## Project Structure

| File | Purpose |
|------|---------|
| `Main.java` | Main loop, menus (register, login, customer, banker) |
| `User.java` | Abstract base class for all users |
| `Customer.java` | Customer subclass (holds Savings and Checking accounts) |
| `Banker.java` | Banker subclass |
| `Account.java` | Account type, balance, card type, overdraft state |
| `UserFileManager.java` | Saves and loads user files |
| `TransactionLogger.java` | Logs transactions and calculates daily totals |

## How to Run

1. Open the project in IntelliJ IDEA (or any Java IDE).
2. Run `Main.java`.
3. Or from the terminal:

```bash
javac *.java
java Main
```

## Data Storage

Each user has their own text file inside the `user` folder, named:

- `Customer-<Name>-<ID>.txt`
- `Banker-<Name>-<ID>.txt`

Transaction history is stored in a separate file per customer.

## Author

Your Name
