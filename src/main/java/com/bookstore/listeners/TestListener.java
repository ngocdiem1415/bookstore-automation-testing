package com.bookstore.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.bookstore.base.BaseSetup;
import com.bookstore.factory.BrowserFactory;
import com.bookstore.report.ExtentManager;
import com.bookstore.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static ExtentReports extent = ExtentManager.createInstance();
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest extentTest =
                extent.createTest(result.getMethod().getDescription());

        test.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());

        try {
            BaseSetup baseTest = (BaseSetup) result.getInstance();
            WebDriver driver = baseTest.getDriver();
            String screenshotPath = ScreenshotUtils.capture(
                    driver,
                    result.getMethod().getMethodName()
            );
            test.get().addScreenCaptureFromPath(screenshotPath);
        } catch (Exception e) {
            test.get().warning("Không chụp được screenshot: " + e.getMessage());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }
}