package com.makehitmusic.hiphopbeats.model;

public class LoginRequest {

    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String userID;
    private String idToken;
    private String loginType;
    private String photo;

    public LoginRequest(String email, String username, String firstname, String lastname, String userID,
                        String idToken, String loginType, String photo) {
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.userID = userID;
        this.idToken = idToken;
        this.loginType = loginType;
        this.photo = photo;
    }

}
