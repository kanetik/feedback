package com.kanetik.feedback.model

import android.content.Context
import com.kanetik.feedback.utility.FeedbackUtils
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Feedback(@Transient val context: Context? = null, val comment: String, val from: String) {
    var appData: ContextData = ContextData()
    var deviceData: ContextData = ContextData()
    var devData: ContextData = ContextData()

    private var retryCount = 0

    init {
        FeedbackUtils.addSystemData(context, this)
    }

    fun incrementRetryCount() {
        retryCount++
    }

    fun retryAllowed(): Boolean {
        return if (RETRY_LIMIT == 0) {
            true
        } else {
            RETRY_LIMIT > retryCount
        }
    }

    override fun toString(): String {
        return "Comment: $comment | From: $from"
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val EXTRA_FEEDBACK_DATA = "FeedbackData"
        const val EXTRA_FEEDBACK_TEMP_FILE_NAME = "FeedbackTempFileName"
        const val RETRY_LIMIT = 1

        val serializer = serializer()
    }
}