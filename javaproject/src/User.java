import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * User class represents a bank customer with authentication capabilities
 * This class handles user registration, login, and user data management
 */
public class User {
    
    // Private instance variables (encapsulation)
    private String userID;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String createdDate;
    
    // Default constructor
    public User() {
        this.createdDate = getCurrentDateTime();
    }
    
    /**
     * Parameterized constructor for creating new users
     * @param username Unique username for login
     * @param password User's password
     * @param fullName Full name of the user
     * @param email Email address
     * @param phone Phone number
     */
    public User(String username, String password, String fullName, String email, String phone) {
        this.userID = generateUserID();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdDate = getCurrentDateTime();
    }
    
    // Getter and Setter methods (Encapsulation)
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    
    /**
     * Generates a unique user ID using timestamp
     * @return String representing unique user ID
     */
    private String generateUserID() {
        return "USER" + System.currentTimeMillis();
    }
    
    /**
     * Gets current date and time in formatted string
     * @return Current date-time as string
     */
    private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    
    /**
     * Registers a new user in the database
     * @return true if registration successful, false otherwise
     */
    public boolean registerUser() {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        
        try {
            // Check if username already exists
            if (isUsernameExists(this.username)) {
                System.out.println("Username already exists! Please choose a different username.");
                return false;
            }
            
            // SQL query to insert new user
            String query = """
                INSERT INTO Users (UserID, Username, Password, FullName, Email, Phone, CreatedDate) 
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            
            statement = connection.prepareStatement(query);
            statement.setString(1, this.userID);
            statement.setString(2, this.username);
            statement.setString(3, this.password); // In real application, password should be hashed
            statement.setString(4, this.fullName);
            statement.setString(5, this.email);
            statement.setString(6, this.phone);
            statement.setString(7, this.createdDate);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("User registered successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        
        return false;
    }
    
    /**
     * Authenticates user login credentials
     * @param username Username to verify
     * @param password Password to verify
     * @return User object if login successful, null if failed
     */
    public static User authenticateUser(String username, String password) {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String query = "SELECT * FROM Users WHERE Username = ? AND Password = ?";
            
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password); // In real application, compare with hashed password
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                // Create User object with retrieved data
                User user = new User();
                user.setUserID(resultSet.getString("UserID"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));
                user.setFullName(resultSet.getString("FullName"));
                user.setEmail(resultSet.getString("Email"));
                user.setPhone(resultSet.getString("Phone"));
                user.setCreatedDate(resultSet.getString("CreatedDate"));
                
                System.out.println("User authenticated successfully!");
                return user;
            } else {
                System.out.println("Invalid username or password!");
            }
            
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        
        return null;
    }
    
    /**
     * Checks if a username already exists in the database
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    private boolean isUsernameExists(String username) {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String query = "SELECT COUNT(*) FROM Users WHERE Username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        
        return false;
    }
    
    /**
     * Updates user information in the database
     * @return true if update successful, false otherwise
     */
    public boolean updateUserInfo() {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        
        try {
            String query = """
                UPDATE Users SET FullName = ?, Email = ?, Phone = ? 
                WHERE UserID = ?
            """;
            
            statement = connection.prepareStatement(query);
            statement.setString(1, this.fullName);
            statement.setString(2, this.email);
            statement.setString(3, this.phone);
            statement.setString(4, this.userID);
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("User information updated successfully!");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating user info: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        
        return false;
    }
    
    /**
     * Gets user by username
     * @param username Username to search for
     * @return User object if found, null otherwise
     */
    public static User getUserByUsername(String username) {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            String query = "SELECT * FROM Users WHERE Username = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                User user = new User();
                user.setUserID(resultSet.getString("UserID"));
                user.setUsername(resultSet.getString("Username"));
                user.setPassword(resultSet.getString("Password"));
                user.setFullName(resultSet.getString("FullName"));
                user.setEmail(resultSet.getString("Email"));
                user.setPhone(resultSet.getString("Phone"));
                user.setCreatedDate(resultSet.getString("CreatedDate"));
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResultSet(resultSet);
            DatabaseConnection.closeStatement(statement);
            DatabaseConnection.closeConnection(connection);
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
