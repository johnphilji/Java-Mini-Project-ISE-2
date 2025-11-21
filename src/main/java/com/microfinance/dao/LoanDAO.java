package com.microfinance.dao;

import com.microfinance.model.Loan;
import com.microfinance.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Loan operations
 */
public class LoanDAO {

    /**
     * Add a new loan to the database
     */
    public boolean addLoan(Loan loan) {
        String sql = "INSERT INTO loans (borrower_id, loan_amount, outstanding_balance, status, loan_date, due_date, interest_rate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getBorrowerId());
            stmt.setDouble(2, loan.getLoanAmount());
            stmt.setDouble(3, loan.getOutstandingBalance());
            stmt.setString(4, loan.getStatus());
            stmt.setDate(5, java.sql.Date.valueOf(loan.getLoanDate()));
            stmt.setDate(6, java.sql.Date.valueOf(loan.getDueDate()));
            stmt.setDouble(7, loan.getInterestRate());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to add loan to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all loans from the database
     */
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.name AS borrower_name FROM loans l JOIN borrowers b ON l.borrower_id = b.id ORDER BY l.loan_date DESC";
        System.out.println("[DEBUG] Executing SQL: " + sql);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("id"),
                    rs.getInt("borrower_id"),
                    rs.getString("borrower_name"),
                    rs.getDouble("loan_amount"),
                    rs.getDouble("outstanding_balance"),
                    rs.getString("status"),
                    rs.getDate("loan_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    rs.getDouble("interest_rate")
                );
                loans.add(loan);
                System.out.println("[DEBUG] Retrieved Loan: " + loan);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving loans: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }

    /**
     * Get a loan by ID
     */
    public Loan getLoanById(int id) {
        String sql = "SELECT l.*, b.name AS borrower_name FROM loans l JOIN borrowers b ON l.borrower_id = b.id WHERE l.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Loan(
                    rs.getInt("id"),
                    rs.getInt("borrower_id"),
                    rs.getString("borrower_name"),
                    rs.getDouble("loan_amount"),
                    rs.getDouble("outstanding_balance"),
                    rs.getString("status"),
                    rs.getDate("loan_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    rs.getDouble("interest_rate")
                );
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving loan: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Record a payment for a loan
     */
    public boolean recordPayment(int loanId, double paymentAmount) {
        String sql = "UPDATE loans SET outstanding_balance = outstanding_balance - ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, paymentAmount);
            stmt.setInt(2, loanId);
            
            int rowsUpdated = stmt.executeUpdate();
            
            // Update status to "Completed" if balance is 0 or less
            if (rowsUpdated > 0) {
                updateLoanStatusIfPaid(loanId);
            }
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error recording payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update loan status to "Completed" if fully paid
     */
    private void updateLoanStatusIfPaid(int loanId) {
        String sql = "UPDATE loans SET status = 'Completed' WHERE id = ? AND outstanding_balance <= 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, loanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERROR] Error updating loan status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Update a loan
     */
    public boolean updateLoan(Loan loan) {
        String sql = "UPDATE loans SET borrower_id = ?, loan_amount = ?, outstanding_balance = ?, status = ?, loan_date = ?, due_date = ?, interest_rate = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, loan.getBorrowerId());
            stmt.setDouble(2, loan.getLoanAmount());
            stmt.setDouble(3, loan.getOutstandingBalance());
            stmt.setString(4, loan.getStatus());
            stmt.setDate(5, java.sql.Date.valueOf(loan.getLoanDate()));
            stmt.setDate(6, java.sql.Date.valueOf(loan.getDueDate()));
            stmt.setDouble(7, loan.getInterestRate());
            stmt.setInt(8, loan.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error updating loan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a loan
     */
    public boolean deleteLoan(int id) {
        String sql = "DELETE FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error deleting loan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get active loans only
     */
    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.name AS borrower_name FROM loans l JOIN borrowers b ON l.borrower_id = b.id WHERE l.status = 'Active' ORDER BY l.loan_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("id"),
                    rs.getInt("borrower_id"),
                    rs.getString("borrower_name"),
                    rs.getDouble("loan_amount"),
                    rs.getDouble("outstanding_balance"),
                    rs.getString("status"),
                    rs.getDate("loan_date").toLocalDate(),
                    rs.getDate("due_date").toLocalDate(),
                    rs.getDouble("interest_rate")
                );
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving active loans: " + e.getMessage());
            e.printStackTrace();
        }
        return loans;
    }
}
