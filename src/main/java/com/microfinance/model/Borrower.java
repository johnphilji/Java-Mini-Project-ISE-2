package com.microfinance.model;

/**
 * Borrower model class representing a borrower record
 */
public class Borrower {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private double income;

    // Default constructor
    public Borrower() {}

    // Constructor for new borrowers (without ID)
    public Borrower(String name, String email, String phone, String address, double income) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.income = income;
    }

    // Constructor for existing borrowers (with ID)
    public Borrower(int id, String name, String email, String phone, String address, double income) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.income = income;
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
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public double getIncome() {
        return income;
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
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setIncome(double income) {
        this.income = income;
    }

    @Override
    public String toString() {
        return "Borrower{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", phone='" + phone + '\'' +
            ", address='" + address + '\'' +
            ", income=" + income +
            '}';
    }
}
