package com.aar.example.tapholdupbutton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aar.tapholdupbutton.TapHoldUpButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = findViewById(R.id.textView);
        TapHoldUpButton btn = findViewById(R.id.btn);
        btn.setOnButtonClickListener(new TapHoldUpButton.OnButtonClickListener() {
            @Override
            public void onLongHoldStart(View v) {
                textView.setText("on long click start");
            }

            @Override
            public void onLongHoldEnd(View v) {
                textView.setText("on long click end");
            }

            @Override
            public void onClick(View v) {
                textView.setText("on click");
            }
        });
    }
}
