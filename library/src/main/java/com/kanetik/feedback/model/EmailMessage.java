package com.kanetik.feedback.model;

import com.squareup.moshi.Json;

import java.util.List;

public class EmailMessage {
    @Json(name = "From")
    public EmailAddress from;

    @Json(name = "To")
    public List<EmailAddress> to;

    @Json(name = "Subject")
    public String subject;

    @Json(name = "TextPart")
    public String text;

    @Json(name = "HTMLPart")
    public String html;
}