package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductsPageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://automationexercise.com/products";

    @BeforeClass
    public void setUp() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Block popups and map Google Ad domains to localhost to disable overlays
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--host-resolver-rules=MAP *.googleads.g.doubleclick.net 127.0.0.1, MAP *.googlesyndication.com 127.0.0.1");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get(BASE_URL);
    }

    @Test(priority = 1, description = "Verify page title and meta metadata consistency")
    public void testPageTitleAndMetadata() {
        driver.get(BASE_URL);
        String pageTitle = driver.getTitle();
        Assert.assertTrue(pageTitle.contains("Automation Exercise - All Products"), 
                "Page title mismatch. Found: " + pageTitle);
    }

    @Test(priority = 2, description = "Verify hero promotional banner visibility")
    public void testHeroBannerVisibility() {
        driver.get(BASE_URL);
        WebElement banner = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sale_image")));
        Assert.assertTrue(banner.isDisplayed(), "Hero banner is not visible.");
    }

    @Test(priority = 3, description = "Verify product search functionality")
    public void testValidProductSearch() {
        driver.get(BASE_URL);

        WebElement searchBox = driver.findElement(By.id("search_product"));
        WebElement searchBtn = driver.findElement(By.id("submit_search"));

        searchBox.clear();
        searchBox.sendKeys("Top");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", searchBtn);

        WebElement searchedProductsHeader = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title') and contains(text(),'Searched Products')]"))
        );
        Assert.assertTrue(searchedProductsHeader.isDisplayed(), "Searched Products header not displayed.");
    }

    @Test(priority = 4, description = "Verify category sidebar filter expansion and selection")
    public void testCategorySidebarFilter() {
        driver.get(BASE_URL);

        WebElement womenCategory = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#Women']")));
        womenCategory.click();

        WebElement topsSubCategory = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'/category_products/2')]"))
        );
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", topsSubCategory);

        WebElement categoryHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(categoryHeader.getText().toUpperCase().contains("TOPS"), "Category filtering failed.");
    }

    @Test(priority = 5, description = "Verify filtering by brand name")
    public void testBrandFilter() {
        driver.get(BASE_URL);

        WebElement poloBrand = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/brand_products/Polo']")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", poloBrand);

        WebElement brandHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(brandHeader.getText().toUpperCase().contains("POLO"), "Brand filter header mismatch.");
    }

    @Test(priority = 6, description = "Verify product card UI details")
    public void testProductCardUIDetails() {
        driver.get(BASE_URL);

        List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        Assert.assertFalse(products.isEmpty(), "No products found on page.");

        WebElement firstProduct = products.get(0);
        Assert.assertTrue(firstProduct.findElement(By.xpath(".//img")).isDisplayed(), "Product image missing.");
        Assert.assertTrue(firstProduct.findElement(By.xpath(".//h2")).isDisplayed(), "Product price missing.");
        Assert.assertTrue(firstProduct.findElement(By.xpath(".//p")).isDisplayed(), "Product name missing.");
    }

    @Test(priority = 7, description = "Verify adding item to shopping cart")
    public void testAddToCart() {
        driver.get(BASE_URL);

        WebElement addToCartBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@class,'add-to-cart')])[1]")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        js.executeScript("arguments[0].click();", addToCartBtn);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
        Assert.assertTrue(modal.isDisplayed(), "Cart confirmation modal not displayed.");

        WebElement continueBtn = driver.findElement(By.xpath("//button[text()='Continue Shopping']"));
        js.executeScript("arguments[0].click();", continueBtn);
    }

    @Test(priority = 8, description = "Verify navigation to detailed product page")
    public void testNavigateToProductDetails() {
        driver.get(BASE_URL);

        WebElement viewProductLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@href,'/product_details/')])[1]")));
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", viewProductLink);
        js.executeScript("arguments[0].click();", viewProductLink);

        wait.until(ExpectedConditions.urlContains("/product_details/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/product_details/"), "Failed to navigate to Product Details page.");
    }

    @Test(priority = 9, description = "Verify header navigation links rendering and redirection")
    public void testHeaderLinks() {
        driver.get(BASE_URL);

        WebElement homeLink = driver.findElement(By.xpath("//a[contains(text(),'Home')]"));
        Assert.assertTrue(homeLink.isDisplayed(), "Home nav link not displayed.");

        WebElement cartLink = driver.findElement(By.xpath("//a[contains(text(),'Cart')]"));
        Assert.assertTrue(cartLink.isDisplayed(), "Cart nav link not displayed.");
    }

    @Test(priority = 10, description = "Verify product search with special characters")
    public void testSpecialCharacterSearch() {
        driver.get(BASE_URL);

        WebElement searchBox = driver.findElement(By.id("search_product"));
        WebElement searchBtn = driver.findElement(By.id("submit_search"));

        searchBox.clear();
        searchBox.sendKeys("@#$%^&*");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", searchBtn);

        WebElement searchedProductsHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(searchedProductsHeader.isDisplayed(), "Page failed to handle special character input cleanly.");
    }

    @Test(priority = 11, description = "Verify product search with non-existent keyword")
    public void testNonExistentProductSearch() {
        driver.get(BASE_URL);

        WebElement searchBox = driver.findElement(By.id("search_product"));
        WebElement searchBtn = driver.findElement(By.id("submit_search"));

        searchBox.clear();
        searchBox.sendKeys("NonExistentProductKeyword999");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", searchBtn);

        WebElement searchedProductsHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(searchedProductsHeader.isDisplayed(), "Searched header failed to render for empty search results.");
    }

    @Test(priority = 12, description = "Verify product search with blank input submission")
    public void testBlankProductSearch() {
        driver.get(BASE_URL);

        WebElement searchBox = driver.findElement(By.id("search_product"));
        WebElement searchBtn = driver.findElement(By.id("submit_search"));

        searchBox.clear();

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", searchBtn);

        WebElement searchedProductsHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(searchedProductsHeader.isDisplayed(), "Blank search submission failed to execute.");
    }

    @Test(priority = 13, description = "Verify switching between different brand filters")
    public void testSwitchingBrandFilters() {
        driver.get(BASE_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement poloBrand = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/brand_products/Polo']")));
        js.executeScript("arguments[0].click();", poloBrand);

        WebElement brandHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(brandHeader.getText().toUpperCase().contains("POLO"));

        WebElement madameBrand = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='/brand_products/Madame']")));
        js.executeScript("arguments[0].click();", madameBrand);

        brandHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(brandHeader.getText().toUpperCase().contains("MADAME"), "Switching brand filter failed.");
    }

    @Test(priority = 14, description = "Verify Men category accordion expansion and filtering")
    public void testMenCategoryFilter() {
        driver.get(BASE_URL);

        WebElement menCategory = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#Men']")));
        menCategory.click();

        WebElement tshirtsSubCategory = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'/category_products/3')]"))
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", tshirtsSubCategory);

        WebElement categoryHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(categoryHeader.getText().toUpperCase().contains("TSHIRTS"), "Men category filtering failed.");
    }

    @Test(priority = 15, description = "Verify Kids category accordion expansion and filtering")
    public void testKidsCategoryFilter() {
        driver.get(BASE_URL);

        WebElement kidsCategory = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#Kids']")));
        kidsCategory.click();

        WebElement dressSubCategory = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'/category_products/4')]"))
        );

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", dressSubCategory);

        WebElement categoryHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title')]")));
        Assert.assertTrue(categoryHeader.isDisplayed(), "Kids category filtering failed.");
    }

    @Test(priority = 16, description = "Verify footer newsletter subscription")
    public void testFooterSubscription() {
        driver.get(BASE_URL);

        WebElement emailField = driver.findElement(By.id("susbscribe_email"));
        WebElement subscribeBtn = driver.findElement(By.id("subscribe"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.sendKeys("automation_test_user@example.com");
        js.executeScript("arguments[0].click();", subscribeBtn);

        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-subscribe")));
        Assert.assertTrue(successAlert.isDisplayed(), "Newsletter subscription success message not displayed.");
    }

    @Test(priority = 17, description = "Verify footer subscription with invalid email format")
    public void testInvalidFooterSubscription() {
        driver.get(BASE_URL);

        WebElement emailField = driver.findElement(By.id("susbscribe_email"));
        WebElement subscribeBtn = driver.findElement(By.id("subscribe"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.clear();
        emailField.sendKeys("invalid-email-format");
        js.executeScript("arguments[0].click();", subscribeBtn);

        Boolean isInvalid = (Boolean) js.executeScript("return arguments[0].matches(':invalid');", emailField);
        Assert.assertTrue(isInvalid, "HTML5 validation failed to flag invalid email format.");
    }

    @Test(priority = 18, description = "Verify image broken check across all product cards")
    public void testAllProductImagesLoaded() {
        driver.get(BASE_URL);

        List<WebElement> images = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]//img"));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (WebElement img : images) {
            Boolean loaded = (Boolean) js.executeScript(
                "return arguments[0].complete && typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0;", 
                img
            );
            Assert.assertTrue(loaded, "Product image broken: " + img.getAttribute("src"));
        }
    }

    @Test(priority = 19, description = "Verify scroll-to-top arrow functionality")
    public void testScrollToTopArrow() {
        driver.get(BASE_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement scrollUpBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("scrollUp")));
        js.executeScript("arguments[0].click();", scrollUpBtn);

        // Convert return object via Number to prevent ClassCastException
        wait.until((WebDriver d) -> ((Number) js.executeScript("return window.pageYOffset;")).doubleValue() < 100);
        
        double scrollY = ((Number) js.executeScript("return window.pageYOffset;")).doubleValue();
        Assert.assertTrue(scrollY < 100, "Scroll to top failed. Position: " + scrollY);
    }

    @Test(priority = 20, description = "Verify responsive layout grid behavior across zoom levels")
    public void testResponsiveZoomLevels() {
        driver.get(BASE_URL);

        driver.manage().window().setSize(new Dimension(1024, 768));
        List<WebElement> productsTablet = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        Assert.assertFalse(productsTablet.isEmpty(), "Products hidden on tablet resolution.");

        driver.manage().window().maximize();
        List<WebElement> productsDesktop = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        Assert.assertFalse(productsDesktop.isEmpty(), "Products hidden on desktop resolution.");
    }

    // --- INTENTIONAL FAILURE TESTS (For reporting & failure validation) ---

    @Test(priority = 21, description = "INTENTIONAL FAILURE: Assertion failure on page header text")
    public void testFailureHeaderMismatch() {
        driver.get(BASE_URL);
        WebElement header = driver.findElement(By.xpath("//h2[contains(@class,'title')]"));
        
        // Fails because the actual text is "ALL PRODUCTS", not "DISCOUNT PRODUCTS"
        Assert.assertEquals(header.getText().trim(), "DISCOUNT PRODUCTS", 
                "Intentional Failure: Page header title did not match expected value.");
    }

    @Test(priority = 22, description = "INTENTIONAL FAILURE: Timeout waiting for non-existent element")
    public void testFailureElementNotFound() {
        driver.get(BASE_URL);
        
        // Fails via TimeoutException because id 'non_existent_checkout_button' does not exist on the page
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        WebElement element = shortWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("non_existent_checkout_button")));
        Assert.assertTrue(element.isDisplayed(), "Intentional Failure: Non-existent element was found.");
    }

    @Test(priority = 23, description = "INTENTIONAL FAILURE: Soft check assertion failure on total product count")
    public void testFailureIncorrectProductCount() {
        driver.get(BASE_URL);
        List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        
        // Fails because product count is greater than 0, not 500
        Assert.assertEquals(products.size(), 500, 
                "Intentional Failure: Expected 500 total products on page but found " + products.size());
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Execution finished. 20 passed tests, 3 intentional failed tests.");
        }
    }
}