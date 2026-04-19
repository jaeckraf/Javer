package ch.zhaw.it.pm4.javer.test;

import ch.zhaw.it.pm4.javer.core.Compiler;
import ch.zhaw.it.pm4.javer.core.VM;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class E2EApplication {

    public static void main(String[] args) {
        // Load files from resources
        ClassLoader classLoader = E2EApplication.class.getClassLoader();
        Path inputFile = copyResourceToTemp(classLoader, "input.javer");
        Path expectedOutputFile = copyResourceToTemp(classLoader, "expected-compiled.jbc");
        Path compiledFile = Path.of(System.getProperty("java.io.tmpdir"), "compiled.jbc");

        try {
            // Step 1: Compile the input file
            Compiler compiler = new Compiler();
            compiler.compile(inputFile, compiledFile);

            // Step 2: Compare the compiled output with the expected output
            String expectedOutput = Files.readString(expectedOutputFile);
            String compiledOutput = Files.readString(compiledFile);

            if (!compiledOutput.equals(expectedOutput)) {
                System.err.println("Compiled output does not match the expected output.");
                return;
            }

            // Step 3: Execute the compiled file with the VM
            VM vm = new VM();
            String executionResult = vm.run(compiledFile);

            // Print the execution result
            System.out.println("Execution Result: " + executionResult);

        } catch (IOException e) {
            System.err.println("Error during file operations: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during E2E process: " + e.getMessage());
        }
    }

    private static Path copyResourceToTemp(ClassLoader classLoader, String resourceName) {
        try {
            Path tempFile = Path.of(System.getProperty("java.io.tmpdir"), resourceName);
            Files.copy(classLoader.getResourceAsStream(resourceName), tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resourceName, e);
        }
    }
}