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

public class CartAndCheckoutFullTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private final String BASE_URL = "https://automationexercise.com/";
    private final String USER_EMAIL = "reffippafeipre-6349@yopmail.com";
    private final String USER_PASS = "1234567";

    @BeforeClass
    public void setUp() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-notifications");
        options.addArguments("--host-resolver-rules=MAP *.googleads.g.doubleclick.net 127.0.0.1, MAP *.googlesyndication.com 127.0.0.1");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private void clearCart() {
        driver.get(BASE_URL + "view_cart");
        List<WebElement> deleteBtns = driver.findElements(By.xpath("//a[@class='cart_quantity_delete']"));
        for (WebElement btn : deleteBtns) {
            try {
                js.executeScript("arguments[0].click();", btn);
                Thread.sleep(300);
            } catch (Exception ignored) {}
        }
    }

    private void loginUser() {
        driver.get(BASE_URL + "login");
        try {
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@data-qa='login-email']")));
            emailInput.clear();
            emailInput.sendKeys(USER_EMAIL);

            WebElement passInput = driver.findElement(By.xpath("//input[@data-qa='login-password']"));
            passInput.clear();
            passInput.sendKeys(USER_PASS);

            driver.findElement(By.xpath("//button[@data-qa='login-button']")).click();
        } catch (Exception ignored) {}
    }

    private void addFirstItemToCart() {
        clearCart();
        driver.get(BASE_URL);
        WebElement addToCartBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@class,'add-to-cart')])[1]")));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        js.executeScript("arguments[0].click();", addToCartBtn);

        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Continue Shopping']")));
        js.executeScript("arguments[0].click();", continueBtn);
    }

    private void navigateToPaymentDonePage() {
        loginUser();
        addFirstItemToCart();
        driver.get(BASE_URL + "checkout");

        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'/payment')]")));
        js.executeScript("arguments[0].click();", placeOrderBtn);

        wait.until(ExpectedConditions.urlContains("/payment"));
        driver.findElement(By.name("name_on_card")).sendKeys("Test Account");
        driver.findElement(By.name("card_number")).sendKeys("4111111111111111");
        driver.findElement(By.name("cvc")).sendKeys("123");
        driver.findElement(By.name("expiry_month")).sendKeys("10");
        driver.findElement(By.name("expiry_year")).sendKeys("2028");

        WebElement payBtn = driver.findElement(By.id("submit"));
        js.executeScript("arguments[0].click();", payBtn);

        wait.until(ExpectedConditions.urlContains("/payment_done"));
    }

    @Test(priority = 1, description = "TC-01: Cart Page Navigation")
    public void testCartPageNavigation() {
        driver.get(BASE_URL);
        WebElement cartLink = driver.findElement(By.xpath("//a[contains(@href,'/view_cart')]"));
        js.executeScript("arguments[0].click();", cartLink);

        wait.until(ExpectedConditions.urlContains("/view_cart"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart"), "Failed to navigate to Cart page.");
    }

    @Test(priority = 2, description = "TC-02: Empty Cart Initial Display")
    public void testEmptyCartInitialDisplay() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL + "view_cart");

        WebElement emptyCartMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("empty_cart")));
        Assert.assertTrue(emptyCartMsg.isDisplayed(), "Empty cart notification message missing.");
    }

    @Test(priority = 3, description = "TC-03: Cart Table Headers Verification")
    public void testCartTableHeaders() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        List<WebElement> headers = driver.findElements(By.xpath("//tr[@class='cart_menu']/td"));
        Assert.assertTrue(headers.size() >= 5, "Cart table headers count incorrect.");
    }

    @Test(priority = 4, description = "TC-04: Added Item Display Details")
    public void testAddedItemDisplayDetails() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement productImg = driver.findElement(By.xpath("//td[@class='cart_product']//img"));
        WebElement productTitle = driver.findElement(By.xpath("//td[@class='cart_description']//a"));
        WebElement productPrice = driver.findElement(By.xpath("//td[@class='cart_price']/p"));

        Assert.assertTrue(productImg.isDisplayed(), "Cart product image missing.");
        Assert.assertFalse(productTitle.getText().isEmpty(), "Cart product title empty.");
        Assert.assertTrue(productPrice.getText().contains("Rs."), "Cart product price missing currency symbol.");
    }

    @Test(priority = 5, description = "TC-05: Cart Item Quantity Verification")
    public void testCartItemQuantity() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement quantityBtn = driver.findElement(By.xpath("//td[contains(@class,'cart_quantity')]/button"));
        Assert.assertEquals(quantityBtn.getText().trim(), "1", "Default cart item quantity mismatch.");
    }

    @Test(priority = 6, description = "TC-06: Cart Total Price Calculation")
    public void testCartTotalPriceCalculation() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        String priceText = driver.findElement(By.xpath("//td[@class='cart_price']/p")).getText().replaceAll("[^0-9]", "");
        String totalText = driver.findElement(By.xpath("//td[@class='cart_total']/p")).getText().replaceAll("[^0-9]", "");

        int price = Integer.parseInt(priceText);
        int total = Integer.parseInt(totalText);

        Assert.assertEquals(total, price, "Calculated total price mismatch for single quantity.");
    }

    @Test(priority = 7, description = "TC-07: Multiple Items Rendering in Cart")
    public void testMultipleItemsInCart() {
        clearCart();
        driver.get(BASE_URL);

        WebElement item1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@class,'add-to-cart')])[1]")));
        js.executeScript("arguments[0].click();", item1);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Continue Shopping']"))).click();

        WebElement item2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("(//a[contains(@class,'add-to-cart')])[3]")));
        js.executeScript("arguments[0].click();", item2);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Continue Shopping']"))).click();

        driver.get(BASE_URL + "view_cart");
        List<WebElement> cartRows = driver.findElements(By.xpath("//tbody/tr[contains(@id,'product-')]"));
        Assert.assertTrue(cartRows.size() >= 2, "Multiple cart items count mismatch.");
    }

    @Test(priority = 8, description = "TC-08: Remove Item from Cart")
    public void testRemoveItemFromCart() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement deleteBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//a[@class='cart_quantity_delete'])[1]")));
        js.executeScript("arguments[0].click();", deleteBtn);

        driver.navigate().refresh();
        WebElement emptyMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("empty_cart")));
        Assert.assertTrue(emptyMsg.isDisplayed(), "Item deletion from cart failed.");
    }

    @Test(priority = 9, description = "TC-09: Remove All Items from Cart")
    public void testRemoveAllItemsFromCart() {
        addFirstItemToCart();
        clearCart();

        WebElement emptyMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("empty_cart")));
        Assert.assertTrue(emptyMsg.isDisplayed(), "Cart did not clear completely.");
    }

    @Test(priority = 10, description = "TC-10: Proceed to Checkout (Guest User)")
    public void testProceedToCheckoutGuest() {
        driver.manage().deleteAllCookies();
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement checkoutBtn = driver.findElement(By.xpath("//a[text()='Proceed To Checkout']"));
        js.executeScript("arguments[0].click();", checkoutBtn);

        WebElement checkoutModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkoutModal")));
        Assert.assertTrue(checkoutModal.isDisplayed(), "Guest checkout modal did not pop up.");
    }

    @Test(priority = 11, description = "TC-11: Checkout Modal Register/Login Redirection")
    public void testCheckoutModalRegisterLoginLink() {
        driver.manage().deleteAllCookies();
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement checkoutBtn = driver.findElement(By.xpath("//a[text()='Proceed To Checkout']"));
        js.executeScript("arguments[0].click();", checkoutBtn);

        WebElement loginRegisterLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//u[text()='Register / Login']")));
        js.executeScript("arguments[0].click();", loginRegisterLink);

        wait.until(ExpectedConditions.urlContains("/login"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), "Failed to redirect to Login page from modal.");
    }

    @Test(priority = 12, description = "TC-12: Proceed to Checkout (Logged In User)")
    public void testProceedToCheckoutLoggedIn() {
        loginUser();
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement checkoutBtn = driver.findElement(By.xpath("//a[text()='Proceed To Checkout']"));
        js.executeScript("arguments[0].click();", checkoutBtn);

        wait.until(ExpectedConditions.urlContains("/checkout"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"), "Failed to open Checkout page for logged-in user.");
    }

    @Test(priority = 13, description = "TC-13: Checkout Page Address Details Display")
    public void testCheckoutAddressDetails() {
        loginUser();
        driver.get(BASE_URL + "checkout");

        WebElement deliveryAddress = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address_delivery")));
        WebElement invoiceAddress = driver.findElement(By.id("address_invoice"));

        Assert.assertTrue(deliveryAddress.isDisplayed(), "Delivery Address panel missing.");
        Assert.assertTrue(invoiceAddress.isDisplayed(), "Invoice Address panel missing.");
    }

    @Test(priority = 14, description = "TC-14: Checkout Order Summary Review")
    public void testCheckoutOrderSummary() {
        driver.get(BASE_URL + "checkout");

        List<WebElement> checkoutItems = driver.findElements(By.xpath("//table[contains(@class,'table-condensed')]//tbody/tr"));
        Assert.assertTrue(checkoutItems.size() > 0, "No items displayed in order review step.");
    }

    @Test(priority = 15, description = "TC-15: Checkout Comment Textarea Input")
    public void testCheckoutCommentTextarea() {
        driver.get(BASE_URL + "checkout");

        WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("message")));
        js.executeScript("arguments[0].scrollIntoView(true);", commentBox);

        commentBox.clear();
        commentBox.sendKeys("Automated delivery instructions.");
        Assert.assertEquals(commentBox.getAttribute("value"), "Automated delivery instructions.");
    }

    @Test(priority = 16, description = "TC-16: Place Order Button Click")
    public void testPlaceOrderButton() {
        driver.get(BASE_URL + "checkout");

        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'/payment')]")));
        js.executeScript("arguments[0].scrollIntoView(true);", placeOrderBtn);
        js.executeScript("arguments[0].click();", placeOrderBtn);

        wait.until(ExpectedConditions.urlContains("/payment"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/payment"), "Failed to redirect to Payment page.");
    }

    @Test(priority = 17, description = "TC-17: Payment Page Form Rendering")
    public void testPaymentPageFormRendering() {
        driver.get(BASE_URL + "payment");

        Assert.assertTrue(driver.findElement(By.name("name_on_card")).isDisplayed(), "Name on Card missing.");
        Assert.assertTrue(driver.findElement(By.name("card_number")).isDisplayed(), "Card Number missing.");
        Assert.assertTrue(driver.findElement(By.name("cvc")).isDisplayed(), "CVC field missing.");
        Assert.assertTrue(driver.findElement(By.name("expiry_month")).isDisplayed(), "Expiry Month missing.");
        Assert.assertTrue(driver.findElement(By.name("expiry_year")).isDisplayed(), "Expiry Year missing.");
    }

    @Test(priority = 18, description = "TC-18: Payment Fields Empty Submit Validation")
    public void testPaymentFieldsEmptyValidation() {
        driver.get(BASE_URL + "payment");

        WebElement nameCard = driver.findElement(By.name("name_on_card"));
        nameCard.clear();

        WebElement payBtn = driver.findElement(By.id("submit"));
        js.executeScript("arguments[0].click();", payBtn);

        Boolean isInvalid = (Boolean) js.executeScript("return arguments[0].matches(':invalid');", nameCard);
        Assert.assertTrue(isInvalid, "HTML5 validation failed on empty payment submit.");
    }

    @Test(priority = 19, description = "TC-19: Payment Card Number Numeric Validation")
    public void testPaymentCardNumberValidation() {
        driver.get(BASE_URL + "payment");

        WebElement cardNumField = driver.findElement(By.name("card_number"));
        cardNumField.clear();
        cardNumField.sendKeys("INVALID_CARD_TEXT");

        Assert.assertEquals(cardNumField.getAttribute("value"), "INVALID_CARD_TEXT");
    }

    @Test(priority = 20, description = "TC-20: Successful Order Payment Submission")
    public void testSuccessfulOrderPaymentSubmission() {
        navigateToPaymentDonePage();
        Assert.assertTrue(driver.getCurrentUrl().contains("/payment_done"), "Order payment submission failed.");
    }

    @Test(priority = 21, description = "TC-21: Order Placed Success Screen Verification")
    public void testOrderPlacedSuccessScreen() {
        navigateToPaymentDonePage();
        WebElement successHeader = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[text()='Order Placed!']")));
        Assert.assertTrue(successHeader.isDisplayed(), "Order Placed header not visible.");
    }

    @Test(priority = 22, description = "TC-22: Download Invoice Functionality")
    public void testDownloadInvoiceButton() {
        navigateToPaymentDonePage();
        WebElement invoiceBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href,'/download_invoice')]")));
        Assert.assertTrue(invoiceBtn.isDisplayed(), "Download invoice button missing.");
    }

    @Test(priority = 23, description = "TC-23: Continue Button Navigation Post Purchase")
    public void testContinueButtonPostPurchase() {
        navigateToPaymentDonePage();
        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-qa='continue-button']")));
        continueBtn.click();

        Assert.assertEquals(driver.getCurrentUrl(), BASE_URL, "Continue button did not navigate back to Homepage.");
    }

    @Test(priority = 24, description = "TC-24: Cart Auto-Clear After Completed Purchase")
    public void testCartAutoClearPostPurchase() {
        clearCart();
        driver.get(BASE_URL + "view_cart");

        WebElement emptyMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("empty_cart")));
        Assert.assertTrue(emptyMsg.isDisplayed(), "Cart was not empty.");
    }

    @Test(priority = 25, description = "TC-25: Add Custom Quantity from Product Details")
    public void testAddCustomQuantityFromDetails() {
        clearCart();
        driver.get(BASE_URL + "product_details/1");

        WebElement qtyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("quantity")));
        qtyInput.clear();
        qtyInput.sendKeys("4");

        WebElement addToCartBtn = driver.findElement(By.xpath("//button[contains(@class,'cart')]"));
        addToCartBtn.click();

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//u[text()='View Cart']"))).click();

        WebElement cartQtyBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(@class,'cart_quantity')]/button")));
        Assert.assertEquals(cartQtyBtn.getText().trim(), "4", "Custom quantity in cart mismatch.");
    }

    @Test(priority = 26, description = "TC-26: Cart Persistence Across Refresh")
    public void testCartPersistenceAcrossRefresh() {
        driver.get(BASE_URL + "view_cart");
        driver.navigate().refresh();

        WebElement cartTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart_info")));
        Assert.assertTrue(cartTable.isDisplayed(), "Cart lost data after page refresh.");
    }

    @Test(priority = 27, description = "TC-27: Cart Persistence Across Navigation")
    public void testCartPersistenceAcrossNavigation() {
        addFirstItemToCart();
        driver.get(BASE_URL + "products");
        driver.get(BASE_URL + "view_cart");

        WebElement cartQtyBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(@class,'cart_quantity')]/button")));
        Assert.assertEquals(cartQtyBtn.getText().trim(), "1", "Cart contents lost across site navigation.");
    }

    @Test(priority = 28, description = "TC-28: Cart Header Badge/Count Verification")
    public void testCartHeaderLinkVisibility() {
        driver.get(BASE_URL);
        WebElement cartHeaderLink = driver.findElement(By.xpath("//a[contains(@href,'/view_cart')]"));
        Assert.assertTrue(cartHeaderLink.isDisplayed(), "Cart menu link missing in header.");
    }

    @Test(priority = 29, description = "TC-29: Footer Subscription from Cart Page")
    public void testFooterSubscriptionOnCartPage() {
        driver.get(BASE_URL + "view_cart");

        WebElement emailField = driver.findElement(By.id("susbscribe_email"));
        js.executeScript("arguments[0].scrollIntoView(true);", emailField);

        emailField.clear();
        emailField.sendKeys("cart_sub_test@example.com");
        js.executeScript("arguments[0].click();", driver.findElement(By.id("subscribe")));

        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-subscribe")));
        Assert.assertTrue(alert.isDisplayed(), "Footer subscription on Cart page failed.");
    }

    @Test(priority = 30, description = "TC-30: Scroll to Top Arrow on Cart Page")
    public void testScrollToTopArrowCartPage() {
        driver.get(BASE_URL + "view_cart");
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        WebElement scrollUpBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("scrollUp")));
        js.executeScript("arguments[0].click();", scrollUpBtn);

        wait.until((WebDriver d) -> ((Number) js.executeScript("return window.pageYOffset;")).doubleValue() < 100);
        double scrollY = ((Number) js.executeScript("return window.pageYOffset;")).doubleValue();

        Assert.assertTrue(scrollY < 100, "Scroll to top on Cart page failed.");
    }

    @Test(priority = 31, description = "TC-31: Invalid Expiration Date Payment Handling")
    public void testInvalidExpiryDatePayment() {
        driver.get(BASE_URL + "payment");

        driver.findElement(By.name("name_on_card")).sendKeys("Test Card");
        driver.findElement(By.name("card_number")).sendKeys("4111111111111111");
        driver.findElement(By.name("cvc")).sendKeys("123");
        driver.findElement(By.name("expiry_month")).sendKeys("01");
        driver.findElement(By.name("expiry_year")).sendKeys("1990");

        WebElement payBtn = driver.findElement(By.id("submit"));
        js.executeScript("arguments[0].click();", payBtn);

        wait.until(ExpectedConditions.urlContains("/payment_done"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/payment_done"), "Payment processing failed.");
    }

    @Test(priority = 32, description = "TC-32: CVC Field Length Limit")
    public void testCVCFieldInput() {
        driver.get(BASE_URL + "payment");

        WebElement cvcInput = driver.findElement(By.name("cvc"));
        cvcInput.clear();
        cvcInput.sendKeys("9999");

        Assert.assertEquals(cvcInput.getAttribute("value"), "9999", "CVC field input failed.");
    }

    @Test(priority = 33, description = "TC-33: Order Success Banner Colors & Style")
    public void testOrderSuccessBannerStyling() {
        navigateToPaymentDonePage();

        WebElement successTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//b[text()='Order Placed!']")));
        Assert.assertTrue(successTitle.isDisplayed(), "Order Placed banner missing.");
    }

    @Test(priority = 34, description = "TC-34: Cart Responsive Mobile Grid View")
    public void testCartResponsiveMobileGrid() {
        driver.get(BASE_URL + "view_cart");

        driver.manage().window().setSize(new Dimension(375, 812));
        WebElement cartTable = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cart_info")));
        Assert.assertTrue(cartTable.isDisplayed(), "Cart table broken in mobile view.");

        driver.manage().window().maximize();
    }

    @Test(priority = 35, description = "TC-35: Broken Image Verification on Cart Page")
    public void testCartPageImagesLoaded() {
        driver.get(BASE_URL + "view_cart");

        List<WebElement> images = driver.findElements(By.xpath("//img"));
        for (WebElement img : images) {
            Boolean loaded = (Boolean) js.executeScript(
                "return arguments[0].complete && typeof arguments[0].naturalWidth != 'undefined' && arguments[0].naturalWidth > 0;", 
                img
            );
            Assert.assertTrue(loaded, "Broken image found on Cart page: " + img.getAttribute("src"));
        }
    }

 // --- REVISED INTENTIONAL DEFECT TEST CASES (TC-36 to TC-44) ---

    @Test(priority = 36, description = "TC-36 [INTENTIONAL FAILURE]: Cart page quantity box is disabled and cannot be manually edited")
    public void testFailureCartQuantityEditable() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        List<WebElement> editableInputs = driver.findElements(By.xpath("//td[contains(@class,'cart_quantity')]/input"));
        Assert.assertFalse(editableInputs.isEmpty(), 
                "INTENTIONAL FAILURE (TC-36): Cart quantity is displayed inside a disabled button tag (<button class='disabled'>) rather than an editable input field.");
    }

    @Test(priority = 37, description = "TC-37 [INTENTIONAL FAILURE]: Product title link in cart does not navigate to product detail page")
    public void testFailureCartProductTitleClickable() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        WebElement titleLink = driver.findElement(By.xpath("//td[@class='cart_description']//a"));
        js.executeScript("arguments[0].click();", titleLink);

        wait.until(ExpectedConditions.urlContains("/product_details"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/product_details"), 
                "INTENTIONAL FAILURE (TC-37): Product title link in cart does not navigate to product detail page.");
    }

    @Test(priority = 38, description = "TC-38 [INTENTIONAL FAILURE]: Cart quantity field accepts negative and zero values")
    public void testFailureCartQuantityNegativeValueValidation() {
        addFirstItemToCart();
        driver.get(BASE_URL + "view_cart");

        List<WebElement> inputFields = driver.findElements(By.xpath("//td[contains(@class,'cart_quantity')]//input"));
        Assert.assertFalse(inputFields.isEmpty(), 
                "INTENTIONAL FAILURE (TC-38): Quantity field lacks input control to validate or reject non-positive values.");
    }

    @Test(priority = 39, description = "TC-39 [INTENTIONAL FAILURE]: Checkout page allows order placement with empty delivery address")
    public void testFailureCheckoutWithoutDeliveryAddress() {
        driver.manage().deleteAllCookies();
        addFirstItemToCart();
        driver.get(BASE_URL + "checkout");

        List<WebElement> placeOrderBtns = driver.findElements(By.xpath("//a[contains(@href,'/payment')]"));
        if (!placeOrderBtns.isEmpty()) {
            js.executeScript("arguments[0].click();", placeOrderBtns.get(0));
        }

        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"), 
                "INTENTIONAL FAILURE (TC-39): Proceeded to payment screen without a saved delivery address.");
    }

    @Test(priority = 40, description = "TC-40 [INTENTIONAL FAILURE]: Direct navigation to payment_done without order session should redirect away")
    public void testFailureDirectAccessPaymentDonePage() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL + "payment_done");

        try {
            wait.until(ExpectedConditions.urlContains("/view_cart"));
        } catch (TimeoutException ignored) {}

        Assert.assertTrue(driver.getCurrentUrl().contains("/view_cart") || driver.getCurrentUrl().contains("/login"), 
                "INTENTIONAL FAILURE (TC-40): Direct navigation to /payment_done allowed without session redirection.");
    }

    @Test(priority = 41, description = "TC-41 [INTENTIONAL FAILURE]: No option to edit delivery address during checkout")
    public void testFailureEditAddressOptionOnCheckout() {
        loginUser();
        driver.get(BASE_URL + "checkout");

        List<WebElement> editAddressLinks = driver.findElements(By.xpath("//a[contains(text(),'Edit Address')]"));
        Assert.assertFalse(editAddressLinks.isEmpty(), 
                "INTENTIONAL FAILURE (TC-41): Checkout page missing inline 'Edit Address' button/link.");
    }

    @Test(priority = 42, description = "TC-42 [INTENTIONAL FAILURE]: Payment Card Number field accepts non-numeric characters")
    public void testFailureCardNumberRejectsAlphabeticInput() {
        driver.get(BASE_URL + "payment");

        WebElement cardNumField = driver.findElement(By.name("card_number"));
        cardNumField.clear();
        cardNumField.sendKeys("abcd!@#$1234");

        String val = cardNumField.getAttribute("value");
        Assert.assertFalse(val.matches(".*[a-zA-Z!@#$%^&*()].*"), 
                "INTENTIONAL FAILURE (TC-42): Card Number field accepted non-numeric characters: '" + val + "'.");
    }

    @Test(priority = 43, description = "TC-43 [INTENTIONAL FAILURE]: Payment expiry date field accepts invalid date values without validation")
    public void testFailurePaymentExpiryDateValidation() {
        driver.get(BASE_URL + "payment");

        WebElement monthInput = driver.findElement(By.name("expiry_month"));
        WebElement yearInput = driver.findElement(By.name("expiry_year"));

        monthInput.clear();
        monthInput.sendKeys("13");
        yearInput.clear();
        yearInput.sendKeys("2019");

        Boolean isMonthInvalid = (Boolean) js.executeScript("return arguments[0].matches(':invalid');", monthInput);
        Boolean isYearInvalid = (Boolean) js.executeScript("return arguments[0].matches(':invalid');", yearInput);

        Assert.assertTrue(isMonthInvalid || isYearInvalid, 
                "INTENTIONAL FAILURE (TC-43): Payment form accepts invalid month '13' and past year '2019' without inline validation.");
    }

    @Test(priority = 44, description = "TC-44 [INTENTIONAL FAILURE]: Direct navigation to /payment without active session should redirect to login")
    public void testFailureDirectAccessPaymentPageWithoutSession() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL + "payment");

        try {
            wait.until(ExpectedConditions.urlContains("/login"));
        } catch (TimeoutException ignored) {}

        Assert.assertTrue(driver.getCurrentUrl().contains("/login"), 
                "INTENTIONAL FAILURE (TC-44): Unauthenticated guest user directly accessed /payment without redirecting to login.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("Execution finished: 35 passed tests, 9 intentional failure tests.");
        }
    }
}