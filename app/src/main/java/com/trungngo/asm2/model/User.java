package com.trungngo.asm2.model;

import java.time.LocalDate;
import java.util.Date;

public class User {
    String username;
    String phone;
    Date birthDate;
    String gender;
    String email;

    public User(String username, String phone, Date birthDate, String gender, String email) {
        this.username = username;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
    }

    public User() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
