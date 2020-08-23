package com.kanetik.feedback.network

import android.app.Activity
import android.content.Context
import android.os.ResultReceiver
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kanetik.feedback.model.Feedback
import com.kanetik.feedback.model.Feedback.Companion.serializer
import kotlinx.serialization.json.Json

class FeedbackSendWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
//        val feedbackObject = Json.decodeFromString(serializer, intent.getStringExtra(Feedback.EXTRA_SELF))
//                ?: throw NullPointerException("Intent's data object is null")
//
//        // TODO: Make the sender configurable
//
//        // TODO: Make the sender configurable
//        val sender: Sender = MailJetSender(this)
//        val isSent = sender.send(feedbackObject)
//
//        var resultReceiver: ResultReceiver? = null
//        if (intent.hasExtra(Feedback.EXTRA_RESULT_RECEIVER)) {
//            resultReceiver = intent.getParcelableExtra<ResultReceiver>(Feedback.EXTRA_RESULT_RECEIVER)
//        }
//
//        if (resultReceiver != null) {
//            if (isSent) {
//                resultReceiver.send(Activity.RESULT_OK, null)
//            } else {
//                resultReceiver.send(Activity.RESULT_CANCELED, null)
//            }
//        }

        return ListenableWorker.Result.failure()
    }
}