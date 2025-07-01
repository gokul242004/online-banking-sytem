import java.sql.*;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_system";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "mullapudi123";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            System.out.println("Database connection established successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully!");
            } catch (SQLException e) {
                System.err.println("Error closing database connection!");
                e.printStackTrace();
            }
        }
    }

    public static void closeStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement!");
                e.printStackTrace();
            }
        }
    }

    public static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet!");
                e.printStackTrace();
            }
        }
    }

    public static void initializeDatabase() {
        Connection connection = getConnection();
        Statement statement = null;

        if (connection != null) {
            try {
                statement = connection.createStatement();

                String createUsersTable = """
                    CREATE TABLE IF NOT EXISTS Users (
                        UserID VARCHAR(50) PRIMARY KEY,
                        Username VARCHAR(50) UNIQUE NOT NULL,
                        Password VARCHAR(100) NOT NULL,
                        FullName VARCHAR(100) NOT NULL,
                        Email VARCHAR(100),
                        Phone VARCHAR(20),
                        CreatedDate VARCHAR(50) NOT NULL
                    )
                """;

                String createCheckingTable = """
                    CREATE TABLE IF NOT EXISTS CheckingAccount (
                        CheckingAccountNumber VARCHAR(50) PRIMARY KEY,
                        CustomerName VARCHAR(50) NOT NULL,
                        Balance FLOAT DEFAULT 0.0,
                        CustomerID VARCHAR(50) NOT NULL,
                        FOREIGN KEY (CustomerID) REFERENCES Users(UserID)
                    )
                """;

                String createSavingsTable = """
                    CREATE TABLE IF NOT EXISTS SavingsAccount (
                        SavingsAccountNumber VARCHAR(50) PRIMARY KEY,
                        CustomerName VARCHAR(50) NOT NULL,
                        Balance FLOAT DEFAULT 0.0,
                        InterestRate FLOAT DEFAULT 2.5,
                        CustomerID VARCHAR(50) NOT NULL,
                        FOREIGN KEY (CustomerID) REFERENCES Users(UserID)
                    )
                """;

                String createTransactionsTable = """
                    CREATE TABLE IF NOT EXISTS Transactions (
                        TransactionNumber VARCHAR(50) PRIMARY KEY,
                        TransactionAmount FLOAT NOT NULL,
                        TransactionType VARCHAR(50) NOT NULL,
                        TransactionTime VARCHAR(50) NOT NULL,
                        TransactionDate VARCHAR(50) NOT NULL,
                        FromAccount VARCHAR(50),
                        ToAccount VARCHAR(50),
                        CustomerID VARCHAR(50) NOT NULL,
                        FOREIGN KEY (CustomerID) REFERENCES Users(UserID)
                    )
                """;

                statement.executeUpdate(createUsersTable);
                statement.executeUpdate(createCheckingTable);
                statement.executeUpdate(createSavingsTable);
                statement.executeUpdate(createTransactionsTable);

                System.out.println("✅ All required database tables initialized successfully!");

            } catch (SQLException e) {
                System.err.println("❌ Error initializing database tables!");
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                closeConnection(connection);
            }
        }
    }
}
