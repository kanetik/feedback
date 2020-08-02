package com.kanetik.feedback.network;

import android.content.Context;

import com.kanetik.feedback.model.Feedback;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

class MailJetSender implements Sender {
    private WeakReference<Context> context;

    public MailJetSender(Context context) {
        this.context = new WeakReference<>(context);
    }

    public boolean send(final Feedback feedback) {
        Context context = this.context.get();
        if (context == null) return false;

        MailjetClient client = new MailjetClient(
                "2b65a83e271971453abd6d80e38d5691",
                "9c099f92dfbd4e33da387eef3c809494",
                new ClientOptions("v3.1"));

        client.setDebug(MailjetClient.VERBOSE_DEBUG);

        MailjetRequest request = null;
        try {
            request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", "jkane001@gmail.com")
                                            .put("Name", "Me"))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", "info@kanetik.com")
                                                    .put("Name", "You")))
                                    .put(Emailv31.Message.SUBJECT, "My first Mailjet Email!")
                                    .put(Emailv31.Message.TEXTPART, "Greetings from Mailjet!")
                                    .put(Emailv31.Message.HTMLPART, "<h3>Dear passenger 1, welcome to <a href=\"https://www.mailjet.com/\">Mailjet</a>!</h3><br />May the delivery force be with you!")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            client.post(request);
        } catch (MailjetException | MailjetSocketTimeoutException e) {
            e.printStackTrace();
        }

        // TODO: Get key & secret from init
//        MailjetClient client = new MailjetClient("2b65a83e271971453abd6d80e38d5691", "9c099f92dfbd4e33da387eef3c809494", new ClientOptions("v3.1"));
//
//        final String developerEmail = "jkane001@gmail.com"; // TODO: Get from init
//        final String developerName = FeedbackUtils.getAppLabel(context) + " Developer"; // TODO: Get from init
//
//        final String appSupportEmail = "info@kanetik.com"; // TODO: Get from init
//        final String appSupportName = FeedbackUtils.getAppLabel(context) + " User"; // TODO: Get from init
//
//        final String userEmail = feedback.getFrom();
//
//        final String subject = String.format(Locale.getDefault(), context.getString(R.string.kanetik_feedback_email_subject), FeedbackUtils.getAppLabel(context), UUID.randomUUID().toString());
//
//        String plainTextEmail = feedback.getComment();
//
//        try {
//            JSONObject message = new JSONObject();
//
//            JSONObject from = new JSONObject()
//                    .put(Emailv31.Message.EMAIL, appSupportEmail)
//                    .put(Emailv31.Message.NAME, appSupportName);
//
//            JSONObject replyTo = new JSONObject()
//                    .put(Emailv31.Message.EMAIL, userEmail)
//                    .put(Emailv31.Message.NAME, userEmail);
//
//            JSONArray to = new JSONArray()
//                    .put(new JSONObject()
//                            .put(Emailv31.Message.EMAIL, developerEmail)
//                            .put(Emailv31.Message.NAME, developerName));
//
//            JSONArray attachments = new JSONArray();
//
//            File path = context.getFilesDir();
//            File list[] = path.listFiles((dir, name) -> name.equalsIgnoreCase("application.log"));
//            if (list != null && list.length == 1) {
//                File log = list[0];
//                int size = (int) log.length();
//                byte[] data = new byte[size];
//                try {
//                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(log));
//                    buf.read(data, 0, data.length);
//                    buf.close();
//
//                    JSONObject logData = new JSONObject()
//                            .put("ContentType", "text/plain")
//                            .put("Filename", "logcat.txt")
//                            .put("Base64Content", Base64.encodeToString(data, Base64.DEFAULT));
//
//                    attachments.put(logData);
//                } catch (Exception ignored) {
//                }
//            }
//
//            plainTextEmail = TextUtils.concat(
//                    plainTextEmail,
//                    "\n\n\n",
//                    feedback.getAppData().toString(),
//                    "\n\n",
//                    feedback.getDeviceData().toString(),
//                    "\n\n",
//                    feedback.getDevData().toString()).toString();
//
//            message.put(Emailv31.Message.FROM, from)
//                    .put(Emailv31.Message.REPLYTO, replyTo)
//                    .put(Emailv31.Message.TO, to)
//                    .put(Emailv31.Message.SUBJECT, subject)
//                    .put(Emailv31.Message.TEXTPART, "test");
//                    //.put(Emailv31.Message.ATTACHMENTS, attachments);
//
//            JSONArray messageArray = new JSONArray();
//            messageArray.put(message);
//
//            String finalJson = messageArray.toString(4).replace("\\", "");
//
//            if (KanetikFeedback.Companion.isDebug()) {
//                LogUtils.i("POST KanetikFeedback - " + finalJson);
//            }
//
//            client.setDebug(MailjetClient.VERBOSE_DEBUG);
//            //client.setDebug(KanetikFeedback.Companion.isDebug() ? MailjetClient.VERBOSE_DEBUG : MailjetClient.NO_DEBUG);
//            client.post(new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, messageArray));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return true;
    }
}
