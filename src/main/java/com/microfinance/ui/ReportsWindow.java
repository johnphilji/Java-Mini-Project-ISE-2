package com.microfinance.ui;

import com.microfinance.dao.LoanDAO;
import com.microfinance.model.Loan;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

/**
 * Beautiful Reports Window with Charts and Visualizations
 */
public class ReportsWindow {
    private Stage stage;
    private List<Loan> allLoans;

    public ReportsWindow(LoanDAO loanDAO) {
        this.allLoans = loanDAO.getAllLoans();
    }

    public void show() {
        stage = new Stage();
        stage.setTitle("Microfinance Loan Tracker - Reports & Analytics");
        stage.setWidth(1400);
        stage.setHeight(900);

        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5;");

        // Header
        VBox header = createHeader();
        root.setTop(header);

        // Tabbed content
        TabPane tabPane = createTabPane();
        root.setCenter(tabPane);

        // Footer with legend
        VBox footer = createFooter();
        root.setBottom(footer);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-padding: 20;");
        header.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #3498db); -fx-padding: 20; -fx-text-fill: white;");

        Label titleLabel = new Label("📊 Microfinance Loan Analytics Dashboard");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitleLabel = new Label("Real-time Loan Portfolio Analysis & Performance Metrics");
        subtitleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #ecf0f1;");

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Overview Tab
        Tab overviewTab = new Tab("📈 Overview", createOverviewTab());
        overviewTab.setStyle("-fx-font-size: 12;");

        // Charts Tab
        Tab chartsTab = new Tab("📉 Charts & Analysis", createChartsTab());
        chartsTab.setStyle("-fx-font-size: 12;");

        // Loan Details Tab
        Tab detailsTab = new Tab("📋 Loan Details", createDetailsTab());
        detailsTab.setStyle("-fx-font-size: 12;");

        // Processing Info Tab
        Tab processingTab = new Tab("⚙️ Loan Processing Info", createProcessingInfoTab());
        processingTab.setStyle("-fx-font-size: 12;");

        tabPane.getTabs().addAll(overviewTab, chartsTab, detailsTab, processingTab);
        return tabPane;
    }

    private VBox createOverviewTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff;");

        // Calculate metrics
        double totalLoaned = allLoans.stream().mapToDouble(Loan::getLoanAmount).sum();
        double totalOutstanding = allLoans.stream().mapToDouble(Loan::getOutstandingBalance).sum();
        double totalRepaid = totalLoaned - totalOutstanding;
        long activeLoans = allLoans.stream().filter(l -> "Active".equals(l.getStatus())).count();
        long completedLoans = allLoans.stream().filter(l -> "Completed".equals(l.getStatus())).count();
        double averageLoanAmount = allLoans.isEmpty() ? 0 : totalLoaned / allLoans.size();

        // Metrics Grid
        HBox metricsBox = createMetricsGrid(totalLoaned, totalOutstanding, totalRepaid, activeLoans, completedLoans, averageLoanAmount);
        vbox.getChildren().add(metricsBox);

        // Summary Section
        VBox summaryBox = createSummarySection(totalLoaned, totalOutstanding, totalRepaid);
        vbox.getChildren().add(summaryBox);

        return vbox;
    }

    private HBox createMetricsGrid(double totalLoaned, double totalOutstanding, double totalRepaid, 
                                    long activeLoans, long completedLoans, double averageLoanAmount) {
        HBox hbox = new HBox(15);
        hbox.setStyle("-fx-padding: 10;");

        hbox.getChildren().addAll(
            createMetricCard("💰 Total Loaned", String.format("$%.2f", totalLoaned), "#3498db"),
            createMetricCard("📌 Outstanding", String.format("$%.2f", totalOutstanding), "#e74c3c"),
            createMetricCard("✅ Repaid", String.format("$%.2f", totalRepaid), "#2ecc71"),
            createMetricCard("🔄 Active Loans", String.valueOf(activeLoans), "#f39c12"),
            createMetricCard("✓ Completed", String.valueOf(completedLoans), "#9b59b6"),
            createMetricCard("📊 Avg Loan", String.format("$%.2f", averageLoanAmount), "#1abc9c")
        );

        return hbox;
    }

    private VBox createMetricCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 10; " +
                     "-fx-background-color: linear-gradient(to bottom, " + color + "22, white); " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #333;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private VBox createSummarySection(double totalLoaned, double totalOutstanding, double totalRepaid) {
        VBox summary = new VBox(10);
        summary.setPadding(new Insets(15));
        summary.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-background-color: #ecf0f1; " +
                        "-fx-border-radius: 8;");

        Label titleLabel = new Label("📊 Portfolio Summary");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        double repaymentRate = totalLoaned == 0 ? 0 : (totalRepaid / totalLoaned) * 100;
        double outstandingRate = totalLoaned == 0 ? 0 : (totalOutstanding / totalLoaned) * 100;

        TextArea summaryText = new TextArea();
        summaryText.setWrapText(true);
        summaryText.setEditable(false);
        summaryText.setPrefRowCount(8);
        summaryText.setStyle("-fx-control-inner-background: #ffffff; -fx-font-size: 11;");

        String summaryContent = String.format(
            "PORTFOLIO ANALYSIS\n" +
            "═════════════════════════════════════════════════════════════════\n\n" +
            "FINANCIAL METRICS:\n" +
            "  • Total Loan Portfolio Value:          $%.2f\n" +
            "  • Total Outstanding Balance:           $%.2f (%.1f%%)\n" +
            "  • Total Repaid Amount:                 $%.2f (%.1f%%)\n" +
            "  • Number of Active Loans:              %d\n" +
            "  • Number of Completed Loans:           %d\n" +
            "  • Total Loan Accounts:                 %d\n\n" +
            "REPAYMENT PERFORMANCE:\n" +
            "  • Overall Repayment Rate:              %.1f%%\n" +
            "  • Outstanding Rate:                    %.1f%%\n" +
            "  • Average Loan Size:                   $%.2f\n",
            totalLoaned,
            totalOutstanding, outstandingRate,
            totalRepaid, repaymentRate,
            allLoans.stream().filter(l -> "Active".equals(l.getStatus())).count(),
            allLoans.stream().filter(l -> "Completed".equals(l.getStatus())).count(),
            allLoans.size(),
            repaymentRate,
            outstandingRate,
            allLoans.isEmpty() ? 0 : totalLoaned / allLoans.size()
        );

        summaryText.setText(summaryContent);
        summary.getChildren().addAll(titleLabel, summaryText);
        return summary;
    }

    private VBox createChartsTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #ffffff;");

        HBox chartBox = new HBox(15);
        chartBox.setPadding(new Insets(10));

        // Pie Chart - Loan Status Distribution
        PieChart statusPie = createLoanStatusPieChart();
        VBox pieContainer = new VBox(10);
        pieContainer.setPadding(new Insets(10));
        pieContainer.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 8;");
        Label pieTitle = new Label("Loan Status Distribution");
        pieTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        pieContainer.getChildren().addAll(pieTitle, statusPie);

        // Pie Chart - Outstanding vs Repaid
        PieChart balancePie = createBalancePieChart();
        VBox balanceContainer = new VBox(10);
        balanceContainer.setPadding(new Insets(10));
        balanceContainer.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 8;");
        Label balanceTitle = new Label("Outstanding vs Repaid");
        balanceTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        balanceContainer.getChildren().addAll(balanceTitle, balancePie);

        chartBox.getChildren().addAll(pieContainer, balanceContainer);
        vbox.getChildren().add(chartBox);

        // Bar Chart - Loan Amounts
        BarChart<String, Number> barChart = createLoanBarChart();
        VBox barContainer = new VBox(10);
        barContainer.setPadding(new Insets(10));
        barContainer.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 8;");
        Label barTitle = new Label("Top Loans by Amount");
        barTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        barContainer.getChildren().addAll(barTitle, barChart);
        VBox.setVgrow(barContainer, javafx.scene.layout.Priority.ALWAYS);

        vbox.getChildren().add(barContainer);
        return vbox;
    }

    private PieChart createLoanStatusPieChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Loan Status Distribution");
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);

        long active = allLoans.stream().filter(l -> "Active".equals(l.getStatus())).count();
        long completed = allLoans.stream().filter(l -> "Completed".equals(l.getStatus())).count();

        PieChart.Data activeData = new PieChart.Data("Active", active);
        PieChart.Data completedData = new PieChart.Data("Completed", completed);

        pieChart.getData().addAll(activeData, completedData);
        
        // Style the pie slices after scene rendering
        pieChart.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (activeData.getNode() != null) {
                    activeData.getNode().setStyle("-fx-pie-color: #f39c12;");
                }
                if (completedData.getNode() != null) {
                    completedData.getNode().setStyle("-fx-pie-color: #2ecc71;");
                }
            }
        });
        
        pieChart.setLabelsVisible(true);
        return pieChart;
    }

    private PieChart createBalancePieChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Outstanding vs Repaid");
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);

        double totalLoaned = allLoans.stream().mapToDouble(Loan::getLoanAmount).sum();
        double totalOutstanding = allLoans.stream().mapToDouble(Loan::getOutstandingBalance).sum();
        double totalRepaid = totalLoaned - totalOutstanding;

        PieChart.Data outstandingData = new PieChart.Data("Outstanding", totalOutstanding);
        PieChart.Data repaidData = new PieChart.Data("Repaid", totalRepaid);

        pieChart.getData().addAll(outstandingData, repaidData);
        
        // Style the pie slices after scene rendering
        pieChart.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (outstandingData.getNode() != null) {
                    outstandingData.getNode().setStyle("-fx-pie-color: #e74c3c;");
                }
                if (repaidData.getNode() != null) {
                    repaidData.getNode().setStyle("-fx-pie-color: #2ecc71;");
                }
            }
        });
        
        pieChart.setLabelsVisible(true);
        return pieChart;
    }

    private BarChart<String, Number> createLoanBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Loan ID");
        yAxis.setLabel("Amount ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Loan Amounts");
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Loan Amount");

        // Add top 10 loans
        allLoans.stream().limit(10).forEach(loan ->
            series.getData().add(new XYChart.Data<>("L" + loan.getId(), loan.getLoanAmount()))
        );

        barChart.getData().add(series);
        return barChart;
    }

    private VBox createDetailsTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));
        vbox.setStyle("-fx-background-color: #ffffff;");

        Label titleLabel = new Label("📋 Detailed Loan Information");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TableView<LoanDetailRecord> table = new TableView<>();

        TableColumn<LoanDetailRecord, String> loanIdCol = new TableColumn<>("Loan ID");
        loanIdCol.setCellValueFactory(p -> p.getValue().loanIdProperty());
        loanIdCol.setPrefWidth(60);

        TableColumn<LoanDetailRecord, String> borrowerCol = new TableColumn<>("Borrower");
        borrowerCol.setCellValueFactory(p -> p.getValue().borrowerProperty());
        borrowerCol.setPrefWidth(100);

        TableColumn<LoanDetailRecord, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(p -> p.getValue().amountProperty());
        amountCol.setPrefWidth(100);

        TableColumn<LoanDetailRecord, String> outstandingCol = new TableColumn<>("Outstanding");
        outstandingCol.setCellValueFactory(p -> p.getValue().outstandingProperty());
        outstandingCol.setPrefWidth(100);

        TableColumn<LoanDetailRecord, String> interestCol = new TableColumn<>("Interest Rate");
        interestCol.setCellValueFactory(p -> p.getValue().interestRateProperty());
        interestCol.setPrefWidth(80);

        TableColumn<LoanDetailRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(p -> p.getValue().statusProperty());
        statusCol.setPrefWidth(80);

        @SuppressWarnings("unchecked")
        TableColumn<LoanDetailRecord, ?>[] columns = new TableColumn[] {
            loanIdCol, borrowerCol, amountCol, outstandingCol, interestCol, statusCol
        };
        table.getColumns().addAll(columns);

        // Add data
        for (Loan loan : allLoans) {
            table.getItems().add(new LoanDetailRecord(loan));
        }

        VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
        vbox.getChildren().addAll(titleLabel, table);

        return vbox;
    }

    private VBox createProcessingInfoTab() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #ffffff;");

        TextArea infoText = new TextArea();
        infoText.setWrapText(true);
        infoText.setEditable(false);
        infoText.setPrefRowCount(35);
        infoText.setStyle("-fx-control-inner-background: #f5f5f5; -fx-font-size: 10; -fx-font-family: 'Courier New';");

        String processingInfo = createProcessingInfoContent();
        infoText.setText(processingInfo);

        ScrollPane scrollPane = new ScrollPane(infoText);
        scrollPane.setFitToWidth(true);

        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);
        vbox.getChildren().add(scrollPane);

        return vbox;
    }

    private String createProcessingInfoContent() {
        return """
╔════════════════════════════════════════════════════════════════════════════════════════╗
║                    LOAN PROCESSING & WORKFLOW INFORMATION                              ║
╚════════════════════════════════════════════════════════════════════════════════════════╝

1️⃣  LOAN APPLICATION & APPROVAL STAGE
─────────────────────────────────────────────────────────────────────────────────────────
   ✓ Borrower submits loan application with required documents
   ✓ Loan Officer reviews borrower's:
     • Personal Information (Name, Contact, Address)
     • Annual Income and Income Source
     • Credit History and Repayment Capacity
   ✓ Due Diligence: Verify income, employment, and financial status
   ✓ Loan Officer assigns Loan ID and determines:
     • Principal Loan Amount
     • Interest Rate (based on risk assessment)
     • Loan Duration (in months)
     • Expected Monthly Payment = (Principal + Interest) / Duration

2️⃣  LOAN DISBURSEMENT STAGE
─────────────────────────────────────────────────────────────────────────────────────────
   ✓ Loan is marked as "ACTIVE" once approved
   ✓ Disbursement Date (Issue_Date) is recorded
   ✓ Outstanding Balance = Total Loan Amount
   ✓ Due Date = Issue_Date + Loan Duration (in months)
   ✓ Borrower receives funds and begins repayment obligations

3️⃣  PAYMENT COLLECTION & TRACKING
─────────────────────────────────────────────────────────────────────────────────────────
   ✓ Borrower makes regular payments (Monthly/Scheduled)
   ✓ Payment Record Created containing:
     • Payment ID (Auto-generated)
     • Loan ID (Link to original loan)
     • Payment Date
     • Amount Paid
     • Remaining Balance = Previous Balance - Amount Paid
     • Payment Status (Completed/Pending)
   ✓ Outstanding Balance Updated automatically
   ✓ Payment records maintain complete audit trail

4️⃣  LOAN STATUS MANAGEMENT
─────────────────────────────────────────────────────────────────────────────────────────
   Status Transitions:
   ACTIVE → Loan is ongoing with outstanding balance
   COMPLETED → Loan is fully repaid (Outstanding Balance = $0.00)
   
   System automatically marks loan as "COMPLETED" when:
   • Outstanding Balance ≤ $0.00 after payment
   • All interest charges are paid off
   • No further payments are required

5️⃣  INTEREST CALCULATION & CHARGES
─────────────────────────────────────────────────────────────────────────────────────────
   Interest Rate: Applied as Annual Percentage Rate (APR)
   
   Total Interest = (Principal × Interest Rate × Loan Duration in Years)
   
   Example:
   • Principal: $5,000
   • Interest Rate: 5% p.a.
   • Duration: 12 months (1 year)
   • Total Interest: $5,000 × 5% × 1 = $250
   • Total Repayment: $5,250

6️⃣  PORTFOLIO ANALYTICS
─────────────────────────────────────────────────────────────────────────────────────────
   Total Loan Portfolio Value = Sum of all Loan Amounts
   
   Outstanding Balance = Sum of all Outstanding Balances
   (Represents loans not yet fully repaid)
   
   Total Repaid Amount = Total Portfolio - Total Outstanding
   (Represents successful recoveries)
   
   Repayment Rate (%) = (Total Repaid / Total Portfolio) × 100
   Active Repayment Rate indicates portfolio health
   
   Delinquency Risk: Loans > Due Date with Outstanding Balance

7️⃣  KEY ENTITIES IN LOAN PROCESSING
─────────────────────────────────────────────────────────────────────────────────────────
   ┌─ BORROWER
   │  ├─ Borrower_ID (Unique Identifier)
   │  ├─ Name (Full Name)
   │  ├─ Contact_No (Mobile/Phone)
   │  ├─ Address (Physical Address)
   │  ├─ Annual_Inc (Annual Income)
   │  └─ Income_Source (Employment/Business)
   │
   ├─ LOAN_OFFICER
   │  ├─ Officer_ID (Staff Identifier)
   │  ├─ Name (Officer Full Name)
   │  ├─ Contact_No (Office Phone)
   │  └─ Email (Office Email)
   │
   ├─ LOAN
   │  ├─ Loan_ID (Unique Loan Number)
   │  ├─ Borrower_ID (FK → Borrower)
   │  ├─ Officer_ID (FK → Loan_Officer)
   │  ├─ Loan_Amount (Principal)
   │  ├─ Interest_Rate (Annual %)
   │  ├─ Issue_Date (Disbursement Date)
   │  ├─ Duration_in_Months (Repayment Period)
   │  ├─ Status (Active/Completed)
   │  └─ Outstanding_Balance (Current Due)
   │
   └─ PAYMENT
      ├─ Payment_ID (Transaction Identifier)
      ├─ Loan_ID (FK → Loan)
      ├─ Payment_Date (When paid)
      ├─ Amount_Paid (Payment Amount)
      ├─ Remaining_Balance (Updated balance)
      └─ Payment_Status (Completed/Pending)

8️⃣  COMPLIANCE & AUDIT REQUIREMENTS
─────────────────────────────────────────────────────────────────────────────────────────
   ✓ All loans require documented approval
   ✓ Payment records provide complete audit trail
   ✓ Interest calculations must be transparent
   ✓ Status changes are permanent and tracked
   ✓ Foreign key relationships maintain data integrity
   ✓ Cascading deletes prevent orphaned records
   ✓ Timestamp fields record all transactions

9️⃣  RISK ASSESSMENT FACTORS
─────────────────────────────────────────────────────────────────────────────────────────
   High Risk Indicators:
   • Low Annual Income vs. Loan Amount Ratio
   • Self-Employed or Irregular Income Sources
   • Short Loan Duration with High Principal
   • Missing or Incomplete Financial Documentation
   
   Mitigation Strategies:
   • Adjust Interest Rate based on Risk Profile
   • Require Collateral or Guarantees
   • Shorter Loan Duration for High-Risk Borrowers
   • More Frequent Payment Schedules

🔟  REPORTING & PERFORMANCE METRICS
─────────────────────────────────────────────────────────────────────────────────────────
   Dashboard Metrics:
   • Portfolio Value: Sum of all active loan amounts
   • Repayment Rate: Success percentage of portfolio
   • Default Rate: Percentage of overdue loans
   • Average Loan Size: Portfolio average
   • Customer Satisfaction: Based on processing time

════════════════════════════════════════════════════════════════════════════════════════════
            Last Updated: November 18, 2025 | Microfinance Loan Tracker v1.0
════════════════════════════════════════════════════════════════════════════════════════════
            """;
    }

    private VBox createFooter() {
        VBox footer = new VBox(8);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1 0 0 0;");

        Label legendTitle = new Label("📌 LEGEND & DATA INTERPRETATION");
        legendTitle.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        HBox legendBox = new HBox(30);
        legendBox.setPadding(new Insets(10));

        legendBox.getChildren().addAll(
            createLegendItem("💰", "Amount in USD Currency", "#3498db"),
            createLegendItem("🔄", "Active Loan Status", "#f39c12"),
            createLegendItem("✅", "Completed Loan Status", "#2ecc71"),
            createLegendItem("📌", "Outstanding Balance Due", "#e74c3c"),
            createLegendItem("📊", "Repayment Performance %", "#9b59b6")
        );

        Label infoLabel = new Label(
            "ℹ️ All amounts are in USD | Dates in MM/DD/YYYY format | Interest rates shown as Annual Percentage Rate (APR) | " +
            "Loan duration specified in months | Outstanding balance represents total amount still owed by borrower"
        );
        infoLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #555; -fx-wrap-text: true;");
        infoLabel.setWrapText(true);

        footer.getChildren().addAll(legendTitle, legendBox, new Separator(), infoLabel);
        return footer;
    }

    private HBox createLegendItem(String icon, String text, String color) {
        HBox hbox = new HBox(10);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 14;");

        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 10; -fx-text-fill: #555;");

        hbox.getChildren().addAll(iconLabel, textLabel);
        return hbox;
    }

    /**
     * Inner class for loan detail table records
     */
    public static class LoanDetailRecord {
        private final javafx.beans.property.SimpleStringProperty loanId;
        private final javafx.beans.property.SimpleStringProperty borrower;
        private final javafx.beans.property.SimpleStringProperty amount;
        private final javafx.beans.property.SimpleStringProperty outstanding;
        private final javafx.beans.property.SimpleStringProperty interestRate;
        private final javafx.beans.property.SimpleStringProperty status;

        public LoanDetailRecord(Loan loan) {
            this.loanId = new javafx.beans.property.SimpleStringProperty(String.valueOf(loan.getId()));
            this.borrower = new javafx.beans.property.SimpleStringProperty(loan.getBorrowerName());
            this.amount = new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", loan.getLoanAmount()));
            this.outstanding = new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", loan.getOutstandingBalance()));
            this.interestRate = new javafx.beans.property.SimpleStringProperty(String.format("%.2f%%", loan.getInterestRate()));
            this.status = new javafx.beans.property.SimpleStringProperty(loan.getStatus());
        }

        public javafx.beans.property.SimpleStringProperty loanIdProperty() { return loanId; }
        public javafx.beans.property.SimpleStringProperty borrowerProperty() { return borrower; }
        public javafx.beans.property.SimpleStringProperty amountProperty() { return amount; }
        public javafx.beans.property.SimpleStringProperty outstandingProperty() { return outstanding; }
        public javafx.beans.property.SimpleStringProperty interestRateProperty() { return interestRate; }
        public javafx.beans.property.SimpleStringProperty statusProperty() { return status; }
    }
}
