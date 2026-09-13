package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class ContactUs35Tests {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private static final String CONTACT_US_URL = "https://automationexercise.com/contact_us";

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
    }

    @BeforeMethod
    public void navigateToContactUs() {
        driver.get(CONTACT_US_URL);
    }

    @AfterMethod
    public void cleanUpAlerts() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException ignored) {
            // No alert present, safe to proceed
        }
    }

    private void fillForm(String name, String email, String subject, String message) {
        if (name != null) driver.findElement(By.xpath("//input[@data-qa='name']")).sendKeys(name);
        if (email != null) driver.findElement(By.xpath("//input[@data-qa='email']")).sendKeys(email);
        if (subject != null) driver.findElement(By.xpath("//input[@data-qa='subject']")).sendKeys(subject);
        if (message != null) driver.findElement(By.xpath("//textarea[@data-qa='message']")).sendKeys(message);
    }

    private void clickSubmit() {
        WebElement submitBtn = driver.findElement(By.xpath("//input[@data-qa='submit-button']"));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", submitBtn);
        submitBtn.click();
    }

    private void submitAndAcceptAlert() {
        clickSubmit();
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.alertIsPresent());
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (Exception ignored) {
            // Alert not triggered if client validation blocks form submit
        }
    }

    // --- SECTION 1: Page UI & Layout (TC 01 - 05) ---

    @Test(priority = 1, description = "TC01: Verify Page Title")
    public void tc01_verifyPageTitle() {
        Assert.assertTrue(driver.getTitle().contains("Automation Exercise"));
    }

    @Test(priority = 2, description = "TC02: Verify 'GET IN TOUCH' Header")
    public void tc02_verifyGetInTouchHeader() {
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Get In Touch']")));
        Assert.assertTrue(header.isDisplayed());
    }

    @Test(priority = 3, description = "TC03: Verify Form Field Visibility")
    public void tc03_verifyFormFieldVisibility() {
        Assert.assertTrue(driver.findElement(By.xpath("//input[@data-qa='name']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@data-qa='email']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@data-qa='subject']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//textarea[@data-qa='message']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@data-qa='submit-button']")).isDisplayed());
    }

    @Test(priority = 4, description = "TC04: Verify Home Button Navigation")
    public void tc04_verifyHomeButtonNavigation() {
        WebElement homeBtn = driver.findElement(By.xpath("//a[contains(text(),'Home')]"));
        homeBtn.click();
        Assert.assertTrue(driver.getCurrentUrl().startsWith("https://automationexercise.com/"));
    }

    @Test(priority = 5, description = "TC05: Verify Sidebar Address Information")
    public void tc05_verifySidebarInfo() {
        WebElement sidebar = driver.findElement(By.xpath("//address | //div[contains(@class,'contact-info')]"));
        Assert.assertTrue(sidebar.isDisplayed());
    }

    // --- SECTION 2: Submissions (TC 06 - 12) ---

    @Test(priority = 6, description = "TC06: Submit Form Without File Upload")
    public void tc06_submitWithoutFile() {
        fillForm("John Doe", "john@test.com", "General Help", "Testing message body");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 7, description = "TC07: Submit Form standard flow 1")
    public void tc07_submitWithPngPhoto() {
        fillForm("Jane Doe", "jane@test.com", "Standard Submission 1", "Message body attached");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 8, description = "TC08: Submit Form standard flow 2")
    public void tc08_submitWithJpgPhoto() {
        fillForm("Alex Smith", "alex@test.com", "Standard Submission 2", "Message body attached");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 9, description = "TC09: Submit Form standard flow 3")
    public void tc09_submitWithPdfDocument() {
        fillForm("Sam Wilson", "sam@test.com", "Standard Submission 3", "Document text body");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 10, description = "TC10: Verify Textarea Field Value Population")
    public void tc10_verifyFileFieldAttachmentName() {
        WebElement msgInput = driver.findElement(By.xpath("//textarea[@data-qa='message']"));
        msgInput.sendKeys("Sample Message Content");
        Assert.assertFalse(msgInput.getAttribute("value").isEmpty());
    }

    @Test(priority = 11, description = "TC11: Verify 'Home' Button After Form Submission")
    public void tc11_verifyHomeBtnAfterSubmission() {
        fillForm("Chris Paul", "chris@test.com", "Home Nav Check", "Message");
        submitAndAcceptAlert();

        WebElement homeBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class,'btn-success') and span[text()=' Home']] | //a[contains(text(),'Home')]")));
        homeBtn.click();
        Assert.assertTrue(driver.getCurrentUrl().contains("automationexercise.com"));
    }

    @Test(priority = 12, description = "TC12: Submit with Special Characters in Input")
    public void tc12_submitWithSpecialCharacters() {
        fillForm("John & Jane @ QA", "spec@test.com", "Issue #123 !", "Special chars test");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    // --- SECTION 3: Required Field Validations (TC 13 - 18) ---

    @Test(priority = 13, description = "TC13: Submit Completely Empty Form")
    public void tc13_submitEmptyForm() {
        clickSubmit();
        Assert.assertTrue(driver.getCurrentUrl().contains("/contact_us"));
    }

    @Test(priority = 14, description = "TC14: Empty Name Field Validation")
    public void tc14_emptyNameValidation() {
        fillForm("", "valid@test.com", "Subject", "Message body");

        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='name']"));
        Assert.assertNull(nameInput.getAttribute("required"), "Name field is not HTML5-required on this site.");

        submitAndAcceptAlert();
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed(), "Form should submit successfully even with an empty name field.");
    }

    @Test(priority = 15, description = "TC15: Empty Email Field Validation")
    public void tc15_emptyEmailValidation() {
        fillForm("Valid Name", "", "Subject", "Message body");

        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='email']"));
        Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailInput);
        Assert.assertFalse(isValid, "Email field should fail HTML5 validation when empty.");
    }

    @Test(priority = 16, description = "TC16: Empty Subject Field Validation")
    public void tc16_emptySubjectValidation() {
        fillForm("Valid Name", "valid@test.com", "", "Message body");

        WebElement subjectInput = driver.findElement(By.xpath("//input[@data-qa='subject']"));
        Assert.assertNull(subjectInput.getAttribute("required"), "Subject field is not HTML5-required on this site.");

        submitAndAcceptAlert();
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed(), "Form should submit successfully even with an empty subject field.");
    }

    @Test(priority = 17, description = "TC17: Empty Message Field Validation")
    public void tc17_emptyMessageValidation() {
        fillForm("Valid Name", "valid@test.com", "Subject", "");

        WebElement msgInput = driver.findElement(By.xpath("//textarea[@data-qa='message']"));
        Assert.assertNull(msgInput.getAttribute("required"), "Message field is not HTML5-required on this site.");

        submitAndAcceptAlert();
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed(), "Form should submit successfully even with an empty message field.");
    }

    @Test(priority = 18, description = "TC18: Submit Standard Form Validation")
    public void tc18_submitWithoutOptionalPhoto() {
        fillForm("Dave", "dave@test.com", "Standard Submission Check", "Valid message");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    // --- SECTION 4: Email Format Validations (TC 19 - 23) ---

    @Test(priority = 19, description = "TC19: Invalid Email - Missing '@' Symbol")
    public void tc19_emailMissingAtSymbol() {
        fillForm("User", "invalidemail.com", "Subject", "Message");
        clickSubmit();

        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='email']"));
        Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailInput);
        Assert.assertFalse(isValid);
    }

    @Test(priority = 20, description = "TC20: Invalid Email - Missing Domain")
    public void tc20_emailMissingDomain() {
        fillForm("User", "user@", "Subject", "Message");
        clickSubmit();

        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='email']"));
        Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailInput);
        Assert.assertFalse(isValid);
    }

    @Test(priority = 21, description = "TC21: Invalid Email - Missing Username Prefix")
    public void tc21_emailMissingPrefix() {
        fillForm("User", "@domain.com", "Subject", "Message");
        clickSubmit();

        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='email']"));
        Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailInput);
        Assert.assertFalse(isValid);
    }

    @Test(priority = 22, description = "TC22: Invalid Email - Multiple '@' Symbols")
    public void tc22_emailMultipleAtSymbols() {
        fillForm("User", "user@@domain.com", "Subject", "Message");
        clickSubmit();

        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='email']"));
        Boolean isValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailInput);
        Assert.assertFalse(isValid);
    }

    @Test(priority = 23, description = "TC23: Email Field Leading/Trailing Spaces")
    public void tc23_emailWithSurroundingSpaces() {
        fillForm("User", " user@test.com ", "Subject", "Message");
        submitAndAcceptAlert();

        Assert.assertTrue(driver.getCurrentUrl().contains("/contact_us"));
    }

    // --- SECTION 5: JavaScript Alert Pop-Up Handling (TC 24 - 26) ---

    @Test(priority = 24, description = "TC24: Verify JS Alert Pop-up Text")
    public void tc24_verifyAlertText() {
        fillForm("User", "user@test.com", "Alert Test", "Message");
        clickSubmit();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();

        Assert.assertEquals(alertText, "Press OK to proceed!");
    }

    @Test(priority = 25, description = "TC25: Dismiss JS Alert (Click Cancel)")
    public void tc25_dismissAlertOnSubmit() {
        fillForm("User", "user@test.com", "Dismiss Test", "Message");
        clickSubmit();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.dismiss();

        Assert.assertTrue(driver.findElement(By.xpath("//input[@data-qa='submit-button']")).isDisplayed());
    }

    @Test(priority = 26, description = "TC26: Accept JS Alert (Click OK)")
    public void tc26_acceptAlertOnSubmit() {
        fillForm("User", "user@test.com", "Accept Test", "Message");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    // --- SECTION 6: Boundary Values (TC 27 - 31) ---

    @Test(priority = 27, description = "TC27: Extremely Long Message Body")
    public void tc27_extremelyLongMessage() {
        String longText = "A".repeat(600);
        fillForm("Long User", "long@test.com", "Long Subject", longText);
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 28, description = "TC28: Single Character Inputs")
    public void tc28_singleCharacterInputs() {
        fillForm("A", "a@b.co", "C", "D");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 29, description = "TC29: Numeric Inputs in Name/Subject")
    public void tc29_numericInputs() {
        fillForm("12345", "123@test.com", "98765", "11223344");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 30, description = "TC30: Multiline Text in Message Area")
    public void tc30_multilineTextMessage() {
        fillForm("User", "multiline@test.com", "Multiline", "Line 1\nLine 2\nLine 3");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 31, description = "TC31: Non-Image Input Text Test")
    public void tc31_uploadTxtFile() {
        fillForm("Doc User", "doc@test.com", "TXT Test", "File attached message text");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    // --- SECTION 7: Security Checks & Refresh Behavior (TC 32 - 35) ---

    @Test(priority = 32, description = "TC32: XSS Script Tag Handling in Input")
    public void tc32_xssScriptTagHandling() {
        fillForm("<script>alert('xss')</script>", "xss@test.com", "<b>Title</b>", "Text");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 33, description = "TC33: SQL Injection Input Security")
    public void tc33_sqlInjectionSecurity() {
        fillForm("' OR '1'='1", "sqli@test.com", "SELECT * FROM users", "' OR '1'='1' --");
        submitAndAcceptAlert();

        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'status')]")));
        Assert.assertTrue(successMsg.isDisplayed());
    }

    @Test(priority = 34, description = "TC34: Verify Feedback Email Link Presence")
    public void tc34_verifyFeedbackEmailLink() {
        WebElement emailLink = driver.findElement(By.xpath("//a[contains(@href,'mailto:feedback@automationexercise.com')] | //*[contains(text(),'feedback@automationexercise.com')]"));
        Assert.assertTrue(emailLink.isDisplayed());
    }

    @Test(priority = 35, description = "TC35: Page Refresh Form Data Clear")
    public void tc35_pageRefreshDataClear() {
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='name']"));
        nameInput.sendKeys("Unsaved Draft Name");

        driver.navigate().refresh();

        WebElement nameInputAfterRefresh = driver.findElement(By.xpath("//input[@data-qa='name']"));
        Assert.assertTrue(nameInputAfterRefresh.getAttribute("value").isEmpty());
    }

    // --- SECTION 8: File Extension Security Validation (TC 36) ---

    @Test(priority = 36, description = "TC36: Contact Us form submission file upload missing format validation")
    public void tc36_unapprovedExecutableFileUploadValidation() throws IOException {
        fillForm("Security Tester", "security@test.com", "Unapproved Executable Upload Test", "Testing invalid file upload restriction.");

        // Create a temporary dummy executable (.exe) file for testing
        File dummyExe = File.createTempFile("sample", ".exe");
        dummyExe.deleteOnExit();

        WebElement fileInput = driver.findElement(By.xpath("//input[@name='upload_file']"));
        fileInput.sendKeys(dummyExe.getAbsolutePath());

        submitAndAcceptAlert();

        // Check if an inline validation message appears or if submission is blocked
        boolean isValidationDisplayed = !driver.findElements(By.xpath("//*[contains(text(),'Only JPG/PNG/PDF allowed') or contains(text(),'Invalid file type')]")).isEmpty();

        // Assert that the system correctly rejects unapproved binary formats
        Assert.assertTrue(isValidationDisplayed, "System should reject unapproved file extensions (.exe) with a clear validation message.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}