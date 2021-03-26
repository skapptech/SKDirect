package com.skdirect.litho

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import com.facebook.litho.ComponentContext
import com.facebook.litho.LithoView
import com.facebook.litho.widget.Text
import com.skdirect.R

class LithoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var context =  ComponentContext(this);
        var component = Text.create(context)
                .text("Hello ")
                .textSizeDip(50F)
                .textColorRes(R.color.black)
                .textAlignment(Layout.Alignment.ALIGN_CENTER)
                .backgroundRes(R.color.white)
                .build();
        setContentView(LithoView.create(context, component));
    }
}