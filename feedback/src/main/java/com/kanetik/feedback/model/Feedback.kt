package com.kanetik.feedback.model

import android.content.Context

data class Feedback(val context: Context? = null, val comment: String, val from: String) {
    var devData: ContextData = ContextData()

    override fun toString(): String {
        return "Comment: $comment | From: $from"
    }
}