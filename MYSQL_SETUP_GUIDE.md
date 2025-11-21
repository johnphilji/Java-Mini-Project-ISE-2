# MySQL Workbench Integration Setup Guide

## Overview
Your Microfinance Loan Tracker is now fully integrated with MySQL. All loan data will be persisted in a MySQL database instead of being stored in memory.

## Prerequisites
- MySQL Server installed and running
- MySQL Workbench installed
- Java 17 or higher

## Step 1: Create the Database Schema

### Option A: Using MySQL Workbench GUI
1. Open MySQL Workbench
2. Click on "File" → "Open SQL Script" and select `database.sql` from your project root
3. Execute the script (Ctrl+Enter or click the Execute button)

### Option B: Using MySQL Command Line
```bash
mysql -u root -p < /path/to/database.sql
```

This will create:
- **Database**: `microfinance_db`
- **Table**: `loans` - stores all loan records
- **Table**: `payments` - tracks individual payments (optional)

## Step 2: Configure Database Credentials

Edit the file: `src/main/java/com/microfinance/util/DatabaseConnection.java`

Update lines 18-19 with your MySQL credentials:
```java
config.setJdbcUrl("jdbc:mysql://localhost:3306/microfinance_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
config.setUsername("root");        // Your MySQL username
config.setPassword("password");    // Your MySQL password
```

## Step 3: Compile the Project

```bash
mvn clean compile
```

## Step 4: Run the Application

```bash
mvn javafx:run
```

Or use the run.sh script:
```bash
./run.sh
```

## Database Schema Details

### loans Table
```
id                    INT (Primary Key, Auto Increment)
borrower_name         VARCHAR(255)
loan_amount           DECIMAL(15,2)
outstanding_balance   DECIMAL(15,2)
status                VARCHAR(50) - 'Active' or 'Completed'
loan_date             DATE
due_date              DATE
interest_rate         DECIMAL(5,2)
created_at            TIMESTAMP
updated_at            TIMESTAMP
```

### payments Table (Optional)
```
id              INT (Primary Key, Auto Increment)
loan_id         INT (Foreign Key)
payment_amount  DECIMAL(15,2)
payment_date    DATE
notes           VARCHAR(500)
created_at      TIMESTAMP
```

## Application Features

### Add Loan
- Enter borrower name, loan amount, due date, and interest rate
- Data is immediately saved to MySQL database

### View Loans
- Displays all loans from the database
- Shows borrower name, loan amount, outstanding balance, and status

### Record Payment
- Select a loan and record a payment
- Outstanding balance is automatically updated
- Loan status changes to "Completed" when fully paid

### Reports
- View total loans issued, outstanding balance, and repaid amounts
- See count of active loans

### Delete Loan
- Select a loan and delete it (with confirmation)
- All associated payment records are automatically deleted

## Connection Details

The application uses:
- **HikariCP** for connection pooling (efficient resource management)
- **MySQL Connector/J** 8.0.33 for JDBC connectivity
- Connection pool settings:
  - Max pool size: 10 connections
  - Min idle connections: 2
  - Connection timeout: 20 seconds

## Troubleshooting

### "Failed to connect to database" error
1. Verify MySQL server is running: `mysql -u root -p`
2. Check database credentials in DatabaseConnection.java
3. Ensure `database.sql` schema has been executed
4. Check that port 3306 is open and accessible

### "Unknown database" error
Run the `database.sql` script again to create the database

### Connection pool errors
- Increase `setMaximumPoolSize()` if handling many concurrent requests
- Check MySQL `max_connections` setting

## Files Created/Modified

### New Files:
- `src/main/java/com/microfinance/util/DatabaseConnection.java` - Connection pool management
- `src/main/java/com/microfinance/model/Loan.java` - Loan data model
- `src/main/java/com/microfinance/dao/LoanDAO.java` - Data access layer
- `database.sql` - Database schema

### Modified Files:
- `src/main/java/com/microfinance/App.java` - Updated to use database layer
- `pom.xml` - MySQL driver and HikariCP dependencies included

## Next Steps

1. Run `database.sql` in MySQL Workbench
2. Update credentials in DatabaseConnection.java
3. Compile and run the application
4. All loan data will now persist in MySQL

Good to go! 🚀
