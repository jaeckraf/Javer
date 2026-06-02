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

/**
 * Starts and supervises one external process while asynchronously forwarding
 * stdout, stderr, and running-state changes to callers.
 */
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

    /**
     * Creates a runner for a named process.
     *
     * @param name                 process name used in log messages and worker thread names
     * @param stdoutListener       receives decoded stdout lines
     * @param stderrListener       receives decoded stderr lines
     * @param runningStateListener receives process running-state changes
     */
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

    /**
     * Starts the process command on a background thread.
     *
     * @param command complete command line passed to {@link ProcessBuilder}
     * @return completion future for the process result, or empty if already
     * running
     */
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

    /**
     * Requests a graceful stop and returns immediately.
     */
    public synchronized void stop() {
        if (!running) {
            JaverLogger.warning(name + " is not running.");
            return;
        }

        requestStop();
    }

    /**
     * Requests process termination and waits for the process result up to the
     * configured shutdown timeout.
     */
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

    /**
     * Reports whether a process run is currently active.
     *
     * @return true while a process is being started, running, or shutting down
     */
    public synchronized boolean isRunning() {
        return running;
    }

    private void runProcess(List<String> command, CompletableFuture<ProcessResult> completion) {
        JaverLogger.info("Starting " + name + ".");

        boolean started = false;
        boolean interrupted = false;
        int exitCode = -1;
        Throwable failure = null;
        Thread stdoutThread;
        Thread stderrThread;

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

    /**
     * Receives process output decoded as UTF-8 text.
     */
    @FunctionalInterface
    public interface OutputListener {
        /**
         * Handles one chunk of output text.
         *
         * @param text output text including a trailing line separator
         */
        void onOutput(String text);
    }

    /**
     * Receives state changes whenever the managed process starts or stops.
     */
    @FunctionalInterface
    public interface RunningStateListener {
        /**
         * Handles the new running state.
         *
         * @param running true while the process is running
         */
        @SuppressWarnings("unused")
        void onRunningStateChanged(boolean running);
    }

    /**
     * Immutable summary of one process run.
     *
     * @param name        process name
     * @param started     whether the process was started successfully
     * @param exitCode    process exit code, or -1 if unavailable
     * @param stopped     whether the run ended after an explicit stop request
     * @param interrupted whether the runner thread was interrupted
     * @param failure     startup or supervision failure, if any
     */
    public record ProcessResult(
            String name,
            boolean started,
            int exitCode,
            boolean stopped,
            boolean interrupted,
            Throwable failure
    ) {
        /**
         * Reports whether the process completed normally.
         *
         * @return true if the process started, exited with code 0, and did not
         * fail or stop early
         */
        public boolean isSuccess() {
            return started && exitCode == 0 && !stopped && !interrupted && failure == null;
        }
    }
}
