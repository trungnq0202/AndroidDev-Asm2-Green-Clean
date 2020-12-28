package com.trungngo.asm2.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class User {
    private String username;
    private String phone;
    private Date birthDate;
    private String gender;
    private String email;
    private List<String> ownSitesId;
    private List<String> participatingSitesId;

    public User(String username, String phone, Date birthDate, String gender, String email, List<String> ownSitesId, List<String> participatingSitesId) {
        this.username = username;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
        this.ownSitesId = ownSitesId;
        this.participatingSitesId = participatingSitesId;
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

    public void addOwnSiteId(String newSiteId){
        this.ownSitesId.add(newSiteId);
    }

    public void addParticipatingSiteId(String newParticipatingSiteId){
        this.participatingSitesId.add(newParticipatingSiteId);
    }

    public List<String> getOwnSitesId() {
        return ownSitesId;
    }

    public void setOwnSitesId(List<String> ownSitesId) {
        this.ownSitesId = ownSitesId;
    }

    public List<String> getParticipatingSitesId() {
        return participatingSitesId;
    }

    public void setParticipatingSitesId(List<String> participatingSitesId) {
        this.participatingSitesId = participatingSitesId;
    }
}