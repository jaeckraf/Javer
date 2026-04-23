package ch.zhaw.it.pm4.javer.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class E2EApplication {

    private static final Path RELEASE_COMPILER_EXE =
            Path.of("Compiler", "target", "installer", "compiler", "javer-compiler", "javer-compiler.exe");

    private static final Path RELEASE_VM_EXE =
            Path.of("VM", "target", "installer", "javer-vm", "javer-vm.exe");

    public static void main(String[] args) {
        try {
            Path inputFile = copyResourceToTemp("input.javer");
            Path expectedOutputFile = copyResourceToTemp("expected-compiled.jbc");
            Path compiledFile = Files.createTempFile("compiled-", ".jbc");

            Path compilerExe = resolveExecutable(RELEASE_COMPILER_EXE, "compiler");
            Path vmExe = resolveExecutable(RELEASE_VM_EXE, "vm");

            System.out.println("Input file:      " + inputFile);
            System.out.println("Expected output: " + expectedOutputFile);
            System.out.println("Compiled output: " + compiledFile);
            System.out.println("Compiler exe:    " + compilerExe);
            System.out.println("VM exe:          " + vmExe);

            int compilerExit = runProcess(
                    List.of(
                            compilerExe.toString(),
                            inputFile.toAbsolutePath().toString(),
                            compiledFile.toAbsolutePath().toString()
                    ),
                    "Compiler"
            );

            if (compilerExit != 0) {
                System.err.println("Compiler failed with exit code " + compilerExit);
                System.exit(1);
            }

            String expectedOutput = Files.readString(expectedOutputFile, StandardCharsets.UTF_8);
            String compiledOutput = Files.readString(compiledFile, StandardCharsets.UTF_8);

            if (!compiledOutput.equals(expectedOutput)) {
                System.err.println("Compiled output does not match expected output.");
                System.err.println("=== EXPECTED ===");
                System.err.println(expectedOutput);
                System.err.println("=== ACTUAL ===");
                System.err.println(compiledOutput);
                System.exit(2);
            }

            int vmExit = runProcess(
                    List.of(
                            vmExe.toString(),
                            compiledFile.toAbsolutePath().toString()
                    ),
                    "VM"
            );

            if (vmExit != 0) {
                System.err.println("VM failed with exit code " + vmExit);
                System.exit(3);
            }

            System.out.println("E2E test passed.");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(10);
        }
    }

    private static Path resolveExecutable(Path relativePath, String label) {
        Path path = relativePath.toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IllegalStateException("Bundled " + label + " executable not found: " + path);
        }
        return path;
    }

    private static Path copyResourceToTemp(String resourceName) {
        try (InputStream in = E2EApplication.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }

            String suffix = resourceName.contains(".")
                    ? resourceName.substring(resourceName.lastIndexOf('.'))
                    : ".tmp";

            Path tempFile = Files.createTempFile("javer-e2e-", suffix);
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy resource to temp file: " + resourceName, e);
        }
    }

    private static int runProcess(List<String> command, String name) throws IOException, InterruptedException {
        System.out.println("Starting " + name + ":");
        for (String part : command) {
            System.out.println("  " + part);
        }

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        Thread stdoutThread = pipeStream(process.getInputStream(), "[" + name + " OUT] ");
        Thread stderrThread = pipeStream(process.getErrorStream(), "[" + name + " ERR] ");

        int exitCode = process.waitFor();
        stdoutThread.join();
        stderrThread.join();

        System.out.println(name + " finished with exit code " + exitCode);
        return exitCode;
    }

    private static Thread pipeStream(InputStream stream, String prefix) {
        Thread thread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(prefix + line);
                }
            } catch (IOException e) {
                System.err.println(prefix + "Failed to read process stream: " + e.getMessage());
            }
        });

        thread.setDaemon(true);
        thread.start();
        return thread;
    }
}