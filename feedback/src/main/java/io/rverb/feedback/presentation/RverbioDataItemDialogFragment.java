package io.rverb.feedback.presentation;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import io.rverb.feedback.R;
import io.rverb.feedback.Rverbio;
import io.rverb.feedback.model.DataItem;
import io.rverb.feedback.model.DataItemAdapter;
import io.rverb.feedback.utility.RverbioUtils;

public class RverbioDataItemDialogFragment extends DialogFragment {
    public static RverbioDataItemDialogFragment create() {
        RverbioDataItemDialogFragment fragment = new RverbioDataItemDialogFragment();
        fragment.setCancelable(true);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        final View view = inflater.inflate(R.layout.rverb_fragment_data_item, container, false);

        getDialog().setTitle(getString(R.string.rverb_data_items));

        ArrayList<DataItem> arrayList = new ArrayList<>();
        arrayList.addAll(RverbioUtils.getExtraData(view.getContext()));
        arrayList.addAll(Rverbio.getInstance(view.getContext()).getContextData());

        ListView listView = (ListView) view.findViewById(R.id.data_item_list);
        DataItemAdapter adapter = new DataItemAdapter(view.getContext(), arrayList);
        listView.setAdapter(adapter);

        return view;
    }

    public int getTheme() {
        return R.style.rverb_fixed_dialog;
    }
}