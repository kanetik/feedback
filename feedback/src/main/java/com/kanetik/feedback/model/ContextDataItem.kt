package com.kanetik.feedback.model

import kotlinx.serialization.*

@Serializable
data class ContextDataItem(val key: String, val stringValue: String?, val intValue: Int?) {
    constructor(key: String, stringValue: String): this(key, stringValue, null)
    constructor(key: String, intValue: Int?): this(key, null, intValue)

    override fun equals(other: Any?): Boolean {
        if (other !is ContextDataItem) {
            return false
        }

        return key == other.key && (stringValue == other.stringValue || intValue == other.intValue)
    }

    fun getValue(): String {
        return stringValue ?: intValue.toString()
    }
}