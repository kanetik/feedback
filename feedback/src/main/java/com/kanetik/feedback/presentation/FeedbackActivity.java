package com.kanetik.feedback.presentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;
import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.utility.FeedbackUtils;
import com.kanetik.feedback.utility.LogUtils;

import java.util.Locale;
import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedback;
    private TextInputLayout feedbackLayout;
    private EditText email;
    private TextInputLayout emailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle(String.format(Locale.US, getString(R.string.kanetik_feedback_feedback_title_format), FeedbackUtils.getAppLabel(this)));

        Drawable closeIcon = Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.close_24dp)).mutate();
        DrawableCompat.setTint(closeIcon, ContextCompat.getColor(this, R.color.kanetik_feedback_primary_text));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(closeIcon);

        setupUiElements();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void setupUiElements() {
        TextView poweredBy = findViewById(R.id.kanetik_feedback_powered_by);
        feedback = findViewById(R.id.kanetik_feedback_feedback);
        feedbackLayout = findViewById(R.id.kanetik_feedback_feedback_layout);
        email = findViewById(R.id.kanetik_feedback_email);
        emailLayout = findViewById(R.id.kanetik_feedback_email_layout);
        TextView viewData = findViewById(R.id.kanetik_feedback_view_data);

        poweredBy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jkane001/kanetik-feedback/"));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
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
                    enableSendButton();
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
                    enableSendButton();
                } else {
                    disableSendButton();
                }
            }
        });

        viewData.setOnClickListener(v -> {
            FragmentManager manager = getSupportFragmentManager();
            Fragment frag = manager.findFragmentByTag("fragment_data_items");

            if (frag != null) {
                manager.beginTransaction().remove(frag).commit();
            }

            final FeedbackDataItemDialogFragment fragment = FeedbackDataItemDialogFragment.create();
            fragment.setShowsDialog(true);
            fragment.show(manager, "fragment_data_items");
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
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mMenu = menu;

        disableSendButton();

        return true;
    }

    private void disableSendButton() {
        if (mMenu == null) {
            return;
        }

        MenuItem sendFeedbackIcon = mMenu.findItem(R.id.action_send_feedback);
        Drawable icon = sendFeedbackIcon.getIcon().mutate();
        DrawableCompat.setTint(icon, ContextCompat.getColor(this, R.color.kanetik_feedback_light_gray));
        sendFeedbackIcon.setIcon(icon);
        sendFeedbackIcon.setEnabled(false);
    }

    private void enableSendButton() {
        if (mMenu == null) {
            return;
        }

        MenuItem sendFeedbackIcon = mMenu.findItem(R.id.action_send_feedback);
        sendFeedbackIcon.setEnabled(true);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            Drawable icon = sendFeedbackIcon.getIcon().mutate();
            int colorAttr = supportActionBar.getThemedContext().getResources()
                    .getIdentifier("colorAccent", "attr", supportActionBar.getThemedContext().getPackageName());

            TypedValue outValue = new TypedValue();
            supportActionBar.getThemedContext().getTheme().resolveAttribute(colorAttr, outValue, true);

            DrawableCompat.setTint(icon, outValue.data);
            sendFeedbackIcon.setIcon(icon);
        }
    }

    @Override
    public void onBackPressed() {
        cancelFeedback();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send_feedback) {
            if (KanetikFeedback.Companion.isDebug()) {
                LogUtils.i("Send KanetikFeedback");
            }

            KanetikFeedback.Companion.getInstance(this).sendFeedback(feedback.getText().toString(), email.getText().toString());

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
}
