package com.skdirect.litho

import com.facebook.litho.ClickEvent
import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.annotations.*
import com.facebook.litho.widget.Text
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import com.skdirect.R

class LayoutSpecs {
    @LayoutSpec
    object MyComponentSpec {
        @OnCreateLayout
        fun onCreateLayout(componentContext: ComponentContext,
                           @Prop title: String,
                           @Prop(optional = true) subtitle: String,
                           @Prop clickAction: () -> Unit): Component {
            return Column.create(componentContext)
                    .backgroundRes(R.color.white)
                    .paddingRes(YogaEdge.BOTTOM, R.dimen.dimen_16dp)
                    .paddingRes(YogaEdge.TOP, R.dimen.dimen_16dp)
                    .paddingRes(YogaEdge.START, R.dimen.dimen_16dp)
                    .paddingRes(YogaEdge.END, R.dimen.dimen_16dp)
                    .alignItems(YogaAlign.CENTER)
                    .clickHandler(MyComponent.onItemClick(componentContext, clickAction))
                    .child(
                            Text.create(componentContext)
                                    .text(title)
                                    .textSizeSp(40f)
                    )
                    .child(
                            if(!subtitle.isNullOrEmpty()){
                                Text.create(componentContext)
                                        .text(subtitle)
                                        .textSizeSp(40f)
                            }else{
                                null
                            }
                    )
                    .build()
        }
        @OnEvent(ClickEvent::class)
        fun onItemClick(c: ComponentContext, @Param clickAction: () -> Unit) {
            clickAction?.invoke()
        }
    }
}