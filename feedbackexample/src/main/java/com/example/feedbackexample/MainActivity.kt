package com.example.feedbackexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kanetik.feedback.KanetikFeedback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.feedback).setOnClickListener {
            KanetikFeedback.getInstance(this).startFeedbackActivity(this)
        }
    }
}