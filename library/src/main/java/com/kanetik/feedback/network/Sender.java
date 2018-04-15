package com.kanetik.feedback.network;

import com.kanetik.feedback.model.Feedback;

public interface Sender {
    boolean send(final Feedback feedback);
}
