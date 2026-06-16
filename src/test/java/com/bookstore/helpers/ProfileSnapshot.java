package com.bookstore.helpers;

public class ProfileSnapshot {
    public final String phone;
    public final String birthdate;
    public final String gender;

    public ProfileSnapshot(String phone, String birthdate, String gender) {
        this.phone = phone;
        this.birthdate = birthdate;
        this.gender = gender;
    }
}
