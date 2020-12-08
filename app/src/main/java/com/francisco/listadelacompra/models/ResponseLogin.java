package com.francisco.listadelacompra.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLogin {







        @SerializedName("user")
        @Expose
        private String user;

        @SerializedName("useremail")
        @Expose
        private String userEmail;

        @SerializedName("userpassword")
        @Expose
        private String userPassword;


        @SerializedName("message")
        @Expose
        private String message;

        @SerializedName("token")
        @Expose
        private String token;








        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }


        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }


        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getUserEmail() { return userEmail; }

        public void setUserEmail(String userEmail) { this.userEmail = userEmail;}

        public String getUserPassword() { return userPassword; }

        public void setUserPassword(String userPassword) {this.userPassword = userPassword;}
}
