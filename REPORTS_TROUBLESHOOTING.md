# Reports Button Not Working - Troubleshooting Guide

## Issue

The Reports button in the main application window is not responding when clicked.

## Root Causes & Solutions

### 1. Database Connection Issue (Most Common)

**Symptom:** Reports button click shows an error alert "Database is not available"

**Fix:**
The Reports window now opens even without database connection. Make sure you recompiled after the fix.

```bash
cd /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker
mvn clean compile
```

Then run:
```bash
mvn javafx:run
```

### 2. Console Output for Debugging

When you click Reports, check the console for debug messages:

**Expected Output:**
```
[DEBUG] Opening Reports window...
[DEBUG] Database available: true/false
[DEBUG] Reports window opened successfully
```

**If you see errors:**
```
[ERROR] Failed to open Reports window: [error message]
```

This tells you the exact problem.

### 3. Check If Window Is Opening Behind Main Window

**Solution:** On macOS, windows sometimes open behind the main window:
- Click on the main application window to bring it to focus
- Press `Cmd+Tab` to switch between windows
- Check your dock for additional windows

### 4. Verify ReportsWindow.java Exists

Check that the file exists:
```bash
ls -la /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker/src/main/java/com/microfinance/ui/ReportsWindow.java
```

You should see:
```
-rw-r--r--  1 user  staff  [size] ... ReportsWindow.java
```

### 5. Clear Maven Cache

Sometimes old compiled files cause issues:

```bash
cd /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker
rm -rf target/
mvn clean compile
mvn javafx:run
```

### 6. Verify Button Handler Is Wired Correctly

In App.java, the Reports button should have this handler (around line 159):
```java
reportsBtn.setOnAction(e -> handleReports());
```

And the handleReports() method should contain:
```java
private void handleReports() {
    try {
        System.out.println("[DEBUG] Opening Reports window...");
        ReportsWindow reportsWindow = new ReportsWindow(loanDAO);
        reportsWindow.show();
        statusLabel.setText("Reports window opened");
        System.out.println("[DEBUG] Reports window opened successfully");
    } catch (Exception e) {
        System.err.println("[ERROR] Failed to open Reports window: " + e.getMessage());
        e.printStackTrace();
        showAlert("Error", "Failed to open Reports window:\n" + e.getMessage());
    }
}
```

## Step-by-Step Testing

### Step 1: Clean and Rebuild
```bash
cd /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker
mvn clean
mvn compile
```

### Step 2: Check for Compilation Errors
The output should end with:
```
[INFO] BUILD SUCCESS
```

### Step 3: Run the Application
```bash
mvn javafx:run
```

### Step 4: Monitor Console While Clicking Reports

Keep the terminal window visible. When you click the Reports button, you should see debug output like:
```
[DEBUG] Opening Reports window...
[DEBUG] Database available: false
[DEBUG] Reports window opened successfully
```

### Step 5: Look for the Reports Window

After clicking Reports:
1. Look for a new window titled "Microfinance Loan Tracker - Reports & Analytics"
2. If not visible on screen, check behind the main window
3. Check your macOS dock for additional windows

## If Still Not Working

### Increase Debug Output

Edit App.java and add this at the very start of handleReports():
```java
System.out.println("\n=== REPORTS BUTTON CLICKED ===");
System.out.println("Time: " + java.time.LocalDateTime.now());
System.out.println("Thread: " + Thread.currentThread().getName());
System.out.println("LoanDAO: " + (loanDAO != null ? "initialized" : "NULL"));
```

### Check Event Handler Registration

The button click handler might not be registered. Add this debug line in createCenterContent():
```java
System.out.println("[DEBUG] Reports button handler registered");
reportsBtn.setOnAction(e -> {
    System.out.println("[DEBUG] Reports button clicked!");
    handleReports();
});
```

## Common Messages & Solutions

| Message | Solution |
|---------|----------|
| "Database is not available" | **FIXED** - Reports now opens offline |
| "Failed to open Reports window" | Check console error - fix the error shown |
| Button doesn't respond at all | Recompile with `mvn clean compile` |
| Window opens but blank | Database queries failing - check MySQL |
| Window doesn't appear | Check macOS dock or press Cmd+Tab |

## Verification Checklist

✅ **Prerequisites:**
- [ ] Java 11+ installed
- [ ] Maven installed
- [ ] JavaFX dependencies in pom.xml
- [ ] MySQL running (optional for Reports viewing)

✅ **Code Files Present:**
- [ ] App.java exists
- [ ] ReportsWindow.java exists
- [ ] LoanDAO.java exists
- [ ] All files in target/classes/

✅ **Recent Changes Applied:**
- [ ] handleReports() method updated (no database check)
- [ ] Better error handling in handleReports()
- [ ] Debug logging added

✅ **Build Status:**
- [ ] `mvn clean compile` succeeds
- [ ] No compilation errors shown
- [ ] BUILD SUCCESS displayed

## If You Need to Manually Test the Window

Create a test file to verify ReportsWindow works independently:

1. Create `TestReports.java`:
```java
import com.microfinance.dao.LoanDAO;
import com.microfinance.ui.ReportsWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestReports extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        LoanDAO dao = new LoanDAO();
        ReportsWindow window = new ReportsWindow(dao);
        window.show();
        primaryStage.setTitle("Test");
        primaryStage.show();
    }
}
```

2. Run: `mvn javafx:run -Dexec.mainClass="TestReports"`

## Next Steps

1. **Recompile everything:**
   ```bash
   cd /Users/chrisfernandes/Desktop/java_proj/MicrofinanceLoanTracker
   mvn clean compile
   ```

2. **Run the application:**
   ```bash
   mvn javafx:run
   ```

3. **Click the Reports button** and watch the console output

4. **Report what you see** in the console - this will help identify the exact issue

## Additional Resources

- Check `app.log` file if one exists
- Review `REPORTS_GUIDE.md` for feature documentation
- Check `REPORTS_QUICKSTART.md` for usage instructions

---

**Last Updated:** November 18, 2025
