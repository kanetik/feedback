package com.kanetik.feedback.presentation;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.utility.AppUtils;
import com.kanetik.feedback.utility.FeedbackUtils;
import com.kanetik.feedback.utility.LogUtils;

import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {
    private TextView poweredBy;
    private EditText feedback;
    private TextInputLayout feedbackLayout;
    private EditText email;
    private TextInputLayout emailLayout;
    private TextView additionalDataDescription;
    private TextView viewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kanetik_feedback_activity_feedback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(String.format(Locale.US, getString(R.string.kanetik_feedback_feedback_title_format), AppUtils.getAppLabel(this)));

        Drawable closeIcon = AppUtils.tintDrawable(this, R.drawable.kanetik_feedback_close_24dp, R.color.kanetik_feedback_primary_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(closeIcon);

        setupUiElements();
    }

    private void setupUiElements() {
        poweredBy = findViewById(R.id.kanetik_feedback_powered_by);
        feedback = findViewById(R.id.kanetik_feedback_feedback);
        feedbackLayout = findViewById(R.id.kanetik_feedback_feedback_layout);
        email = findViewById(R.id.kanetik_feedback_email);
        emailLayout = findViewById(R.id.kanetik_feedback_email_layout);
        viewData = findViewById(R.id.kanetik_feedback_view_data);
        additionalDataDescription = findViewById(R.id.kanetik_feedback_additional_data_description);

        poweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openWebPage(FeedbackActivity.this, "https://github.com/jkane001/kanetik-feedback/");
            }
        });

        feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateForm()) {
                    enableSendButton(R.attr.colorAccent);
                } else {
                    disableSendButton();
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validateForm()) {
                    enableSendButton(R.attr.colorAccent);
                } else {
                    disableSendButton();
                }
            }
        });

        viewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                Fragment frag = manager.findFragmentByTag("fragment_data_items");

                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }

                final FeedbackDataItemDialogFragment fragment = FeedbackDataItemDialogFragment.create();
                fragment.setShowsDialog(true);
                fragment.show(manager, "fragment_data_items");
            }
        });
    }

    private boolean validateForm() {
        boolean feedbackEntered = FeedbackUtils.validateTextEntryNotEmpty(feedbackLayout.getEditText());
        boolean emailEntered = FeedbackUtils.validateTextEntryNotEmpty(emailLayout.getEditText());
        boolean emailValid = FeedbackUtils.validateTextEntryIsValid(emailLayout.getEditText(), Patterns.EMAIL_ADDRESS);

        return feedbackEntered && emailEntered && emailValid;
    }

    Menu mMenu;

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kanetik_feedback_main_menu, menu);
        mMenu = menu;

        disableSendButton();

        return true;
    }

    private void disableSendButton() {
        if (mMenu == null) {
            return;
        }

        MenuItem sendFeedbackIcon = mMenu.findItem(R.id.action_send_feedback);
        AppUtils.tintSupportBarIcon(this, sendFeedbackIcon);
        sendFeedbackIcon.setEnabled(false);
    }

    private void enableSendButton(int color) {
        if (mMenu == null) {
            return;
        }

        MenuItem sendFeedbackIcon = mMenu.findItem(R.id.action_send_feedback);
        sendFeedbackIcon.setEnabled(true);
        AppUtils.tintSupportBarIcon(getSupportActionBar(), sendFeedbackIcon, color);
    }

    @Override
    public void onBackPressed() {
        cancelFeedback();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send_feedback) {
            if (KanetikFeedback.isDebug()) {
                LogUtils.i("Send KanetikFeedback");
            }

            KanetikFeedback.getInstance(this).sendFeedback("", feedback.getText().toString());

            finish();

            return true;
        } else if (item.getItemId() == android.R.id.home) {
            cancelFeedback();

            return true;
        }

        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    private void cancelFeedback() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (KanetikFeedback.isDebug()) {
            LogUtils.i("Screenshot Updated");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
