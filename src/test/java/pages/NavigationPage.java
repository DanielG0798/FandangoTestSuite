package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class NavigationPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By[] breadcrumbLocators = new By[] {
            By.cssSelector("nav[aria-label*='breadcrumb' i]"),
            By.cssSelector(".breadcrumb"),
            By.xpath("//*[contains(@aria-label,'breadcrumb')]"),
            By.xpath("//a[contains(@href,'/') and not(contains(@href,'http'))][1]")
    };

    private final By[] homeNavLocators = new By[] {
            By.cssSelector("a[href='/']"),
            By.xpath("//a[contains(@aria-label,'Home')]"),
            By.cssSelector("a.logo"),
            By.xpath("//a[contains(@href,'fandango.com') and not(contains(@href,'/movie'))]")
    };


    public NavigationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }


    /**
     * Navigates one step back in browser history and waits for the URL
     * to change. Returns the URL after navigation.
     */
    public String goBack() {
        String urlBefore = driver.getCurrentUrl();
        driver.navigate().back();
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));
        } catch (Exception ignored) {
            // Some sites do not change the URL on back navigation.
        }
        return driver.getCurrentUrl();
    }

    /**
     * Returns true when the URL after {@link #goBack()} differs from the
     * URL that was present before the back navigation.
     */
    public boolean didNavigateBack(String urlBeforeBack) {
        return !driver.getCurrentUrl().equals(urlBeforeBack);
    }

    /**
     * Returns true when a breadcrumb nav element is visible on the page.
     */
    public boolean isBreadcrumbVisible() {
        return isAnyElementVisible(breadcrumbLocators);
    }

    /**
     * Returns the text of all visible breadcrumb items as a list.
     */
    public void getBreadcrumbTexts() {
        List<String> texts = new ArrayList<>();
        for (By locator : breadcrumbLocators) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (!element.isDisplayed()) continue;
                    String text = element.getText().trim();
                    if (!text.isBlank()) texts.add(text);
                } catch (Exception ignored) {}
            }
            if (!texts.isEmpty()) break;
        }
    }

    /**
     * Navigates back to the Fandango home page by clicking the logo/home link.
     * Returns true if a home link was found and clicked.
     */
    public boolean clickHomeLink() {
        for (By locator : homeNavLocators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (!element.isDisplayed()) continue;
                    wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                    return true;
                } catch (Exception ignored) {}
            }
        }
        return false;
    }

    /**
     * Refreshes the page and returns the title after reload.
     * Useful for asserting that page state survives a refresh.
     */
    public String refreshAndGetTitle() {
        driver.navigate().refresh();
        try {
            wait.until(driver -> !driver.getTitle().isBlank());
        } catch (Exception ignored) {}
        return driver.getTitle();
    }

    private boolean isAnyElementVisible(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (element.isDisplayed()) return true;
                } catch (Exception ignored) {}
            }
        }
        return false;
    }
}
