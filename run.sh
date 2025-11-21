#!/bin/bash

# Microfinance Loan Tracker Startup Script

echo "================================"
echo "Microfinance Loan Tracker"
echo "================================"
echo ""

# Check if MySQL is running
echo "Checking MySQL connection..."
mysql -u root -p12345678 -e "SELECT 1" > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo "✓ MySQL is running"
else
    echo "✗ MySQL is not running or credentials are incorrect"
    echo ""
    echo "To fix this:"
    echo "1. Start MySQL: brew services start mysql"
    echo "2. Update credentials in: src/main/java/com/microfinance/util/DatabaseConnection.java"
    echo ""
fi

echo ""
echo "Starting application..."
echo ""

# Build and run the application
mvn clean javafx:run
