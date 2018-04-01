package com.kanetik.feedback.presentation;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.kanetik.feedback.KanetikFeedback;
import com.kanetik.feedback.R;
import com.kanetik.feedback.model.DataItem;
import com.kanetik.feedback.model.DataItemAdapter;
import com.kanetik.feedback.utility.FeedbackUtils;

import java.util.ArrayList;

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
        final View view = inflater.inflate(R.layout.kanetik_feedback_fragment_data_item, container, false);

        getDialog().setTitle(getString(R.string.kanetik_feedback_data_items));

        ArrayList<DataItem> arrayList = new ArrayList<>();
        arrayList.addAll(FeedbackUtils.getExtraData(view.getContext()));
        arrayList.addAll(KanetikFeedback.getInstance(view.getContext()).getContextData());

        ListView listView = (ListView) view.findViewById(R.id.data_item_list);
        DataItemAdapter adapter = new DataItemAdapter(view.getContext(), arrayList);
        listView.setAdapter(adapter);

        return view;
    }

    public int getTheme() {
        return R.style.kanetik_feedback_fixed_dialog;
    }
}