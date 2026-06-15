package com.bookstore.utils;

import com.bookstore.constants.TestConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.DataProvider;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonDataProvider {

    @DataProvider(name = "GlobalJsonFeeder")
    public static Object[][] globalJsonFeeder(Method method) {
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();

        String filePath = resolveFilePath(className);
        String typeFilter = extractTypeFilter(methodName);

        return getFilteredData(filePath, typeFilter);
    }

    private static String resolveFilePath(String className) {
        if (className.contains("Register")) return TestConstants.REGISTER_DATA;
        if (className.contains("Login")) return TestConstants.LOGIN_DATA;
        if (className.contains("Profile")) return TestConstants.PROFILE_DATA;
        if (className.contains("Cart")) return TestConstants.CART_DATA;
        if (className.contains("ProductDetail")) return TestConstants.DETAIL_BOOK_DATA;
        if (className.contains("ProductFilter")) return TestConstants.PRODUCT_FILTER_DATA;
        if (className.contains("ProductNavigate")) return TestConstants.PRODUCT_NAVIGATE_DATA;
        if (className.contains("Order")) return TestConstants.ORDER_DATA;
        if (className.contains("Checkout")) return TestConstants.CHECKOUT_DATA;
        if (className.contains("Admin")) return TestConstants.ADMIN_DATA;

        throw new RuntimeException("Không tìm thấy file data cho class: " + className);
    }

    private static String extractTypeFilter(String methodName) {
        String[] parts = methodName.split("_");

        if (parts.length < 3) {
            throw new RuntimeException("Tên testcase không đúng format: " + methodName);
        }

        return parts[0] + "_" + parts[1] + "_" + parts[2];
    }

    private static Object[][] getFilteredData(String filePath, String typeFilter) {
        LoggerHelper.info("[DATA][PROVIDER] Load dữ liệu cho testcase: " + typeFilter);
        Gson gson = new Gson();
        Type readerType = new TypeToken<List<Map<String, String>>>() {
        }.getType();
        List<Map<String, String>> filteredList = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            List<Map<String, String>> allData = gson.fromJson(reader, readerType);
            if (allData != null) {
                for (Map<String, String> data : allData) {
                    if (data.get("type") != null && data.get("type").startsWith(typeFilter)) {
                        filteredList.add(data);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file data cung cấp cho Test: " + filePath, e);
        }
        if (filteredList.isEmpty()) {
            throw new RuntimeException(
                    "Không tìm thấy test data với type: " + typeFilter + " trong file: " + filePath
            );
        }
        Object[][] result = new Object[filteredList.size()][1];
        for (int i = 0; i < filteredList.size(); i++) {
            result[i][0] = filteredList.get(i);
        }
        return result;
    }
}