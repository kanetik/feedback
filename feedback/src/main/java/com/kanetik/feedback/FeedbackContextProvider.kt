package com.kanetik.feedback

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.work.Configuration
import androidx.work.WorkManager
import com.kanetik.feedback.utility.MessageUtils

class FeedbackContextProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        val applicationContext = context ?: return false

        // initialize WorkManager
        //WorkManager.initialize(applicationContext, Configuration.Builder().build())

        KanetikFeedback.getInstance(applicationContext).setUserIdentifier(MessageUtils.getSupportId(applicationContext))
        MessageUtils.sendQueuedRequests(applicationContext)

        return true
    }

    override fun query(uri: Uri, strings: Array<String>?, s: String?, strings1: Array<String>?, s1: String?): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        return 0
    }

    override fun update(uri: Uri, contentValues: ContentValues?, s: String?, strings: Array<String>?): Int {
        return 0
    }
}