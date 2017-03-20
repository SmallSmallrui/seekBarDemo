package com.hurui.seekbardemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;


/**
 * Created by anderson on 2016/6/5.
 */
public class DashboardViewAttr {
    private String mText = "";
    private int progressStrokeWidth;
    private int background;
    private float maxNum;
    private int padding;
    private int progressColor; //进度条颜色
    private CharSequence[] tikeStrArray;
    private int tikeStrColor;
    private float tikeStrSize;
    private int circleColor;
    private float shuDuStrSize ;
    private int littebackgroud ;
    public DashboardViewAttr(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DashboardView, defStyleAttr, 0);
        progressStrokeWidth = (int) ta.getDimension(R.styleable.DashboardView_progressStrokeWidth, 24);
        background = ta.getColor(R.styleable.DashboardView_backgroundColor, 0);
        shuDuStrSize = ta.getDimension(R.styleable.DashboardView_shuDuSize , PxUtils.spToPx(35 , context) );
        maxNum = ta.getInt(R.styleable.DashboardView_maxNumber, 120);
        padding = PxUtils.dpToPx(ta.getInt(R.styleable.DashboardView_padding, 0), context);
        progressColor = ta.getColor(R.styleable.DashboardView_progressColor, context.getResources().getColor(R.color.skyblue));
        tikeStrColor = ta.getColor(R.styleable.DashboardView_tikeStrColor, context.getResources().getColor(android.R.color.black));
        tikeStrSize = ta.getDimension(R.styleable.DashboardView_tikeStrSize, 10);
        circleColor = ta.getColor(R.styleable.DashboardView_centerCircleColor, context.getResources().getColor(R.color.outsideBlue));
        littebackgroud = ta.getColor(R.styleable.DashboardView_littebackgroud , context.getResources().getColor(R.color.yuanHu));
        ta.recycle();
    }

    public int getCircleColor() {
        return circleColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public int getPadding() {
        return padding;
    }


    public float getMaxNumber() {
        return maxNum;
    }

    public int getBackground() {
        return background;
    }


    public String getmText() {
        return mText;
    }

    public int getProgressStrokeWidth() {
        return progressStrokeWidth;
    }

    public CharSequence[] getTikeStrArray() {
        return tikeStrArray;
    }

    public float getTikeStrSize() {
        return tikeStrSize;
    }

    public int getTikeStrColor() {
        return tikeStrColor;
    }

    public float getShuDuStrSize() {
        return shuDuStrSize;
    }

    public int getLittebackgroud() {
        return littebackgroud;
    }
}
