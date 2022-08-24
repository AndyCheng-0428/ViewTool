package com.machines0008.viewlibrary.video;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/23
 * Usage:
 **/
public interface MediaCallback {
    void callback(IVideoMessage message, @VideoConstant.VideoMessage int what);
}
