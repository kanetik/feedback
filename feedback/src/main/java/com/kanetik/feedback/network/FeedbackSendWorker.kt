package com.kanetik.feedback.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kanetik.feedback.model.Feedback
import com.kanetik.feedback.model.Feedback.Companion.serializer
import com.kanetik.feedback.utility.FeedbackUtils
import kotlinx.serialization.json.Json

class FeedbackSendWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    init {
        context = appContext
        tempFileName = workerParams.inputData.getString(Feedback.EXTRA_FEEDBACK_TEMP_FILE_NAME)
        feedbackObject = Json.decodeFromString(serializer, workerParams.inputData.getString(Feedback.EXTRA_FEEDBACK_DATA)
                ?: throw NullPointerException("Intent's data object is null"))
    }

    override fun doWork(): Result {
        // TODO: Make the sender configurable
        return if (MailJetSender(context).send(feedbackObject)) {
            if (!tempFileName.isNullOrEmpty()) {
                FeedbackUtils.deleteQueuedFeedback(tempFileName)
            }

            Result.success()
        } else {
            Result.failure()
        }
    }

    companion object {
        lateinit var context: Context
        lateinit var feedbackObject: Feedback
        var tempFileName: String? = null
    }
}