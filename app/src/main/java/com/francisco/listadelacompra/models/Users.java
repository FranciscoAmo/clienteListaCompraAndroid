package com.francisco.listadelacompra.models;

import java.util.Date;

public class Users {

    public String email;

    public String displayName;

    public String avatar;

    public String password;

    public Date signupDate;

    public Date Date;

   // metodos contructores
    public Users(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Users(String email, String displayName, String password) {
        this.email = email;
        this.displayName = displayName;
        this.password = password;
    }


    // getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public java.util.Date getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(java.util.Date signupDate) {
        this.signupDate = signupDate;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }
}
