package com.microfinance.service;

import com.microfinance.dao.LoanDAO;
import com.microfinance.exception.BorrowerNotFoundException;
import com.microfinance.exception.InvalidLoanAmountException;
import com.microfinance.model.Loan;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic service for loan operations
 * Handles loan creation, validation, status tracking, and overdue calculations
 */
public class LoanService {
    private final LoanDAO loanDAO;

    public LoanService(LoanDAO loanDAO) {
        this.loanDAO = loanDAO;
    }

    /**
     * Issue a new loan with validation
     *
     * @param borrowerId The ID of the borrower
     * @param borrowerName The name of the borrower
     * @param loanAmount The loan principal amount
     * @param interestRate Annual interest rate
     * @param tenureMonths Loan tenure in months
     * @return The created Loan object
     * @throws InvalidLoanAmountException if loan amount is invalid
     * @throws BorrowerNotFoundException if borrower is not found
     */
    public Loan issueLoan(int borrowerId, String borrowerName, double loanAmount, double interestRate, int tenureMonths)
            throws InvalidLoanAmountException, BorrowerNotFoundException {
        // Validate loan amount
        if (!LoanCalculationService.isValidLoanAmount(loanAmount)) {
            throw new InvalidLoanAmountException(LoanCalculationService.getInvalidLoanAmountMessage());
        }
        // Validate interest rate
        if (!LoanCalculationService.isValidInterestRate(interestRate)) {
            throw new InvalidLoanAmountException("Interest rate must be between 0 and 100 percent");
        }
        // Validate tenure
        if (tenureMonths <= 0) {
            throw new InvalidLoanAmountException("Loan tenure must be greater than 0 months");
        }
        // Validate borrower exists
        if (borrowerId <= 0 || borrowerName == null || borrowerName.trim().isEmpty()) {
            throw new BorrowerNotFoundException("Valid borrower must be selected");
        }
        // Create the loan with current date as loan date
        LocalDate loanDate = LocalDate.now();
        LocalDate nextDueDate = loanDate.plusMonths(1);
        Loan loan = new Loan(
            borrowerId,
            borrowerName,
            loanAmount,
            loanAmount,
            "ACTIVE",
            loanDate,
            nextDueDate,
            interestRate
        );
        // Save to database
        if (loanDAO.addLoan(loan)) {
            return loan;
        } else {
            throw new BorrowerNotFoundException("Failed to create loan in database");
        }
    }

    /**
     * Determine if a loan is overdue
     * A loan is OVERDUE if current date > next_due_date AND outstanding_balance > 0
     *
     * @param loan The loan to check
     * @return true if loan is overdue, false otherwise
     */
    public boolean isLoanOverdue(Loan loan) {
        if (loan == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return today.isAfter(loan.getDueDate()) && loan.getOutstandingBalance() > 0;
    }

    /**
     * Determine the loan status based on business logic
     * OVERDUE: Current Date > next_due_date AND outstanding_balance > 0
     * PAID_OFF: outstanding_balance = 0
     * ACTIVE: Otherwise
     *
     * @param loan The loan to check
     * @return The appropriate status string
     */
    public String determineLoanStatus(Loan loan) {
        if (loan == null) {
            return "UNKNOWN";
        }

        if (loan.getOutstandingBalance() <= 0) {
            return "PAID_OFF";
        }

        if (isLoanOverdue(loan)) {
            return "OVERDUE";
        }

        return "ACTIVE";
    }

    /**
     * Record a payment and update loan details
     * Apportion payment into principal and interest
     *
     * @param loanId The ID of the loan
     * @param paymentAmount The payment amount
     * @return true if payment recorded successfully
     * @throws IllegalArgumentException if payment amount is invalid
     */
    public boolean recordPayment(int loanId, double paymentAmount) throws IllegalArgumentException {
        if (paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        Loan loan = loanDAO.getLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found with ID: " + loanId);
        }

        // Record the payment in database
        boolean success = loanDAO.recordPayment(loanId, paymentAmount);

        if (success) {
            // Update the next due date for next month's payment
            Loan updatedLoan = loanDAO.getLoanById(loanId);
            if (updatedLoan != null && updatedLoan.getOutstandingBalance() > 0) {
                updatedLoan.setDueDate(updatedLoan.getDueDate().plusMonths(1));
                loanDAO.updateLoan(updatedLoan);
            }
        }

        return success;
    }

    /**
     * Get all overdue loans
     *
     * @return List of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        List<Loan> allLoans = loanDAO.getAllLoans();
        return allLoans.stream().filter(this::isLoanOverdue).toList();
    }

    /**
     * Get all active loans (not fully paid)
     *
     * @return List of active loans
     */
    public List<Loan> getActiveLoans() {
        return loanDAO.getActiveLoans();
    }

    /**
     * Get total outstanding balance for all loans
     *
     * @return Total outstanding balance
     */
    public double getTotalOutstandingBalance() {
        List<Loan> allLoans = loanDAO.getAllLoans();
        return allLoans.stream().mapToDouble(Loan::getOutstandingBalance).sum();
    }

    /**
     * Get count of overdue accounts
     *
     * @return Number of overdue loans
     */
    public long getOverdueAccountCount() {
        return getOverdueLoans().size();
    }

    /**
     * Get total principal outstanding (same as getTotalOutstandingBalance)
     *
     * @return Total principal outstanding
     */
    public double getTotalPrincipalOutstanding() {
        return getTotalOutstandingBalance();
    }
}
