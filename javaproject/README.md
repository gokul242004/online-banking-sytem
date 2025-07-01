
# 🏦 Java Online Banking System

This is a simple **console-based banking system** built with **Java** and **MySQL**. It allows users to register, log in, create accounts (checking and savings), manage money (deposit, withdraw), view transaction history, and calculate interest on savings.

## 📁 Project Structure

```
javapro/
├── src/                      # Java source files
│   ├── BankingSystem.java
│   ├── DatabaseConnection.java
│   ├── User.java
│   ├── CheckingAccount.java
│   ├── SavingsAccount.java
│   ├── Transaction.java
├── lib/                      # MySQL JDBC driver
│   └── mysql-connector-java-9.3.0.jar
├── README.md                 # This file
```

## ✅ What You Can Do

- Create a new user account and log in
- Open a checking or savings account
- Deposit or withdraw money
- View your account and profile information
- View full or recent transaction history
- Calculate and apply interest to savings accounts
- (Optional) Use the graphical login/register window (GUI)

## 🧰 Requirements

Before you get started, make sure you have:

- ✅ **Java JDK 17 or higher**
- ✅ **MySQL** (Workbench or CLI)
- ✅ **MySQL Connector/J** (already provided in the `lib/` folder)

## 🧩 Set Up MySQL

1. Open **MySQL Workbench** or any SQL client.
2. Run this command to create your database:

```sql
CREATE DATABASE banking_system;
```

3. Open `DatabaseConnection.java` and check these lines:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "mullapudi123";
```

Change them if your MySQL uses a different username or password.

> ✅ All required tables will be created automatically when you run the program.

## 🚀 How to Run the Program

### 🧱 Step 1: Compile the code

Open a terminal or PowerShell in the `javapro` folder:

```bash
javac -cp ".;lib/mysql-connector-java-9.3.0.jar" src/*.java
```

### ▶️ Step 2: Run the app

#### To run the console app:

```bash
java -cp ".;lib/mysql-connector-java-9.3.0.jar;src" BankingSystem
```


## 🧪 How to Use It

| Menu | Options You’ll See |
|------|--------------------|
| Login Menu | Register, Login, Exit |
| Account Management | Create Accounts, View Account Info |
| Transaction Menu | Deposit, Withdraw, Calculate Interest |
| History | View All / Recent Transactions |
| Profile | View or Edit Your Details |

## 📝 Good to Know

- ✅ Tables are created automatically the first time you run the app
- 🔐 Each user has a unique User ID that links their accounts
- ⚠️ Usernames must be unique — no duplicates allowed

## 🖼️ Screenshots

_Add screenshots here of your app running in the terminal or GUI if needed._

## 🤝 Contribute

Feel free to suggest features or submit pull requests to improve the project!

## 📄 License

This is a free and open project for **educational** and **demonstration** use only.
