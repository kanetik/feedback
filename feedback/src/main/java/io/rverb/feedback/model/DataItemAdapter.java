package io.rverb.feedback.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.rverb.feedback.R;

public class DataItemAdapter extends ArrayAdapter<DataItem> {
    public DataItemAdapter(Context context, ArrayList<DataItem> dataItems) {
        super(context, 0, dataItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataItem dataItem = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.rverb_data_item, parent, false);
        }

        TextView key = (TextView) convertView.findViewById(R.id.key);
        TextView value = (TextView) convertView.findViewById(R.id.value);

        key.setText(dataItem.key.replace("_", " "));
        value.setText(dataItem.value.toString());

        return convertView;
    }
}
