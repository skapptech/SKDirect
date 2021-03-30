package com.skdirect.activity;

import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.widget.Text;

@LayoutSpec
class FirstComponentSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c) {
        return Text.create(c).text("Hello ").build();
    }
}