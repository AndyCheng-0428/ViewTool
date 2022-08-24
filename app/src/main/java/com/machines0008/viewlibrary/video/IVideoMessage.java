package com.machines0008.viewlibrary.video;

/**
 * Project Name: ViewLibrary
 * Created By: user
 * Created On: 2022/8/23
 * Usage: 本介面用於VideoView，將影片播放過程中可能傳達之訊息，遞交給外部資源進行處理
 * ex.
 **/
public interface IVideoMessage {

    boolean isPlaying(); //是否正在播放

    boolean isPause(); //是否暫停

    int progress(); //目前進度

    int duration(); //總進度
}
