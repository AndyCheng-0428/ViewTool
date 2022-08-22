package com.machines0008.viewlibrary.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
        VideoView videoView = new VideoView(context, attrs);
        LayoutParams videoParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoParams.bottomToBottom = LayoutParams.PARENT_ID;
        videoParams.topToTop = LayoutParams.PARENT_ID;
        videoParams.startToStart = LayoutParams.PARENT_ID;
        videoParams.endToEnd = LayoutParams.PARENT_ID;
        videoView.setLayoutParams(videoParams);
        MediaControlBar mediaControlBar = new MediaControlBar(context, attrs);
        ConstraintLayout.LayoutParams mediaControlBarParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mediaControlBarParams.bottomToBottom = ConstraintSet.PARENT_ID;
        mediaControlBarParams.startToStart = ConstraintSet.PARENT_ID;
        mediaControlBarParams.endToEnd = ConstraintSet.PARENT_ID;
        mediaControlBar.setLayoutParams(mediaControlBarParams);
        addView(videoView);
        addView(mediaControlBar);
    }
}
