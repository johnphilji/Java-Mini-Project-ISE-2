package com.microfinance.service;

/**
 * Utility service for loan calculations
 * Implements EMI calculation and other financial formulas
 */
public class LoanCalculationService {

    private static final double MAX_LOAN_AMOUNT = 100000.0;
    private static final double MIN_LOAN_AMOUNT = 1.0;

    /**
     * Calculate Equated Monthly Installment (EMI) using standard amortization formula
     * EMI = P × r × (1+r)^n / ((1+r)^n - 1)
     * where:
     *   P = Principal amount
     *   r = Monthly interest rate (annual rate / 12 / 100)
     *   n = Number of months (tenure)
     *
     * @param principal The loan principal amount
     * @param annualInterestRate The annual interest rate (in percentage)
     * @param tenureMonths The loan tenure in months
     * @return The calculated EMI amount
     */
    public static double calculateEMI(double principal, double annualInterestRate, int tenureMonths) {
        if (principal <= 0 || tenureMonths <= 0 || annualInterestRate < 0) {
            throw new IllegalArgumentException("Invalid parameters for EMI calculation");
        }

        // Convert annual interest rate to monthly decimal rate
        double monthlyRate = annualInterestRate / 12.0 / 100.0;

        // If interest rate is 0, simple division
        if (monthlyRate == 0) {
            return principal / tenureMonths;
        }

        // Standard EMI formula
        double numerator = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths);
        double denominator = Math.pow(1 + monthlyRate, tenureMonths) - 1;

        return numerator / denominator;
    }

    /**
     * Calculate total interest payable on a loan
     *
     * @param emi The monthly EMI amount
     * @param tenureMonths The loan tenure in months
     * @param principal The loan principal amount
     * @return Total interest payable (EMI × Tenure - Principal)
     */
    public static double calculateTotalInterest(double emi, int tenureMonths, double principal) {
        return (emi * tenureMonths) - principal;
    }

    /**
     * Calculate total payable amount
     *
     * @param emi The monthly EMI amount
     * @param tenureMonths The loan tenure in months
     * @return Total amount payable (EMI × Tenure)
     */
    public static double calculateTotalPayable(double emi, int tenureMonths) {
        return emi * tenureMonths;
    }

    /**
     * Validate loan amount
     *
     * @param loanAmount The loan amount to validate
     * @return true if loan amount is valid, false otherwise
     */
    public static boolean isValidLoanAmount(double loanAmount) {
        return loanAmount >= MIN_LOAN_AMOUNT && loanAmount <= MAX_LOAN_AMOUNT;
    }

    /**
     * Get error message for invalid loan amount
     *
     * @return Error message describing valid loan amount range
     */
    public static String getInvalidLoanAmountMessage() {
        return String.format("Loan amount must be between $%.2f and $%.2f", MIN_LOAN_AMOUNT, MAX_LOAN_AMOUNT);
    }

    /**
     * Validate phone number format (basic validation)
     * Accepts 10-15 digits, possibly with common separators
     *
     * @param phoneNumber The phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // Remove common separators
        String cleaned = phoneNumber.replaceAll("[\\s\\-().]", "");
        // Check if it's 10-15 digits
        return cleaned.matches("\\d{10,15}");
    }

    /**
     * Validate income amount
     *
     * @param income The income to validate
     * @return true if income is positive, false otherwise
     */
    public static boolean isValidIncome(double income) {
        return income > 0;
    }

    /**
     * Validate interest rate
     *
     * @param interestRate The interest rate to validate
     * @return true if interest rate is valid (0-100), false otherwise
     */
    public static boolean isValidInterestRate(double interestRate) {
        return interestRate >= 0 && interestRate <= 100;
    }
}
