package com.bookstore.base;

import com.bookstore.factory.BrowserFactory;
import com.bookstore.utils.LoggerHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Duration;

public class BaseSetup {
    @Getter
    protected WebDriver driver;
    protected long startTime;
    @Getter
    protected String baseUrl;


    @Parameters({"browser", "appURL"})
    @BeforeClass
    public void initializeTestBaseSetup(
            @Optional("chrome") String browser,
            @Optional("http://localhost:8080") String appURL) {
//            @Optional("http://165.245.178.123") String appURL) {
        try {
            this.driver = BrowserFactory.getBrowser(browser);
            this.baseUrl = appURL;
            driver.get(appURL);
        } catch (Exception e) {
            System.err.println("[Error] Cannot initialize driver: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            LoggerHelper.info("Đóng trình duyệt...");
            driver.quit();
        }
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        startTime = System.currentTimeMillis();
        LoggerHelper.info("[TESTCASE] Bắt đầu: " + method.getName());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        long duration = System.currentTimeMillis() - startTime;
        LoggerHelper.info("[TESTCASE] Kết thúc: " + result.getName());
        LoggerHelper.info("[TESTCASE] Trạng thái: " + getTestStatus(result));
        LoggerHelper.info("[TESTCASE] Thời gian chạy: " + duration + " ms (" + (duration / 1000.0) + " giây)");
        if (driver != null) {
            try {
                driver.manage().deleteAllCookies();
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("window.localStorage.clear();");
                js.executeScript("window.sessionStorage.clear();");
            } catch (Exception e) {
                LoggerHelper.warn("[BaseSetup] Lỗi dọn dẹp sai khi chạy ");
            }
        }
    }

    private String getTestStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }
}
