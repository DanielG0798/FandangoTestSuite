package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;

public class HomeTests extends BaseTest {

    @Test
    public void verifyHomePageLoads() {
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isLoaded(), "The Fandango home page should load successfully.");
    }

    @Test
    public void verifySearchBarExists() {
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isSearchBarDisplayed(), "The global search bar should be visible.");
    }

    @Test
    public void verifyLocationPopupHandling() {
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.dismissLocationPopup("33101"),
                "Location prompts should be handled safely when they appear.");
    }

    @Test
    public void verifyTrendingMoviesDisplayed() {
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isTrendingMoviesSectionDisplayed(),
                "The home page should show a trending or in-theaters section.");
    }

    @Test
    public void verifySignInButtonVisible() {
        HomePage homePage = new HomePage(driver);
        Assert.assertTrue(homePage.isSignInButtonVisible(), "The sign-in entry point should be visible.");
    }
}