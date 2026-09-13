package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class AutomationExerciseApiTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private static final String API_LIST_URL = "https://automationexercise.com/api_list";

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        js = (JavascriptExecutor) driver;

        // Open page once before all tests
        driver.get(API_LIST_URL);
        
        // Handle initial load or popups if present
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h2[contains(@class,'title') or contains(text(),'APIs List') or .//b[contains(text(),'APIs List')]]")
        ));
    }

    // Helper method to scroll to, highlight, open tab, and verify content
    private void openAndVerifyApiTab(int index, String expectedTitleSnippet) {
        String panelXpath = "(//div[@class='panel-group']//div[@class='panel panel-default'])[" + index + "]";
        
        WebElement panel = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(panelXpath)));

        // 1. Smooth scroll into center view
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", panel);
        
        // 2. Highlight border in red
        js.executeScript("arguments[0].style.border='3px solid red'", panel);

        // 3. Expand the tab if not already expanded
        WebElement toggleLink = panel.findElement(By.xpath(".//a[@data-toggle='collapse'] | .//h4/a"));
        WebElement collapseContent = panel.findElement(By.xpath(".//div[contains(@class,'panel-collapse')]"));
        
        if (!collapseContent.isDisplayed()) {
            try {
                toggleLink.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", toggleLink);
            }
        }

        // Wait briefly for smooth expansion transition
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 4. Verify title text inside panel
        String panelText = panel.getText();
        Assert.assertTrue(panelText.contains(expectedTitleSnippet), 
                "Test Case " + index + " title mismatch! Expected snippet: " + expectedTitleSnippet);
        System.out.println("PASSED -> Test Case " + index + " opened & verified.");
    }

    @Test(priority = 1, description = "Test Case 1: API 1 - Get All Products List")
    public void testCase01_getAllProductsList() {
        openAndVerifyApiTab(1, "API 1: Get All Products List");
    }

    @Test(priority = 2, description = "Test Case 2: API 2 - POST To All Products List")
    public void testCase02_postToAllProductsList() {
        openAndVerifyApiTab(2, "API 2: POST To All Products List");
    }

    @Test(priority = 3, description = "Test Case 3: API 3 - Get All Brands List")
    public void testCase03_getAllBrandsList() {
        openAndVerifyApiTab(3, "API 3: Get All Brands List");
    }

    @Test(priority = 4, description = "Test Case 4: API 4 - PUT To All Brands List")
    public void testCase04_putToAllBrandsList() {
        openAndVerifyApiTab(4, "API 4: PUT To All Brands List");
    }

    @Test(priority = 5, description = "Test Case 5: API 5 - POST To Search Product")
    public void testCase05_postToSearchProduct() {
        openAndVerifyApiTab(5, "API 5: POST To Search Product");
    }

    @Test(priority = 6, description = "Test Case 6: API 6 - POST To Search Product without parameter")
    public void testCase06_postToSearchProductWithoutParameter() {
        openAndVerifyApiTab(6, "API 6: POST To Search Product");
    }

    @Test(priority = 7, description = "Test Case 7: API 7 - POST To Verify Login with valid details")
    public void testCase07_postToVerifyLoginValid() {
        openAndVerifyApiTab(7, "API 7: POST To Verify Login");
    }

    @Test(priority = 8, description = "Test Case 8: API 8 - POST To Verify Login without email parameter")
    public void testCase08_postToVerifyLoginWithoutEmail() {
        openAndVerifyApiTab(8, "API 8: POST To Verify Login");
    }

    @Test(priority = 9, description = "Test Case 9: API 9 - DELETE To Verify Login")
    public void testCase09_deleteVerifyLogin() {
        openAndVerifyApiTab(9, "API 9: DELETE To Verify Login");
    }

    @Test(priority = 10, description = "Test Case 10: API 10 - POST To Verify Login with invalid details")
    public void testCase10_postToVerifyLoginInvalid() {
        openAndVerifyApiTab(10, "API 10: POST To Verify Login");
    }

    @Test(priority = 11, description = "Test Case 11: API 11 - POST To Create/Register User Account")
    public void testCase11_postToCreateAccount() {
        openAndVerifyApiTab(11, "API 11: POST To Create/Register User Account");
    }

    @Test(priority = 12, description = "Test Case 12: API 12 - DELETE METHOD To Delete User Account")
    public void testCase12_deleteUserAccount() {
        openAndVerifyApiTab(12, "API 12: DELETE METHOD To Delete User Account");
    }

    @Test(priority = 13, description = "Test Case 13: API 13 - PUT METHOD To Update User Account")
    public void testCase13_putUpdateUserAccount() {
        openAndVerifyApiTab(13, "API 13: PUT METHOD To Update User Account");
    }

    @Test(priority = 14, description = "Test Case 14: API 14 - GET user account detail by email")
    public void testCase14_getUserAccountDetailByEmail() {
        openAndVerifyApiTab(14, "API 14: GET user account detail by email");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}