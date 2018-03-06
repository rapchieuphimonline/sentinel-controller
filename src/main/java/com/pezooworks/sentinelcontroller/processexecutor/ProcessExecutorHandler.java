package com.pezooworks.sentinelcontroller.processexecutor;

// interface.
public interface ProcessExecutorHandler {
    void onStandardOutput(String msg);
    void onStandardError(String msg);
    void onProcessFinished(long resultCode);
}
