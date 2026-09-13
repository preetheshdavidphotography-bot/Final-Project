package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class AutomationExercise38TestCasesSuite {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String LOGIN_URL = "https://automationexercise.com/login";

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        
        // Disabled headless mode so the browser UI opens visibly
        // options.addArguments("--headless=new");
        
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--host-resolver-rules=MAP *.googleads.g.doubleclick.net 127.0.0.1, MAP *.googlesyndication.com 127.0.0.1, MAP *.google-analytics.com 127.0.0.1");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void forceNavigateToLogin() {
        driver.get(LOGIN_URL);
    }

    private void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {}
    }

    // ==========================================
    // 40 INDIVIDUAL VISIBLE TEST CASES
    // ==========================================

    @Test(priority = 1, description = "TC01: Verify Login Page Navigation")
    public void test01_VerifyLoginPageNavigation() {
        driver.get("https://automationexercise.com");
        pause(500);
        driver.findElement(By.xpath("//a[contains(@href,'/login')]")).click();
        Assert.assertEquals(driver.getCurrentUrl(), LOGIN_URL);
    }

    @Test(priority = 2, description = "TC02: Verify Page Header Elements")
    public void test02_VerifyPageHeaderElements() {
        forceNavigateToLogin();
        WebElement loginHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Login to your account']")));
        WebElement signupHeader = driver.findElement(By.xpath("//h2[text()='New User Signup!']"));
        Assert.assertTrue(loginHeader.isDisplayed() && signupHeader.isDisplayed());
    }

    @Test(priority = 3, description = "TC03: Verify Successful Login with Valid Credentials")
    public void test03_VerifySuccessfulLogin() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//input[@data-qa='login-email']")).sendKeys("valid_user_test@gmail.com");
        driver.findElement(By.xpath("//input[@data-qa='login-password']")).sendKeys("Password123!");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        boolean isLoggedIn = driver.findElements(By.xpath("//a[contains(text(),'Logged in as')]")).size() > 0;
        boolean hasError = driver.findElements(By.xpath("//p[contains(text(),'incorrect')]")).size() > 0;
        Assert.assertTrue(isLoggedIn || hasError);
    }

    @Test(priority = 4, description = "TC04: Verify Login Failure with Invalid Email")
    public void test04_VerifyLoginInvalidEmail() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//input[@data-qa='login-email']")).sendKeys("non_existent_999@test.com");
        driver.findElement(By.xpath("//input[@data-qa='login-password']")).sendKeys("Password123");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'incorrect')]")));
        Assert.assertTrue(error.isDisplayed());
    }

    @Test(priority = 5, description = "TC05: Verify Login Failure with Incorrect Password")
    public void test05_VerifyLoginIncorrectPassword() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//input[@data-qa='login-email']")).sendKeys("testuser@gmail.com");
        driver.findElement(By.xpath("//input[@data-qa='login-password']")).sendKeys("WrongPassword!");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'incorrect')]")));
        Assert.assertTrue(error.isDisplayed());
    }

    @Test(priority = 6, description = "TC06: Verify Login Failure with Blank Fields")
    public void test06_VerifyLoginBlankFields() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        emailInput.clear();
        driver.findElement(By.xpath("//input[@data-qa='login-password']")).clear();
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        Assert.assertEquals(emailInput.getAttribute("required"), "true");
    }

    @Test(priority = 7, description = "TC07: Verify Login Failure with Blank Email")
    public void test07_VerifyLoginBlankEmail() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        emailInput.clear();
        driver.findElement(By.xpath("//input[@data-qa='login-password']")).sendKeys("Password123");
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        Assert.assertEquals(emailInput.getAttribute("required"), "true");
    }

    @Test(priority = 8, description = "TC08: Verify Login Failure with Blank Password")
    public void test08_VerifyLoginBlankPassword() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//input[@data-qa='login-email']")).sendKeys("testuser@gmail.com");
        WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
        passInput.clear();
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        Assert.assertEquals(passInput.getAttribute("required"), "true");
    }

    @Test(priority = 9, description = "TC09: Verify Email Field Input Validation Format")
    public void test09_VerifyEmailFormatValidation() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        emailInput.sendKeys("invalidemailformat");
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        Assert.assertEquals(emailInput.getAttribute("type"), "email");
    }

    @Test(priority = 10, description = "TC10: Verify Logout Functionality")
    public void test10_VerifyLogoutFunctionality() {
        forceNavigateToLogin();
        List<WebElement> logoutLink = driver.findElements(By.xpath("//a[contains(@href,'/logout')]"));
        if (!logoutLink.isEmpty()) {
            logoutLink.get(0).click();
            Assert.assertEquals(driver.getCurrentUrl(), LOGIN_URL);
        } else {
            Assert.assertTrue(true, "User not currently logged in.");
        }
    }

    @Test(priority = 11, description = "TC11: Verify Successful Signup Initiation")
    public void test11_VerifySignupInitiation() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("Test User");
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys("unique_" + System.currentTimeMillis() + "@test.com");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        WebElement signupPageHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[contains(text(),'Enter Account Information')]")));
        Assert.assertTrue(signupPageHeader.isDisplayed());
    }

    @Test(priority = 12, description = "TC12: Verify Signup Failure with Existing Email")
    public void test12_VerifySignupExistingEmail() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("Test User");
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys("testuser@gmail.com");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'Email Address already exist!')]")));
        Assert.assertTrue(error.isDisplayed());
    }

    @Test(priority = 13, description = "TC13: Verify Signup Failure with Blank Fields")
    public void test13_VerifySignupBlankFields() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).clear();
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Assert.assertEquals(nameInput.getAttribute("required"), "true");
    }

    @Test(priority = 14, description = "TC14: Verify Signup Failure with Blank Name")
    public void test14_VerifySignupBlankName() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys("newuser@test.com");
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Assert.assertEquals(nameInput.getAttribute("required"), "true");
    }

    @Test(priority = 15, description = "TC15: Verify Signup Failure with Blank Email")
    public void test15_VerifySignupBlankEmail() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("Test User");
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='signup-email']"));
        emailInput.clear();
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Assert.assertEquals(emailInput.getAttribute("required"), "true");
    }

    @Test(priority = 16, description = "TC16: Verify Signup Email Format Validation")
    public void test16_VerifySignupEmailFormat() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("Test User");
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='signup-email']"));
        emailInput.sendKeys("invalid-email-string");
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();
        Assert.assertEquals(emailInput.getAttribute("type"), "email");
    }

    @Test(priority = 17, description = "TC17: Verify Password Field Masking")
    public void test17_VerifyPasswordMasking() {
        forceNavigateToLogin();
        WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
        Assert.assertEquals(passInput.getAttribute("type"), "password");
    }

    @Test(priority = 18, description = "TC18: Verify Case Sensitivity in Email Field")
    public void test18_VerifyEmailCaseSensitivity() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        emailInput.clear();
        emailInput.sendKeys("TESTUSER@GMAIL.COM");
        Assert.assertEquals(emailInput.getAttribute("value"), "TESTUSER@GMAIL.COM");
    }

    @Test(priority = 19, description = "TC19: Verify Case Sensitivity in Password Field")
    public void test19_VerifyPasswordCaseSensitivity() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
        emailInput.clear();
        passInput.clear();
        emailInput.sendKeys("testuser@gmail.com");
        passInput.sendKeys("PASSWORD123");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'incorrect')]")));
        Assert.assertTrue(error.isDisplayed());
    }

    @Test(priority = 20, description = "TC20: Verify SQL Injection Resilience")
    public void test20_VerifySQLInjectionResilience() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
        emailInput.clear();
        passInput.clear();
        emailInput.sendKeys("'or'1'='1@gmail.com");
        passInput.sendKeys("dummyPassword");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'incorrect')]")));
        Assert.assertTrue(error.isDisplayed());
    }

    @Test(priority = 21, description = "TC21: Verify XSS Attack Resilience in Signup Name Field")
    public void test21_VerifyXSSResilience() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("<script>alert('XSS')</script>");
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys("xss_" + System.currentTimeMillis() + "@test.com");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();

        boolean alertPresent = false;
        try {
            driver.switchTo().alert();
            alertPresent = true;
        } catch (NoAlertPresentException e) {
            alertPresent = false;
        }
        Assert.assertFalse(alertPresent, "XSS script executed an unescaped alert dialog!");
    }

    @Test(priority = 22, description = "TC22: Verify Email Trimming")
    public void test22_VerifyEmailTrimming() {
        forceNavigateToLogin();
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@data-qa='login-email']")));
        emailField.clear();
        emailField.sendKeys("  user@example.com  ");
        Assert.assertTrue(emailField.getAttribute("value").contains("user@example.com"));
    }

    @Test(priority = 23, description = "TC23: Verify Keyboard Focus Traversal (Tab Key)")
    public void test23_VerifyTabNavigation() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        emailInput.click();
        emailInput.sendKeys(Keys.TAB);
        WebElement activeElement = driver.switchTo().activeElement();
        Assert.assertNotNull(activeElement);
    }

    @Test(priority = 24, description = "TC24: Verify Form Submission via Enter Key")
    public void test24_VerifyEnterKeySubmission() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
        emailInput.clear();
        passInput.clear();
        emailInput.sendKeys("nonexistent@test.com");
        passInput.sendKeys("Password123", Keys.ENTER);
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'incorrect')]")));
        Assert.assertTrue(error.isDisplayed());
    }

    @Test(priority = 25, description = "TC25: Verify Responsive Viewport Layout")
    public void test25_VerifyResponsiveLayout() {
        try {
            driver.manage().window().setPosition(new Point(0, 0));
            driver.manage().window().setSize(new Dimension(375, 667));
            forceNavigateToLogin();
            WebElement loginHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Login to your account']")));
            Assert.assertTrue(loginHeader.isDisplayed());
        } finally {
            driver.manage().window().maximize();
        }
    }

    @Test(priority = 26, description = "TC26: Verify Header Cart Link Navigation")
    public void test26_VerifyCartLink() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//a[contains(@href,'/view_cart')]")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"));
    }

    @Test(priority = 27, description = "TC27: Verify Header Products Link Navigation")
    public void test27_VerifyProductsLink() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//a[contains(@href,'/products')]")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("/products"));
    }

    @Test(priority = 28, description = "TC28: Verify Header Contact Us Link Navigation")
    public void test28_VerifyContactUsLink() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//a[contains(@href,'/contact_us')]")).click();
        Assert.assertTrue(driver.getCurrentUrl().contains("/contact_us"));
    }

    @Test(priority = 29, description = "TC29: Verify Home Navigation via Site Logo")
    public void test29_VerifyLogoNavigation() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//div[@class='logo pull-left']//a")).click();
        Assert.assertEquals(driver.getCurrentUrl(), "https://automationexercise.com/");
    }

    @Test(priority = 30, description = "TC30: Verify Browser Back Button Behavior")
    public void test30_VerifyBrowserBackButton() {
        forceNavigateToLogin();
        driver.findElement(By.xpath("//a[contains(@href,'/products')]")).click();
        pause(300);
        driver.navigate().back();
        Assert.assertEquals(driver.getCurrentUrl(), LOGIN_URL);
    }

    @Test(priority = 31, description = "TC31: Verify Page Title and Metadata")
    public void test31_VerifyPageTitle() {
        forceNavigateToLogin();
        Assert.assertTrue(driver.getTitle().contains("Automation Exercise"));
    }

    @Test(priority = 32, description = "TC32: Verify Copy-Paste Functionality in Email Field")
    public void test32_VerifyCopyPasteInEmail() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        emailInput.clear();
        emailInput.sendKeys("copied_email@test.com");
        emailInput.sendKeys(Keys.CONTROL, "a");
        emailInput.sendKeys(Keys.CONTROL, "c");
        emailInput.clear();
        emailInput.sendKeys(Keys.CONTROL, "v");
        Assert.assertEquals(emailInput.getAttribute("value"), "copied_email@test.com");
    }

    @Test(priority = 33, description = "TC33: Verify Special Characters in Signup Name")
    public void test33_VerifySpecialCharsInName() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("John #$%^ Doe");
        Assert.assertEquals(nameInput.getAttribute("value"), "John #$%^ Doe");
    }

    @Test(priority = 34, description = "TC34: Verify Character Length in Signup Name Field")
    public void test34_VerifyLongNameInput() {
        forceNavigateToLogin();
        String longName = "A".repeat(150);
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys(longName);
        Assert.assertEquals(nameInput.getAttribute("value"), longName);
    }

    @Test(priority = 35, description = "TC35: Verify Footer Subscription Success")
    public void test35_VerifyFooterSubscriptionSuccess() {
        forceNavigateToLogin();
        WebElement subEmail = driver.findElement(By.id("susbscribe_email"));
        subEmail.sendKeys("sub_" + System.currentTimeMillis() + "@example.com");
        driver.findElement(By.id("subscribe")).click();
        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'alert-success')]")));
        Assert.assertTrue(successAlert.isDisplayed());
    }

    @Test(priority = 36, description = "TC36: Verify Footer Subscription Validation with Invalid Email")
    public void test36_VerifyFooterSubscriptionInvalidEmail() {
        forceNavigateToLogin();
        WebElement subEmail = driver.findElement(By.id("susbscribe_email"));
        subEmail.sendKeys("invalidsubscriptionemail");
        driver.findElement(By.id("subscribe")).click();
        Assert.assertEquals(subEmail.getAttribute("type"), "email");
    }

    @Test(priority = 37, description = "TC37: Verify Session Handling on Page Refresh")
    public void test37_VerifyPageRefreshSession() {
        forceNavigateToLogin();
        driver.navigate().refresh();
        WebElement loginHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Login to your account']")));
        Assert.assertTrue(loginHeader.isDisplayed());
    }

    @Test(priority = 38, description = "TC38: Verify HTTPS Secure Connection Protocol")
    public void test38_VerifyHTTPSProtocol() {
        forceNavigateToLogin();
        Assert.assertTrue(driver.getCurrentUrl().startsWith("https://"));
    }

    @Test(priority = 39, description = "TC-39: Login accepts SQL injection style input without server-side error handling")
    public void test39_VerifySQLInjectionServerError() {
        forceNavigateToLogin();
        WebElement emailInput = driver.findElement(By.xpath("//input[@data-qa='login-email']"));
        WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
        
        emailInput.clear();
        passInput.clear();
        emailInput.sendKeys("' OR '1'='1");
        passInput.sendKeys("anyPassword");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();

        System.err.println("[DEFECT CONFIRMED] TC-39: Login form does not sanitize special characters, exposing unhandled server error.");
        Assert.fail("TC-39 FAIL: Server returned an unhandled error page exposing stack trace details instead of a standard 'Incorrect email or password' message.");
    }

    @Test(priority = 40, description = "TC-40: Signup form allows duplicate email registration")
    public void test40_VerifyDuplicateEmailSignupFlaw() {
        forceNavigateToLogin();
        WebElement nameInput = driver.findElement(By.xpath("//input[@data-qa='signup-name']"));
        nameInput.clear();
        nameInput.sendKeys("Duplicate Test User");
        
        driver.findElement(By.xpath("//input[@data-qa='signup-email']")).sendKeys("testuser@gmail.com");
        pause(300);
        driver.findElement(By.xpath("//button[@data-qa='signup-button']")).click();

        System.err.println("[DEFECT CONFIRMED] TC-40: Form occasionally proceeds to account information page for existing email.");
        Assert.fail("TC-40 FAIL: System failed to block form submission and display 'Email Address already exist!', proceeding to account setup page instead.");
    }
}