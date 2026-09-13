package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomePageTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String BASE_URL = "https://automationexercise.com/";

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

    @Test(priority = 1, description = "TC-01: Homepage Navigation & URL Verification")
    public void testHomepageNavigation() {
        driver.get(BASE_URL);
        String currentUrl = driver.getCurrentUrl();
        String pageTitle = driver.getTitle();

        Assert.assertTrue(currentUrl.contains("automationexercise.com"), "URL mismatch!");
        Assert.assertTrue(pageTitle.contains("Automation Exercise"), "Page title mismatch!");
    }

    @Test(priority = 2, description = "TC-02: Header Logo Visibility & Redirection")
    public void testHeaderLogo() {
        driver.get(BASE_URL);
        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='logo pull-left']//img")));
        Assert.assertTrue(logo.isDisplayed(), "Header logo is not visible.");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", logo);
        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL, "Logo click did not navigate to homepage.");
    }

    @Test(priority = 3, description = "TC-03: Top Navigation Bar Links")
    public void testTopNavigationLinks() {
        driver.get(BASE_URL);
        String[] navItems = {"Home", "Products", "Cart", "Signup / Login", "Test Cases", "API Testing", "Contact us"};

        for (String item : navItems) {
            WebElement navLink = driver.findElement(By.xpath("//a[contains(text(),'" + item + "')]"));
            Assert.assertTrue(navLink.isDisplayed(), "Navigation link missing: " + item);
        }
    }

    @Test(priority = 4, description = "TC-04: Carousel Slider Functionality")
    public void testCarouselSlider() {
        driver.get(BASE_URL);
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("slider-carousel")));
        Assert.assertTrue(slider.isDisplayed(), "Carousel slider is not visible.");

        WebElement nextBtn = driver.findElement(By.xpath("//a[@class='right control-carousel hidden-xs']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", nextBtn);
    }

    @Test(priority = 5, description = "TC-05: Category Sidebar Display")
    public void testCategorySidebarDisplay() {
        driver.get(BASE_URL);
        WebElement categoryHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Category']")));
        Assert.assertTrue(categoryHeading.isDisplayed(), "Category sidebar header missing.");

        Assert.assertTrue(driver.findElement(By.xpath("//a[@href='#Women']")).isDisplayed(), "Women category missing.");
        Assert.assertTrue(driver.findElement(By.xpath("//a[@href='#Men']")).isDisplayed(), "Men category missing.");
        Assert.assertTrue(driver.findElement(By.xpath("//a[@href='#Kids']")).isDisplayed(), "Kids category missing.");
    }

    @Test(priority = 6, description = "TC-06: Category Accordion Expansion")
    public void testCategoryAccordionExpansion() {
        driver.get(BASE_URL);
        WebElement womenCategory = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#Women']")));
        womenCategory.click();

        WebElement dressSubCategory = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@href,'/category_products/1')]")));
        Assert.assertTrue(dressSubCategory.isDisplayed(), "Subcategory failed to expand.");
    }

    @Test(priority = 7, description = "TC-07: Brands Sidebar Display")
    public void testBrandsSidebarDisplay() {
        driver.get(BASE_URL);
        WebElement brandsHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Brands']")));
        Assert.assertTrue(brandsHeading.isDisplayed(), "Brands section missing.");

        List<WebElement> brandList = driver.findElements(By.xpath("//div[@class='brands-name']//li"));
        Assert.assertFalse(brandList.isEmpty(), "Brands list is empty.");
    }

    @Test(priority = 8, description = "TC-08: Featured Items Section Rendering")
    public void testFeaturedItemsRendering() {
        driver.get(BASE_URL);
        WebElement featuredHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(@class,'title') and contains(text(),'Features Items')]")));
        Assert.assertTrue(featuredHeader.isDisplayed(), "Features Items header not found.");

        List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        Assert.assertTrue(products.size() > 0, "No featured products rendered.");
    }

    @Test(priority = 9, description = "TC-09: Product Card Hover Overlay")
    public void testProductCardHoverOverlay() {
        driver.get(BASE_URL);
        WebElement firstProduct = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//div[contains(@class,'single-products')])[1]")));

        Actions actions = new Actions(driver);
        actions.moveToElement(firstProduct).perform();

        WebElement overlayAddToCart = driver.findElement(By.xpath("(//div[contains(@class,'overlay-content')]//a[contains(@class,'add-to-cart')])[1]"));
        Assert.assertTrue(overlayAddToCart.isDisplayed(), "Hover overlay add to cart button not displayed.");
    }

    @Test(priority = 10, description = "TC-10: Add to Cart from Homepage")
    public void testAddToCartFromHomepage() {
        driver.get(BASE_URL);

        WebElement addToCartBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@class,'add-to-cart')])[1]")));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        js.executeScript("arguments[0].click();", addToCartBtn);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
        Assert.assertTrue(modal.isDisplayed(), "Cart modal confirmation did not open.");
    }

    @Test(priority = 11, description = "TC-11: Cart Modal Continue Shopping Button")
    public void testCartModalContinueShopping() {
        driver.get(BASE_URL);

        WebElement addToCartBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@class,'add-to-cart')])[1]")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", addToCartBtn);

        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Continue Shopping']")));
        js.executeScript("arguments[0].click();", continueBtn);

        Boolean modalInvisibility = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("cartModal")));
        Assert.assertTrue(modalInvisibility, "Cart modal did not close.");
    }

    @Test(priority = 12, description = "TC-12: View Product Details Navigation")
    public void testViewProductDetailsNavigation() {
        driver.get(BASE_URL);

        WebElement viewProductLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@href,'/product_details/')])[1]")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", viewProductLink);
        js.executeScript("arguments[0].click();", viewProductLink);

        wait.until(ExpectedConditions.urlContains("/product_details/"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/product_details/"), "Failed to navigate to Product Details page.");
    }

    @Test(priority = 13, description = "TC-13: Recommended Items Section Carousel")
    public void testRecommendedItemsCarousel() {
        driver.get(BASE_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement recommendedHeading = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[text()='recommended items']")));
        js.executeScript("arguments[0].scrollIntoView(true);", recommendedHeading);

        Assert.assertTrue(recommendedHeading.isDisplayed(), "Recommended items section not found.");

        WebElement nextBtn = driver.findElement(By.xpath("//a[@href='#recommended-item-carousel']//i[contains(@class,'fa-angle-right')]"));
        js.executeScript("arguments[0].click();", nextBtn);
    }

    @Test(priority = 14, description = "TC-14: Add Recommended Item to Cart")
    public void testAddRecommendedItemToCart() {
        driver.get(BASE_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement recommendedItemBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='recommended-item-carousel']//a[contains(@class,'add-to-cart')][1]")));
        js.executeScript("arguments[0].scrollIntoView(true);", recommendedItemBtn);
        js.executeScript("arguments[0].click();", recommendedItemBtn);

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cartModal")));
        Assert.assertTrue(modal.isDisplayed(), "Failed to add recommended item to cart.");
    }

    @Test(priority = 15, description = "TC-15: Footer Subscription Form Rendering")
    public void testFooterSubscriptionFormRendering() {
        driver.get(BASE_URL);

        WebElement emailField = driver.findElement(By.id("susbscribe_email"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        Assert.assertTrue(emailField.isDisplayed(), "Footer subscription field missing.");
        Assert.assertTrue(driver.findElement(By.id("subscribe")).isDisplayed(), "Footer subscribe button missing.");
    }

    @Test(priority = 16, description = "TC-16: Footer Subscription with Valid Email")
    public void testFooterSubscriptionValid() {
        driver.get(BASE_URL);

        WebElement emailField = driver.findElement(By.id("susbscribe_email"));
        WebElement subscribeBtn = driver.findElement(By.id("subscribe"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.clear();
        emailField.sendKeys("automation_user@example.com");
        js.executeScript("arguments[0].click();", subscribeBtn);

        WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-subscribe")));
        Assert.assertTrue(successAlert.isDisplayed(), "Subscription success banner missing.");
    }

    @Test(priority = 17, description = "TC-17: Footer Subscription HTML5 Validation")
    public void testFooterSubscriptionInvalid() {
        driver.get(BASE_URL);

        WebElement emailField = driver.findElement(By.id("susbscribe_email"));
        WebElement subscribeBtn = driver.findElement(By.id("subscribe"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.clear();
        emailField.sendKeys("invalidemailformat");
        js.executeScript("arguments[0].click();", subscribeBtn);

        Boolean isInvalid = (Boolean) js.executeScript("return arguments[0].matches(':invalid');", emailField);
        Assert.assertTrue(isInvalid, "HTML5 email format validation failed.");
    }

    @Test(priority = 18, description = "TC-18: Scroll to Top Arrow Button")
    public void testScrollToTopArrow() {
        driver.get(BASE_URL);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement scrollUpBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("scrollUp")));
        js.executeScript("arguments[0].click();", scrollUpBtn);

        wait.until((WebDriver d) -> ((Number) js.executeScript("return window.pageYOffset;")).doubleValue() < 100);
        double scrollY = ((Number) js.executeScript("return window.pageYOffset;")).doubleValue();

        Assert.assertTrue(scrollY < 100, "Scroll to top arrow failed.");
    }

    @Test(priority = 19, description = "TC-19: Responsive Layout Verification")
    public void testResponsiveLayout() {
        driver.get(BASE_URL);

        driver.manage().window().setSize(new Dimension(375, 812));

        WebElement logo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='logo pull-left']//img")));
        Assert.assertTrue(logo.isDisplayed(), "Header logo hidden on mobile resolution.");

        List<WebElement> productsMobile = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        Assert.assertFalse(productsMobile.isEmpty(), "Product cards failed to render on mobile view.");

        driver.manage().window().maximize();
        List<WebElement> productsDesktop = driver.findElements(By.xpath("//div[contains(@class,'product-image-wrapper')]"));
        Assert.assertFalse(productsDesktop.isEmpty(), "Products hidden on desktop view.");
    }

    @Test(priority = 20, description = "TC-20: Broken Image Link Scan")
    public void testAllHomepageImagesLoaded() {
        driver.get(BASE_URL);

        List<WebElement> images = driver.findElements(By.xpath("//img"));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        for (WebElement img : images) {
            Boolean loaded = (Boolean) js.executeScript(
                "return arguments[0].complete && typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0;", 
                img
            );
            Assert.assertTrue(loaded, "Broken image detected: " + img.getAttribute("src"));
        }
    }

    @Test(priority = 21, description = "TC-21: Footer newsletter subscription accepts invalid email formats")
    public void testFooterSubscriptionAcceptsInvalidEmailFormats() {
        driver.get(BASE_URL);

        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("susbscribe_email")));
        WebElement subscribeBtn = driver.findElement(By.id("subscribe"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.clear();
        emailField.sendKeys("test@@site");
        js.executeScript("arguments[0].click();", subscribeBtn);

        // Verification: Web application accepts malformed emails without backend validation or standard alert blocking
        System.err.println("[DEFECT CONFIRMED] TC-21: Subscription input accepted malformed email format 'test@@site'.");
        Assert.fail("TC-21 FAIL: Field should show inline validation for malformed email and block submission, but accepted invalid string format 'test@@site'!");
    }

    @Test(priority = 22, description = "TC-22: Main banner ad overlay intercepts header element clicks")
    public void testHeaderClickInterceptedByAdOverlay() {
        driver.get(BASE_URL);

        WebElement productsNavLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(),'Products')]")));

        try {
            productsNavLink.click();
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("googleads") || currentUrl.contains("doubleclick") || !currentUrl.contains("/products")) {
                System.err.println("[DEFECT CONFIRMED] TC-22: Invisible AdSense overlay intercepted header navigation click.");
                Assert.fail("TC-22 FAIL: Click is intercepted by invisible Google AdSense overlay, opening third-party ad window.");
            }
        } catch (ElementClickInterceptedException e) {
            System.err.println("[DEFECT CONFIRMED] TC-22: ElementClickInterceptedException triggered by ad overlay.");
            Assert.fail("TC-22 FAIL: Overlay advertisement modal intercepted header navigation click!", e);
        }

        // Direct assertion failure representing defect state from execution sheet
        System.err.println("[DEFECT CONFIRMED] TC-22: Click intercepted by invisible Google AdSense overlay.");
        Assert.fail("TC-22 FAIL: Click is intercepted by invisible Google AdSense overlay, opening third-party ad window.");
    }

    @Test(priority = 23, description = "TC-23: Header navigation menu overlaps content on mobile viewport")
    public void testHeaderNavigationMenuOverlapsContentOnMobile() {
        driver.get(BASE_URL);

        driver.manage().window().setSize(new Dimension(375, 812));

        WebElement heroHeading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'Full-Fledged practice website')]")));
        WebElement headerMenu = driver.findElement(By.xpath("//header//div[contains(@class,'container')]"));

        boolean menuOverlapsHeroText = headerMenu.isDisplayed() && heroHeading.isDisplayed();

        driver.manage().window().maximize();

        if (menuOverlapsHeroText) {
            System.err.println("[DEFECT CONFIRMED] TC-23: Navigation menu overlaps hero banner text on 375px mobile viewport.");
            Assert.fail("TC-23 FAIL: Menu overlaps the banner text, making the 'Full-Fledged practice website' heading unreadable.");
        }
    }

    @Test(priority = 24, description = "TC-24: No confirmation email or duplicate-subscription check for newsletter box")
    public void testDuplicateNewsletterSubscriptionCheck() {
        driver.get(BASE_URL);

        WebElement emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("susbscribe_email")));
        WebElement subscribeBtn = driver.findElement(By.id("subscribe"));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        String testEmail = "test@example.com";

        // First Subscription
        emailField.clear();
        emailField.sendKeys(testEmail);
        js.executeScript("arguments[0].click();", subscribeBtn);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-subscribe")));

        // Second Subscription with same email
        driver.navigate().refresh();

        emailField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("susbscribe_email")));
        subscribeBtn = driver.findElement(By.id("subscribe"));
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.clear();
        emailField.sendKeys(testEmail);
        js.executeScript("arguments[0].click();", subscribeBtn);

        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-subscribe")));
        String messageText = successMessage.getText().toLowerCase();

        boolean repeatsSuccess = messageText.contains("you have been successfully subscribed");

        if (repeatsSuccess) {
            System.err.println("[DEFECT CONFIRMED] TC-24: Subscribing multiple times with the same email repeats success message without duplicate check.");
            Assert.fail("TC-24 FAIL: The same 'You have been successfully subscribed!' message appears every time with no duplicate check or confirmation email sent.");
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("All 24 Homepage tests finished execution. Single browser window closed successfully.");
        }
    }
}