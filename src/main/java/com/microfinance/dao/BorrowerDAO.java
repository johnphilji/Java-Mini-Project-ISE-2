package com.microfinance.dao;

import com.microfinance.model.Borrower;
import com.microfinance.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Borrower operations
 */
public class BorrowerDAO {

    /**
     * Add a new borrower to the database
     */
    public boolean addBorrower(Borrower borrower) {
        String sql = "INSERT INTO borrowers (name, email, phone, address, income) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, borrower.getName());
            stmt.setString(2, borrower.getEmail());
            stmt.setString(3, borrower.getPhone());
            stmt.setString(4, borrower.getAddress());
            stmt.setDouble(5, borrower.getIncome());
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to add borrower: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all borrowers from the database
     */
    public List<Borrower> getAllBorrowers() {
        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM borrowers ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Borrower borrower = new Borrower(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getDouble("income")
                );
                borrowers.add(borrower);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving borrowers: " + e.getMessage());
            e.printStackTrace();
        }
        return borrowers;
    }

    /**
     * Get a borrower by ID
     */
    public Borrower getBorrowerById(int id) {
        String sql = "SELECT * FROM borrowers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Borrower(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getDouble("income")
                );
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error retrieving borrower: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update a borrower
     */
    public boolean updateBorrower(Borrower borrower) {
        String sql = "UPDATE borrowers SET name = ?, email = ?, phone = ?, address = ?, income = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, borrower.getName());
            stmt.setString(2, borrower.getEmail());
            stmt.setString(3, borrower.getPhone());
            stmt.setString(4, borrower.getAddress());
            stmt.setDouble(5, borrower.getIncome());
            stmt.setInt(6, borrower.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error updating borrower: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a borrower
     */
    public boolean deleteBorrower(int id) {
        String sql = "DELETE FROM borrowers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR] Error deleting borrower: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Search borrowers by name
     */
    public List<Borrower> searchBorrowersByName(String name) {
        List<Borrower> borrowers = new ArrayList<>();
        String sql = "SELECT * FROM borrowers WHERE name LIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Borrower borrower = new Borrower(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getDouble("income")
                );
                borrowers.add(borrower);
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Error searching borrowers: " + e.getMessage());
            e.printStackTrace();
        }
        return borrowers;
    }
}
