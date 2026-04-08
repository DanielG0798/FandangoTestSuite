package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CheckoutPage {
    private final WebDriver driver;

    private final By[] checkoutLocators = new By[] {
            By.xpath("//*[contains(normalize-space(),'Checkout')]"),
            By.xpath("//*[contains(normalize-space(),'Payment')]"),
            By.xpath("//*[contains(normalize-space(),'Order Summary')]")
    };

    private final By[] summaryLocators = new By[] {
            By.xpath("//*[contains(normalize-space(),'Order Summary')]"),
            By.xpath("//*[contains(normalize-space(),'Ticket Summary')]"),
            By.xpath("//*[contains(normalize-space(),'Tickets')]")
    };

    private final By[] paymentFormLocators = new By[] {
            By.cssSelector("form"),
            By.xpath("//input[contains(@name,'card')]"),
            By.xpath("//*[contains(normalize-space(),'Payment Method')]")
    };

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isCheckoutPageLoaded() {
        return driver.getCurrentUrl().contains("checkout") || isAnyElementVisible(checkoutLocators);
    }

    public String getTicketSummary() {
        for (By locator : summaryLocators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        String text = element.getText().trim();
                        if (!text.isBlank()) {
                            return text;
                        }
                    }
                } catch (Exception ignored) {
                    // Ignore stale elements while reading the summary.
                }
            }
        }
        return "";
    }

    public boolean isPaymentFormDisplayed() {
        return isAnyElementVisible(paymentFormLocators);
    }

    private boolean isAnyElementVisible(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (element.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // Ignore stale elements while checking fallback locators.
                }
            }
        }
        return false;
    }
}
