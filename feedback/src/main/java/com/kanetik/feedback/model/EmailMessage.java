package com.kanetik.feedback.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmailMessage {
    @SerializedName("From")
    public EmailAddress from;

    @SerializedName("To")
    public List<EmailAddress> to;

    @SerializedName("Subject")
    public String subject;

    @SerializedName("TextPart")
    public String text;

    @SerializedName("HTMLPart")
    public String html;
}