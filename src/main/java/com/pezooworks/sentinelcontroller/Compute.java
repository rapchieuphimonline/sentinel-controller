package com.pezooworks.sentinelcontroller;

import com.google.common.base.Strings;
import com.pezooworks.framework.common.Misc;
import com.pezooworks.framework.log.LogHelper;
import com.pezooworks.sentinelcontroller.processexecutor.ProcessExecutor;
import com.pezooworks.sentinelcontroller.shared.Cookies;
import com.pezooworks.sentinelcontroller.shared.HumanProfile;
import com.pezooworks.sentinelcontroller.shared.Target;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.pezooworks.sentinelcontroller.App.SENTINEL_FARM_ENDPOINT;

/**
 * Created by Administrator on 16/12/2017.
 */
public enum Compute {
    INSTANCE;

    private final String[] LINUX_SENTINEL_EXECUTE_SCRIPTS = new String[] {
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1024_768.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1280_800.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1360_768.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1366_768.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1440_900.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1600_900.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1680_1050.sh",
            "/home/thinhnn3/sentinel/sentinel_controller/run_sentinel_1920_1080.sh",
    };

    private final String WINDOWS_SENTINEL_EXECUTE_SCRIPT = ".\\tools\\Sentinel\\run.bat";

    private HumanProfile humanProfile;
    private SentinelProcessExecutorHandler currentSentinel;

    public void start() {
        /* notify service has started and start working */
        notifyStarted();

        /* request task */
        humanProfile = requestTask();
        if (humanProfile == null) {
            LogHelper.Log("Compute.. can not get human profile. System now exit!");
            App.exit();
        }
        startScheduleTask();
    }

    private HumanProfile requestTask() {
        try {
            StringBuilder url = new StringBuilder();
            url.append(SENTINEL_FARM_ENDPOINT);
            url.append("/request");
            url.append("?action=").append("sentinel-request-task");
            url.append("&sentinel-name=").append(App.machineName);
            LogHelper.Log("requestTask.. url := " + url.toString());
            Map<String, String> httpResponse = Misc.simpleGetHttp(url.toString(), null, null, 3, true, false);
            if (httpResponse.containsKey("response")) {
                String response = httpResponse.get("response");
                response = URLDecoder.decode(response, "UTF-8");
                HumanProfile humanProfile = Misc.gson.fromJson(response, HumanProfile.class);
                LogHelper.Log("Received human profile := " + Misc.gson.toJson(humanProfile));
                if (humanProfile != null) {
                    return humanProfile;
                }
            }
        } catch (Exception e) {
            LogHelper.LogException("requestTask", e);
        }
        return null;
    }

    private void startScheduleTask() {
        LogHelper.Log("startScheduleTask.. ");
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    /* send heartbeart*/
                    notifyHeartbeat();

                    /* launch next task */
                    launchSentinel();
                } catch (Exception e) {
                    LogHelper.LogException("scheduledTask", e);
                }
            }
        }, 2, 5, TimeUnit.SECONDS);
    }

    private void launchSentinel() {
        LogHelper.Log("launchSentinel.. ");
        try {
            if (currentSentinel == null) {
                Target target = null;
                boolean isFirstTarget = false;
                for (int i = 0; i < humanProfile.getTargets().size(); i++) {
                    if (!humanProfile.getTargets().get(i).isFinished()) {
                        target = humanProfile.getTargets().get(i);
                        isFirstTarget = i == 0;
                        break;
                    }
                }

                LogHelper.Log("launchSentinel.. new target = " + Misc.gson.toJson(target));
                if (target == null) {
                    /* all targets are executed => finalize and exit */
                    notifyFinished();
                    App.exit();
                } else {
                    /* write config to file */
                    HumanProfile config = Misc.gson.fromJson(Misc.gson.toJson(humanProfile), HumanProfile.class);
                    config.getTargets().clear();
                    config.getTargets().add(target);
                    if (!isFirstTarget) {
                        config.getDirectAdsList().clear();
                        config.getShortenAdsList().clear();
                        config.getShortenAdsSkipIdList().clear();
                    }
                    FileUtils.writeStringToFile(new File("tools/Sentinel/default_config.cfg"), Misc.gson.toJson(config), "UTF-8");
                    LogHelper.Log("launchSentinel.. write config to file OK!");

                    currentSentinel = new SentinelProcessExecutorHandler("sentinel", target);

                    String script = null;
                    LogHelper.Log("os name := " + System.getProperty("os.name"));
                    if (System.getProperty("os.name").contains("Windows")) {
                        script = WINDOWS_SENTINEL_EXECUTE_SCRIPT;
                    } else {
                        script = LINUX_SENTINEL_EXECUTE_SCRIPTS[Misc.RANDOM_RANGE(0, LINUX_SENTINEL_EXECUTE_SCRIPTS.length - 1)];
                    }

                    CommandLine commandLine = new CommandLine(script);
                    ProcessExecutor.runProcess(commandLine, currentSentinel, 60000);
                }
            } else {
                LogHelper.Log("launchSentinel.. sentinel is running! Aborted");
            }
        } catch (Exception e) {
            LogHelper.LogException("launchSentinel", e);
        }
    }

    public void onProcessExecutorFinished(Target target, List<String> outputMessages) {
        try {
            LogHelper.Log("onProcessExecutorFinished.. target id := " + target.getId());
            for (Target temp : humanProfile.getTargets()) {
                LogHelper.Log("onProcessExecutorFinished... temp id := " + temp.getId());
                if (temp.getId().equalsIgnoreCase(target.getId())) {
                    temp.setFinished(true);
                    LogHelper.Log("onProcessExecutorFinished... set finished to target id := " + temp.getId());
//                    break;
                }
            }

            /* read output message, get cookies and notify to sentinel farm */
            try {
                List<String> lines = FileUtils.readLines(new File(target.getId() + "_" + "cookies"), "UTF-8");
                List<Cookies> cookiesList = new ArrayList<>();

                if (lines != null) {
                    for (String line : lines) {
                        if (!Strings.isNullOrEmpty(line)) {
                            Cookies cookies = Misc.gson.fromJson(line, Cookies.class);
                            cookiesList.add(cookies);
                        }
                    }
                }
                LogHelper.Log("target id := " + target.getId() + ", cookies := " + Misc.gson.toJson(cookiesList));

                StringBuilder url = new StringBuilder();
                url.append(SENTINEL_FARM_ENDPOINT);
                url.append("/request");
                url.append("?action=").append("sentinel-notify-cookies");
                url.append("&sentinel-name=").append(App.machineName);
                url.append("&target-id=").append(target.getId());
                url.append("&cookies=").append(URLEncoder.encode(Misc.gson.toJson(cookiesList), "UTF-8"));
                LogHelper.Log("onProcessExecutorFinished.. url := " + url.toString());
                Misc.simpleGetHttp(url.toString(), null, null, 3, true, false);
            } catch (IOException e) {
                LogHelper.LogException("onProcessExecutorFinished", e);
            }

            /* read report and send to sentinel farm */
            try {
                List<String> lines = FileUtils.readLines(new File("report"), "UTF-8");
                if (lines != null && lines.size() > 0) {
                    StringBuilder url = new StringBuilder();
                    url.append(SENTINEL_FARM_ENDPOINT);
                    url.append("/request");
                    url.append("?action=").append("sentinel-report");
                    url.append("&sentinel-name=").append(App.machineName);
                    url.append("&report=").append(URLEncoder.encode(lines.get(0), "UTF-8"));
                    LogHelper.Log("onProcessExecutorFinished.. url := " + url.toString());
                    Misc.simpleGetHttp(url.toString(), null, null, 3, true, false);
                }
            } catch (IOException e) {
                LogHelper.LogException("onProcessExecutorFinished", e);
            }

            currentSentinel = null;
        } catch (Exception e) {
            LogHelper.LogException("onProcessExecutorFinished", e);
        }
    }

    private void notifyHeartbeat() {
        StringBuilder url = new StringBuilder();
        url.append(SENTINEL_FARM_ENDPOINT);
        url.append("/request");
        url.append("?action=").append("sentinel-heartbeat");
        url.append("&sentinel-name=").append(App.machineName);
        LogHelper.Log("notifyHeartbeat... url := " + url.toString());
        Misc.simpleGetHttp(url.toString(), null, null, 3, true, false);
    }

    private void notifyStarted() {
        StringBuilder url = new StringBuilder();
        url.append(SENTINEL_FARM_ENDPOINT);
        url.append("/request");
        url.append("?action=").append("sentinel-service-start");
        url.append("&sentinel-name=").append(App.machineName);
        LogHelper.Log("notifyStarted... url := " + url.toString());
        Misc.simpleGetHttp(url.toString(), null, null, 3, true, false);
    }

    private void notifyFinished() {
        StringBuilder url = new StringBuilder();
        url.append(SENTINEL_FARM_ENDPOINT);
        url.append("/request");
        url.append("?action=").append("sentinel-finished");
        url.append("&sentinel-name=").append(App.machineName);
        LogHelper.Log("notifyFinished... url := " + url.toString());
        Misc.simpleGetHttp(url.toString(), null, null, 3, true, false);
    }
}
