package com.kanetik.feedback.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.model.ContextDataItem;
import com.kanetik.feedback.model.DataItemAdapter;
import com.kanetik.feedback.utility.FeedbackUtils;

import java.util.ArrayList;
import java.util.Objects;

public class FeedbackDataItemDialogFragment extends DialogFragment {
    public static FeedbackDataItemDialogFragment create() {
        FeedbackDataItemDialogFragment fragment = new FeedbackDataItemDialogFragment();
        fragment.setCancelable(true);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_data_item, container, false);

        Objects.requireNonNull(getDialog()).setTitle(getString(R.string.kanetik_feedback_data_items));

        ArrayList<ContextDataItem> arrayList = new ArrayList<>();
        arrayList.addAll(FeedbackUtils.getExtraData(view.getContext()));
        arrayList.addAll(Objects.requireNonNull(KanetikFeedback.Companion.getContextData()));

        ListView listView = view.findViewById(R.id.data_item_list);
        DataItemAdapter adapter = new DataItemAdapter(view.getContext(), arrayList);
        listView.setAdapter(adapter);

        return view;
    }

    public int getTheme() {
        return R.style.kanetik_feedback_fixed_dialog;
    }
}