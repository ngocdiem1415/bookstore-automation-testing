package com.bookstore.utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotHelper {
    public static String capture(WebDriver driver, String testName) {

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = "screenshots/" + testName + "_" + time + ".png";

        try {
            File src = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(src, new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
}
