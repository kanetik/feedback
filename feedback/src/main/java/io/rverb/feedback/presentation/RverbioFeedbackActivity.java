package io.rverb.feedback.presentation;

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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Event;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.DataUtils;
import io.rverb.feedback.utility.LogUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class RverbioFeedbackActivity extends AppCompatActivity {
    private String _screenshotFileName;
    private boolean _suppressScreenshot = false;

    private TextView _rverbPoweredBy;
    private EditText _rverbFeedback;
    private TextInputLayout _rverbFeedbackLayout;
    private EditText _rverbEmail;
    private TextInputLayout _rverbEmailLayout;
    private TextView _rverbAdditionalDataDescription;
    private ImageView _rverbThumbnail;
    private TextView _rverbEditScreenshot;
    private TextView _rverbViewData;
    private ImageView _rverbThumbnailDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rverb_activity_feedback);

        sendEvent(Event.EVENT_TYPE_FEEDBACK_START);

        Intent intent = getIntent();
        if (intent != null) {
            _screenshotFileName = getIntent().getStringExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(String.format(Locale.US, getString(R.string.rverb_feedback_title_format),
                AppUtils.getAppLabel(this)));

        Drawable closeIcon = AppUtils.tintDrawable(this, R.drawable.rverb_close_24dp, R.color
                .rverb_primary_text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(closeIcon);

        setupUiElements();
        setupScreenshotUI();
    }

    private void setupUiElements() {
        _rverbPoweredBy = (TextView) findViewById(R.id.rverb_powered_by);
        _rverbFeedback = (EditText) findViewById(R.id.rverb_feedback);
        _rverbFeedbackLayout = (TextInputLayout) findViewById(R.id.rverb_feedback_layout);
        _rverbEmail = (EditText) findViewById(R.id.rverb_email);
        _rverbEmailLayout = (TextInputLayout) findViewById(R.id.rverb_email_layout);
        _rverbThumbnail = (ImageView) findViewById(R.id.rverb_thumbnail);
        _rverbEditScreenshot = (TextView) findViewById(R.id.rverb_edit_screenshot);
        _rverbViewData = (TextView) findViewById(R.id.rverb_view_data);
        _rverbAdditionalDataDescription = (TextView) findViewById(R.id
                .rverb_additional_data_description);
        _rverbThumbnailDelete = (ImageView) findViewById(R.id.rverb_thumbnail_delete);

        _rverbPoweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openWebPage(RverbioFeedbackActivity.this, "https://rverb.io");
            }
        });

        _rverbFeedback.addTextChangedListener(new TextWatcher() {
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

        _rverbEmail.addTextChangedListener(new TextWatcher() {
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

        final EndUser endUser = RverbioUtils.getEndUser(this);
        if (endUser != null && !TextUtils.isEmpty(endUser.emailAddress)) {
            _rverbEmail.setText(endUser.emailAddress);
            _rverbEmailLayout.setVisibility(View.GONE);
        }

        _rverbViewData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                Fragment frag = manager.findFragmentByTag("fragment_data_items");

                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }

                final RverbioDataItemDialogFragment fragment = RverbioDataItemDialogFragment.create();
                fragment.setShowsDialog(true);
                fragment.show(manager, "fragment_data_items");
            }
        });

        _rverbEditScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        final ClickableSpan span = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                FragmentManager manager = getFragmentManager();
//                Fragment frag = manager.findFragmentByTag("fragment_data_items");
//
//                if (frag != null) {
//                    manager.beginTransaction().remove(frag).commit();
//                }
//
//                final RverbioDataItemDialogFragment fragment = RverbioDataItemDialogFragment.create();
//                fragment.show(manager, "fragment_data_items");
//            }
//        };

//        setLinkText(_rverbAdditionalDataDescription, span);

//        _rverbAdditionalDataDescription.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupScreenshotUI() {
        File screenshot = null;
        if (!TextUtils.isEmpty(_screenshotFileName) && !_suppressScreenshot) {
            screenshot = new File(_screenshotFileName);
        }

        if (screenshot != null) {
            final Intent previewScreenshotIntent = new Intent(this, RverbioScreenshotPreviewActivity.class);
            previewScreenshotIntent.putExtra(DataUtils.EXTRA_SCREENSHOT_FILE_NAME, _screenshotFileName);

            _rverbThumbnail.setImageDrawable(Drawable.createFromPath(_screenshotFileName));
            _rverbEditScreenshot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(previewScreenshotIntent, 764);
                }
            });
        } else {
            _rverbEditScreenshot.setVisibility(View.GONE);
            _rverbThumbnail.setVisibility(View.GONE);
            _rverbThumbnailDelete.setVisibility(View.GONE);
        }

        if (_rverbThumbnailDelete != null) {
            _rverbThumbnailDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _suppressScreenshot = true;

                    _rverbEditScreenshot.setVisibility(View.GONE);
                    _rverbThumbnail.setVisibility(View.GONE);
                    _rverbThumbnailDelete.setVisibility(View.GONE);

                    File screenshot = new File(_screenshotFileName);
                    screenshot.delete();
                }
            });
        }
    }

    private boolean validateForm() {
        boolean feedbackEntered = DataUtils.validateTextEntryNotEmpty(_rverbFeedbackLayout.getEditText());
        boolean emailEntered = DataUtils.validateTextEntryNotEmpty(_rverbEmailLayout.getEditText());
        boolean emailValid = DataUtils.validateTextEntryIsValid(_rverbEmailLayout.getEditText(),
                Patterns.EMAIL_ADDRESS);

        return feedbackEntered && emailEntered && emailValid;
    }

//    private void setLinkText(TextView showAll, ClickableSpan span) {
//        String extraDataClickable = getString(R.string.rverb_extra_data_description_clickable);
//        String extraDataDescription = String.format(Locale.getDefault(),
//                getString(R.string.rverb_extra_data_description),
//                extraDataClickable);
//        SpannableString ss = new SpannableString(extraDataDescription);
//
//        int startSpan = extraDataDescription.indexOf(extraDataClickable);
//        int endSpan = startSpan + extraDataClickable.length();
//
//        if (extraDataClickable.length() > 0) {
//            ss.setSpan(span, startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//
//        showAll.setText(ss);
//    }

    public void sendEvent(String event) {
        EndUser endUser = RverbioUtils.getEndUser(this);
        Event eventData = new Event(endUser.endUserId, event);
        RverbioUtils.recordData(this, eventData);
    }

    Menu mMenu;

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rverb_main_menu, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_send_feedback) {
            LogUtils.d("Send Feedback");

            final EndUser endUser = RverbioUtils.getEndUser(this);
            if (!TextUtils.isEmpty(_rverbEmail.getText()) && TextUtils.isEmpty(endUser.emailAddress)) {
                Rverbio.getInstance().setUserEmail(_rverbEmail.getText().toString());
            }

            File screenshot = null;
            if (!_suppressScreenshot) {
                screenshot = new File(_screenshotFileName);
            }

            Rverbio.getInstance().sendFeedback("", _rverbFeedback.getText().toString(), screenshot);

            finish();

            return true;
        } else if (item.getItemId() == android.R.id.home) {
            sendEvent(Event.EVENT_TYPE_FEEDBACK_CANCEL);

            File screenshot = new File(_screenshotFileName);
            screenshot.delete();

            finish();

            return true;
        }

        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.d("Screenshot Updated");
        super.onActivityResult(requestCode, resultCode, data);

        _rverbThumbnail.setImageDrawable(Drawable.createFromPath(_screenshotFileName));
    }
}
