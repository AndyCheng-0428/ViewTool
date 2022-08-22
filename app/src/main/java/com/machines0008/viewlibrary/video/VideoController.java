package com.machines0008.viewlibrary.video;

import android.view.View;
import android.widget.SeekBar;

import lombok.Data;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/22
 * Usage:
 **/
public class VideoController {

    @Data
    public static class Params {
        private View.OnClickListener playBtnListener;
        private View.OnClickListener nextBtnListener;
        private View.OnClickListener voiceBtnListener;
        private SeekBar.OnSeekBarChangeListener progressChangeListener;
        private SeekBar.OnSeekBarChangeListener voiceChangeListener;
    }

}
