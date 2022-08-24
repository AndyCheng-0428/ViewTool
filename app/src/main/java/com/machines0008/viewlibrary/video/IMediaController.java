package com.machines0008.viewlibrary.video;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/23
 * Usage:
 **/
public interface IMediaController {
    void clickPlay(); //開始播放

    void setVolume(float volume);

    void seekToPosition(int position);
}