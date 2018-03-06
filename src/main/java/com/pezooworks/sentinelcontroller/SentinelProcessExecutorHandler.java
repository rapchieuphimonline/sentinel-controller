package com.pezooworks.sentinelcontroller;

import com.pezooworks.framework.common.Misc;
import com.pezooworks.framework.log.LogHelper;
import com.pezooworks.sentinelcontroller.processexecutor.ProcessExecutorHandler;
import com.pezooworks.sentinelcontroller.shared.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by LAP11313-local on 11/13/2017.
 */
public class SentinelProcessExecutorHandler implements ProcessExecutorHandler {
    private boolean isFinished;
    private String processName;
    private long resultCode, startTime, endTime;
    private List<String> outputMessages = new ArrayList<>();
    private transient Target target;

    public SentinelProcessExecutorHandler(String processName, Target target) {
        this.processName = processName;
        this.target = target;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void onStandardOutput(String msg) {
        LogHelper.Log("[" + processName + "] " + msg);
        outputMessages.add(msg);
    }

    @Override
    public void onStandardError(String msg) {
        onStandardOutput(msg);
    }

    @Override
    public void onProcessFinished(long resultCode) {
        this.isFinished = true;
        this.resultCode = resultCode;
        this.endTime = System.currentTimeMillis();
        LogHelper.Log("SentinelProcessExecutorHandler... " + processName);
        LogHelper.Log("onProcessFinished... resultCode := " + resultCode);
        LogHelper.Log("onProcessFinished... startTime := " + getStartTimePretty());
        LogHelper.Log("onProcessFinished... endTime := " + getEndTimePretty());
        LogHelper.Log("onProcessFinished... elapsed := " + getElapsedTimePretty());
        Compute.INSTANCE.onProcessExecutorFinished(target, outputMessages);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public long getResultCode() {
        return resultCode;
    }

    public void setResultCode(long resultCode) {
        this.resultCode = resultCode;
    }

    public String getStartTimePretty() {
        return Misc.dateToString(new Date(startTime), "yyyy-MM-dd HH:mm:ss");
    }

    public String getEndTimePretty() {
        if (isFinished) {
            return Misc.dateToString(new Date(endTime), "yyyy-MM-dd HH:mm:ss");
        }
        return "n/a";
    }

    public String getElapsedTimePretty() {
        if (isFinished) {
            return Misc.secondsPrettyConvert((endTime - startTime)/1000);
        }
        return Misc.secondsPrettyConvert((System.currentTimeMillis() - startTime)/1000);
    }
}
