package com.bookstore.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BrowserFactory {

    public static WebDriver getBrowser(String browserName) {
        WebDriver driver;
        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();

                // Tắt các popup cảnh báo
                ChromeOptions options = new ChromeOptions();
                Map<String, Object> prefs = new HashMap<>();

                // Tắt popup lưu password
                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);

                // Tắt cảnh báo password bị leak
                prefs.put("profile.password_manager_leak_detection", false);

                // Tắt notification
                prefs.put("profile.default_content_setting_values.notifications", 2);
                options.setExperimentalOption("prefs", prefs);

                // Disable popup & notification
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-save-password-bubble");

                driver = new ChromeDriver(options);
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;

            default:
                throw new RuntimeException("Trình duyệt không hỗ trợ: " + browserName);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        return driver;
    }
}