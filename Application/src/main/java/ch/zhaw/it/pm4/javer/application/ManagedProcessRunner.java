package ch.zhaw.it.pm4.javer.application;

import ch.zhaw.it.pm4.misc.JaverLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ManagedProcessRunner {

    private final String name;
    private final OutputListener stdoutListener;
    private final OutputListener stderrListener;
    private final RunningStateListener runningStateListener;

    private volatile Process process;

    public ManagedProcessRunner(
            String name,
            OutputListener stdoutListener,
            OutputListener stderrListener,
            RunningStateListener runningStateListener
    ) {
        this.name = name;
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.runningStateListener = runningStateListener;
    }

    public synchronized boolean start(List<String> command) {
        if (isRunning()) {
            return false;
        }

        Thread worker = new Thread(() -> runProcess(command), name.toLowerCase() + "-runner");
        worker.setDaemon(true);
        worker.start();
        return true;
    }

    public synchronized void stop() {
        if (!isRunning()) {
            JaverLogger.warning(name + " is not running.");
            return;
        }

        process.destroyForcibly();
        JaverLogger.warning(name + " process was killed.");
    }

    public synchronized boolean isRunning() {
        return process != null && process.isAlive();
    }

    private void runProcess(List<String> command) {
        runningStateListener.onRunningStateChanged(true);
        JaverLogger.info("Starting " + name + ".");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process startedProcess = processBuilder.start();

            synchronized (this) {
                process = startedProcess;
            }

            Thread stdoutThread = pipeStream(startedProcess.getInputStream(), stdoutListener);
            Thread stderrThread = pipeStream(startedProcess.getErrorStream(), stderrListener);

            int exitCode = startedProcess.waitFor();

            stdoutThread.join();
            stderrThread.join();

            JaverLogger.info(name + " finished with exit code " + exitCode + ".");
        } catch (IOException e) {
            JaverLogger.error("Failed to start " + name + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            JaverLogger.error(name + " was interrupted.", e);
        } finally {
            synchronized (this) {
                process = null;
            }
            runningStateListener.onRunningStateChanged(false);
        }
    }

    private Thread pipeStream(InputStream stream, OutputListener listener) {
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    listener.onOutput(line + System.lineSeparator());
                }
            } catch (IOException e) {
                JaverLogger.error("Stream read error in " + name + ": " + e.getMessage(), e);
            }
        }, name.toLowerCase() + "-stream");

        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    @FunctionalInterface
    public interface OutputListener {
        void onOutput(String text);
    }

    @FunctionalInterface
    public interface RunningStateListener {
        void onRunningStateChanged(boolean running);
    }
}