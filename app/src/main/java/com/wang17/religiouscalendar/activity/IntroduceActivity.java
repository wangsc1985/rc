package com.wang17.religiouscalendar.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.wang17.religiouscalendar.R;

public class IntroduceActivity extends AppCompatActivity {

    TextView textViewIntroduce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);

        textViewIntroduce = (TextView)findViewById(R.id.textView_introduce);
        textViewIntroduce.setText(R.string.tdr);
        textViewIntroduce.setText(R.string.jyw);
    }
}
