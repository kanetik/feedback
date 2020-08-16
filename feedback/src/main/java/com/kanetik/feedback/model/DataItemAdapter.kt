package com.kanetik.feedback.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Keep
import com.kanetik.feedback.R
import java.util.*

@Keep
class DataItemAdapter(context: Context, contextDataItems: ArrayList<ContextDataItem>) : ArrayAdapter<ContextDataItem>(context, 0, contextDataItems) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cvtView = convertView
        val contextDataItem = getItem(position)

        if (cvtView == null) {
            cvtView = LayoutInflater.from(context).inflate(R.layout.kanetik_feedback_data_item, parent, false)
        }

        val key = convertView?.findViewById<TextView>(R.id.key)
        key?.text = contextDataItem!!.key.replace("_", " ")

        val value = convertView?.findViewById<TextView>(R.id.value)
        value?.text = contextDataItem.getValue()

        return cvtView!!
    }
}