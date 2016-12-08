package io.rverb.feedback.presentation;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.File;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;
import io.rverb.feedback.utility.AppUtils;
import io.rverb.feedback.utility.RverbioUtils;

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

        String appLabel = AppUtils.getAppLabel(getContext());
        if (RverbioUtils.isNullOrWhiteSpace(appLabel)) {
            appLabel = "App";
        }

        getDialog().setTitle(String.format(getString(R.string.rverb_feedback_title_format), appLabel));
        final File screenshot = Rverbio.getInstance().getScreenshot(getActivity());

        ImageView thumbnail = (ImageView) view.findViewById(R.id.rverb_thumbnail);
        if (screenshot != null) {
            thumbnail.setImageDrawable(Drawable.createFromPath(screenshot.getAbsolutePath()));
        } else {
            thumbnail.setVisibility(View.GONE);
        }

        final EditText feedback = (EditText) view.findViewById(R.id.rverb_problem);
        final Spinner feedbackType = (Spinner) view.findViewById(R.id.rverb_feedback_type);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.rverb_feedback_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        feedbackType.setAdapter(adapter);
        feedbackType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button button1 = (Button) view.findViewById(R.id.rverb_button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        Button button2 = (Button) view.findViewById(R.id.rverb_button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rverbio.getInstance().sendFeedback(feedbackType.getSelectedItem().toString(),
                        feedback.getText().toString(), screenshot);
                getDialog().dismiss();
            }
        });

        return view;
    }

    public int getTheme() {
        return R.style.rverb_fixed_dialog;
    }
}