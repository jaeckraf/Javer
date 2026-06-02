package ch.zhaw.it.pm4.javer.application;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ManagedProcessRunnerTest {

    @Test
    void shouldStartAndCompleteSuccessfully() throws ExecutionException, InterruptedException, TimeoutException {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        AtomicBoolean isRunningState = new AtomicBoolean(false);

        ManagedProcessRunner runner = new ManagedProcessRunner(
                "TestProcess",
                stdout::append,
                stderr::append,
                isRunningState::set
        );

        List<String> command;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = List.of("cmd.exe", "/c", "echo Hello World");
        } else {
            command = List.of("echo", "Hello World");
        }

        var completionOpt = runner.start(command);
        assertTrue(completionOpt.isPresent(), "Process should have started");

        CompletableFuture<ManagedProcessRunner.ProcessResult> completion = completionOpt.get();
        ManagedProcessRunner.ProcessResult result = completion.get(5, TimeUnit.SECONDS);

        assertTrue(result.isSuccess(), "Process should complete successfully");
        assertEquals(0, result.exitCode());
        assertTrue(stdout.toString().contains("Hello World"));
        assertEquals("", stderr.toString());
        assertFalse(isRunningState.get(), "Process should not be running after completion");
    }

    @Test
    void shouldReportFailureForInvalidCommand() throws ExecutionException, InterruptedException, TimeoutException {
        ManagedProcessRunner runner = new ManagedProcessRunner(
                "TestProcess",
                System.out::println,
                System.err::println,
                state -> {
                }
        );

        List<String> command = List.of("nonexistent_command_12345");

        var completionOpt = runner.start(command);
        assertTrue(completionOpt.isPresent());

        CompletableFuture<ManagedProcessRunner.ProcessResult> completion = completionOpt.get();
        ManagedProcessRunner.ProcessResult result = completion.get(5, TimeUnit.SECONDS);

        assertFalse(result.isSuccess(), "Process should not be successful");
        assertFalse(result.started(), "Process should not have started");
        assertNotNull(result.failure(), "Process should have a failure exception");
    }

    @Test
    void shouldStopRunningProcess() throws InterruptedException, ExecutionException, TimeoutException {
        ManagedProcessRunner runner = new ManagedProcessRunner(
                "TestProcess",
                System.out::println,
                System.err::println,
                state -> {
                }
        );

        List<String> command;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = List.of("ping", "-n", "10", "127.0.0.1");
        } else {
            command = List.of("sleep", "10");
        }

        var completionOpt = runner.start(command);
        assertTrue(completionOpt.isPresent());

        long deadline = System.currentTimeMillis() + 500;

        while (!runner.isRunning() && System.currentTimeMillis() < deadline) {
            Thread.onSpinWait();
        }
        assertTrue(runner.isRunning());

        runner.stopAndWait();

        CompletableFuture<ManagedProcessRunner.ProcessResult> completion = completionOpt.get();
        ManagedProcessRunner.ProcessResult result = completion.get(5, TimeUnit.SECONDS);

        assertFalse(result.isSuccess(), "Process should not be successful since it was stopped");
        assertTrue(result.stopped(), "Process should be marked as stopped");
        assertFalse(runner.isRunning(), "Process should not be running after stop");
    }

    @Test
    void shouldHandleNonZeroExitCode() throws ExecutionException, InterruptedException, TimeoutException {
        ManagedProcessRunner runner = new ManagedProcessRunner(
                "TestProcess",
                s -> {
                },
                s -> {
                },
                state -> {
                }
        );

        List<String> command;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = List.of("cmd.exe", "/c", "exit 42");
        } else {
            command = List.of("sh", "-c", "exit 42");
        }

        var completionOpt = runner.start(command);
        assertTrue(completionOpt.isPresent());

        CompletableFuture<ManagedProcessRunner.ProcessResult> completion = completionOpt.get();
        ManagedProcessRunner.ProcessResult result = completion.get(5, TimeUnit.SECONDS);

        assertFalse(result.isSuccess(), "Process should not be successful");
        assertEquals(42, result.exitCode(), "Exit code should be captured");
    }

    @Test
    void shouldCaptureStderr() throws ExecutionException, InterruptedException, TimeoutException {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        ManagedProcessRunner runner = new ManagedProcessRunner(
                "TestProcess",
                stdout::append,
                stderr::append,
                state -> {
                }
        );

        List<String> command;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = List.of("cmd.exe", "/c", "echo Error Message >&2");
        } else {
            command = List.of("sh", "-c", "echo Error Message >&2");
        }

        var completionOpt = runner.start(command);
        assertTrue(completionOpt.isPresent());

        CompletableFuture<ManagedProcessRunner.ProcessResult> completion = completionOpt.get();
        completion.get(5, TimeUnit.SECONDS);

        assertTrue(stderr.toString().contains("Error Message"));
        assertTrue(stdout.toString().isEmpty());
    }
}
