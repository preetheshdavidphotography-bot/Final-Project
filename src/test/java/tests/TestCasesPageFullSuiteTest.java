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

public class TestCasesPageFullSuiteTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String TEST_CASES_URL = "https://automationexercise.com/test_cases";

    // Execution delay per step (1.2 seconds)
    private final int DELAY_MS = 1200;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--host-resolver-rules=MAP *.googleads.g.doubleclick.net 127.0.0.1, MAP *.googlesyndication.com 127.0.0.1, MAP *.google-analytics.com 127.0.0.1");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        wait = new WebDriverWait(driver, Duration.ofSeconds(4));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            sleep(DELAY_MS * 2);
            driver.quit();
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {}
    }

    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void slowScrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        sleep(600);
    }

    private void verifyTestCaseAccordion(int index) {
        driver.get(TEST_CASES_URL);
        sleep(DELAY_MS);

        List<WebElement> panels = driver.findElements(By.xpath("//a[contains(@data-toggle,'collapse')]"));
        Assert.assertTrue(panels.size() >= index, "Test case accordion " + index + " not found.");

        WebElement panel = panels.get(index - 1);
        slowScrollIntoView(panel);
        sleep(DELAY_MS);

        jsClick(panel);

        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("(//div[contains(@class,'panel-collapse')])[" + index + "]")));
        Assert.assertTrue(body.isDisplayed(), "Test Case panel " + index + " failed to expand.");
        sleep(DELAY_MS);
    }

    private void verifyOptionalAdSection(String sectionText) {
        driver.get(TEST_CASES_URL);
        sleep(DELAY_MS);

        List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(),'" + sectionText + "')]"));
        if (!elements.isEmpty()) {
            slowScrollIntoView(elements.get(0));
            sleep(DELAY_MS);
            System.out.println("Found section: " + sectionText);
        } else {
            System.out.println("Section '" + sectionText + "' dynamic/ad element not loaded. Skipped safely.");
        }
    }

    // --- 20 INDIVIDUAL TEST CASES ---

    @Test(priority = 1, description = "TC01: Verify Test Cases Page Header")
    public void test01_VerifyPageHeader() {
        driver.get(TEST_CASES_URL);
        sleep(DELAY_MS);
        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//b[contains(text(),'Test Cases')]")));
        slowScrollIntoView(header);
        Assert.assertTrue(header.isDisplayed(), "Test Cases header not displayed!");
    }

    @Test(priority = 2, description = "TC02: Verify 'Feedback for Us' Email Link")
    public void test02_VerifyFeedbackForUsLink() {
        driver.get(TEST_CASES_URL);
        sleep(DELAY_MS);
        WebElement feedbackLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href,'mailto:feedback@automationexercise.com')]")));
        slowScrollIntoView(feedbackLink);
        Assert.assertTrue(feedbackLink.isDisplayed(), "'Feedback for Us' link is not visible.");
        Assert.assertEquals(feedbackLink.getAttribute("href"), "mailto:feedback@automationexercise.com");
    }

    @Test(priority = 3, description = "TC03: Verify Expandable Test Case 1")
    public void test03_VerifyTestCase1() { verifyTestCaseAccordion(1); }

    @Test(priority = 4, description = "TC04: Verify Expandable Test Case 2")
    public void test04_VerifyTestCase2() { verifyTestCaseAccordion(2); }

    @Test(priority = 5, description = "TC05: Verify Expandable Test Case 3")
    public void test05_VerifyTestCase3() { verifyTestCaseAccordion(3); }

    @Test(priority = 6, description = "TC06: Verify Expandable Test Case 4")
    public void test06_VerifyTestCase4() { verifyTestCaseAccordion(4); }

    @Test(priority = 7, description = "TC07: Verify Expandable Test Case 5")
    public void test07_VerifyTestCase5() { verifyTestCaseAccordion(5); }

    @Test(priority = 8, description = "TC08: Verify Expandable Test Case 6")
    public void test08_VerifyTestCase6() { verifyTestCaseAccordion(6); }

    @Test(priority = 9, description = "TC09: Verify Expandable Test Case 7")
    public void test09_VerifyTestCase7() { verifyTestCaseAccordion(7); }

    @Test(priority = 10, description = "TC10: Verify Expandable Test Case 8")
    public void test10_VerifyTestCase8() { verifyTestCaseAccordion(8); }

    @Test(priority = 11, description = "TC11: Verify Expandable Test Case 9")
    public void test11_VerifyTestCase9() { verifyTestCaseAccordion(9); }

    @Test(priority = 12, description = "TC12: Verify Expandable Test Case 10")
    public void test12_VerifyTestCase10() { verifyTestCaseAccordion(10); }

    @Test(priority = 13, description = "TC13: Verify Expandable Test Case 11")
    public void test13_VerifyTestCase11() { verifyTestCaseAccordion(11); }

    @Test(priority = 14, description = "TC14: Verify Expandable Test Case 12")
    public void test14_VerifyTestCase12() { verifyTestCaseAccordion(12); }

    @Test(priority = 15, description = "TC15: Verify Expandable Test Case 13")
    public void test15_VerifyTestCase13() { verifyTestCaseAccordion(13); }

    @Test(priority = 16, description = "TC16: Verify Expandable Test Case 14")
    public void test16_VerifyTestCase14() { verifyTestCaseAccordion(14); }

    @Test(priority = 17, description = "TC17: Verify Expandable Test Case 15")
    public void test17_VerifyTestCase15() { verifyTestCaseAccordion(15); }

    @Test(priority = 18, description = "TC18: Verify Discover More - Computer Security")
    public void test18_VerifyDiscoverMoreComputerSecurity() { verifyOptionalAdSection("Computer Security"); }

    @Test(priority = 19, description = "TC19: Verify Discover More - Dictionaries & Encyclopedias")
    public void test19_VerifyDiscoverMoreDictionaries() { verifyOptionalAdSection("Dictionaries & Encyclopedias"); }

    @Test(priority = 20, description = "TC20: Verify Discover More - Networking")
    public void test20_VerifyDiscoverMoreNetworking() { verifyOptionalAdSection("Networking"); }
}