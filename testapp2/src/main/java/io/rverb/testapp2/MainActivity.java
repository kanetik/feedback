package io.rverb.testapp2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.rverb.feedback.Rverbio;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rverbio.getInstance().startFeedbackActivity(MainActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView ticks = (TextView)findViewById(R.id.ticks);
        ticks.setText(Long.toString(System.currentTimeMillis()));
    }
}
