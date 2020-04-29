package com.kanetik.feedback.model;

import com.squareup.moshi.Json;

public class EmailAddress {
    @Json(name = "Email")
    public String address;

    @Json(name = "Name")
    public String name;

    public EmailAddress(String address, String name) {
        this.address = address;
        this.name = name;
    }
}