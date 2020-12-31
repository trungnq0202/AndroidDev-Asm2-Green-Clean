package com.trungngo.asm2.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;
import java.util.List;

public class Site{

    @DocumentId
    private String docId;
    private String siteName;
    private String adminId;
    private List<String> participantsId;
    private Date startDate;
    private Date endDate;

    private String placeId;
    private String placeName;
    private double placeLatitude;
    private double placeLongitude;
    private String placeAddress;


    public Site() {
    }

    public Site(String docId, String siteName, String adminId, List<String> participantsId, Date startDate, Date endDate, String placeId, String placeName, double placeLatitude, double placeLongitude, String placeAddress) {
        this.docId = docId;
        this.siteName = siteName;
        this.adminId = adminId;
        this.participantsId = participantsId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
        this.placeAddress = placeAddress;
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

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(double placeLatitude) {
        this.placeLatitude = placeLatitude;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(double placeLongitude) {
        this.placeLongitude = placeLongitude;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
