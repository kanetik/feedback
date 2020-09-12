package com.kanetik.feedback.network;

import android.content.Context;
import android.text.TextUtils;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.model.Feedback;
import com.kanetik.feedback.model.Message;
import com.kanetik.feedback.utility.LogUtils;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.resource.Emailv31;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

class MailJetSender implements Sender {
    private final WeakReference<Context> _context;

    private MailjetClient _client;

    public MailJetSender(Context context, String identifier, String secret) {
        _context = new WeakReference<>(context);

        ClientOptions options = new ClientOptions("v3.1");
        options.setTimeout(10000);

        _client = new MailjetClient(identifier, secret, options);
    }

    public boolean send(Message message) {
        Context context = _context.get();
        if (context == null) {
            return false;
        }

        try {
            JSONObject messageJson = new JSONObject();

            JSONObject from = new JSONObject()
                    .put(Emailv31.Message.EMAIL, message.getSupportEmail())
                    .put(Emailv31.Message.NAME, message.getSupportName());

            JSONObject replyTo = new JSONObject()
                    .put(Emailv31.Message.EMAIL, message.getReplyToEmail())
                    .put(Emailv31.Message.NAME, message.getReplyToName());

            JSONArray to = new JSONArray()
                    .put(new JSONObject()
                            .put(Emailv31.Message.EMAIL, message.getDeveloperEmail())
                            .put(Emailv31.Message.NAME, message.getDeveloperName()));

            messageJson.put(Emailv31.Message.FROM, from)
                    .put(Emailv31.Message.REPLYTO, replyTo)
                    .put(Emailv31.Message.TO, to)
                    .put(Emailv31.Message.SUBJECT, message.getSubject())
                    .put(Emailv31.Message.TEXTPART, message.getMessageBody());
            //.put(Emailv31.Message.ATTACHMENTS, attachments);

            JSONArray messageArray = new JSONArray();
            messageArray.put(message);

            String finalJson = messageArray.toString(4).replace("\\", "");

            if (KanetikFeedback.Companion.isDebug()) {
                LogUtils.i("POST KanetikFeedback - " + finalJson);
            }

            _client.setDebug(KanetikFeedback.Companion.isDebug() ? MailjetClient.VERBOSE_DEBUG : MailjetClient.NO_DEBUG);
            _client.post(new MailjetRequest(Emailv31.resource).property(Emailv31.MESSAGES, messageArray));

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}
