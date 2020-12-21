package com.longya.player;

/*
 * Created by　Dullyoung on 2020/12/21
 */
public class ActionBean {
    public static final String LOGOUT = "logout";
    public static final String LOGIN = "login";
    private String action;
    private String longya_username;
    private String longya_password;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLongya_username() {
        return longya_username;
    }

    public void setLongya_username(String longya_username) {
        this.longya_username = longya_username;
    }

    public String getLongya_password() {
        return longya_password;
    }

    public void setLongya_password(String longya_password) {
        this.longya_password = longya_password;
    }
}
