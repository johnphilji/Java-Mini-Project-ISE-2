package com.microfinance.model;

import java.time.LocalDate;

/**
 * Loan model class representing a loan record
 */
public class Loan {
    private int id;
    private int borrowerId;
    private String borrowerName;
    private double loanAmount;
    private double outstandingBalance;
    private String status;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private double interestRate;

    // Default constructor
    public Loan() {}

    /**
     * Constructor for new loans (without ID)
     */
    public Loan(int borrowerId, String borrowerName, double loanAmount, double outstandingBalance,
                String status, LocalDate loanDate, LocalDate dueDate, double interestRate) {
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.loanAmount = loanAmount;
        this.outstandingBalance = outstandingBalance;
        this.status = status;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.interestRate = interestRate;
    }

    /**
     * Constructor for existing loans (with ID)
     */
    public Loan(int id, int borrowerId, String borrowerName, double loanAmount, double outstandingBalance,
                String status, LocalDate loanDate, LocalDate dueDate, double interestRate) {
        this.id = id;
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.loanAmount = loanAmount;
        this.outstandingBalance = outstandingBalance;
        this.status = status;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.interestRate = interestRate;
    }

    // Getters
    public int getId() {
        return id;
    }
    public int getBorrowerId() {
        return borrowerId;
    }
    public String getBorrowerName() {
        return borrowerName;
    }
    public double getLoanAmount() {
        return loanAmount;
    }
    public double getOutstandingBalance() {
        return outstandingBalance;
    }
    public String getStatus() {
        return status;
    }
    public LocalDate getLoanDate() {
        return loanDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public double getInterestRate() {
        return interestRate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setBorrowerId(int borrowerId) {
        this.borrowerId = borrowerId;
    }
    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }
    public void setOutstandingBalance(double outstandingBalance) {
        this.outstandingBalance = outstandingBalance;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", borrowerId=" + borrowerId +
                ", borrowerName='" + borrowerName + '\'' +
                ", loanAmount=" + loanAmount +
                ", outstandingBalance=" + outstandingBalance +
                ", status='" + status + '\'' +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", interestRate=" + interestRate +
                '}';
    }
}
