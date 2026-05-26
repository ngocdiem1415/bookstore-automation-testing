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
        String methodName = method.getName(); // Lấy tên hàm test đang gọi (Ví dụ: AUTH_REG_03)
        String className = method.getDeclaringClass().getSimpleName(); // Lấy tên Class test (Ví dụ: RegisterTest)

        String filePath = "";
        String typeFilter = "";

        // 1. Tự động điều phối file path dựa trên Class nghiệp vụ
        if (className.contains("Register")) {
            filePath = TestConstants.REGISTER_DATA;
        } else if (className.contains("Login")) {
            filePath = TestConstants.LOGIN_DATA;
        } else if (className.contains("Profile")) {
            filePath = TestConstants.PROFILE_DATA;
        } else if (className.contains("Cart")) {
            filePath = TestConstants.CART_DATA;
        }

        // Ví dụ: Hàm "AUTH_REG_03_InvalidPassword" -> lọc theo từ khóa "AUTH_REG_03"
        if (methodName.contains("_")) {
            typeFilter = methodName.substring(0, methodName.lastIndexOf("_"));
        }

        return getFilteredData(filePath, typeFilter);
    }

    private static Object[][] getFilteredData(String filePath, String typeFilter) {
        System.out.println(typeFilter);
        Gson gson = new Gson();
        Type readerType = new TypeToken<List<Map<String, String>>>() {}.getType();
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

        Object[][] result = new Object[filteredList.size()][1];
        for (int i = 0; i < filteredList.size(); i++) {
            result[i][0] = filteredList.get(i);
        }
        return result;
    }
}