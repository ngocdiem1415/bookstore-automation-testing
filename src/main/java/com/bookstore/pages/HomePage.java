package com.bookstore.pages;


import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    private final String PAGE_URL = "/home";

    public HomePage(WebDriver driver,String baseUrl) {
        super(driver, baseUrl);
    }

    public void open() {
        driver.get(baseUrl+ PAGE_URL);
    }

}
