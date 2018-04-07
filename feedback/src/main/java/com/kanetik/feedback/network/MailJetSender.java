package com.kanetik.feedback.network;

import android.content.Context;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.model.Feedback;
import com.kanetik.feedback.utility.AppUtils;
import com.kanetik.feedback.utility.LogUtils;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Emailv31;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;

class MailJetSender implements Sender {
    private WeakReference<Context> context;

    public MailJetSender(Context context) {
        this.context = new WeakReference<>(context);
    }

    public boolean send(final Feedback feedback) {
        Context context = this.context.get();
        if (context == null) return false;

        // TODO: Get key & secret from init
        MailjetClient client = new MailjetClient("2b65a83e271971453abd6d80e38d5691", "9c099f92dfbd4e33da387eef3c809494", new ClientOptions("v3.1"));

        final String developerEmail = "jkane001@gmail.com"; // TODO: Get from init
        final String developerName = AppUtils.getAppLabel(context) + " Developer"; // TODO: Get from init

        final String appSupportEmail = "info@kanetik.com"; // TODO: Get from init
        final String appSupportName = AppUtils.getAppLabel(context) + " User"; // TODO: Get from init

        final String userEmail = feedback.from;
        final String userName = AppUtils.getAppLabel(context) + " User";

        final String subject = String.format(Locale.getDefault(), context.getString(R.string.kanetik_feedback_email_subject), AppUtils.getAppLabel(context));

        final String plainTextEmail = String.format(Locale.getDefault(), context.getString(R.string.kanetik_feedback_text_email_template), feedback.appData, feedback.deviceData, feedback.comment, new Date().toString());
        final String htmlEmail = String.format(Locale.getDefault(), context.getString(R.string.kanetik_feedback_html_email_template), feedback.appData.toHtml(), feedback.deviceData.toHtml(), feedback.comment, new Date().toString());

        try {
            JSONObject message = new JSONObject();

            JSONObject from = new JSONObject()
                    .put(Emailv31.Message.EMAIL, appSupportEmail)
                    .put(Emailv31.Message.NAME, appSupportName);

            JSONObject replyTo = new JSONObject()
                    .put(Emailv31.Message.EMAIL, userEmail)
                    .put(Emailv31.Message.NAME, userName);

            JSONArray to = new JSONArray()
                    .put(new JSONObject()
                            .put(Emailv31.Message.EMAIL, developerEmail)
                            .put(Emailv31.Message.NAME, developerName));

            message.put(Emailv31.Message.FROM, from)
                    .put(Emailv31.Message.REPLYTO, replyTo)
                    .put(Emailv31.Message.TO, to)
                    .put(Emailv31.Message.SUBJECT, subject)
                    .put(Emailv31.Message.TEXTPART, plainTextEmail)
                    .put(Emailv31.Message.HTMLPART, htmlEmail);

            JSONArray messageArray = new JSONArray();
            messageArray.put(message);

            String finalJson = messageArray.toString(4).replace("\\", "");

            if (KanetikFeedback.isDebug()) {
                LogUtils.i("POST KanetikFeedback - " + finalJson);
            }

            client.setDebug(MailjetClient.VERBOSE_DEBUG);
            client.post(new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, messageArray));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
