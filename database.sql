-- Drop tables if they exist to ensure schema is correct
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS borrowers;
CREATE DATABASE IF NOT EXISTS microfinance_db;
USE microfinance_db;

-- Create the borrowers table
CREATE TABLE IF NOT EXISTS borrowers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(500),
    income DECIMAL(15,2) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS loans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    borrower_id INT NOT NULL,
    loan_amount DECIMAL(15, 2) NOT NULL,
    outstanding_balance DECIMAL(15, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'Active',
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    interest_rate DECIMAL(5, 2) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE,
    INDEX idx_borrower_id (borrower_id),
    INDEX idx_status (status),
    INDEX idx_loan_date (loan_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create the payments table (optional, for tracking individual payments)
CREATE TABLE IF NOT EXISTS payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    payment_amount DECIMAL(15, 2) NOT NULL,
    payment_date DATE NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE,
    INDEX idx_loan_id (loan_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sample data (optional)
INSERT INTO borrowers (name, email, phone, address, income) VALUES
    ('John Doe', 'john@example.com', '1234567890', '123 Main St', 40000.00),
    ('Jane Smith', 'jane@example.com', '2345678901', '456 Oak Ave', 55000.00),
    ('Bob Johnson', 'bob@example.com', '3456789012', '789 Pine Rd', 32000.00);

INSERT INTO loans (borrower_id, loan_amount, outstanding_balance, status, loan_date, due_date, interest_rate)
VALUES 
    (1, 5000.00, 3500.00, 'Active', '2024-11-01', '2025-11-01', 5.0),
    (2, 10000.00, 5000.00, 'Active', '2024-10-15', '2025-10-15', 4.5),
    (3, 7500.00, 0.00, 'Completed', '2024-06-01', '2025-06-01', 5.5);
