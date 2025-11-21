package com.microfinance;

import com.microfinance.dao.LoanDAO;
import com.microfinance.dao.BorrowerDAO;
import com.microfinance.model.Loan;
import com.microfinance.model.Borrower;
import com.microfinance.service.LoanService;
import com.microfinance.service.LoanCalculationService;
import com.microfinance.exception.InvalidLoanAmountException;
import com.microfinance.exception.BorrowerNotFoundException;
import com.microfinance.util.DatabaseConnection;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.util.List;

/**
 * JavaFX Application for Microfinance Loan Tracker
 * Enhanced with side navigation, KPI dashboard, and improved UI
 */
public class App extends Application {
    private TableView<LoanRecord> loanTable;
    private Label statusLabel;
    private Label totalActiveLoanLabel;
    private Label totalOutstandingLabel;
    private Label overdueAccountsLabel;
    private LoanDAO loanDAO;
    private BorrowerDAO borrowerDAO;
    private LoanService loanService;
    private boolean databaseAvailable = false;
    private BorderPane root;
    private VBox contentArea;
    private List<Loan> allLoans;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize DAOs and Services
            loanDAO = new LoanDAO();
            borrowerDAO = new BorrowerDAO();
            loanService = new LoanService(loanDAO);
            
            // Test database connection
            databaseAvailable = DatabaseConnection.testConnection();
            
            if (!databaseAvailable) {
                System.out.println("Warning: Database connection not available. Running in offline mode.");
            }
            
            // Create root layout with BorderPane
            root = new BorderPane();

            // Create side navigation bar
            VBox sideNav = createSideNavigationBar();
            root.setLeft(sideNav);

            // Create content area
            contentArea = new VBox();
            ScrollPane scrollPane = new ScrollPane(contentArea);
            scrollPane.setFitToWidth(true);
            root.setCenter(scrollPane);

            // Create bottom status bar
            HBox statusBar = createStatusBar();
            root.setBottom(statusBar);

            // Create scene
            Scene scene = new Scene(root, 1400, 800);
            primaryStage.setTitle("Microfinance Loan Tracker");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                DatabaseConnection.closePool();
            });
            primaryStage.show();
            
            // Load and display dashboard
            if (databaseAvailable) {
                loadLoansFromDatabase();
                displayDashboard();
            } else {
                displayOfflineMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox createSideNavigationBar() {
        VBox sideNav = new VBox();
        sideNav.setPadding(new Insets(15));
        sideNav.setSpacing(15);
        sideNav.setStyle("-fx-background-color: #2c3e50; -fx-min-width: 200;");
        
        // App Title
        Label appTitle = new Label("MicroFinance\nTracker");
        appTitle.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: white; -fx-text-alignment: center;");
        appTitle.setPadding(new Insets(10));
        
        Separator separator1 = new Separator();
        separator1.setStyle("-fx-text-fill: #34495e;");
        
        // Navigation Buttons
        Button dashboardBtn = createNavButton("📊 Dashboard", e -> displayDashboard());
        Button borrowersBtn = createNavButton("👥 Borrowers", e -> displayBorrowers());
        Button loansBtn = createNavButton("💳 Loans", e -> displayLoans());
        Button paymentsBtn = createNavButton("💰 Payments", e -> displayPayments());
        Button reportsBtn = createNavButton("📈 Reports", e -> displayReports());
        
        Separator separator2 = new Separator();
        separator2.setStyle("-fx-text-fill: #34495e;");
        
        // Refresh Button
        Button refreshBtn = createNavButton("🔄 Refresh", e -> {
            if (databaseAvailable) {
                loadLoansFromDatabase();
                displayDashboard();
            }
        });
        
        sideNav.getChildren().addAll(
            appTitle, separator1,
            dashboardBtn, borrowersBtn, loansBtn, paymentsBtn, reportsBtn,
            separator2, refreshBtn
        );
        
        VBox.setVgrow(sideNav, Priority.ALWAYS);
        return sideNav;
    }

    private Button createNavButton(String text, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button btn = new Button(text);
        btn.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-text-fill: white; -fx-background-color: #34495e; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(handler);
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-text-fill: white; " +
                    "-fx-background-color: #3498db; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-font-size: 12; -fx-padding: 10; -fx-text-fill: white; " +
                    "-fx-background-color: #34495e; -fx-border-radius: 5; -fx-background-radius: 5; -fx-cursor: hand;"));
        return btn;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");
        
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-font-size: 11;");
        
        Label dbStatus = new Label(databaseAvailable ? "✅ Database Connected" : "❌ Database Offline");
        dbStatus.setStyle("-fx-font-size: 11; -fx-font-weight: bold;");
        
        statusBar.getChildren().addAll(statusLabel, new Separator(), dbStatus);
        return statusBar;
    }

    private void displayDashboard() {
        contentArea.getChildren().clear();
        
        VBox dashboard = new VBox(15);
        dashboard.setPadding(new Insets(20));
        dashboard.setStyle("-fx-background-color: #ecf0f1;");
        
        // Header
        Label headerLabel = new Label("📊 Dashboard");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        // KPI Cards
        HBox kpiBox = createKPIDashboard();
        
        // Loans Table with Search
        VBox loansSection = createLoansTableWithSearch();
        VBox.setVgrow(loansSection, Priority.ALWAYS);
        
        dashboard.getChildren().addAll(headerLabel, kpiBox, new Separator(), loansSection);
        contentArea.getChildren().add(dashboard);
    }

    private HBox createKPIDashboard() {
        HBox kpiBox = new HBox(15);
        kpiBox.setPadding(new Insets(10));
        
        // Load fresh data
        allLoans = loanDAO.getAllLoans();
        
        // Calculate KPIs
        long activeLoans = allLoans.stream().filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus())).count();
        double totalOutstanding = allLoans.stream().mapToDouble(Loan::getOutstandingBalance).sum();
        long overdueCount = loanService.getOverdueAccountCount();
        
        // Create KPI Cards
        totalActiveLoanLabel = new Label(String.valueOf(activeLoans));
        totalOutstandingLabel = new Label(String.format("$%.2f", totalOutstanding));
        overdueAccountsLabel = new Label(String.valueOf(overdueCount));
        
        kpiBox.getChildren().addAll(
            createKPICard("💳 Active Loans", totalActiveLoanLabel, "#3498db"),
            createKPICard("📌 Outstanding Balance", totalOutstandingLabel, "#e74c3c"),
            createKPICard("⚠️ Overdue Accounts", overdueAccountsLabel, "#f39c12")
        );
        
        return kpiBox;
    }

    private VBox createKPICard(String title, Label valueLabel, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8; " +
                     "-fx-background-color: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
        card.setPrefWidth(250);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        valueLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    @SuppressWarnings("unchecked")
    private VBox createLoansTableWithSearch() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(15));
        section.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-width: 1; " +
                        "-fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 3, 0, 0, 1);");
        
        // Search Bar
        HBox searchBox = new HBox(10);
        searchBox.setPadding(new Insets(10));
        Label searchLabel = new Label("🔍 Search:");
        searchLabel.setStyle("-fx-font-weight: bold;");
        TextField searchField = new TextField();
        searchField.setPromptText("Search by borrower name or loan ID...");
        searchField.setPrefWidth(300);
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        // Table
        loanTable = new TableView<>();
        
        TableColumn<LoanRecord, Number> idCol = new TableColumn<>("Loan ID");
        idCol.setPrefWidth(80);
        idCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getId()));
        
        TableColumn<LoanRecord, String> borrowerCol = new TableColumn<>("Borrower Name");
        borrowerCol.setPrefWidth(150);
        borrowerCol.setCellValueFactory(cellData -> cellData.getValue().borrowerProperty());
        
        TableColumn<LoanRecord, Number> amountCol = new TableColumn<>("Principal");
        amountCol.setPrefWidth(120);
        amountCol.setCellValueFactory(cellData -> cellData.getValue().loanAmountProperty());
        
        TableColumn<LoanRecord, Number> balanceCol = new TableColumn<>("Outstanding Balance");
        balanceCol.setPrefWidth(150);
        balanceCol.setCellValueFactory(cellData -> cellData.getValue().outstandingBalanceProperty());
        
        TableColumn<LoanRecord, String> dueCol = new TableColumn<>("Next Due Date");
        dueCol.setPrefWidth(130);
        dueCol.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());
        
        TableColumn<LoanRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        
        // Apply search filter
        ObservableList<LoanRecord> loanList = FXCollections.observableArrayList();
        for (Loan loan : allLoans) {
            loanList.add(new LoanRecord(
                loan.getId(), loan.getBorrowerName(), loan.getLoanAmount(),
                loan.getOutstandingBalance(), loan.getDueDate().toString(), loanService.determineLoanStatus(loan)
            ));
        }
        
        FilteredList<LoanRecord> filteredList = new FilteredList<>(loanList);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(record -> {
                if (newVal == null || newVal.isEmpty()) {
                    return true;
                }
                String lowerCase = newVal.toLowerCase();
                return record.getBorrower().toLowerCase().contains(lowerCase) ||
                       String.valueOf(record.getId()).contains(lowerCase);
            });
        });
        
        loanTable.getColumns().addAll(idCol, borrowerCol, amountCol, balanceCol, dueCol, statusCol);
        loanTable.setItems(filteredList);
        loanTable.setPrefHeight(400);
        VBox.setVgrow(loanTable, Priority.ALWAYS);
        
        section.getChildren().addAll(searchBox, loanTable);
        return section;
    }

    @SuppressWarnings("unchecked")
    private void displayBorrowers() {
        contentArea.getChildren().clear();
        
        VBox borrowersView = new VBox(15);
        borrowersView.setPadding(new Insets(20));
        
        Label headerLabel = new Label("👥 Borrower Management");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        HBox buttonBox = new HBox(10);
        Button addNewBtn = new Button("➕ Add New Borrower");
        Button updateBtn = new Button("✏️ Update Borrower");
        Button deleteBtn = new Button("🗑️ Delete Borrower");
        
        addNewBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        updateBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        deleteBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        
        addNewBtn.setOnAction(e -> handleAddBorrower());
        updateBtn.setOnAction(e -> handleUpdateBorrower());
        deleteBtn.setOnAction(e -> handleDeleteBorrower());
        
        buttonBox.getChildren().addAll(addNewBtn, updateBtn, deleteBtn);
        
        // Borrowers Table
        TableView<BorrowerRecord> borrowerTable = new TableView<>();
        
        TableColumn<BorrowerRecord, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setPrefWidth(150);
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        
        TableColumn<BorrowerRecord, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setPrefWidth(120);
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        TableColumn<BorrowerRecord, String> addressCol = new TableColumn<>("Address");
        addressCol.setPrefWidth(200);
        addressCol.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        
        TableColumn<BorrowerRecord, Number> incomeCol = new TableColumn<>("Income");
        incomeCol.setPrefWidth(120);
        incomeCol.setCellValueFactory(cellData -> cellData.getValue().incomeProperty());
        
        borrowerTable.getColumns().addAll(nameCol, phoneCol, addressCol, incomeCol);
        
        // Load borrowers
        List<Borrower> borrowers = borrowerDAO.getAllBorrowers();
        ObservableList<BorrowerRecord> borrowerList = FXCollections.observableArrayList();
        for (Borrower b : borrowers) {
            borrowerList.add(new BorrowerRecord(b.getId(), b.getName(), b.getPhone(), b.getAddress(), b.getIncome()));
        }
        borrowerTable.setItems(borrowerList);
        borrowerTable.setPrefHeight(400);
        VBox.setVgrow(borrowerTable, Priority.ALWAYS);
        
        borrowersView.getChildren().addAll(headerLabel, buttonBox, new Separator(), borrowerTable);
        contentArea.getChildren().add(borrowersView);
    }

    private void displayLoans() {
        contentArea.getChildren().clear();
        
        VBox loansView = new VBox(15);
        loansView.setPadding(new Insets(20));
        
        Label headerLabel = new Label("💳 Loan Management");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        HBox buttonBox = new HBox(10);
        Button addLoanBtn = new Button("➕ Issue New Loan");
        Button viewDetailsBtn = new Button("👁️ View Details");
        
        addLoanBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        viewDetailsBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        
        addLoanBtn.setOnAction(e -> handleIssueLoan());
        viewDetailsBtn.setOnAction(e -> showAlert("Info", "Select a loan from the table to view details"));
        
        buttonBox.getChildren().addAll(addLoanBtn, viewDetailsBtn);
        
        // Include the loans table with search
        VBox tableSection = createLoansTableWithSearch();
        VBox.setVgrow(tableSection, Priority.ALWAYS);
        
        loansView.getChildren().addAll(headerLabel, buttonBox, new Separator(), tableSection);
        contentArea.getChildren().add(loansView);
    }

    private void displayPayments() {
        contentArea.getChildren().clear();
        
        VBox paymentsView = new VBox(15);
        paymentsView.setPadding(new Insets(20));
        
        Label headerLabel = new Label("💰 Payment Recording");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        HBox buttonBox = new HBox(10);
        Button recordPaymentBtn = new Button("💵 Record Payment");
        recordPaymentBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        recordPaymentBtn.setOnAction(e -> handleRecordPayment());
        buttonBox.getChildren().add(recordPaymentBtn);
        
        // Loans table for selection
        VBox tableSection = createLoansTableWithSearch();
        VBox.setVgrow(tableSection, Priority.ALWAYS);
        
        paymentsView.getChildren().addAll(headerLabel, buttonBox, new Separator(), tableSection);
        contentArea.getChildren().add(paymentsView);
    }

    private void displayReports() {
        contentArea.getChildren().clear();
        
        VBox reportsView = new VBox(15);
        reportsView.setPadding(new Insets(20));
        reportsView.setStyle("-fx-background-color: #ecf0f1;");
        
        Label headerLabel = new Label("📈 Reports & Analytics");
        headerLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        // Metrics Display
        allLoans = loanDAO.getAllLoans();
        HBox metricsBox = createDetailedMetrics();
        
        // Charts
        HBox chartsBox = new HBox(15);
        chartsBox.setPadding(new Insets(10));
        
        PieChart statusPie = createLoanStatusPieChart();
        VBox pieContainer = new VBox(10);
        pieContainer.setPadding(new Insets(10));
        pieContainer.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-background-color: white;");
        Label pieTitle = new Label("Loan Status Distribution");
        pieTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        pieContainer.getChildren().addAll(pieTitle, statusPie);
        
        PieChart balancePie = createBalancePieChart();
        VBox balanceContainer = new VBox(10);
        balanceContainer.setPadding(new Insets(10));
        balanceContainer.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-background-color: white;");
        Label balanceTitle = new Label("Outstanding vs Repaid");
        balanceTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        balanceContainer.getChildren().addAll(balanceTitle, balancePie);
        
        chartsBox.getChildren().addAll(pieContainer, balanceContainer);
        
        reportsView.getChildren().addAll(headerLabel, metricsBox, chartsBox);
        contentArea.getChildren().add(reportsView);
    }

    private HBox createDetailedMetrics() {
        double totalLoaned = allLoans.stream().mapToDouble(Loan::getLoanAmount).sum();
        double totalOutstanding = allLoans.stream().mapToDouble(Loan::getOutstandingBalance).sum();
        double totalRepaid = totalLoaned - totalOutstanding;
        long activeLoans = allLoans.stream().filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus())).count();
        long completedLoans = allLoans.stream().filter(l -> "PAID_OFF".equalsIgnoreCase(l.getStatus())).count();
        double averageLoanAmount = allLoans.isEmpty() ? 0 : totalLoaned / allLoans.size();

        HBox hbox = new HBox(12);
        hbox.setStyle("-fx-padding: 10;");

        hbox.getChildren().addAll(
            createMetricCard("💰 Total Loaned", String.format("$%.2f", totalLoaned), "#3498db"),
            createMetricCard("📌 Outstanding", String.format("$%.2f", totalOutstanding), "#e74c3c"),
            createMetricCard("✅ Repaid", String.format("$%.2f", totalRepaid), "#2ecc71"),
            createMetricCard("🔄 Active", String.valueOf(activeLoans), "#f39c12"),
            createMetricCard("✓ Completed", String.valueOf(completedLoans), "#9b59b6"),
            createMetricCard("📊 Avg Loan", String.format("$%.2f", averageLoanAmount), "#1abc9c")
        );

        return hbox;
    }

    private VBox createMetricCard(String title, String value, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 8; " +
                     "-fx-background-color: linear-gradient(to bottom, " + color + "22, white); " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private PieChart createLoanStatusPieChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Loan Status Distribution");
        pieChart.setLegendSide(Side.BOTTOM);

        if (allLoans == null || allLoans.isEmpty()) {
            System.out.println("[DEBUG] No loans available.");
            pieChart.getData().add(new PieChart.Data("No Data", 1));
            pieChart.getData().get(0).getNode().setStyle("-fx-pie-color: #bdc3c7;");
            return pieChart;
        }

        long active = allLoans.stream().filter(l -> "ACTIVE".equalsIgnoreCase(l.getStatus())).count();
        long overdue = allLoans.stream().filter(l -> "OVERDUE".equalsIgnoreCase(l.getStatus())).count();
        long paidOff = allLoans.stream().filter(l -> "PAID_OFF".equalsIgnoreCase(l.getStatus())).count();

        System.out.println("[DEBUG] Active Loans: " + active);
        System.out.println("[DEBUG] Overdue Loans: " + overdue);
        System.out.println("[DEBUG] Paid Off Loans: " + paidOff);

        PieChart.Data activeData = new PieChart.Data("Active", active);
        PieChart.Data overdueData = new PieChart.Data("Overdue", overdue);
        PieChart.Data paidData = new PieChart.Data("Paid Off", paidOff);

        pieChart.getData().addAll(activeData, overdueData, paidData);

        // Force node initialization
        pieChart.applyCss();
        activeData.getNode().setStyle("-fx-pie-color: #3498db;");
        overdueData.getNode().setStyle("-fx-pie-color: #e74c3c;");
        paidData.getNode().setStyle("-fx-pie-color: #2ecc71;");

        pieChart.setLabelsVisible(true);
        return pieChart;
    }

    private PieChart createBalancePieChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Outstanding vs Repaid");
        pieChart.setLegendSide(Side.BOTTOM);

        if (allLoans == null || allLoans.isEmpty()) {
            pieChart.getData().add(new PieChart.Data("No Data", 1));
            pieChart.getData().get(0).getNode().setStyle("-fx-pie-color: #bdc3c7;");
            return pieChart;
        }

        double totalLoaned = allLoans.stream().mapToDouble(Loan::getLoanAmount).sum();
        double totalOutstanding = allLoans.stream().mapToDouble(Loan::getOutstandingBalance).sum();
        double totalRepaid = totalLoaned - totalOutstanding;

        PieChart.Data outstandingData = new PieChart.Data("Outstanding", totalOutstanding);
        PieChart.Data repaidData = new PieChart.Data("Repaid", totalRepaid);

        pieChart.getData().addAll(outstandingData, repaidData);

        applyPieChartStyles(outstandingData, "#e74c3c");
        applyPieChartStyles(repaidData, "#2ecc71");

        pieChart.setLabelsVisible(true);
        return pieChart;
    }

    private void applyPieChartStyles(PieChart.Data data, String color) {
        data.getNode().styleProperty().addListener((obs, oldStyle, newStyle) -> {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            }
        });
    }

    private void handleAddBorrower() {
        if (!databaseAvailable) {
            showAlert("Error", "Database is offline. Cannot add borrower.");
            return;
        }
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Borrower");
        dialog.setHeaderText("Enter borrower details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone number");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        TextField incomeField = new TextField();
        incomeField.setPromptText("Income");

        grid.add(new Label("First Name:"), 0, 0);
        grid.add(firstNameField, 1, 0);
        grid.add(new Label("Last Name:"), 0, 1);
        grid.add(lastNameField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Phone Number:"), 0, 3);
        grid.add(phoneField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        grid.add(new Label("Income:"), 0, 5);
        grid.add(incomeField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String incomeText = incomeField.getText().trim();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty()) {
                showAlert("Error", "First Name and Last Name cannot be empty");
                return;
            }
            if (email.isEmpty()) {
                showAlert("Error", "Email cannot be empty");
                return;
            }
            if (!LoanCalculationService.isValidPhoneNumber(phone)) {
                showAlert("Error", "Please enter a valid phone number (10-15 digits)");
                return;
            }
            double income;
            try {
                income = Double.parseDouble(incomeText);
                if (!LoanCalculationService.isValidIncome(income)) {
                    showAlert("Error", "Income must be a positive number");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid income amount");
                return;
            }

            String fullName = firstName + " " + lastName;
            Borrower newBorrower = new Borrower(fullName, email, phone, address, income);
            boolean added = false;
            try {
                added = borrowerDAO.addBorrower(newBorrower);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Exception occurred: " + ex.getMessage());
                return;
            }
            if (added) {
                showAlert("Success", "Borrower added successfully!");
                displayBorrowers();
            } else {
                showAlert("Error", "Failed to add borrower. Please check database and logs.");
            }
        }
    }

    private void handleUpdateBorrower() {
        // Find selected borrower
        TableView<BorrowerRecord> borrowerTable = null;
        for (javafx.scene.Node node : contentArea.getChildren()) {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                for (javafx.scene.Node child : vbox.getChildren()) {
                    if (child instanceof TableView) {
                        borrowerTable = (TableView<BorrowerRecord>) child;
                        break;
                    }
                }
            }
        }
        if (borrowerTable == null || borrowerTable.getSelectionModel().getSelectedItem() == null) {
            showAlert("Error", "Please select a borrower from the table first");
            return;
        }
        BorrowerRecord selected = borrowerTable.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Update Borrower");
        dialog.setHeaderText("Edit borrower details");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        TextField nameField = new TextField(selected.nameProperty().get());
        TextField phoneField = new TextField(selected.phoneProperty().get());
        TextField addressField = new TextField(selected.addressProperty().get());
        TextField incomeField = new TextField(String.valueOf(selected.incomeProperty().get()));
        grid.add(new Label("Full Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Phone:"), 0, 1);
        grid.add(phoneField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Income:"), 0, 3);
        grid.add(incomeField, 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            double income;
            try {
                income = Double.parseDouble(incomeField.getText().trim());
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid income amount");
                return;
            }
            Borrower updated = new Borrower(selected.getId(), name, "", phone, address, income);
            boolean success = borrowerDAO.updateBorrower(updated);
            if (success) {
                showAlert("Success", "Borrower updated successfully!");
                displayBorrowers();
            } else {
                showAlert("Error", "Failed to update borrower.");
            }
        }
    }

    private void handleDeleteBorrower() {
        // Find selected borrower
        TableView<BorrowerRecord> borrowerTable = null;
        for (javafx.scene.Node node : contentArea.getChildren()) {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                for (javafx.scene.Node child : vbox.getChildren()) {
                    if (child instanceof TableView) {
                        borrowerTable = (TableView<BorrowerRecord>) child;
                        break;
                    }
                }
            }
        }
        if (borrowerTable == null || borrowerTable.getSelectionModel().getSelectedItem() == null) {
            showAlert("Error", "Please select a borrower from the table first");
            return;
        }
        BorrowerRecord selected = borrowerTable.getSelectionModel().getSelectedItem();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Borrower");
        confirm.setHeaderText("Are you sure you want to delete this borrower?");
        confirm.setContentText("Borrower: " + selected.nameProperty().get());
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = borrowerDAO.deleteBorrower(selected.getId());
            if (success) {
                showAlert("Success", "Borrower deleted successfully!");
                displayBorrowers();
            } else {
                showAlert("Error", "Failed to delete borrower.");
            }
        }
    }

    private void handleIssueLoan() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Issue New Loan");
        dialog.setHeaderText("Enter loan details");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Left side - Input fields
        VBox inputBox = new VBox(10);
        TextField borrowerField = new TextField();
        borrowerField.setPromptText("Borrower name");
        TextField amountField = new TextField();
        amountField.setPromptText("Loan amount");
        TextField interestRateField = new TextField();
        interestRateField.setPromptText("Annual interest rate (%)");
        interestRateField.setText("5.0");
        TextField tenureField = new TextField();
        tenureField.setPromptText("Tenure (months)");
        tenureField.setText("12");

        inputBox.getChildren().addAll(
            new Label("Borrower Name:"), borrowerField,
            new Label("Loan Amount:"), amountField,
            new Label("Interest Rate (%):"), interestRateField,
            new Label("Tenure (Months):"), tenureField
        );

        // Right side - EMI Calculator Panel
        VBox emiPanel = new VBox(10);
        emiPanel.setPadding(new Insets(10));
        emiPanel.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        Label emiTitle = new Label("💡 EMI Calculator");
        emiTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        Label emiValueLabel = new Label("EMI: -");
        Label totalInterestLabel = new Label("Total Interest: -");
        Label totalPayableLabel = new Label("Total Payable: -");

        emiPanel.getChildren().addAll(emiTitle, new Separator(), emiValueLabel, totalInterestLabel, totalPayableLabel);

        // Dynamic calculation
        javafx.event.EventHandler<javafx.scene.input.KeyEvent> calculator = event -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                double rate = Double.parseDouble(interestRateField.getText());
                int months = Integer.parseInt(tenureField.getText());

                if (amount > 0 && months > 0 && rate >= 0) {
                    double emi = LoanCalculationService.calculateEMI(amount, rate, months);
                    double totalInterest = LoanCalculationService.calculateTotalInterest(emi, months, amount);
                    double totalPayable = LoanCalculationService.calculateTotalPayable(emi, months);

                    emiValueLabel.setText(String.format("EMI: $%.2f/month", emi));
                    totalInterestLabel.setText(String.format("Total Interest: $%.2f", totalInterest));
                    totalPayableLabel.setText(String.format("Total Payable: $%.2f", totalPayable));
                }
            } catch (Exception e) {
                // Silently ignore parsing errors during typing
            }
        };

        amountField.setOnKeyReleased(calculator);
        interestRateField.setOnKeyReleased(calculator);
        tenureField.setOnKeyReleased(calculator);

        grid.add(inputBox, 0, 0);
        grid.add(emiPanel, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                String borrowerName = borrowerField.getText().trim();
                double amount = Double.parseDouble(amountField.getText());
                double rate = Double.parseDouble(interestRateField.getText());
                int months = Integer.parseInt(tenureField.getText());

                // Find borrowerId by name
                int borrowerId = -1;
                List<Borrower> borrowersList = borrowerDAO.getAllBorrowers();
                for (Borrower b : borrowersList) {
                    if (b.getName().equalsIgnoreCase(borrowerName)) {
                        borrowerId = b.getId();
                        break;
                    }
                }
                if (borrowerId == -1) {
                    showAlert("Error", "Borrower not found. Please add the borrower first.");
                    return;
                }

                // Use LoanService for validation and creation
                loanService.issueLoan(borrowerId, borrowerName, amount, rate, months);

                showAlert("Success", "Loan issued successfully!");
                loadLoansFromDatabase();
                displayLoans();
                statusLabel.setText("✓ Loan issued successfully!");
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter valid values for all fields");
            } catch (InvalidLoanAmountException ex) {
                showAlert("Validation Error", ex.getMessage());
            } catch (BorrowerNotFoundException ex) {
                showAlert("Error", ex.getMessage());
            }
        }
    }

    private void handleRecordPayment() {
        if (loanTable.getSelectionModel().getSelectedItem() == null) {
            showAlert("Error", "Please select a loan from the table first");
            return;
        }

        LoanRecord selectedLoan = loanTable.getSelectionModel().getSelectedItem();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Record Payment");
        dialog.setHeaderText("Enter payment for: " + selectedLoan.getBorrower());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField paymentField = new TextField();
        paymentField.setPromptText("Payment amount");

        grid.add(new Label("Payment Amount:"), 0, 0);
        grid.add(paymentField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                double payment = Double.parseDouble(paymentField.getText());
                double outstanding = selectedLoan.getOutstandingBalance();
                if (payment <= 0) {
                    showAlert("Error", "Payment amount must be greater than 0");
                    return;
                }
                if (payment > outstanding) {
                    showAlert("Error", String.format("Payment cannot exceed outstanding balance ($%.2f)", outstanding));
                    return;
                }
                if (loanService.recordPayment(selectedLoan.getId(), payment)) {
                    showAlert("Success", String.format("Payment of $%.2f recorded successfully!", payment));
                    loadLoansFromDatabase();
                    displayPayments();
                    statusLabel.setText("✓ Payment recorded: $" + payment);
                } else {
                    showAlert("Error", "Failed to record payment");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid payment amount");
            } catch (IllegalArgumentException ex) {
                showAlert("Error", ex.getMessage());
            }
        }
    }

    private void displayOfflineMessage() {
        contentArea.getChildren().clear();
        VBox offlineBox = new VBox(20);
        offlineBox.setPadding(new Insets(40));
        offlineBox.setStyle("-fx-alignment: center;");
        
        Label titleLabel = new Label("📡 Offline Mode");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        Label messageLabel = new Label("Database connection not available.\n\nPlease ensure:\n1. MySQL is running\n2. Database 'microfinance_db' exists\n3. Credentials are correct in DatabaseConnection.java");
        messageLabel.setStyle("-fx-font-size: 14; -fx-text-alignment: center;");
        
        Button retryBtn = new Button("🔄 Retry Connection");
        retryBtn.setStyle("-fx-font-size: 12; -fx-padding: 10;");
        retryBtn.setOnAction(e -> {
            databaseAvailable = DatabaseConnection.testConnection();
            if (databaseAvailable) {
                loadLoansFromDatabase();
                displayDashboard();
            }
        });
        
        offlineBox.getChildren().addAll(titleLabel, messageLabel, retryBtn);
        contentArea.getChildren().add(offlineBox);
    }

    private void loadLoansFromDatabase() {
        if (!databaseAvailable) {
            return;
        }
        
        try {
            allLoans = loanDAO.getAllLoans();
        } catch (Exception e) {
            System.err.println("Error loading loans: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data class for Loan Records
    public static class LoanRecord {
        private final int id;
        private final SimpleStringProperty borrower;
        private final SimpleDoubleProperty loanAmount;
        private final SimpleDoubleProperty outstandingBalance;
        private final SimpleStringProperty dueDate;
        private final SimpleStringProperty status;

        public LoanRecord(int id, String borrower, double loanAmount, double balance, String dueDate, String status) {
            this.id = id;
            this.borrower = new SimpleStringProperty(borrower);
            this.loanAmount = new SimpleDoubleProperty(loanAmount);
            this.outstandingBalance = new SimpleDoubleProperty(balance);
            this.dueDate = new SimpleStringProperty(dueDate);
            this.status = new SimpleStringProperty(status);
        }

        public int getId() { return id; }
        public SimpleStringProperty borrowerProperty() { return borrower; }
        public SimpleDoubleProperty loanAmountProperty() { return loanAmount; }
        public SimpleDoubleProperty outstandingBalanceProperty() { return outstandingBalance; }
        public SimpleStringProperty dueDateProperty() { return dueDate; }
        public SimpleStringProperty statusProperty() { return status; }
        public String getBorrower() { return borrower.get(); }
        public double getLoanAmount() { return loanAmount.get(); }
        public double getOutstandingBalance() { return outstandingBalance.get(); }
    }

    // Data class for Borrower Records
    public static class BorrowerRecord {
        private final int id;
        private final SimpleStringProperty name;
        private final SimpleStringProperty phone;
        private final SimpleStringProperty address;
        private final SimpleDoubleProperty income;

        public BorrowerRecord(int id, String name, String phone, String address, double income) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
            this.income = new SimpleDoubleProperty(income);
        }

        public int getId() { return id; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty phoneProperty() { return phone; }
        public SimpleStringProperty addressProperty() { return address; }
        public SimpleDoubleProperty incomeProperty() { return income; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
