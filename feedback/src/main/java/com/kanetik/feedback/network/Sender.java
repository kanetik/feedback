package com.kanetik.feedback.network;

import androidx.annotation.Keep;

import com.kanetik.feedback.model.Feedback;

@Keep
public interface Sender {
    boolean send(final Feedback feedback);
}
