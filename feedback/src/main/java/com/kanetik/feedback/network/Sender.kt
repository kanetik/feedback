package com.kanetik.feedback.network

import com.kanetik.feedback.model.Message

interface Sender {
    fun send(message: Message): Boolean
}