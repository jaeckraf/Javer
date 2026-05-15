package ch.zhaw.it.pm4.javer.application;

import ch.zhaw.it.pm4.misc.JaverLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ManagedProcessRunner {

    private static final long STOP_TIMEOUT_SECONDS = 2;

    private final String name;
    private final OutputListener stdoutListener;
    private final OutputListener stderrListener;
    private final RunningStateListener runningStateListener;

    private volatile Process process;
    private volatile Thread workerThread;
    private volatile boolean running;
    private volatile boolean stopRequested;
    private volatile CompletableFuture<ProcessResult> currentCompletion;

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

    public synchronized Optional<CompletableFuture<ProcessResult>> start(List<String> command) {
        if (running) {
            return Optional.empty();
        }

        CompletableFuture<ProcessResult> completion = new CompletableFuture<>();
        running = true;
        stopRequested = false;
        currentCompletion = completion;
        runningStateListener.onRunningStateChanged(true);

        Thread worker = new Thread(() -> runProcess(command, completion), name.toLowerCase() + "-runner");
        workerThread = worker;
        worker.setDaemon(true);
        worker.start();
        return Optional.of(completion);
    }

    public synchronized void stop() {
        if (!running) {
            JaverLogger.warning(name + " is not running.");
            return;
        }

        requestStop();
    }

    public void stopAndWait() {
        CompletableFuture<ProcessResult> completion;
        synchronized (this) {
            if (!running) {
                return;
            }
            completion = currentCompletion;
            requestStop();
        }

        waitForCompletion(completion);
    }

    private void requestStop() {
        if (stopRequested) {
            return;
        }

        stopRequested = true;
        Process runningProcess = process;
        Thread runningWorker = workerThread;
        if (runningProcess != null) {
            runningProcess.destroy();
            forceStopIfStillRunning(runningProcess);
        } else if (runningWorker != null) {
            runningWorker.interrupt();
        }
        JaverLogger.warning(name + " process stop was requested.");
    }

    private void waitForCompletion(CompletableFuture<ProcessResult> completion) {
        if (completion == null) {
            return;
        }

        try {
            completion.get(STOP_TIMEOUT_SECONDS + 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            JaverLogger.error(name + " failed while stopping.", e);
        } catch (TimeoutException e) {
            JaverLogger.warning(name + " did not finish before shutdown continued.");
        }
    }

    private void forceStopIfStillRunning(Process runningProcess) {
        Thread forceStopper = new Thread(() -> {
            try {
                if (!runningProcess.waitFor(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS) && runningProcess.isAlive()) {
                    runningProcess.destroyForcibly();
                    JaverLogger.warning(name + " process did not stop in time and was killed.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, name.toLowerCase() + "-force-stop");

        forceStopper.setDaemon(true);
        forceStopper.start();
    }

    public synchronized boolean isRunning() {
        return running;
    }

    private void runProcess(List<String> command, CompletableFuture<ProcessResult> completion) {
        JaverLogger.info("Starting " + name + ".");

        boolean started = false;
        boolean interrupted = false;
        int exitCode = -1;
        Throwable failure = null;
        Thread stdoutThread = null;
        Thread stderrThread = null;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process startedProcess = processBuilder.start();
            started = true;

            synchronized (this) {
                process = startedProcess;
                if (stopRequested) {
                    startedProcess.destroy();
                    forceStopIfStillRunning(startedProcess);
                }
            }

            stdoutThread = pipeStream(startedProcess.getInputStream(), stdoutListener);
            stderrThread = pipeStream(startedProcess.getErrorStream(), stderrListener);

            exitCode = startedProcess.waitFor();

            joinThread(stdoutThread);
            joinThread(stderrThread);

            JaverLogger.info(name + " finished with exit code " + exitCode + ".");
        } catch (IOException e) {
            failure = e;
            JaverLogger.error("Failed to start " + name + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            interrupted = true;
            Process runningProcess = process;
            if (runningProcess != null && runningProcess.isAlive()) {
                runningProcess.destroy();
                forceStopIfStillRunning(runningProcess);
            }
            Thread.currentThread().interrupt();
            JaverLogger.error(name + " was interrupted.", e);
        } finally {
            boolean stopped;
            synchronized (this) {
                stopped = stopRequested;
                process = null;
                workerThread = null;
                running = false;
                stopRequested = false;
                currentCompletion = null;
            }

            ProcessResult result = new ProcessResult(name, started, exitCode, stopped, interrupted, failure);
            try {
                runningStateListener.onRunningStateChanged(false);
            } finally {
                completion.complete(result);
            }
        }
    }

    private void joinThread(Thread thread) throws InterruptedException {
        if (thread != null) {
            thread.join();
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

    public record ProcessResult(
            String name,
            boolean started,
            int exitCode,
            boolean stopped,
            boolean interrupted,
            Throwable failure
    ) {
        public boolean isSuccess() {
            return started && exitCode == 0 && !stopped && !interrupted && failure == null;
        }
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
