package com.bookstore.tests;

import com.bookstore.base.BaseSetup;
import com.bookstore.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomeTest extends BaseSetup {

    @Test(priority = 1, description = "Kiểm tra Banner chính")
    public void verifyBannerIsPresent() {
        HomePage homePage = new HomePage(driver);
        homePage.open();
        Assert.assertTrue(homePage.isBannerDisplayed(), "Banner không hiển thị trên trang chủ!");
    }

    @Test(priority = 2, description = "Kiểm tra danh sách sách văn học có dữ liệu")
    public void verifyLiteratureBooksLoaded() {
        HomePage homePage = new HomePage(driver);
        homePage.open();
        int count = homePage.getLiteratureBooksCount();
        System.out.println("Số lượng sách văn học tìm thấy: " + count);
        Assert.assertTrue(count > 0, "Không tìm thấy sách nào trong danh sách Sách văn học!");
    }

    @Test(priority = 3,description = "Kiểm tra điều hướng khi click vào tên sách")
    public void testNavigateToBookDetail() {
        HomePage homePage = new HomePage(driver);
        homePage.open();
        homePage.clickFirstLiteratureBook();
        Assert.assertTrue(driver.getCurrentUrl().contains("book-detail"), "Không chuyển hướng đến trang chi tiết!");
    }
}
