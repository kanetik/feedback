package com.kanetik.feedback.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.kanetik.feedback.model.Message
import com.kanetik.feedback.model.Message.Companion.serializer
import com.kanetik.feedback.utility.MessageUtils
import kotlinx.serialization.json.Json

class MessageWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    init {
        context = appContext
        tempFileName = workerParams.inputData.getString(Message.EXTRA_MESSAGE_TEMP_FILE_NAME)
        message = Json.decodeFromString(serializer, workerParams.inputData.getString(Message.EXTRA_MESSAGE_DATA)
                ?: throw NullPointerException("Intent's data object cannot be null"))
    }

    override fun doWork(): Result {
        // TODO: Make the sender configurable
        // TODO: Get identifier and secret from config
        val sender: Sender = MailJetSender(context, "2b65a83e271971453abd6d80e38d5691", "9c099f92dfbd4e33da387eef3c809494")

        return if (sender.send(message)) {
            if (!tempFileName.isNullOrEmpty()) {
                MessageUtils.deleteQueuedMessage(tempFileName)
            }

            Result.success()
        } else {
            Result.failure()
        }
    }

    companion object {
        lateinit var context: Context
        lateinit var message: Message
        var tempFileName: String? = null
    }
}