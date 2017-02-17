package io.rverb.feedback.presentation;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;
import io.rverb.feedback.model.EndUser;
import io.rverb.feedback.model.Event;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class RverbioFeedbackDialogFragment extends AppCompatDialogFragment {
    private static final String PARAM_CONTENT_VIEW = "content_view";

    private static File _screenshot;
    private boolean _suppressScreenshot = false;

    private TextView _rverbPoweredBy;
    private EditText _rverbFeedback;
    private TextInputLayout _rverbFeedbackLayout;
    private EditText _rverbEmail;
    private TextInputLayout _rverbEmailLayout;
    private Button _rverbSubmit;
    private Button _rverbCancel;
    private TextView _rverbSystemData;
    private ScrollView _rverbSystemDataScrollview;
    private ImageView _rverbThumbnail;
    private TextView _rverbAdditionalDataDescription;
    private ImageView _rverbThumbnailDelete;
    private FrameLayout _rverbScreenshotContainer;

    public static RverbioFeedbackDialogFragment create() {
        RverbioFeedbackDialogFragment fragment = new RverbioFeedbackDialogFragment();
        fragment.setCancelable(false);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Rverbio.getInstance().sendEvent(Event.EVENT_TYPE_FEEDBACK_START);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.rverb_fragment_dialog, container, false);

        String appLabel = AppUtils.getAppLabel(getContext());
        if (RverbioUtils.isNullOrWhiteSpace(appLabel)) {
            appLabel = "App";
        }

        getDialog().setTitle(String.format(getString(R.string.rverb_feedback_title_format), appLabel));

        setupUiElements(view);
        setupScreenshotUI();

        _rverbPoweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openWebPage(getContext(), "https://rverb.io");
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
                resetValidationMessage(_rverbFeedbackLayout);
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
                resetValidationMessage(_rverbEmailLayout);
            }
        });

        final EndUser endUser = RverbioUtils.getEndUser(getContext());

        if (endUser != null && !TextUtils.isEmpty(endUser.emailAddress)) {
            _rverbEmail.setText(endUser.emailAddress);
            _rverbEmailLayout.setVisibility(View.GONE);
        }

        _rverbSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    if (_suppressScreenshot) {
                        _screenshot = null;
                    }

                    if (!TextUtils.isEmpty(_rverbEmail.getText()) && TextUtils.isEmpty(endUser.emailAddress)) {
                        Rverbio.getInstance().updateUserEmail(_rverbEmail.getText().toString());
                    }

                    Rverbio.getInstance().sendFeedback("", _rverbFeedback.getText().toString(), _screenshot);

                    _screenshot = null;
                    getDialog().dismiss();
                }
            }
        });

        _rverbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rverbio.getInstance().sendEvent(Event.EVENT_TYPE_FEEDBACK_CANCEL);
                _screenshot = null;
                getDialog().dismiss();
            }
        });

        StringBuilder dataString = new StringBuilder();

        for (Map.Entry<String, String> data : RverbioUtils.getExtraData(getContext()).entrySet()) {
            if (dataString.length() > 0) {
                dataString.append("\n");
            }

            dataString.append(data.getKey().replace("_", " ")).append(": ").append(data.getValue());
        }

        _rverbSystemData.setText(dataString);

        final ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (_rverbSystemDataScrollview.getVisibility() == View.GONE) {
                    _rverbSystemDataScrollview.setVisibility(View.VISIBLE);
                    setLinkText(_rverbAdditionalDataDescription, this, "Hide Data");
                } else {
                    _rverbSystemDataScrollview.setVisibility(View.GONE);
                    setLinkText(_rverbAdditionalDataDescription, this, "Show Data");
                }
            }
        };

        setLinkText(_rverbAdditionalDataDescription, span, "Show Data");

        _rverbAdditionalDataDescription.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private void setupUiElements(View view) {
        _rverbPoweredBy = (TextView) view.findViewById(R.id.rverb_powered_by);
        _rverbFeedback = (EditText) view.findViewById(R.id.rverb_feedback);
        _rverbFeedbackLayout = (TextInputLayout) view.findViewById(R.id.rverb_feedback_layout);
        _rverbEmail = (EditText) view.findViewById(R.id.rverb_email);
        _rverbEmailLayout = (TextInputLayout) view.findViewById(R.id.rverb_email_layout);
        _rverbSubmit = (Button) view.findViewById(R.id.rverb_submit);
        _rverbCancel = (Button) view.findViewById(R.id.rverb_cancel);
        _rverbSystemData = (TextView) view.findViewById(R.id.rverb_system_data);
        _rverbSystemDataScrollview = (ScrollView) view.findViewById(R.id.rverb_system_data_scrollview);
        _rverbThumbnail = (ImageView) view.findViewById(R.id.rverb_thumbnail);
        _rverbAdditionalDataDescription = (TextView) view.findViewById(R.id.rverb_additional_data_description);
        _rverbThumbnailDelete = (ImageView) view.findViewById(R.id.rverb_thumbnail_delete);
        _rverbScreenshotContainer = (FrameLayout) view.findViewById(R.id.rverb_screenshot_container);
    }

    private void setupScreenshotUI() {
        if (_screenshot == null && !_suppressScreenshot && Rverbio.getInstance().getOptions().isAttachScreenshotEnabled()) {
            _screenshot = Rverbio.getInstance().takeScreenshot(getActivity());
        }

        if (_screenshot != null) {
            _rverbThumbnail.setImageDrawable(Drawable.createFromPath(_screenshot.getAbsolutePath()));
        } else if (_rverbScreenshotContainer != null) {
            _rverbScreenshotContainer.setVisibility(View.GONE);
        }

        if (_rverbThumbnailDelete != null) {
            _rverbThumbnailDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _suppressScreenshot = true;

                    _rverbScreenshotContainer.setVisibility(View.GONE);

                    if (_screenshot != null) {
                        _screenshot.delete();
                        _screenshot = null;
                    }
                }
            });
        }
    }

    private boolean validateForm() {
        boolean feedbackValid = validateTextEntry(_rverbFeedbackLayout, getString(R.string.rverb_feedback_empty_validation_error));
        boolean emailValid = validateTextEntry(_rverbEmailLayout, getString(R.string.rverb_email_empty_validation_error));

        return feedbackValid && emailValid;
    }

    private boolean validateTextEntry(TextInputLayout fieldContainer, String errorMessage) {
        EditText field = fieldContainer.getEditText();

        if (field == null) return true;

        if (!TextUtils.isEmpty(field.getText())) {
            fieldContainer.setError(null);
            return true;
        } else {
            fieldContainer.setError(errorMessage);
            return false;
        }
    }

    private void resetValidationMessage(TextInputLayout fieldContainer) {
        EditText field = fieldContainer.getEditText();

        if (field == null) return;

        fieldContainer.setError(null);
    }

    private void setLinkText(TextView showAll, ClickableSpan span, String linkText) {
        String extraDataClickable = getString(R.string.rverb_extra_data_description_clickable);
        String extraDataDescription = String.format(Locale.getDefault(),
                getString(R.string.rverb_extra_data_description),
                extraDataClickable);
        SpannableString ss = new SpannableString(extraDataDescription);

        int startSpan = extraDataDescription.indexOf(extraDataClickable);
        int endSpan = startSpan + extraDataClickable.length();
        ss.setSpan(span, startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        showAll.setText(ss);
    }

    public int getTheme() {
        return R.style.rverb_fixed_dialog;
    }
}