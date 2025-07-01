import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private String transactionNumber;
    private float transactionAmount;
    private String transactionType;
    private String transactionTime;
    private String transactionDate;
    private String fromAccount;
    private String toAccount;
    private String customerID;

    public Transaction() {
        this.transactionNumber = generateTransactionNumber();
        this.transactionTime = getCurrentTime();
        this.transactionDate = getCurrentDate();
    }

    public Transaction(float transactionAmount, String transactionType,
                       String fromAccount, String toAccount, String customerID) {
        this.transactionNumber = generateTransactionNumber();
        this.transactionAmount = transactionAmount;
        this.transactionType = transactionType;
        this.transactionTime = getCurrentTime();
        this.transactionDate = getCurrentDate();
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.customerID = customerID;
    }

    // Getters and Setters
    public String getTransactionNumber() { return transactionNumber; }
    public void setTransactionNumber(String transactionNumber) { this.transactionNumber = transactionNumber; }

    public float getTransactionAmount() { return transactionAmount; }
    public void setTransactionAmount(float transactionAmount) { this.transactionAmount = transactionAmount; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public String getTransactionTime() { return transactionTime; }
    public void setTransactionTime(String transactionTime) { this.transactionTime = transactionTime; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public String getFromAccount() { return fromAccount; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }

    public String getToAccount() { return toAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }

    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

    private String generateTransactionNumber() {
        return "TXN" + System.currentTimeMillis();
    }

    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public boolean recordTransaction() {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;

        try {
            String query = "INSERT INTO Transactions (TransactionNumber, TransactionAmount, TransactionType, " +
                    "TransactionTime, TransactionDate, FromAccount, ToAccount, CustomerID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            statement = connection.prepareStatement(query);
            statement.setString(1, this.transactionNumber);
            statement.setFloat(2, this.transactionAmount);
            statement.setString(3, this.transactionType);
            statement.setString(4, this.transactionTime);
            statement.setString(5, this.transactionDate);
            statement.setString(6, this.fromAccount);
            statement.setString(7, this.toAccount);
            statement.setString(8, this.customerID);

            int rows = statement.executeUpdate();
            if (rows > 0) {
                System.out.println("Transaction recorded successfully!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
        } finally {
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }

        return false;
    }

    public static List<Transaction> getAllTransactions(String customerID) {
        List<Transaction> transactions = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM Transactions WHERE CustomerID = ? ORDER BY TransactionDate DESC, TransactionTime DESC";
            statement = connection.prepareStatement(query);
            statement.setString(1, customerID);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction t = new Transaction();
                t.setTransactionNumber(resultSet.getString("TransactionNumber"));
                t.setTransactionAmount(resultSet.getFloat("TransactionAmount"));
                t.setTransactionType(resultSet.getString("TransactionType"));
                t.setTransactionTime(resultSet.getString("TransactionTime"));
                t.setTransactionDate(resultSet.getString("TransactionDate"));
                t.setFromAccount(resultSet.getString("FromAccount"));
                t.setToAccount(resultSet.getString("ToAccount"));
                t.setCustomerID(resultSet.getString("CustomerID"));
                transactions.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }

        return transactions;
    }

    public static List<Transaction> getRecentTransactions(String customerID) {
        List<Transaction> transactions = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM Transactions WHERE CustomerID = ? ORDER BY TransactionDate DESC, TransactionTime DESC LIMIT 10";
            statement = connection.prepareStatement(query);
            statement.setString(1, customerID);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction t = new Transaction();
                t.setTransactionNumber(resultSet.getString("TransactionNumber"));
                t.setTransactionAmount(resultSet.getFloat("TransactionAmount"));
                t.setTransactionType(resultSet.getString("TransactionType"));
                t.setTransactionTime(resultSet.getString("TransactionTime"));
                t.setTransactionDate(resultSet.getString("TransactionDate"));
                t.setFromAccount(resultSet.getString("FromAccount"));
                t.setToAccount(resultSet.getString("ToAccount"));
                t.setCustomerID(resultSet.getString("CustomerID"));
                transactions.add(t);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving recent transactions: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }

        return transactions;
    }

    public static List<Transaction> searchTransactions(String customerID, String startDate, String endDate) {
        List<Transaction> transactions = new ArrayList<>();
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM Transactions WHERE CustomerID = ? AND TransactionDate BETWEEN ? AND ? " +
                    "ORDER BY TransactionDate DESC, TransactionTime DESC";

            statement = connection.prepareStatement(query);
            statement.setString(1, customerID);
            statement.setString(2, startDate);
            statement.setString(3, endDate);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction t = new Transaction();
                t.setTransactionNumber(resultSet.getString("TransactionNumber"));
                t.setTransactionAmount(resultSet.getFloat("TransactionAmount"));
                t.setTransactionType(resultSet.getString("TransactionType"));
                t.setTransactionTime(resultSet.getString("TransactionTime"));
                t.setTransactionDate(resultSet.getString("TransactionDate"));
                t.setFromAccount(resultSet.getString("FromAccount"));
                t.setToAccount(resultSet.getString("ToAccount"));
                t.setCustomerID(resultSet.getString("CustomerID"));
                transactions.add(t);
            }

            System.out.println("Found " + transactions.size() + " transactions for the specified date range.");

        } catch (SQLException e) {
            System.err.println("Error searching transactions: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }

        return transactions;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionNumber='" + transactionNumber + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", transactionType='" + transactionType + '\'' +
                ", transactionTime='" + transactionTime + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", fromAccount='" + fromAccount + '\'' +
                ", toAccount='" + toAccount + '\'' +
                ", customerID='" + customerID + '\'' +
                '}';
    }
}
