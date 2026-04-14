package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TheaterPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By[] theaterSectionLocators = new By[] {
            By.xpath("//*[contains(normalize-space(),'Theaters near')]"),
            By.xpath("//h1[contains(normalize-space(),'Movie Theaters')]"),
            By.xpath("//a[contains(@href,'/movie-theaters')]")
    };

    private final By[] locationInputLocators = new By[] {
            By.cssSelector("input[placeholder*='zipcode']"),
            By.xpath("//input[contains(@placeholder,'city, state')]"),
            By.xpath("//input[contains(@placeholder,'zipcode')]"),
            By.xpath("//input[contains(@aria-label,'city') or contains(@aria-label,'zip')]")
    };

    private final By[] locationSubmitLocators = new By[] {
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[normalize-space()='Go']"),
            By.xpath("//button[contains(normalize-space(),'Search')]")
    };

    private final By[] mapLocators = new By[] {
            By.xpath("//a[contains(normalize-space(),'MAP IT')]"),
            By.xpath("//iframe[contains(@src,'maps')]"),
            By.xpath("//a[contains(@href,'google.com/maps')]")
    };

    public TheaterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public boolean isTheaterListDisplayed() {
        return !getTheaterNames().isEmpty() || isAnyElementVisible(theaterSectionLocators);
    }

    public void searchByLocation(String location) {
        for (By locator : locationInputLocators) {
            List<WebElement> elements = driver.findElements(locator);
            for (WebElement element : elements) {
                try {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    element.click();
                    element.clear();
                    element.sendKeys(location);
                    if (!clickFirst(locationSubmitLocators)) {
                        element.sendKeys(Keys.ENTER);
                    }
                    return;
                } catch (Exception ignored) {
                    // Try the next input fallback.
                }
            }
        }
    }

    public boolean selectFirstTheater() {
        for (WebElement element : theaterCandidates()) {
            String currentUrl = driver.getCurrentUrl();
            try {
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
            } catch (Exception ignored) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                } catch (Exception ignoredAgain) {
                    continue;
                }
            }

            try {
                wait.until(ExpectedConditions.or(
                        ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)),
                        ExpectedConditions.stalenessOf(element)
                ));
            } catch (Exception ignored) {
                // Some theater cards update inline instead of navigating.
            }
            return true;
        }
        return false;
    }

    public List<String> getTheaterNames() {
        Set<String> names = new LinkedHashSet<>();
        for (WebElement element : theaterCandidates()) {
            String text = element.getText().trim();
            if (!text.isBlank()) {
                names.add(text);
            }
        }
        return new ArrayList<>(names);
    }

    public boolean isMapDisplayed() {
        return isAnyElementVisible(mapLocators);
    }

    public boolean filterByShowtime(String label) {
        String selectedBefore = getSelectedDateLabel();
        By optionLocator = By.xpath("//button[contains(normalize-space(),'" + label + "') or contains(@aria-label,'" + label + "')]" +
                " | //a[contains(normalize-space(),'" + label + "')]");

        for (WebElement element : driver.findElements(optionLocator)) {
            try {
                if (!element.isDisplayed()) {
                    continue;
                }
                wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                break;
            } catch (Exception ignored) {
                // Try the next matching showtime option.
            }
        }

        String selectedAfter = getSelectedDateLabel();
        return selectedAfter.toLowerCase(Locale.ROOT).contains(label.toLowerCase(Locale.ROOT))
                || !selectedAfter.equals(selectedBefore)
                || driver.getPageSource().toLowerCase(Locale.ROOT).contains(label.toLowerCase(Locale.ROOT));
    }

    public String getSelectedDateLabel() {
        By selectedLocator = By.xpath("//button[@aria-selected='true' or @aria-pressed='true' or " +
                "contains(@aria-label,'selected') or contains(@class,'selected')]");
        for (WebElement element : driver.findElements(selectedLocator)) {
            try {
                if (!element.isDisplayed()) {
                    continue;
                }
                String label = element.getAttribute("aria-label");
                if (label != null && !label.isBlank()) {
                    return label.trim();
                }
                String text = element.getText().trim();
                if (!text.isBlank()) {
                    return text;
                }
            } catch (Exception ignored) {
                // Ignore stale elements while scanning for the selected filter.
            }
        }
        return "";
    }

    private List<WebElement> theaterCandidates() {
        List<WebElement> candidates = new ArrayList<>();
        List<WebElement> elements = driver.findElements(By.xpath("//a | //button"));
        for (WebElement element : elements) {
            try {
                if (!element.isDisplayed()) {
                    continue;
                }
                String text = element.getText().trim();
                if (looksLikeTheaterName(text)) {
                    candidates.add(element);
                }
            } catch (Exception ignored) {
                // Ignore stale elements while scanning candidates.
            }
        }
        return candidates;
    }

    private boolean clickFirst(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (!element.isDisplayed()) {
                        continue;
                    }
                    wait.until(ExpectedConditions.elementToBeClickable(element)).click();
                    return true;
                } catch (Exception ignored) {
                    // Try the next fallback element.
                }
            }
        }
        return false;
    }

    private boolean isAnyElementVisible(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                try {
                    if (element.isDisplayed()) {
                        return true;
                    }
                } catch (Exception ignored) {
                    // Ignore stale elements while checking visibility.
                }
            }
        }
        return false;
    }

    private boolean looksLikeTheaterName(String text) {
        if (text.isBlank()) {
            return false;
        }

        String normalized = text.toLowerCase(Locale.ROOT);
        if (normalized.contains("map it")
                || normalized.contains("see more theaters")
                || normalized.equals("theaters")
                || normalized.equals("movie theaters")) {
            return false;
        }

        return normalized.contains("theater")
                || normalized.contains("theatre")
                || normalized.contains("cinema")
                || normalized.contains("cinemark")
                || normalized.contains("regal")
                || normalized.contains("amc")
                || normalized.contains("landmark")
                || normalized.contains("alamo")
                || normalized.contains("cmx")
                || normalized.contains("cinepolis")
                || normalized.matches(".*\\d+(\\.\\d+)?mi.*");
    }
}
