package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class user {
    public String name;
    public String screenName;
    public String publicURL;


    //Parecl empty constructor
    public user(){}

    public static user fromJson(JSONObject obj) throws JSONException {
        user user = new user();
        user.name = obj.getString("name");
        user.screenName = "@" + obj.getString("screen_name");
        user.publicURL = obj.getString("profile_image_url_https");
        return user;
    }
}
