package io.rverb.feedback.presentation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
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

        final ImageView deleteThumbnailButton = (ImageView) view.findViewById(R.id.rverb_thumnail_delete);
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

        final EditText feedback = (EditText) view.findViewById(R.id.rverb_problem);

        final Button button1 = (Button) view.findViewById(R.id.rverb_cancel);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _screenshot = null;
                getDialog().dismiss();
            }
        });

        final Button button2 = (Button) view.findViewById(R.id.rverb_submit);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_suppressScreenshot) {
                    _screenshot = null;
                }

                Rverbio.getInstance().sendFeedback("", feedback.getText().toString(), _screenshot);

                _screenshot = null;
                getDialog().dismiss();
            }
        });

        final TextView showAll = (TextView) view.findViewById(R.id.detail_read_all);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView layout = (ScrollView) view.findViewById(R.id.legal_privacy_scrollview);
                layout.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    public int getTheme() {
        return R.style.rverb_fixed_dialog;
    }
}