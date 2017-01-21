package io.rverb.feedback.presentation;

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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.RverbioUtils;

public class RverbioFeedbackDialogFragment extends AppCompatDialogFragment {
    private static final String PARAM_CONTENT_VIEW = "content_view";

    private static File _screenshot;
    private boolean _suppressScreenshot = false;

    public static RverbioFeedbackDialogFragment create(int contentView) {
        Bundle b = new Bundle();
        b.putInt(PARAM_CONTENT_VIEW, contentView);

        RverbioFeedbackDialogFragment fragment = new RverbioFeedbackDialogFragment();
        fragment.setCancelable(false);
        fragment.setArguments(b);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        int contentView = args.getInt(PARAM_CONTENT_VIEW);
        final View view = inflater.inflate(contentView, container, false);

        String appLabel = AppUtils.getAppLabel(getContext());
        if (RverbioUtils.isNullOrWhiteSpace(appLabel)) {
            appLabel = "App";
        }

        getDialog().setTitle(String.format(getString(R.string.rverb_feedback_title_format), appLabel));

        if (_screenshot == null && !_suppressScreenshot && Rverbio.getInstance().getOptions().isAttachScreenshotEnabled()) {
            _screenshot = Rverbio.getInstance().getScreenshot(getActivity());
        }

        final FrameLayout screenshotContainer = (FrameLayout) view.findViewById(R.id.rverb_screenshot_container);
        final ImageView thumbnail = (ImageView) view.findViewById(R.id.rverb_thumbnail);

        if (_screenshot != null) {
            thumbnail.setImageDrawable(Drawable.createFromPath(_screenshot.getAbsolutePath()));
        } else if (screenshotContainer != null) {
            screenshotContainer.setVisibility(View.GONE);
        }

        final ImageView deleteThumbnailButton = (ImageView) view.findViewById(R.id.rverb_thumbnail_delete);
        if (deleteThumbnailButton != null) {
            deleteThumbnailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _suppressScreenshot = true;

                    if (screenshotContainer != null) {
                        screenshotContainer.setVisibility(View.GONE);
                    }

                    if (_screenshot != null) {
                        _screenshot.delete();
                        _screenshot = null;
                    }
                }
            });
        }

        final TextView poweredBy = (TextView) view.findViewById(R.id.rverb_powered_by);
        poweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.openWebPage(getContext(), "https://rverb.io");
            }
        });

        final TextInputLayout rverbFeedbackLayout = (TextInputLayout) view.findViewById(R.id.rverb_problem_layout);
        final EditText rverbFeedback = (EditText) view.findViewById(R.id.rverb_problem);

        rverbFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateTextEntry(rverbFeedbackLayout);
            }
        });

        rverbFeedback.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validateTextEntry(rverbFeedbackLayout);
                }
            }
        });

        final Button submitButton = (Button) view.findViewById(R.id.rverb_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTextEntry(rverbFeedbackLayout)) {
                    if (_suppressScreenshot) {
                        _screenshot = null;
                    }

                    Rverbio.getInstance().sendFeedback("", rverbFeedback.getText().toString(), _screenshot);

                    _screenshot = null;
                    getDialog().dismiss();
                }
            }
        });

        final Button cancelButton = (Button) view.findViewById(R.id.rverb_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _screenshot = null;
                getDialog().dismiss();
            }
        });

        TextView dataPairs = (TextView) view.findViewById(R.id.system_data);
        StringBuilder dataString = new StringBuilder();

        for (Map.Entry<String, String> data : RverbioUtils.getExtraData(getContext()).entrySet()) {
            if (dataString.length() > 0) {
                dataString.append("\n");
            }

            dataString.append(data.getKey().replace("_", " ")).append(": ").append(data.getValue());
        }

        dataPairs.setText(dataString);

        final TextView showAll = (TextView) view.findViewById(R.id.additional_data_description);

        final ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                ScrollView layout = (ScrollView) view.findViewById(R.id.system_data_scrollview);

                if (layout.getVisibility() == View.GONE) {
                    layout.setVisibility(View.VISIBLE);
                    setLinkText(showAll, this, "Hide Data");
                } else {
                    layout.setVisibility(View.GONE);
                    setLinkText(showAll, this, "Show Data");
                }
            }
        };

        setLinkText(showAll, span, "Show Data");

        showAll.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    private boolean validateTextEntry(TextInputLayout fieldContainer) {
        EditText field = fieldContainer.getEditText();

        if (field == null) return true;

        if (!TextUtils.isEmpty(field.getText())) {
            fieldContainer.setError(null);
            return true;
        } else {
            fieldContainer.setError(getString(R.string.rverb_feedback_empty_validation_error));
            return false;
        }
    }

    private void setLinkText(TextView showAll, ClickableSpan span, String linkText) {
        String extraDataClickable = "additional data";
        String extraDataDescription = String.format(Locale.getDefault(),
                "In order to provide excellent customer service, some %s will be sent with this feedback.",
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