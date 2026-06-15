package com.bookstore.utils;

import com.bookstore.constants.ReportPath;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtils {
    public static String capture(WebDriver driver, String testName) {
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String folderPath = ReportPath.REPORT_DIR + "/screenshots/";
        String filePath = folderPath + testName + "_" + time + ".png";

        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File src = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);

            FileUtils.copyFile(src, new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }
}
