import java.sql.*;

public class CheckingAccount {
    private String checkingAccountNumber;
    private String customerName;
    private float balance;
    private String customerID;

    public CheckingAccount() {}

    public CheckingAccount(String customerName, float initialDeposit, String customerID) {
        this.checkingAccountNumber = generateAccountNumber();
        this.customerName = customerName;
        this.balance = initialDeposit;
        this.customerID = customerID;
    }

    public String getCheckingAccountNumber() { return checkingAccountNumber; }
    public float getBalance() { return balance; }
    public String getCustomerID() { return customerID; }

    public void setCheckingAccountNumber(String number) { this.checkingAccountNumber = number; }
    public void setCustomerName(String name) { this.customerName = name; }
    public void setBalance(float balance) { this.balance = balance; }
    public void setCustomerID(String id) { this.customerID = id; }

    private String generateAccountNumber() {
        return "CHK" + System.currentTimeMillis();
    }

    public boolean createAccount() {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement("INSERT INTO CheckingAccount (CheckingAccountNumber, CustomerName, Balance, CustomerID) VALUES (?, ?, ?, ?)");
            ps.setString(1, checkingAccountNumber);
            ps.setString(2, customerName);
            ps.setFloat(3, balance);
            ps.setString(4, customerID);

            int rows = ps.executeUpdate();
            if (rows > 0 && balance > 0) {
                new Transaction(balance, "Initial Deposit", null, checkingAccountNumber, customerID).recordTransaction();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Create Checking Account Error: " + e.getMessage());
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
            ps = con.prepareStatement("UPDATE CheckingAccount SET Balance = Balance + ? WHERE CheckingAccountNumber = ?");
            ps.setFloat(1, amount);
            ps.setString(2, checkingAccountNumber);
            if (ps.executeUpdate() > 0) {
                balance += amount;
                new Transaction(amount, "Deposit", null, checkingAccountNumber, customerID).recordTransaction();
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
            ps = con.prepareStatement("UPDATE CheckingAccount SET Balance = Balance - ? WHERE CheckingAccountNumber = ?");
            ps.setFloat(1, amount);
            ps.setString(2, checkingAccountNumber);
            if (ps.executeUpdate() > 0) {
                balance -= amount;
                new Transaction(amount, "Withdrawal", checkingAccountNumber, null, customerID).recordTransaction();
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

    public void refreshBalance() {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = ?");
            ps.setString(1, checkingAccountNumber);
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

    public static CheckingAccount getAccountByCustomerID(String customerID) {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement("SELECT * FROM CheckingAccount WHERE CustomerID = ?");
            ps.setString(1, customerID);
            rs = ps.executeQuery();
            if (rs.next()) {
                CheckingAccount acc = new CheckingAccount();
                acc.setCheckingAccountNumber(rs.getString("CheckingAccountNumber"));
                acc.setCustomerName(rs.getString("CustomerName"));
                acc.setBalance(rs.getFloat("Balance"));
                acc.setCustomerID(rs.getString("CustomerID"));
                return acc;
            }
        } catch (SQLException e) {
            System.err.println("Fetch Checking Account Error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(rs);
            DatabaseConnection.closeStatement(ps);
            DatabaseConnection.closeConnection(con);
        }
        return null;
    }

    @Override
    public String toString() {
        return "CheckingAccount{" +
                "accountNumber='" + checkingAccountNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", balance=" + balance +
                ", customerID='" + customerID + '\'' +
                '}';
    }
}


