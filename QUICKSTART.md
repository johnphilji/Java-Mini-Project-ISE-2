# Microfinance Loan Tracker - Quick Start Guide

## 🎯 What's New

Your application has been completely enhanced with professional features:

### ✨ New Features
1. **Side Navigation Bar** - Easy access to all modules (Dashboard, Borrowers, Loans, Payments, Reports)
2. **KPI Dashboard** - Real-time metrics: Active Loans, Outstanding Balance, Overdue Accounts
3. **Borrower Management** - Add, view, and manage borrowers with validation
4. **EMI Calculator** - Dynamic real-time loan calculation panel
5. **Advanced Search** - Filter loans by borrower name or loan ID
6. **Overdue Tracking** - Automatic loan status updates (ACTIVE, OVERDUE, PAID_OFF)
7. **Custom Exceptions** - Better error handling with specific messages
8. **Input Validation** - All fields validated with user-friendly error messages

---

## 🚀 Running the Application

### Prerequisites
- Java 11+ installed
- MySQL 8.0+ running
- Database `microfinance_db` created (run `database.sql`)
- Maven installed

### Start the App

```bash
cd /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker
mvn clean javafx:run
```

Or compile and run:
```bash
mvn clean compile
mvn javafx:run
```

---

## 📱 User Interface Tour

### 1. Dashboard (Default View)
- **Left Sidebar**: Navigation menu with 5 main sections
- **KPI Cards**: Three prominent metrics at the top
- **Loans Table**: All loans with search functionality
- **Features**:
  - Search by borrower name or loan ID
  - View all active loans
  - Click to select for payment recording

### 2. Borrower Management 👥
- **Add New Borrower** button opens validation form
- **Required Fields**:
  - First Name (cannot be empty)
  - Last Name (cannot be empty)
  - Phone (10-15 digits, e.g., 555-123-4567)
  - Address
  - Income (positive number)
- **Borrowers Table** displays all registered borrowers

### 3. Loan Management 💳
- **Issue New Loan** button with:
  - **Left Panel**: Loan input fields
  - **Right Panel**: Real-time EMI Calculator
    - Shows EMI amount per month
    - Total interest payable
    - Total amount payable
- **Validation**:
  - Amount: $1-$100,000
  - Interest Rate: 0-100%
  - Tenure: > 0 months
- **Loans Table**: All loans with status (ACTIVE/OVERDUE/PAID_OFF)

### 4. Payment Recording 💰
1. Select a loan from the table
2. Click "Record Payment"
3. Enter payment amount
4. Outstanding balance updates automatically
5. Next due date advances by one month

### 5. Reports & Analytics 📈
- **Metrics Cards**:
  - Total Loaned
  - Outstanding Balance
  - Amount Repaid
  - Active Loans
  - Completed Loans
  - Average Loan Size
- **Pie Charts**:
  - Loan Status Distribution (Active/Overdue/Paid Off)
  - Outstanding vs Repaid amount

---

## 🔧 Key Classes

### Business Logic
- **LoanCalculationService**: EMI calculations and validation
- **LoanService**: Loan operations and status tracking
- **BorrowerDAO**: Borrower database operations

### Data Models
- **Loan**: Loan records with outstanding_balance and next_due_date
- **Borrower**: Borrower information with income tracking

### Custom Exceptions
- **InvalidLoanAmountException**: For invalid loan amounts
- **BorrowerNotFoundException**: For borrower lookup failures

---

## ✅ Validation Rules

### Borrower Fields
| Field | Rule | Example |
|-------|------|---------|
| First Name | Cannot be empty | John |
| Last Name | Cannot be empty | Doe |
| Phone | 10-15 digits | 555-123-4567 |
| Address | Any text | 123 Main St |
| Income | Positive number | 50000 |

### Loan Fields
| Field | Rule | Example |
|-------|------|---------|
| Amount | $1-$100,000 | 50000 |
| Interest | 0-100% | 5.5 |
| Tenure | > 0 months | 12 |

---

## 💡 Usage Tips

### Issuing a Loan
1. Go to **Loans** → **Issue New Loan**
2. Enter borrower name and loan details
3. Watch EMI Calculator update in real-time
4. Click OK to save

### Recording a Payment
1. Go to **Payments**
2. Select a loan from the table
3. Click **Record Payment**
4. Enter payment amount
5. Outstanding balance updates instantly

### Finding Loans
1. Use the search bar in Dashboard/Loans view
2. Type borrower name (partial match works)
3. Or type loan ID number
4. Results filter in real-time

### Checking Overdue Loans
1. Go to **Dashboard**
2. Check **⚠️ Overdue Accounts** KPI card
3. Go to **Loans** to see which loans are marked OVERDUE

---

## 🎨 Color Scheme

- **Sidebar**: Dark gray (#2c3e50) - Professional, easy on eyes
- **Active Loans**: Blue (#3498db) - Calm and informative
- **Outstanding**: Red (#e74c3c) - Attention-grabbing
- **Overdue**: Orange (#f39c12) - Warning color
- **Completed**: Green (#2ecc71) - Success color
- **Background**: Light gray (#ecf0f1) - Clean and readable

---

## 🐛 Troubleshooting

### "Database Offline" Error
- Ensure MySQL is running
- Check database credentials in `DatabaseConnection.java`
- Verify `microfinance_db` database exists
- Click "Retry Connection" button

### Validation Errors
- Check error message in alert popup
- Correct the field according to error
- Try again

### EMI Calculator Not Working
- Ensure all three fields (Amount, Rate, Tenure) have valid numbers
- Calculator updates as you type
- Decimal values are supported

---

## 📊 Example Workflow

### Complete Scenario
1. **Create Borrower**
   - Name: John Smith
   - Phone: 555-123-4567
   - Income: $75,000

2. **Issue Loan**
   - Amount: $50,000
   - Rate: 5% annual
   - Tenure: 12 months
   - EMI: ~$4,344/month

3. **Record Payment**
   - Payment: $4,344
   - Outstanding reduces to $45,656
   - Next due date: 1 month later

4. **Check Dashboard**
   - Active Loans: 1
   - Outstanding: $45,656
   - Overdue: 0 (if paid on time)

---

## 📁 Project Structure

```
MicrofinanceLoanTracker/
├── src/main/java/com/microfinance/
│   ├── App.java                          (Main UI)
│   ├── model/
│   │   ├── Loan.java                     (Loan model)
│   │   └── Borrower.java                 (Borrower model)
│   ├── dao/
│   │   ├── LoanDAO.java                  (Loan operations)
│   │   └── BorrowerDAO.java              (Borrower operations)
│   ├── service/
│   │   ├── LoanService.java              (Business logic)
│   │   └── LoanCalculationService.java   (Calculations)
│   ├── exception/
│   │   ├── InvalidLoanAmountException.java
│   │   └── BorrowerNotFoundException.java
│   └── util/
│       └── DatabaseConnection.java       (DB config)
├── database.sql                          (Schema)
└── pom.xml                               (Maven config)
```

---

## 🎓 Financial Formulas Used

### EMI Calculation
```
EMI = P × r × (1+r)^n / ((1+r)^n - 1)

Where:
P = Principal amount
r = Monthly interest rate (Annual% / 12 / 100)
n = Number of months
```

### Total Interest
```
Total Interest = (EMI × Tenure) - Principal
```

### Total Payable
```
Total Payable = EMI × Tenure
```

---

## 🔐 Security Notes

- Database credentials are in `DatabaseConnection.java`
- Update username/password for production
- Use environment variables for credentials
- Connection pooling via HikariCP (10 max connections)

---

## 📞 Support

For issues or questions:
1. Check the error message in the alert
2. Review the troubleshooting section
3. Verify all prerequisites are installed
4. Check database connection status

---

## ✨ What's Next?

Future enhancements could include:
- Email notifications for due payments
- SMS alerts for overdue loans
- Advanced reporting (PDF export)
- Multi-currency support
- User authentication
- Loan amortization schedules

---

**Version**: 2.0  
**Last Updated**: November 2025  
**Status**: Production Ready ✅
