package com.machines0008.viewlibrary.video;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/24
 * Usage:
 **/
public interface VideoConstant {
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @IntDef({VIDEO_PREPARED, VIDEO_FRAME_UPDATE, VIDEO_PAUSE, VIDEO_PLAY})
    @interface VideoMessage {

    }
    int VIDEO_PREPARED = 0x00000;
    int VIDEO_FRAME_UPDATE = 0x00001;
    int VIDEO_PAUSE = 0x00002;
    int VIDEO_PLAY = 0x00003;
}
