package com.microfinance.model;

/**
 * LoanOfficer model class representing a loan officer record
 */
public class LoanOfficer {
    private int id;
    private String name;
    private String email;
    private String phoneNumber;
    private String department;
    private String status;

    // Default constructor
    public LoanOfficer() {
    }

    /**
     * Constructor for new loan officers (without ID)
     */
    public LoanOfficer(String name, String email, String phoneNumber, String department, String status) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.status = status;
    }

    /**
     * Constructor for existing loan officers (with ID)
     */
    public LoanOfficer(int id, String name, String email, String phoneNumber, String department, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.status = status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoanOfficer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", department='" + department + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
