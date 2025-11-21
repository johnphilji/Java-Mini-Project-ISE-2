# Troubleshooting: Loan Not Saving to Database

## Steps to Debug:

### 1. Check if MySQL is running
```bash
mysql -u root -p12345678 -e "SELECT 1"
```
If this fails, start MySQL:
```bash
brew services start mysql
```

### 2. Verify the database exists
```bash
mysql -u root -p12345678 -e "USE microfinance_db; SHOW TABLES;"
```

You should see: `loans` and `payments` tables

### 3. Run the application with debug output
```bash
cd /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker
mvn javafx:run 2>&1 | tee app.log
```

This will save all debug output to `app.log`

### 4. Look for these debug messages in the output:

**Connection Initialization:**
- `[DEBUG] Initializing database connection pool...`
- `[SUCCESS] Database connection pool initialized successfully`

**Adding a Loan:**
- `[DEBUG] Executing INSERT query with parameters:`
- `[DEBUG] Rows inserted: 1` (should be 1 if successful)
- `[SUCCESS] Loan added successfully to database`

**Retrieving Loans:**
- `[DEBUG] Executing SELECT all loans query`
- `[DEBUG] Retrieved X loans from database`

### 5. If you see errors:

**Connection Error:**
- Check MySQL is running
- Verify credentials in DatabaseConnection.java match your MySQL setup
- Check that microfinance_db exists

**Query Error:**
- Check the exact error message from the logs
- Verify table structure with: `DESCRIBE loans;`

### 6. Manual database verification
```bash
mysql -u root -p12345678
USE microfinance_db;
SELECT COUNT(*) FROM loans;
SELECT * FROM loans;
```

## Common Issues:

| Issue | Solution |
|-------|----------|
| "Access denied for user 'root'" | Check password in DatabaseConnection.java |
| "Unknown database 'microfinance_db'" | Run database.sql in MySQL Workbench |
| "Table 'loans' doesn't exist" | Execute database.sql again |
| "Can't get a connection, pool error" | MySQL not running, start with `brew services start mysql` |

## Quick Test:

Once you run the app with debug logging, try adding a loan and share the console output from this section:
1. When the app starts
2. When you click "Add Loan"
3. When you click OK to save

This will help us identify exactly where the issue is!
