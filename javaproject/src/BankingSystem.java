import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class BankingSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        System.out.println("=== Welcome to Online Banking System ===");
        DatabaseConnection.initializeDatabase();

        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== LOGIN MENU ===");
        System.out.println("1. Login");
        System.out.println("2. Register New User");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        int choice = getIntInput();
        switch (choice) {
            case 1 -> loginUser();
            case 2 -> registerUser();
            case 3 -> System.exit(0);
            default -> System.out.println("Invalid option! Try again.");
        }
    }

    private static void loginUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        currentUser = User.authenticateUser(username, password);
        if (currentUser != null) {
            System.out.println("Login successful! Welcome back, " + currentUser.getFullName());
        } else {
            System.out.println("Login failed. Check your credentials.");
        }
    }

    private static void registerUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        User newUser = new User(username, password, fullName, email, phone);
        if (newUser.registerUser()) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Try again.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Welcome, " + currentUser.getFullName() + "!");
        System.out.println("1. Account Management");
        System.out.println("2. Transaction Operations");
        System.out.println("3. View Transaction History");
        System.out.println("4. Account Information");
        System.out.println("5. View Account Balance");
        System.out.println("6. Update Profile");
        System.out.println("7. Export Transactions to CSV");
        System.out.println("8. Logout");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1 -> showAccountManagementMenu();
            case 2 -> showTransactionMenu();
            case 3 -> viewAllTransactions();
            case 4 -> showAccountInformation();
            case 5 -> viewBalance();
            case 6 -> updateProfile();
            case 7 -> exportTransactionsToCSV();
            case 8 -> currentUser = null;
            default -> System.out.println("Invalid option! Try again.");
        }
    }
    private static void depositMoney() {
        System.out.println("\n1. Deposit to Checking Account");
        System.out.println("2. Deposit to Savings Account");
        System.out.print("Choose account type: ");
        int type = getIntInput();
        System.out.print("Enter amount to deposit: $");
        float amount = getFloatInput();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        boolean success = false;
        float newBalance = 0f;

        if (type == 1) {
            CheckingAccount acc = CheckingAccount.getAccountByCustomerID(currentUser.getUserID());
            if (acc != null) {
                success = acc.deposit(amount);
                newBalance = acc.getBalance();
            }
        } else if (type == 2) {
            SavingsAccount acc = SavingsAccount.getAccountByCustomerID(currentUser.getUserID());
            if (acc != null) {
                success = acc.deposit(amount);
                newBalance = acc.getBalance();
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(null, "Deposit successful! New Balance: $" + newBalance);
        } else {
            JOptionPane.showMessageDialog(null, "Deposit failed.");
        }
    }

    private static void withdrawMoney() {
        System.out.println("\n1. Withdraw from Checking Account");
        System.out.println("2. Withdraw from Savings Account");
        System.out.print("Choose account type: ");
        int type = getIntInput();
        System.out.print("Enter amount to withdraw: $");
        float amount = getFloatInput();

        if (amount <= 0) {
            System.out.println("Invalid amount.");
            return;
        }

        boolean success = false;
        float newBalance = 0f;

        if (type == 1) {
            CheckingAccount acc = CheckingAccount.getAccountByCustomerID(currentUser.getUserID());
            if (acc != null) {
                success = acc.withdraw(amount);
                newBalance = acc.getBalance();
            }
        } else if (type == 2) {
            SavingsAccount acc = SavingsAccount.getAccountByCustomerID(currentUser.getUserID());
            if (acc != null) {
                success = acc.withdraw(amount);
                newBalance = acc.getBalance();
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(null, "Withdrawal successful! New Balance: $" + newBalance);
        } else {
            JOptionPane.showMessageDialog(null, "Withdrawal failed.");
        }
    }

    private static void viewBalance() {
        CheckingAccount checking = CheckingAccount.getAccountByCustomerID(currentUser.getUserID());
        SavingsAccount savings = SavingsAccount.getAccountByCustomerID(currentUser.getUserID());

        StringBuilder sb = new StringBuilder("=== Account Balances ===\n");
        if (checking != null) {
            checking.refreshBalance();
            sb.append("Checking Account: $").append(checking.getBalance()).append("\n");
        } else {
            sb.append("No checking account.\n");
        }

        if (savings != null) {
            savings.refreshBalance();
            sb.append("Savings Account: $").append(savings.getBalance());
        } else {
            sb.append("No savings account.");
        }

        JOptionPane.showMessageDialog(null, sb.toString(), "Account Balances", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void exportTransactionsToCSV() {
        List<Transaction> transactions = Transaction.getAllTransactions(currentUser.getUserID());
        if (transactions.isEmpty()) {
            System.out.println("No transactions to export.");
            return;
        }

        String fileName = "transactions_" + currentUser.getUserID() + ".csv";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("TransactionNumber,TransactionAmount,TransactionType,TransactionDate,TransactionTime,FromAccount,ToAccount\n");
            for (Transaction t : transactions) {
                writer.write(String.format("%s,%.2f,%s,%s,%s,%s,%s\n",
                    t.getTransactionNumber(),
                    t.getTransactionAmount(),
                    t.getTransactionType(),
                    t.getTransactionDate(),
                    t.getTransactionTime(),
                    t.getFromAccount() != null ? t.getFromAccount() : "",
                    t.getToAccount() != null ? t.getToAccount() : "")
                );
            }
            System.out.println("Transactions exported to " + fileName);
        } catch (IOException e) {
            System.err.println("Error exporting transactions: " + e.getMessage());
        }
    }
    private static void showAccountManagementMenu() {
        System.out.println("\n=== ACCOUNT MANAGEMENT ===");
        System.out.println("1. Create Checking Account");
        System.out.println("2. Create Savings Account");
        System.out.println("3. View Account Details");
        System.out.println("4. Back to Main Menu");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1 -> createCheckingAccount();
            case 2 -> createSavingsAccount();
            case 3 -> showAccountInformation();
            case 4 -> {}
            default -> System.out.println("Invalid option!");
        }
    }

    private static void showTransactionMenu() {
        System.out.println("\n=== TRANSACTION OPERATIONS ===");
        System.out.println("1. Deposit Money");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Calculate Interest (Savings)");
        System.out.println("4. Apply Interest (Savings)");
        System.out.println("5. Back to Main Menu");
        System.out.print("Choose an option: ");

        int choice = getIntInput();
        switch (choice) {
            case 1 -> depositMoney();
            case 2 -> withdrawMoney();
            case 3 -> calculateInterest();
            case 4 -> applyInterest();
            case 5 -> {}
            default -> System.out.println("Invalid option!");
        }
    }
private static void createCheckingAccount() {
    System.out.print("Enter initial deposit: $");
    float amount = getFloatInput();
    CheckingAccount acc = new CheckingAccount(currentUser.getFullName(), amount, currentUser.getUserID());
    if (acc.createAccount()) {
        System.out.println("Checking account created: " + acc.getCheckingAccountNumber());
    } else {
        System.out.println("Failed to create checking account.");
    }
}

private static void createSavingsAccount() {
    System.out.print("Enter initial deposit: $");
    float amount = getFloatInput();
    System.out.print("Enter interest rate (default 2.5): ");
    String rateInput = scanner.nextLine();
    float interestRate = 2.5f;
    try {
        if (!rateInput.isBlank()) {
            interestRate = Float.parseFloat(rateInput);
        }
    } catch (NumberFormatException e) {
        System.out.println("Invalid interest rate. Using default 2.5%");
    }
    SavingsAccount acc = new SavingsAccount(currentUser.getFullName(), amount, interestRate, currentUser.getUserID());
    if (acc.createAccount()) {
        System.out.println("Savings account created: " + acc.getSavingsAccountNumber());
    } else {
        System.out.println("Failed to create savings account.");
    }
}

   
    private static void calculateInterest() {
        SavingsAccount acc = SavingsAccount.getAccountByCustomerID(currentUser.getUserID());
        if (acc == null) {
            System.out.println("No savings account found.");
            return;
        }
        System.out.print("Enter number of months: ");
        int months = getIntInput();
        float interest = acc.calculateInterest(months);
        System.out.println("Calculated interest: $" + interest);
    }

    private static void applyInterest() {
        SavingsAccount acc = SavingsAccount.getAccountByCustomerID(currentUser.getUserID());
        if (acc == null) {
            System.out.println("No savings account found.");
            return;
        }
        System.out.print("Enter number of months: ");
        int months = getIntInput();
        acc.applyInterest(months);
    }

       private static void showAccountInformation() {
        CheckingAccount checking = CheckingAccount.getAccountByCustomerID(currentUser.getUserID());
        SavingsAccount savings = SavingsAccount.getAccountByCustomerID(currentUser.getUserID());

        System.out.println("\n=== ACCOUNT INFORMATION ===");
        System.out.println("Full Name: " + currentUser.getFullName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Phone: " + currentUser.getPhone());

        if (checking != null) {
            checking.refreshBalance();
            System.out.println("Checking Account #: " + checking.getCheckingAccountNumber());
            System.out.println("Balance: $" + checking.getBalance());
        }

        if (savings != null) {
            savings.refreshBalance();
            System.out.println("Savings Account #: " + savings.getSavingsAccountNumber());
            System.out.println("Balance: $" + savings.getBalance());
            System.out.println("Interest Rate: " + savings.getInterestRate() + "%");
        }
    }

    private static void updateProfile() {
        System.out.print("Enter new full name (leave blank to keep current): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) currentUser.setFullName(name);

        System.out.print("Enter new email (leave blank to keep current): ");
        String email = scanner.nextLine();
        if (!email.isBlank()) currentUser.setEmail(email);

        System.out.print("Enter new phone (leave blank to keep current): ");
        String phone = scanner.nextLine();
        if (!phone.isBlank()) currentUser.setPhone(phone);

        if (currentUser.updateUserInfo()) {
            System.out.println("Profile updated successfully.");
        } else {
            System.out.println("Profile update failed.");
        }
    }

    private static void viewAllTransactions() {
        List<Transaction> transactions = Transaction.getAllTransactions(currentUser.getUserID());
        System.out.println("\n=== TRANSACTIONS ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (Transaction t : transactions) {
                System.out.println(t);
            }
        }
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    private static float getFloatInput() {
        try {
            return Float.parseFloat(scanner.nextLine());
        } catch (Exception e) {
            return -1f;
        }
    }
}
