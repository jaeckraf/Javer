package ch.zhaw.it.pm4.javer.application;

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
    private final StatusListener statusListener;
    private final RunningStateListener runningStateListener;

    private volatile Process process;

    public ManagedProcessRunner(
            String name,
            OutputListener stdoutListener,
            OutputListener stderrListener,
            StatusListener statusListener,
            RunningStateListener runningStateListener
    ) {
        this.name = name;
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.statusListener = statusListener;
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
            statusListener.onStatus(name + " is not running.\n");
            return;
        }

        process.destroyForcibly();
        statusListener.onStatus(name + " process was killed.\n");
    }

    public synchronized boolean isRunning() {
        return process != null && process.isAlive();
    }

    private void runProcess(List<String> command) {
        runningStateListener.onRunningStateChanged(true);
        statusListener.onStatus("Starting " + name + "...\n");

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

            statusListener.onStatus(name + " finished with exit code " + exitCode + ".\n");
        } catch (IOException e) {
            statusListener.onStatus("Failed to start " + name + ": " + e.getMessage() + "\n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            statusListener.onStatus(name + " was interrupted.\n");
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
                statusListener.onStatus("Stream read error in " + name + ": " + e.getMessage() + "\n");
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
    public interface StatusListener {
        void onStatus(String text);
    }

    @FunctionalInterface
    public interface RunningStateListener {
        void onRunningStateChanged(boolean running);
    }
}