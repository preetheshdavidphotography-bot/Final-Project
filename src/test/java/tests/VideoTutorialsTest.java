package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.TestNG;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * VideoTutorialsTestSuite
 * ------------------------
 * TestNG + Selenium WebDriver program for test cases from the
 * "VIDEO TUTORIALS" section of the test plan.
 */
public class VideoTutorialsTest {

    private static final String BASE_URL = "https://automationexercise.com";
    private static final String CHANNEL_URL = "https://www.youtube.com/c/AutomationExercise";

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * Main method to continuously execute the TestNG suite in a loop
     * until manually terminated.
     */
    public static void main(String[] args) {
        System.out.println("=== Starting Continuous Execution (Press Ctrl+C to Stop) ===");
        int executionCount = 1;

        while (true) {
            System.out.println("\n--------------------------------------------------");
            System.out.println("Starting Test Suite Execution Run #" + executionCount);
            System.out.println("--------------------------------------------------");

            TestNG testng = new TestNG();
            testng.setTestClasses(new Class[] { VideoTutorialsTest.class });
            testng.run();

            executionCount++;
            
            try {
                // Short delay between execution runs
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                System.out.println("Continuous execution interrupted. Stopping execution.");
                break;
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless=new", "--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // -----------------------------------------------------------------
    // VID-01 — Verify Video Tutorials link visibility in header [Pass]
    // -----------------------------------------------------------------
    @Test(description = "VID-01: 'Video Tutorials' link is visible in the header navigation menu")
    public void vid01_videoTutorialsLinkVisibleInHeader() {
        driver.get(BASE_URL + "/");
        WebElement link = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.linkText("Video Tutorials")));
        Assert.assertTrue(link.isDisplayed(),
                "VID-01: 'Video Tutorials' link should be visible in the header navigation menu");
    }

    // -----------------------------------------------------------------
    // VID-02 — Verify Video Tutorials link redirects to correct channel [Pass]
    // -----------------------------------------------------------------
    @Test(description = "VID-02: 'Video Tutorials' link redirects to the correct YouTube channel")
    public void vid02_linkRedirectsToCorrectChannel() {
        driver.get(BASE_URL + "/");
        Set<String> handlesBefore = driver.getWindowHandles();

        driver.findElement(By.linkText("Video Tutorials")).click();

        Set<String> handlesAfter = driver.getWindowHandles();
        if (handlesAfter.size() > handlesBefore.size()) {
            handlesAfter.removeAll(handlesBefore);
            driver.switchTo().window(handlesAfter.iterator().next());
        }

        wait.until(ExpectedConditions.urlContains("youtube.com"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("youtube.com/c/AutomationExercise")
                        || currentUrl.contains("youtube.com/@AutomationExercise")
                        || currentUrl.contains("AutomationExercise"),
                "VID-02: Expected to land on the AutomationExercise YouTube channel, got: " + currentUrl);
    }

    // -----------------------------------------------------------------
    // VID-03 — Verify Video Tutorials link opens in a NEW tab [Fail / BUG-01]
    // -----------------------------------------------------------------
    @Test(description = "VID-03: 'Video Tutorials' link should open the channel in a NEW browser tab")
    public void vid03_linkShouldOpenInNewTab() {
        driver.get(BASE_URL + "/");
        int handlesBefore = driver.getWindowHandles().size();

        driver.findElement(By.linkText("Video Tutorials")).click();
        wait.until(d -> d.getWindowHandles().size() != handlesBefore
                || d.getCurrentUrl().contains("youtube.com"));

        int handlesAfter = driver.getWindowHandles().size();

        Assert.assertTrue(handlesAfter > handlesBefore,
                "VID-03 / BUG-01: Video Tutorials link opened the YouTube channel in the SAME "
                        + "tab instead of a new one (window count stayed at " + handlesBefore + ")");
    }

    // -----------------------------------------------------------------
    // VID-04 — Verify the YouTube channel loads tutorial videos [Fail / BUG-02]
    // -----------------------------------------------------------------
    @Test(description = "VID-04: AutomationExercise YouTube channel loads tutorial videos without manual scrolling")
    public void vid04_channelLoadsTutorialVideos() {
        driver.get(CHANNEL_URL + "/videos");
        dismissYouTubeConsentIfPresent();

        List<WebElement> videoTiles = driver.findElements(By.cssSelector("ytd-rich-grid-media"));

        Assert.assertTrue(!videoTiles.isEmpty(),
                "VID-04 / BUG-02: Video grid did not render any 'ytd-rich-grid-media' tiles "
                        + "without manual scrolling (found " + videoTiles.size() + ")");
    }

    // -----------------------------------------------------------------
    // VID-05 — Verify subscribe button visibility on the channel [Pass]
    // -----------------------------------------------------------------
    @Test(description = "VID-05: 'Subscribe' button is visible and clickable on the channel page")
    public void vid05_subscribeButtonVisible() {
        driver.get(CHANNEL_URL);
        dismissYouTubeConsentIfPresent();

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("ytd-channel-name, #channel-header")));

        By[] subscribeLocators = {
                By.cssSelector("ytd-subscribe-button-renderer #subscribe-button"),
                By.cssSelector("yt-button-shape#subscribe-button"),
                By.cssSelector("#subscribe-button"),
                By.xpath("//*[@aria-label[contains(" +
                        "translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')," +
                        " 'subscribe')]]"),
                By.xpath("//tp-yt-paper-button[.//div[contains(text(),'Subscribe')]]"),
                By.xpath("//button[contains(., 'Subscribe')]"),
        };

        WebElement subscribeButton = null;
        for (By locator : subscribeLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                subscribeButton = shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                if (subscribeButton != null) {
                    break;
                }
            } catch (TimeoutException ignored) {
                // try next locator
            }
        }

        Assert.assertNotNull(subscribeButton,
                "VID-05: Could not locate a 'Subscribe' button on the channel page with any known locator");
        Assert.assertTrue(subscribeButton.isDisplayed(),
                "VID-05: 'Subscribe' button should be visible on the channel page");
        Assert.assertTrue(subscribeButton.isEnabled(),
                "VID-05: 'Subscribe' button should be clickable on the channel page");
    }

    // Helper: Dismiss YouTube consent dialog if present
    private void dismissYouTubeConsentIfPresent() {
        By[] consentButtonLocators = {
                By.xpath("//button[.//span[contains(text(),'Accept all')]]"),
                By.xpath("//button[contains(., 'Accept all')]"),
                By.xpath("//button[contains(., 'I agree')]"),
                By.xpath("//tp-yt-paper-button[contains(., 'Accept all')]"),
        };
        for (By locator : consentButtonLocators) {
            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
                WebElement acceptAll = shortWait.until(ExpectedConditions.elementToBeClickable(locator));
                acceptAll.click();
                return;
            } catch (TimeoutException ignored) {
                // try next locator
            }
        }
    }
}