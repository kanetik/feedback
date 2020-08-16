package com.kanetik.feedback.model

import androidx.annotation.Keep

@Keep
open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}