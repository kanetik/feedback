package com.kanetik.feedback.model

import android.content.Context
import com.kanetik.feedback.utility.MessageUtils
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Message(@Transient val context: Context? = null, val developerEmail: String, val developerName: String, val supportEmail: String, val supportName: String, val replyToEmail: String, val replyToName: String, val subject: String, var messageBody: String) {
    constructor(context: Context?, developerEmail: String, developerName: String, supportEmail: String, supportName: String, replyToEmail: String, replyToName: String, subject: String) :
            this(context, developerEmail, developerName, supportEmail, supportName, replyToEmail, replyToName, subject, "")

    var appData: ContextData = ContextData()
    var deviceData: ContextData = ContextData()

    private var retryCount = 0

    init {
        MessageUtils.addSystemData(context, this)
    }

    fun incrementRetryCount() {
        retryCount++
    }

//    private fun addLogs() {
//            JSONArray attachments = new JSONArray();

//            File path = context.getFilesDir();
//            File[] list = path.listFiles((dir, name) -> name.equalsIgnoreCase("application.log"));
//            if (list != null && list.length == 1) {
//                File log = list[0];
//                int size = (int) log.length();
//                byte[] data = new byte[size];
//
//                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(log));
//                buf.read(data, 0, data.length);
//                buf.close();

//                JSONObject logData = new JSONObject()
//                        .put("ContentType", "text/plain")
//                        .put("Filename", "logcat.txt")
//                        .put("Base64Content", Base64.encodeToString(data, Base64.DEFAULT));

//                attachments.put(logData);
//            }
//    }

    fun retryAllowed(): Boolean {
        return if (RETRY_LIMIT == 0) {
            true
        } else {
            RETRY_LIMIT > retryCount
        }
    }

    fun toJson(): String {
        return Json.encodeToString(this)
    }

    companion object {
        const val EXTRA_MESSAGE_DATA = "MessageData"

        const val EXTRA_MESSAGE_TEMP_FILE_NAME = "MessageTempFileName"
        const val RETRY_LIMIT = 1

        val serializer = serializer()
    }
}