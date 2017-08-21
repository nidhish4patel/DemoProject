package com.nidhi.demoproject.callbacks;

public interface IDownloadCallback {
    void onStateChange(int what, int arg1, int arg2, Object obj, int requestId);
}
