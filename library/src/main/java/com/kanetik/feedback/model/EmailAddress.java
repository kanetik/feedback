package com.kanetik.feedback.model;

import com.google.gson.annotations.SerializedName;

public class EmailAddress {
    @SerializedName("Email")
    public String address;

    @SerializedName("Name")
    public String name;

    public EmailAddress(String address, String name) {
        this.address = address;
        this.name = name;
    }
}