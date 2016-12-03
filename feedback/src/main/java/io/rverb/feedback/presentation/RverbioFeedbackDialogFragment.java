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
import android.widget.ImageView;

import java.io.File;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;

public class RverbioFeedbackDialogFragment extends AppCompatDialogFragment {
    private static final String PARAM_CONTENT_VIEW = "content_view";

    public static RverbioFeedbackDialogFragment create(int contentView) {
        Bundle b = new Bundle();
        b.putInt(PARAM_CONTENT_VIEW, contentView);

        RverbioFeedbackDialogFragment fragment = new RverbioFeedbackDialogFragment();
        fragment.setArguments(b);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();

        int contentView = args.getInt(PARAM_CONTENT_VIEW);
        View view = inflater.inflate(contentView, container, false);

        getDialog().setTitle("Rverbio Feedback");
        final File screenshot = Rverbio.getInstance().getScreenshot(getActivity());

        ImageView thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        if (screenshot != null) {
            thumbnail.setImageDrawable(Drawable.createFromPath(screenshot.getAbsolutePath()));
        }
        else {
            thumbnail.setVisibility(View.GONE);
        }

        final EditText feedback = (EditText) view.findViewById(R.id.problem);

        Button button1 = (Button) view.findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        Button button2 = (Button) view.findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rverbio.getInstance().sendFeedback(screenshot, feedback.getText().toString());
                getDialog().dismiss();
            }
        });

        return view;
    }

    public int getTheme() {
        return R.style.FixedDialog;
    }
}