package com.trungngo.asm2.model;

import com.google.android.libraries.places.api.model.Place;
import com.trungngo.asm2.Constants;

import java.util.Date;
import java.util.List;

public class Site {
    private String siteName;
    private String adminId;
    private List<String> participantsId;
    private Date startDate;
    private Date endDate;
    private String locationId;

    public Site() {
    }

    public Site(String siteName, String adminId, List<String> participantsId, Date startDate, Date endDate, String locationId) {
        this.siteName = siteName;
        this.adminId = adminId;
        this.participantsId = participantsId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locationId = locationId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public List<String> getParticipantsId() {
        return participantsId;
    }

    public void setParticipantsId(List<String> participantsId) {
        this.participantsId = participantsId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }


}
