package com.kanetik.feedback.model

import android.content.Context
import android.content.Intent
import android.os.ResultReceiver
import androidx.annotation.Keep
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.kanetik.feedback.network.FeedbackService
import com.kanetik.feedback.utility.FeedbackUtils
import java.io.Serializable

@Keep
@JsonSerialize
data class Feedback(val context: Context?, val comment: String, val from: String) { //} : Serializable {
    var appData: ContextData = ContextData()
    var deviceData: ContextData? = null
    var devData: ContextData? = null

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

    fun getSendServiceIntent(context: Context?, resultReceiver: ResultReceiver?, data: Feedback?): Intent {
        val serviceIntent = Intent(context, FeedbackService::class.java)
        serviceIntent.putExtra(EXTRA_RESULT_RECEIVER, resultReceiver)
        serviceIntent.putExtra(EXTRA_SELF, data)
        return serviceIntent
    }

    override fun toString(): String {
        return "Comment: $comment | From: $from"
    }

    companion object {
        const val serialVersionUID = 325L
        const val EXTRA_RESULT_RECEIVER = "result_receiver"
        const val EXTRA_SELF = "data"
        const val RETRY_LIMIT = 1
    }
}