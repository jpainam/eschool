package com.edis.eschool.views;

import android.content.Context;
import android.util.AttributeSet;

public class SquareButton extends android.support.v7.widget.AppCompatButton {

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SquareButton(Context context) {
        super(context);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();

        setMeasuredDimension(height , height);
    }

}