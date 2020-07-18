package com.kanetik.feedback.model

import java.io.Serializable
import java.lang.StringBuilder

data class ContextData(val title: String? = "Context Data") : Serializable {
    var contextData: MutableList<ContextDataItem> = mutableListOf()

    fun add(key: String, value: Any) {
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
                    .append(item.value)
                    .append("\n")
        }

        return builder.toString()
    }

    companion object {
        const val serialVersionUID = 327L
    }
}