package com.kanetik.feedback.network;

import android.content.Context;

import com.kanetik.feedback.model.Feedback;

public interface Sender {
    boolean send(final Context context, final Feedback feedback);
}
