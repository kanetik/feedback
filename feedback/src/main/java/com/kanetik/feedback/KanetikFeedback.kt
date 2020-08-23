package com.kanetik.feedback

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.annotation.Keep
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkInfo
import com.kanetik.feedback.model.ContextDataItem
import com.kanetik.feedback.model.Feedback
import com.kanetik.feedback.model.SingletonHolder
import com.kanetik.feedback.presentation.FeedbackActivity
import com.kanetik.feedback.utility.FeedbackUtils
import com.kanetik.feedback.utility.LogUtils
import java.util.*

@Keep
class KanetikFeedback private constructor(context: Context) {
    init {
        appContext = context.applicationContext
    }

    fun setUserIdentifier(identifier: String) {
        userIdentifier = identifier
    }

    val supportId: String
        get() {
            return FeedbackUtils.getSupportId(appContext)
        }

    /**
     * Add a single name-value pair to be sent to the developer with a feedback request.
     *
     * @param key   The name of the context data item
     * @param value The value of the context data item
     */
    fun addContextItem(key: String, value: String): KanetikFeedback? {
        val newItem = ContextDataItem(key, value)
        contextData!!.remove(newItem)
        contextData!!.add(newItem)
        return getInstance(appContext)
    }

    /**
     * Remove a single name-value pair from the context data to be sent to the developer with
     * a feedback request.
     *
     * @param key The name of the context data item to be removed
     */
    fun removeContextItem(key: String): KanetikFeedback? {
        for (item in contextData!!) {
            if (item.key == key) {
                contextData!!.remove(item)
            }
        }
        return getInstance(appContext)
    }

    /**
     * Sends the user's request to the developer.
     *
     * @param feedbackText The text submitted by the end-user.
     */
    fun sendFeedback(context: Context, feedbackText: String, from: String) {
        val feedback = Feedback(appContext, feedbackText, from)

        FeedbackUtils.addContextDataToFeedback(
                appContext,
                feedback
        )

        FeedbackUtils.queueSending(
                context,
                feedback
        ) { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.SUCCEEDED -> FeedbackUtils.alertUser(appContext)
                WorkInfo.State.FAILED -> FeedbackUtils.handleSendingFailure(appContext, feedback)
                else -> return@queueSending
            }
        }
    }

    /**
     * Show the feedback activity
     *
     * @param context The context from which you are launching the KanetikFeedback Activity.
     */
    fun startFeedbackActivity(context: Context) {
        context.startActivity(Intent(context, FeedbackActivity::class.java))
    }

    @Keep
    companion object : SingletonHolder<KanetikFeedback, Context>(::KanetikFeedback) {
        private lateinit var appContext: Context

        /**
         * Gets the User Identifier.
         *
         * @return userIdentifier
         */
        var userIdentifier: String = ""

        var contextData: ArrayList<ContextDataItem>? = null
            get() {
                if (field == null) {
                    contextData = arrayListOf()
                }

                return field
            }

        /**
         * Gets the Debugging state.
         *
         * @return debugging
         */
        val isDebug: Boolean
            get() = 0 != appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE


        /**
         * Initializes the KanetikFeedback singleton. The developer's interactions with Kanetik KanetikFeedback will be
         * entirely via the singleton.
         *
         *
         * Initialization must be done before the KanetikFeedback singleton can be used.
         *
         * @param context Activity or Application Context
         */
//        fun initialize(context: Context, userIdentifier: String) {
//            KanetikFeedback(context)
//
//            if (isDebug) {
//                LogUtils.i("KanetikFeedback Initialize")
//            }
//
//            this.userIdentifier = userIdentifier
//
//            // Send any previously queued requests
//            FeedbackUtils.sendQueuedRequests(context)
//        }
    }

//    companion object {
//        /**
//         * Gets the User Identifier.
//         *
//         * @return userIdentifier
//         */
//        var userIdentifier: String = ""
//        var contextData: ArrayList<ContextDataItem> = arrayListOf()
//
//        private lateinit var appContext: Context
//        private lateinit var instance: KanetikFeedback
//
//        /**
//         * Gets the Kanetik feedback singleton, which is the primary interaction point the developer will have
//         * with the feedback SDK.
//         *
//         * @return KanetikFeedback singleton instance.
//         */
//        @JvmStatic
//        fun getInstance(): KanetikFeedback {
//            // TODO: Proper singletons
////            synchronized(KanetikFeedback::class.java) {
////                if (instance == null) {
////                    instance = KanetikFeedback(appContext)
////                }
//
//                return instance
////            }
//        }
//
//        /**
//         * Gets the Debugging state.
//         *
//         * @return debugging
//         */
//        val isDebug: Boolean
//            get() = 0 != appContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
//
//        /**
//         * Initializes the KanetikFeedback singleton. The developer's interactions with Kanetik KanetikFeedback will be
//         * entirely via the singleton.
//         *
//         *
//         * Initialization must be done before the KanetikFeedback singleton can be used.
//         *
//         * @param context Activity or Application Context
//         */
//        fun initialize(context: Context, userIdentifier: String) {
//            KanetikFeedback(context)
//
//            if (isDebug) {
//                LogUtils.i("KanetikFeedback Initialize")
//            }
//
//            this.userIdentifier = userIdentifier
//
//            // Send any previously queued requests
//            FeedbackUtils.sendQueuedRequests(context)
//        }
//    }
}