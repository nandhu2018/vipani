package com.gigaappz.vipani.models;

public class UserModel {
    private String userMobile;
    private String dateRemains;
    private String registeredOn;
    private String purchasedOn;
    private String expiredon;
    private int userStatus;

    public String getDateRemains() {
        return dateRemains;
    }

    public void setDateRemains(String dateRemains) {
        this.dateRemains = dateRemains;
    }

    public String getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(String registeredOn) {
        this.registeredOn = registeredOn;
    }

    public String getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(String purchasedOn) {
        this.purchasedOn = purchasedOn;
    }

    public String getExpiredon() {
        return expiredon;
    }

    public void setExpiredon(String expiredon) {
        this.expiredon = expiredon;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }
    public UserModel(String mobile) {
        this.userMobile = mobile;
    }
    public UserModel() {

    }
}
