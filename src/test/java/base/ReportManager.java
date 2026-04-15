package base;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

/**
 * This class manages the extent report for test results.
 * It provides a singleton instance of ExtentReports, which is used to generate
 * the HTML report for the test results.
 */
public class ReportManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter reporter =
                    new ExtentSparkReporter("target/extent-report.html");

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }
        return extent;
    }
}