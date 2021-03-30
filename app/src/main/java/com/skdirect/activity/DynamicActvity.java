package com.skdirect.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.litho.ComponentContext;
import com.facebook.litho.LithoView;
import com.facebook.litho.widget.Text;

public class DynamicActvity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ComponentContext c = new ComponentContext(this);

        setContentView(LithoView.create(this, Text.create(c).text("Hello World!").build()));
    }
}
