package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.TheaterPage;

public class TheaterTests extends BaseTest {
    private static final String MOVIE_URL = "https://www.fandango.com/the-super-mario-galaxy-movie-2026-242307/movie-overview";
    private static final String THEATERS_URL = "https://www.fandango.com/movie-theaters";

    @Test
    public void verifyTheaterPageLoads() {
        driver.get(MOVIE_URL);
        TheaterPage theaterPage = new TheaterPage(driver);

        Assert.assertTrue(theaterPage.isTheaterListDisplayed(),
                "The movie page should display nearby theaters or theater controls.");
    }

    @Test
    public void verifyTheaterSearch() {
        driver.get(THEATERS_URL);
        TheaterPage theaterPage = new TheaterPage(driver);
        theaterPage.searchByLocation("33101");

        Assert.assertTrue(theaterPage.isTheaterListDisplayed() || !theaterPage.getTheaterNames().isEmpty(),
                "Searching by ZIP code should expose theater results or theater content.");
    }

    @Test
    public void verifyTheaterSelection() {
        driver.get(MOVIE_URL);
        TheaterPage theaterPage = new TheaterPage(driver);

        Assert.assertTrue(theaterPage.selectFirstTheater(),
                "A visible theater entry should be selectable from the theater list.");
    }

    @Test
    public void verifyShowtimeFiltering() {
        driver.get(MOVIE_URL);
        TheaterPage theaterPage = new TheaterPage(driver);
        String selectedBefore = theaterPage.getSelectedDateLabel();

        Assert.assertTrue(theaterPage.filterByShowtime("Wednesday"),
                "Selecting a different showtime day should be handled successfully.");

        String selectedAfter = theaterPage.getSelectedDateLabel();
        Assert.assertTrue(selectedAfter.contains("Wed") || !selectedAfter.equals(selectedBefore),
                "The active showtime day should update after filtering.");
    }

    @Test
    public void verifyMapOrLocationPresent() {
        driver.get(MOVIE_URL);
        TheaterPage theaterPage = new TheaterPage(driver);

        Assert.assertTrue(theaterPage.isMapDisplayed() || !theaterPage.getTheaterNames().isEmpty(),
                "The theater section should show a map link or location details.");
    }
}