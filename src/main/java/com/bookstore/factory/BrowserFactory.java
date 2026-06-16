package com.bookstore.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BrowserFactory {

    public static WebDriver getBrowser(String browserName) {
        WebDriver driver;

        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();

                ChromeOptions options = new ChromeOptions();

                Map<String, Object> prefs = new HashMap<>();
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);
                prefs.put("profile.default_content_setting_values.notifications", 2);

                options.setExperimentalOption("prefs", prefs);

                options.addArguments("--disable-notifications");
                options.addArguments("--disable-save-password-bubble");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--start-maximized");
                if (isHeadless()) {
                    options.addArguments("--headless=new");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                    options.addArguments("--window-size=1920,1080");
                }

                // Dùng khi test server thật để ổn định hơn
                options.addArguments("--remote-allow-origins=*");

                driver = new ChromeDriver(options);
                break;
            default:
                throw new RuntimeException("Trình duyệt không hỗ trợ: " + browserName);
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));

        return driver;
    }
    private static boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", "false"))
                || Boolean.parseBoolean(System.getenv("CI"));
    }
}
