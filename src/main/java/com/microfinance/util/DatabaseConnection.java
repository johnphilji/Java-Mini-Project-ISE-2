package com.microfinance.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection utility using HikariCP connection pooling
 */
public class DatabaseConnection {
    private static HikariDataSource dataSource;
    private static boolean connectionFailed = false;

    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            System.err.println("[ERROR] Warning: Could not initialize database connection pool: " + e.getMessage());
            e.printStackTrace();
            connectionFailed = true;
        }
    }

    /**
     * Initialize the HikariCP data source with MySQL configuration
     */
    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();
        
        // MySQL connection details - UPDATE THESE WITH YOUR SETTINGS
        config.setJdbcUrl("jdbc:mysql://localhost:3306/microfinance_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        config.setUsername("root");  // Change to your MySQL username
        config.setPassword("John@3007");  // Change to your MySQL password
        
        System.out.println("[DEBUG] Initializing database connection pool...");
        System.out.println("[DEBUG] JDBC URL: " + config.getJdbcUrl());
        System.out.println("[DEBUG] Username: " + config.getUsername());
        
        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(20000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        config.setLeakDetectionThreshold(15000);
        
        // Driver class
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        dataSource = new HikariDataSource(config);
        System.out.println("[SUCCESS] Database connection pool initialized successfully");
    }

    /**
     * Get a connection from the pool
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            System.err.println("[ERROR] DataSource is null - connection pool not initialized");
            throw new SQLException("DataSource not initialized. Make sure MySQL is running and database.sql has been executed.");
        }
        System.out.println("[DEBUG] Getting connection from pool");
        Connection conn = dataSource.getConnection();
        System.out.println("[DEBUG] Connection obtained successfully");
        return conn;
    }

    /**
     * Close the connection pool
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            System.out.println("[DEBUG] Closing connection pool");
            dataSource.close();
            System.out.println("[DEBUG] Connection pool closed");
        }
    }

    /**
     * Test the database connection
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        if (connectionFailed) {
            System.out.println("[ERROR] Connection failed during initialization");
            return false;
        }
        try (Connection conn = getConnection()) {
            boolean valid = conn.isValid(2);
            if (valid) {
                System.out.println("[SUCCESS] Database connection test passed");
            } else {
                System.out.println("[ERROR] Database connection test failed - connection not valid");
            }
            return valid;
        } catch (SQLException e) {
            System.err.println("[ERROR] Database connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if database connection is available
     * @return true if connection pool was successfully initialized
     */
    public static boolean isConnected() {
        return !connectionFailed && dataSource != null;
    }
}
