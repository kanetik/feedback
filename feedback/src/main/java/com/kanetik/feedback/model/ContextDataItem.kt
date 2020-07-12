package com.kanetik.feedback.model

import androidx.annotation.Keep
import java.io.Serializable
import java.util.ArrayList

@Keep
data class ContextDataItem(val key: String, val value: Any?) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (other !is ContextDataItem) {
            return false
        }

        return key == other.key && value == other.value
    }

    companion object {
        const val serialVersionUID = 326L
    }
}