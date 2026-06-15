package com.bookstore.constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportPath {
    public static final String RUN_ID =
            LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    public static final String REPORT_DIR =
            System.getProperty("user.dir")
                    + "/target/reports/" + RUN_ID;
}