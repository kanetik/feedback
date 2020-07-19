package com.kanetik.feedback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import androidx.annotation.Keep
import com.kanetik.feedback.model.ContextDataItem
import com.kanetik.feedback.model.Feedback
import com.kanetik.feedback.presentation.FeedbackActivity
import com.kanetik.feedback.utility.FeedbackUtils
import com.kanetik.feedback.utility.LogUtils
import java.util.*

@Keep
class KanetikFeedback(context: Context) {
    var contextData: ArrayList<ContextDataItem>? = null
        get() {
            if (field == null) {
                contextData = ArrayList()
            }

            return field
        }

    init {
        appContext = context
    }

    /**
     * Add a single name-value pair to be sent to the developer with a feedback request.
     *
     * @param key   The name of the context data item
     * @param value The value of the context data item
     */
    fun addContextDataItem(key: String, value: String): KanetikFeedback? {
        val newItem = ContextDataItem(key, value)
        contextData!!.remove(newItem)
        contextData!!.add(newItem)
        return instance
    }

    /**
     * Add a collection of name-value pairs to be sent to the developer with a feedback request.
     *
     * @param items The map of name-value pairs to be sent
     */
    fun addContextDataItems(items: Map<String, Any?>): KanetikFeedback? {
        for ((key, value) in items) {
            val newItem = ContextDataItem(key, value)
            contextData!!.remove(newItem)
            contextData!!.add(newItem)
        }
        return instance
    }

    /**
     * Remove a single name-value pair from the context data to be sent to the developer with
     * a feedback request.
     *
     * @param key The name of the context data item to be removed
     */
    fun removeContextDataItem(key: String): KanetikFeedback? {
        for (item in contextData!!) {
            if (item.key == key) {
                contextData!!.remove(item)
            }
        }
        return instance
    }

    /**
     * Sends the user's request to the developer.
     *
     * @param feedbackText The text submitted by the end-user.
     */
    fun sendFeedback(feedbackText: String, from: String) {
        val feedback = Feedback(appContext, feedbackText, from)

        FeedbackUtils.addInstanceContextDataToFeedback(
                appContext,
                feedback
        )

        FeedbackUtils.persistData(
                appContext,
                feedback,
                object : ResultReceiver(Handler()) {
                    override fun onReceiveResult(
                            resultCode: Int,
                            resultData: Bundle
                    ) {
                        if (resultCode == Activity.RESULT_OK) {
                            FeedbackUtils.alertUser(appContext)
                        } else {
                            FeedbackUtils.handlePersistenceFailure(
                                    appContext,
                                    feedback
                            )
                        }
                    }
                })
    }

    /**
     * Show the feedback activity
     *
     * @param context The context from which you are launching the KanetikFeedback Activity.
     */
    fun startFeedbackActivity(context: Context) {
        context.startActivity(Intent(context, FeedbackActivity::class.java))
    }

    fun nothing(): Int {
        return 212
    }

    @Keep
    companion object {
        private var appContext: Context? = null

        /**
         * Helper method that indicates if KanetikFeedback has been initialized and is ready to use
         *
         * @return boolean indicating that initialization has or has not completed
         */
        var isInitialized = false
            private set

        /**
         * Gets the User Identifier.
         *
         * @return userIdentifier
         */
        var userIdentifier: String? = null
        private var contextData: ArrayList<ContextDataItem>? = null
        private lateinit var instance: KanetikFeedback

        /**
         * Gets the Kanetik feedback singleton, which is the primary interaction point the developer will have
         * with the feedback SDK.
         *
         * @return KanetikFeedback singleton instance.
         */
        @JvmStatic
        fun getInstance(context: Context): KanetikFeedback? {
            synchronized(KanetikFeedback::class.java) {
                if (instance == null || appContext == null) {
                    instance = KanetikFeedback(context.applicationContext)
                }

                return instance
            }
        }

        /**
         * Gets the Debugging state.
         *
         * @return debugging
         */
        val isDebug: Boolean
            get() = 0 != appContext!!.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE

        /**
         * Initializes the KanetikFeedback singleton. The developer's interactions with Kanetik KanetikFeedback will be
         * entirely via the singleton.
         *
         *
         * Initialization must be done before the KanetikFeedback singleton can be used.
         *
         * @param context Activity or Application Context
         */
        fun initialize(context: Context, userIdentifier: String?) {
            if (isInitialized) {
                return
            }

            KanetikFeedback(context)

            if (isDebug) {
                LogUtils.i("KanetikFeedback Initialize")
            }

            this.userIdentifier = userIdentifier

            // Send any previously queued requests
            FeedbackUtils.sendQueuedRequests(context)

            this.isInitialized = true
        }
    }
}