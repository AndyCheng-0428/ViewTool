package com.machines0008.viewlibrary.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/5
 * Usage:
 **/
public class VideoLayout extends ConstraintLayout {
    private IMediaController controller;
    private VideoView videoView;
    private MediaControlBar controlBar;

    public VideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams guildParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        guildParams.orientation = LayoutParams.HORIZONTAL;
        settingVideoView(context, attrs);
        settingControlBar(context, attrs);
        settingControllerBarCommand();
    }

    private void settingControllerBarCommand() {
        videoView.setCallback(controlBar.getCallback());
    }

    private void settingControlBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        controlBar = new MediaControlBar(context, attrs);
        LayoutParams mediaControlBarParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mediaControlBarParams.bottomToBottom = ConstraintSet.PARENT_ID;
        mediaControlBarParams.startToStart = ConstraintSet.PARENT_ID;
        mediaControlBarParams.endToEnd = ConstraintSet.PARENT_ID;
        controlBar.setLayoutParams(mediaControlBarParams);
        controller = new IMediaController() {
            @Override
            public void clickPlay() {
                videoView.switchPlay();
            }

            @Override
            public void setVolume(float volume) {
                videoView.setVolume(volume);
            }

            @Override
            public void seekToPosition(int position) {
                videoView.seekTo(position);
            }
        };
        controlBar.setController(controller);
        addView(controlBar);
    }

    private void settingVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        videoView = new VideoView(context, attrs);
        LayoutParams videoParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoParams.bottomToBottom = LayoutParams.PARENT_ID;
        videoParams.topToTop = LayoutParams.PARENT_ID;
        videoParams.startToStart = LayoutParams.PARENT_ID;
        videoParams.endToEnd = LayoutParams.PARENT_ID;
        videoView.setLayoutParams(videoParams);
        addView(videoView);
    }
}
