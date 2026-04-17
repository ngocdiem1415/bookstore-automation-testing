package com.bookstore.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.time.Duration;

public class BaseSetup {
    protected WebDriver driver;
    public static String baseUrl;

    @Parameters({"appURL"})
    @BeforeSuite
    public void getUrlFromXml(String appURL) {
        baseUrl = appURL;
    }

    private WebDriver initDriver(String appURL) {
        System.out.println("Launching Chrome browser...");
        WebDriverManager.chromedriver().setup(); // quản lý driver

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

        driver.get(appURL);
        return driver;
    }

    @Parameters({"appURL"})
    @BeforeClass
    public void initializeTestBaseSetup(String appURL) {
        try {
            this.driver = initDriver(appURL);
            // Tắt log WARNING của Selenium
            System.setProperty("webdriver.chrome.silentOutput", "true");
            java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(java.util.logging.Level.OFF);
        } catch (Exception e) {
            System.out.println("Error... " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            System.out.println("Closing browser...");
            driver.quit();
        }
    }
}