package com.machines0008.viewlibrary.video;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

import lombok.Getter;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/5
 * Usage: 媒體控制列
 * 本類別 採用IMediaBarController告知影片播放器，媒體控制列所操作之行為
 * 採用MediaCallback獲得影片播放器播放過程之訊息　包括影片播放器準備／開始播放／暫停播放／更新畫面幀等（不一定在主執行緒）
 **/
public class MediaControlBar extends LinearLayout {
    private SeekBar sbMediaProgress;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnVoice;
    private SeekBar sbVoice;
    private TextView tvMediaTime;
    private LinearLayout linearLayout;
    private final Handler mainHandler;
    @Getter
    private final MediaCallback callback = new MediaCallback() {
        @Override
        public void callback(IVideoMessage message, int what) {
            switch (what) {
                case VideoConstant.VIDEO_PREPARED:
                case VideoConstant.VIDEO_FRAME_UPDATE:
                    final int current = message.progress();
                    final int duration = message.duration();
                    final int currentMinute = current / 1000 / 60;
                    final int currentSecond = current / 1000 % 60;
                    final int totalMinute = duration / 1000 / 60;
                    final int totalSecond = duration / 1000 % 60;
                    mainHandler.post(() -> {
                        if (sbMediaProgress != null) {
                            sbMediaProgress.setMax(duration);
                            sbMediaProgress.setProgress(current);
                        }
                        if (tvMediaTime != null) {
                            tvMediaTime.setText(String.format(Locale.TAIWAN, "%02d:%02d/%02d:%02d", currentMinute, currentSecond, totalMinute, totalSecond));
                        }
                    });
                    break;
                case VideoConstant.VIDEO_PLAY:
                    mainHandler.post(() -> btnPlay.setImageResource(android.R.drawable.ic_media_pause));
                    break;
                case VideoConstant.VIDEO_PAUSE:
                    mainHandler.post(() -> btnPlay.setImageResource(android.R.drawable.ic_media_play));
                    break;
            }
        }
    };

    public MediaControlBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mainHandler = new Handler(Looper.getMainLooper());
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
        sbVoice.setMax(100);
        btnPlay.setImageResource(android.R.drawable.ic_media_play);
        btnNext.setImageResource(android.R.drawable.ic_media_next);
        btnVoice.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
        tvMediaTime.setText("00:00/00:00");
    }

    public void setController(IMediaController controller) {
        btnPlay.setOnClickListener(v -> controller.clickPlay());
        sbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                controller.setVolume(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbMediaProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                controller.seekToPosition(seekBar.getProgress());
            }
        });
    }
}
