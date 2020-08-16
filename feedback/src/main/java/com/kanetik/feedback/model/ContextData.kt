package com.kanetik.feedback.model

import androidx.annotation.Keep
import java.lang.StringBuilder
import kotlinx.serialization.*

@Keep
@Serializable
data class ContextData(val title: String? = "Context Data") {
    var contextData: MutableList<ContextDataItem> = mutableListOf()

    fun add(key: String, value: String) {
        contextData.add(ContextDataItem(key, value))
    }

    fun add(key: String, value: Int) {
        contextData.add(ContextDataItem(key, value))
    }

    override fun toString(): String {
        val builder = StringBuilder(title!!)
                .append("\n")
                .append("------------------------")
                .append("\n")

        contextData.onEach { item ->
            builder
                    .append((item.key))
                    .append(": ")
                    .append(item.getValue())
                    .append("\n")
        }

        return builder.toString()
    }
}