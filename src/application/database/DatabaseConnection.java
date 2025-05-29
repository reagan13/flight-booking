package application.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/jetsetgo";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established successfully!");
                
                // Initialize tables if they don't exist
                createUsersTable();
                createFlightsTable();
                createBookingsTable();
                createTransactionsTable();
                createSeatsTable();
                createMessagesTable();
                createNotificationsTable();

            } catch (ClassNotFoundException e) {
                System.err.println("Database Error: MySQL JDBC Driver not found");
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Create the users table if it doesn't exist
     */
    private static void createUsersTable() {
        try {
            Statement stmt = connection.createStatement();

            // Create users table with the specified fields
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "first_name VARCHAR(50) NOT NULL,"
                    + "last_name VARCHAR(50) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "age INT,"
                    + "address VARCHAR(255) NOT NULL,"
                    + "user_type ENUM('regular', 'admin') DEFAULT 'regular',"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";

            stmt.execute(createUsersTable);
            System.out.println("Users table initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing users table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create the flights table if it doesn't exist
     */
    private static void createFlightsTable() {
        try {
            Statement stmt = connection.createStatement();

            // Create flights table based on the actual table structure used in the SQL script
            String createFlightsTable = "CREATE TABLE IF NOT EXISTS flights ("
                    + "id INT PRIMARY KEY,"
                    + "flight_number VARCHAR(10),"
                    + "airline_name VARCHAR(50),"
                    + "origin VARCHAR(10),"
                    + "destination VARCHAR(10),"
                    + "departure_time DATETIME,"
                    + "arrival_time DATETIME,"
                    + "duration TIME,"
                    + "aircraft_type VARCHAR(50),"
                    + "available_seats INT,"
                    + "status VARCHAR(20),"
                    + "base_price DECIMAL(10,2),"
                    + "created_at DATETIME,"
                    + "updated_at DATETIME"
                    + ")";

            stmt.execute(createFlightsTable);
            System.out.println("Flights table initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing flights table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
     /**
     * Create the bookings table if it doesn't exist
     */
    private static void createBookingsTable() {
        try {
            Statement stmt = connection.createStatement();
            
            // Create bookings table with NULL allowed for user_id
            String createBookingsTable = "CREATE TABLE IF NOT EXISTS bookings ("
                + "id INT PRIMARY KEY AUTO_INCREMENT,"
                + "user_id INT NULL,"  // Explicitly allow NULL for guest bookings
                + "flight_id INT NOT NULL,"
                + "booking_reference VARCHAR(20) UNIQUE,"
                + "seat_number VARCHAR(5),"
                + "booking_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "status VARCHAR(20) NOT NULL DEFAULT 'confirmed'," 
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,"
                + "FOREIGN KEY (flight_id) REFERENCES flights(id)"
                + ")";
            
            stmt.execute(createBookingsTable);
            System.out.println("Bookings table initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing bookings table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    /**
     * Create the transactions table if it doesn't exist
     */
    private static void createTransactionsTable() {
        try {
            Statement stmt = connection.createStatement();

            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "booking_id INT,"
                    + "transaction_reference VARCHAR(30) UNIQUE,"
                    + "payment_method VARCHAR(20),"
                    + "payment_provider VARCHAR(50),"
                    + "amount DECIMAL(10,2),"
                    + "processing_fee DECIMAL(10,2) DEFAULT 0.00,"
                    + "total_amount DECIMAL(10,2),"
                    + "payment_status VARCHAR(20) DEFAULT 'pending',"
                    + "payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "gateway_transaction_id VARCHAR(100),"
                    + "gateway_response_code VARCHAR(20),"
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (booking_id) REFERENCES bookings(id)"
                    + ")";

            stmt.execute(createTransactionsTable);
            System.out.println("Transactions table initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing transactions table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    

    
    private static void createSeatsTable() {
        try {
            Statement stmt = connection.createStatement();

            // Create seats table to track available seats per flight
            String createSeatsTable = "CREATE TABLE IF NOT EXISTS seats ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "flight_id INT NOT NULL,"
                    + "seat_number VARCHAR(5) NOT NULL,"
                    + "seat_class VARCHAR(20) DEFAULT 'economy'," // economy, business, first
                    + "is_available BOOLEAN DEFAULT TRUE,"
                    + "is_window BOOLEAN DEFAULT FALSE,"
                    + "is_aisle BOOLEAN DEFAULT FALSE,"
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "UNIQUE KEY unique_flight_seat (flight_id, seat_number),"
                    + "FOREIGN KEY (flight_id) REFERENCES flights(id)"
                    + ")";

            stmt.execute(createSeatsTable);
            System.out.println("Seats table initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing seats table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createMessagesTable() {
        try {
            Statement stmt = connection.createStatement();

            String createMessagesTable = "CREATE TABLE IF NOT EXISTS messages ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id INT NULL,"
                    + "message_text TEXT NOT NULL,"
                    + "sender_type ENUM('user', 'bot', 'admin') NOT NULL,"
                    + "is_read BOOLEAN DEFAULT FALSE,"
                    + "reply_to INT NULL,"
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,"
                    + "FOREIGN KEY (reply_to) REFERENCES messages(id)"
                    + ")";

            stmt.execute(createMessagesTable);
            System.out.println("Messages table initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing messages table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createNotificationsTable() {
        try {
            Statement stmt = connection.createStatement();

            String createNotificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id INT NULL,"
                    + "type VARCHAR(20) NOT NULL," // 'booking' or 'message'
                    + "title VARCHAR(100) NOT NULL,"
                    + "message TEXT NOT NULL,"
                    + "related_id INT NULL," // booking_id or message_id
                    + "is_read BOOLEAN DEFAULT FALSE,"
                    + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")";

            stmt.execute(createNotificationsTable);
            System.out.println("Notifications table initialized successfully!");

        } catch (SQLException e) {
            System.err.println("Error initializing notifications table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
}