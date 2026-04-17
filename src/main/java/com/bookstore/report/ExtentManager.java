package com.bookstore.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    private static ExtentReports extent;

    public static ExtentReports createInstance() {
        String fileName = "bookstore-automation-report.html";
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("target/reports/" + fileName);

        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle("Bookstore Automation Report");
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName("Kết quả kiểm thử tự động - Bookstore Project");

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);

        extent.setSystemInfo("Project Name", "Bookstore Ecommerce System");
        extent.setSystemInfo("Author", "Nguyen Thi Ngoc Diem");
        extent.setSystemInfo("Environment", "Localhost");
        extent.setSystemInfo("Browser", "Chrome");

        return extent;
    }
}