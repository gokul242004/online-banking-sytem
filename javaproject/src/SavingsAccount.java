
// === SavingsAccount.java ===

import java.sql.*;

public class SavingsAccount {
    private String savingsAccountNumber;
    private String customerName;
    private float balance;
    private float interestRate;
    private String customerID;

    public SavingsAccount() { this.interestRate = 2.5f; }

    public SavingsAccount(String customerName, float deposit, float rate, String customerID) {
        this.savingsAccountNumber = generateAccountNumber();
        this.customerName = customerName;
        this.balance = deposit;
        this.interestRate = rate;
        this.customerID = customerID;
    }

    private String generateAccountNumber() { return "SAV" + System.currentTimeMillis(); }

    public String getSavingsAccountNumber() { return savingsAccountNumber; }
    public float getBalance() { return balance; }
    public float getInterestRate() { return interestRate; }
    public String getCustomerID() { return customerID; }

    public boolean createAccount() {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO SavingsAccount (SavingsAccountNumber, CustomerName, Balance, InterestRate, CustomerID) VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, savingsAccountNumber);
            ps.setString(2, customerName);
            ps.setFloat(3, balance);
            ps.setFloat(4, interestRate);
            ps.setString(5, customerID);
            if (ps.executeUpdate() > 0 && balance > 0) {
                new Transaction(balance, "Initial Deposit", null, savingsAccountNumber, customerID).recordTransaction();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Create Savings Account Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
        return false;
    }

    public boolean deposit(float amount) {
        if (amount <= 0) return false;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE SavingsAccount SET Balance = Balance + ? WHERE SavingsAccountNumber = ?");
            ps.setFloat(1, amount);
            ps.setString(2, savingsAccountNumber);
            if (ps.executeUpdate() > 0) {
                balance += amount;
                new Transaction(amount, "Deposit", null, savingsAccountNumber, customerID).recordTransaction();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Deposit Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
        return false;
    }

    public boolean withdraw(float amount) {
        if (amount <= 0 || amount > balance) return false;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE SavingsAccount SET Balance = Balance - ? WHERE SavingsAccountNumber = ?");
            ps.setFloat(1, amount);
            ps.setString(2, savingsAccountNumber);
            if (ps.executeUpdate() > 0) {
                balance -= amount;
                new Transaction(amount, "Withdrawal", savingsAccountNumber, null, customerID).recordTransaction();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Withdraw Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
        return false;
    }

    public float calculateInterest(int months) {
        if (months <= 0) return 0.0f;
        return (balance * interestRate * months) / (12 * 100);
    }

    public boolean applyInterest(int months) {
        float interest = calculateInterest(months);
        if (interest <= 0) return false;
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("UPDATE SavingsAccount SET Balance = Balance + ? WHERE SavingsAccountNumber = ?");
            ps.setFloat(1, interest);
            ps.setString(2, savingsAccountNumber);
            if (ps.executeUpdate() > 0) {
                balance += interest;
                new Transaction(interest, "Interest Credit", null, savingsAccountNumber, customerID).recordTransaction();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Apply Interest Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
        return false;
    }

    public void refreshBalance() {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT Balance FROM SavingsAccount WHERE SavingsAccountNumber = ?");
            ps.setString(1, savingsAccountNumber);
            rs = ps.executeQuery();
            if (rs.next()) {
                balance = rs.getFloat("Balance");
            }
        } catch (SQLException e) {
            System.err.println("Balance Refresh Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
    }

    public static SavingsAccount getAccountByCustomerID(String customerID) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT * FROM SavingsAccount WHERE CustomerID = ?");
            ps.setString(1, customerID);
            rs = ps.executeQuery();
            if (rs.next()) {
                SavingsAccount acc = new SavingsAccount();
                acc.setSavingsAccountNumber(rs.getString("SavingsAccountNumber"));
                acc.setCustomerName(rs.getString("CustomerName"));
                acc.setBalance(rs.getFloat("Balance"));
                acc.setInterestRate(rs.getFloat("InterestRate"));
                acc.setCustomerID(rs.getString("CustomerID"));
                return acc;
            }
        } catch (SQLException e) {
            System.err.println("Fetch Savings Account Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
        return null;
    }

    public void setSavingsAccountNumber(String savingsAccountNumber) { this.savingsAccountNumber = savingsAccountNumber; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setBalance(float balance) { this.balance = balance; }
    public void setInterestRate(float interestRate) { this.interestRate = interestRate; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    @Override
    public String toString() {
        return "SavingsAccount{" +
                "accountNumber='" + savingsAccountNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", balance=" + balance +
                ", interestRate=" + interestRate +
                ", customerID='" + customerID + '\'' +
                '}';
    }
}
