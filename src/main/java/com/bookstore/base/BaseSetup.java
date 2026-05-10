package com.bookstore.base;

import com.bookstore.factory.BrowserFactory;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Duration;

@Getter
public class BaseSetup {
    protected WebDriver driver;
    protected long startTime;
    protected String baseUrl;

    @Parameters({"browser", "appURL"})
    @BeforeClass
    public void initializeTestBaseSetup(String browser, String appURL) {
        try {
            System.out.println("Initializing " + browser + " browser...");
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
            System.out.println("Closing browser...");
            driver.quit();
        }
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        startTime = System.currentTimeMillis();
        System.out.println("[RUNNING TEST]: " + method.getName());
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[EXECUTION TIME]: " + duration + " ms (" + (duration / 1000.0) + "s)");
    }
}