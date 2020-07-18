package com.kanetik.feedback.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.model.Feedback;
import com.kanetik.feedback.utility.FeedbackUtils;
import com.kanetik.feedback.utility.LogUtils;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Emailv31;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.UUID;

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
        final String developerName = FeedbackUtils.getAppLabel(context) + " Developer"; // TODO: Get from init

        final String appSupportEmail = "info@kanetik.com"; // TODO: Get from init
        final String appSupportName = FeedbackUtils.getAppLabel(context) + " User"; // TODO: Get from init

        final String userEmail = feedback.getFrom();

        final String subject = String.format(Locale.getDefault(), context.getString(R.string.kanetik_feedback_email_subject), FeedbackUtils.getAppLabel(context), UUID.randomUUID().toString());

        String plainTextEmail = feedback.getComment();

        try {
            JSONObject message = new JSONObject();

            JSONObject from = new JSONObject()
                    .put(Emailv31.Message.EMAIL, appSupportEmail)
                    .put(Emailv31.Message.NAME, appSupportName);

            JSONObject replyTo = new JSONObject()
                    .put(Emailv31.Message.EMAIL, userEmail)
                    .put(Emailv31.Message.NAME, userEmail);

            JSONArray to = new JSONArray()
                    .put(new JSONObject()
                            .put(Emailv31.Message.EMAIL, developerEmail)
                            .put(Emailv31.Message.NAME, developerName));

            JSONArray attachments = new JSONArray();

            File path = context.getFilesDir();
            File list[] = path.listFiles((dir, name) -> name.equalsIgnoreCase("application.log"));
            if (list != null && list.length == 1) {
                File log = list[0];
                int size = (int) log.length();
                byte[] data = new byte[size];
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(log));
                    buf.read(data, 0, data.length);
                    buf.close();

                    JSONObject logData = new JSONObject()
                            .put("ContentType", "text/plain")
                            .put("Filename", "logcat.txt")
                            .put("Base64Content", Base64.encodeToString(data, Base64.DEFAULT));

                    attachments.put(logData);
                } catch (Exception ignored) {
                }
            }

            plainTextEmail = TextUtils.concat(
                    plainTextEmail,
                    "\n\n\n",
                    feedback.getAppData().toString(),
                    "\n\n",
                    feedback.getDeviceData().toString(),
                    "\n\n",
                    feedback.getDevData().toString()).toString();

            message.put(Emailv31.Message.FROM, from)
                    .put(Emailv31.Message.REPLYTO, replyTo)
                    .put(Emailv31.Message.TO, to)
                    .put(Emailv31.Message.SUBJECT, subject)
                    .put(Emailv31.Message.TEXTPART, plainTextEmail)
                    .put(Emailv31.Message.ATTACHMENTS, attachments);

            JSONArray messageArray = new JSONArray();
            messageArray.put(message);

            String finalJson = messageArray.toString(4).replace("\\", "");

            if (KanetikFeedback.Companion.isDebug()) {
                LogUtils.i("POST KanetikFeedback - " + finalJson);
            }

            client.setDebug(KanetikFeedback.Companion.isDebug() ? MailjetClient.VERBOSE_DEBUG : MailjetClient.NO_DEBUG);
            client.post(new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, messageArray));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
