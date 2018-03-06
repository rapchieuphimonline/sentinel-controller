package com.pezooworks.sentinelcontroller.processexecutor;

import org.apache.commons.exec.*;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ProcessExecutor {
    public static final Long WATCHDOG_EXIST_VALUE = -999L;

    public static Future<Long> runProcess(final CommandLine commandline, final ProcessExecutorHandler handler, final long watchdogTimeout) throws IOException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Long> result = executor.submit(new ProcessCallable(watchdogTimeout, handler, commandline));
        executor.shutdown();
        return result;
    }

    private static class ProcessCallable implements Callable<Long> {
        private long watchdogTimeout;
        private ProcessExecutorHandler handler;
        private CommandLine commandline;

        private ProcessCallable(long watchdogTimeout, ProcessExecutorHandler handler, CommandLine commandline) {
            this.watchdogTimeout = watchdogTimeout;
            this.handler = handler;
            this.commandline = commandline;
        }

        @Override
        public Long call() throws Exception {
            Executor executor = new DefaultExecutor();
            executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
            ExecuteWatchdog watchDog = new ExecuteWatchdog(watchdogTimeout);
            executor.setWatchdog(watchDog);
            executor.setStreamHandler(new PumpStreamHandler(new MyLogOutputStream(handler, true), new MyLogOutputStream(handler, false)));
            Long exitValue;
            try {
                exitValue = new Long(executor.execute(commandline));
            } catch (ExecuteException e) {
                exitValue = new Long(e.getExitValue());
            }
            if (watchDog.killedProcess()) {
                exitValue = WATCHDOG_EXIST_VALUE;
            }

            handler.onProcessFinished(exitValue);
            return exitValue;
        }
    }

    private static class MyLogOutputStream extends LogOutputStream {
        private ProcessExecutorHandler handler;
        private boolean forewordToStandardOutput;

        private MyLogOutputStream(ProcessExecutorHandler handler, boolean forewordToStandardOutput) {
            this.handler = handler;
            this.forewordToStandardOutput = forewordToStandardOutput;
        }

        @Override
        protected void processLine(String line, int level) {
            if (forewordToStandardOutput) {
                handler.onStandardOutput(line);
            } else {
                handler.onStandardError(line);
            }
        }
    }
}
