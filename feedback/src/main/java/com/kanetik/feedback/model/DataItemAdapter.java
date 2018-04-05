package com.kanetik.feedback.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kanetik.feedback.R;

import java.util.ArrayList;

public class DataItemAdapter extends ArrayAdapter<ContextDataItem> {
    public DataItemAdapter(Context context, ArrayList<ContextDataItem> contextDataItems) {
        super(context, 0, contextDataItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ContextDataItem contextDataItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.kanetik_feedback_data_item, parent, false);
        }

        TextView key = convertView.findViewById(R.id.key);
        TextView value = convertView.findViewById(R.id.value);

        key.setText(contextDataItem.key.replace("_", " "));
        value.setText(contextDataItem.value.toString());

        return convertView;
    }
}
