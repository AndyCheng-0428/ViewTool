package com.machines0008.viewlibrary.video;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import lombok.Setter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/5
 * Usage:
 **/
public class MediaControlBar extends LinearLayout {
    private SeekBar sbMediaProgress;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnVoice;
    private SeekBar sbVoice;
    private TextView tvMediaTime;
    private LinearLayout linearLayout;


    public MediaControlBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        initChild(context, attrs);
        fillView();
    }

    private void fillView() {
        addView(sbMediaProgress);

        linearLayout.addView(btnPlay);
        linearLayout.addView(btnNext);
        linearLayout.addView(btnVoice);
        linearLayout.addView(sbVoice);
        linearLayout.addView(tvMediaTime);
        addView(linearLayout);
    }

    private void initChild(Context context, @Nullable AttributeSet attrs) {
        sbMediaProgress = new SeekBar(context, attrs);
        linearLayout = new LinearLayout(context, attrs);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnPlay = new ImageView(context, attrs);
        btnPlay.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnNext = new ImageView(context, attrs);
        btnNext.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnVoice = new ImageView(context, attrs);
        btnVoice.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        sbVoice = new SeekBar(context, attrs);
        LayoutParams lpVoice = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpVoice.gravity = Gravity.CENTER_VERTICAL;
        lpVoice.width = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getContext().getResources().getDisplayMetrics()));
        sbVoice.setLayoutParams(lpVoice);
        tvMediaTime = new TextView(context, attrs);
        LayoutParams lpMediaTime = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpMediaTime.gravity = Gravity.CENTER_VERTICAL;
        tvMediaTime.setLayoutParams(lpMediaTime);
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
        btnNext.setImageResource(android.R.drawable.ic_media_next);
        btnVoice.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        tvMediaTime.setText("0:00/0:00");
    }
}
