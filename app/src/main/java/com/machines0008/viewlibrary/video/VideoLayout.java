package com.machines0008.viewlibrary.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/5
 * Usage:
 **/
public class VideoLayout extends ConstraintLayout {


    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Guideline gl90 = new Guideline(context, attrs);
        LayoutParams guildParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        guildParams.orientation = LayoutParams.HORIZONTAL;
        gl90.setLayoutParams(guildParams);
        gl90.setGuidelinePercent(0.90f);


        VideoView videoView = new VideoView(context, attrs);
        LayoutParams videoParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoParams.bottomToBottom = LayoutParams.PARENT_ID;
        videoParams.topToTop = LayoutParams.PARENT_ID;
        videoParams.startToStart = LayoutParams.PARENT_ID;
        videoParams.endToEnd = LayoutParams.PARENT_ID;
        videoView.setLayoutParams(videoParams);
        MediaControlBar mediaControlBar = new MediaControlBar(context, attrs);
        LayoutParams mediaControlBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        mediaControlBarParams.topToTop = gl90.getId();
        mediaControlBarParams.bottomToBottom = LayoutParams.PARENT_ID;
        mediaControlBarParams.startToStart = LayoutParams.PARENT_ID;
        mediaControlBarParams.endToEnd = LayoutParams.PARENT_ID;
        mediaControlBar.setLayoutParams(mediaControlBarParams);
        addView(gl90);
        addView(videoView);
        addView(mediaControlBar);
    }
}
