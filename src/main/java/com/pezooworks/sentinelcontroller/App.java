package com.pezooworks.sentinelcontroller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.pezooworks.framework.log.LogHelper;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Administrator on 16/12/2017.
 */
public class App {
    public static String machineName;
    public static String SENTINEL_FARM_ENDPOINT;
    public static String APP_CONFIG_PATH = "app_config.cfg";

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("### user.dir: " + System.getProperty("user.dir"));
            System.out.println("### java.library.path: " + System.getProperty("java.library.path"));
            System.out.println("### java.ext.dirs: " + System.getProperty("java.ext.dirs"));
            machineName = InetAddress.getLocalHost().getHostName();
            if (machineName.contains("v")) {
                machineName = machineName.replace("v", "");
            }

            /* load log config */
            LogHelper.setUseDefaultLog(false);
            LogHelper.setUseConsoleOutput(false);
            LogHelper.LoadConfig("log4j.properties");

            /* start schedule tasks */
            startScheduleTask();

            /* read input arguments */
            readArguments(args);

            /* read app config */
            readConfig();

            /* start application logic */
            Compute.INSTANCE.start();
        } catch (Exception e) {
            LogHelper.LogException("Exception in start server. System exit", e);
            System.exit(1);
        }
    }

    private static void startScheduleTask() {
    }

    private static void readArguments(String[] args) {
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-config": {
                        APP_CONFIG_PATH = args[i + 1];
                        break;
                    }
                }
            }
        }
        LogHelper.Log("readArguments APP_CONFIG_PATH := " + APP_CONFIG_PATH);
    }

    private static void readConfig() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(APP_CONFIG_PATH), Charsets.UTF_8);
            for (String line : lines) {
                if (Strings.isNullOrEmpty(line) || line.startsWith("#")) {
                    continue;
                }
                String key = line.split("=")[0];
                String value = line.split("=")[1];
                switch (key) {
                    case "SENTINEL_FARM_ENDPOINT":
                        SENTINEL_FARM_ENDPOINT = value;
                        break;
                }
            }
            LogHelper.Log("readConfig.. SENTINEL_FARM_ENDPOINT := " + SENTINEL_FARM_ENDPOINT);
        } catch (Exception e) {
            LogHelper.LogException("readConfig", e);
        }
    }

    public static void exit() {
        LogHelper.Log("application exit");
        System.exit(0);
    }
}
