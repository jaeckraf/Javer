package ch.zhaw.it.pm4.javer.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

@ExtendWith(ApplicationExtension.class)
class GuiTest {

    private static final String TEST_CODE = "fn void main () {call prints(\"Works!\");}";
    private static final String SAVE_LOAD_TEST_CONTENT = "This is a test for save and load.";

    private Map<String, Object> namespace;
    private GuiController controller;

    private Path getProjectRoot() {
        Path path = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (path != null) {
            if (Files.isRegularFile(path.resolve("pom.xml"))
                    && Files.isDirectory(path.resolve("Application"))
                    && Files.isDirectory(path.resolve("Compiler"))
                    && Files.isDirectory(path.resolve("VM"))) {
                return path;
            }
            path = path.getParent();
        }
        return Path.of(System.getProperty("user.dir"));
    }

    private Path getConsoleInputFile() {
        return getProjectRoot().resolve("console-input.javer");
    }

    private Path getVmInputFile() {
        return getProjectRoot().resolve("vm-input.jbc");
    }

    @BeforeAll
    static void setupHeadlessMode() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
    }

    @Start
    void start(Stage stage) throws IOException {
        URL logbackConfig = Launcher.class.getResource("/ch/zhaw/it/pm4/javer/application/logback.xml");
        if (logbackConfig != null) {
            System.setProperty("logback.configurationFile", logbackConfig.toExternalForm());
        }
        System.setProperty("MODULE", "app");
        System.setProperty("LOG_LEVEL", "INFO");
        JarConfigLoader.loadConfiguration();
        
        Path root = getProjectRoot();
        if (root != null) {
            System.setProperty("javer.compiler.jar", root.resolve("Compiler/target/Compiler-1.0-SNAPSHOT-all.jar").toString());
            System.setProperty("javer.vm.jar", root.resolve("VM/target/VM-1.0-SNAPSHOT-all.jar").toString());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        controller = fxmlLoader.getController();
        namespace = fxmlLoader.getNamespace();
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp(FxRobot robot) throws IOException {
        robot.lookup("#consoleInput").queryAs(TextArea.class).clear();
        robot.lookup("#compilerOutput").queryAs(TextArea.class).clear();
        robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).clear();
        robot.lookup("#statusOutput").queryAs(TextArea.class).clear();

        Files.deleteIfExists(getConsoleInputFile());
        Files.deleteIfExists(getVmInputFile());
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(getConsoleInputFile());
        Files.deleteIfExists(getVmInputFile());
    }

    @Test
    void shouldCompileAndRun_whenButtonIsClicked(FxRobot robot) throws TimeoutException, IOException {
        Files.createFile(getVmInputFile());
        assertTrue(Files.exists(getVmInputFile()), "Pre-existing VM input file should exist");
        
        robot.clickOn("#consoleInput").write(TEST_CODE);
        robot.clickOn("#runCompilerAndVMButton");

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#compilerOutput").queryAs(TextArea.class).getText();
            return output.toLowerCase().contains("compilation successful");
        });

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText();
            return output.contains("Works!");
        });

        assertTrue(Files.exists(getConsoleInputFile()), "Console input file should be created");
        assertTrue(Files.exists(getVmInputFile()), "VM input file should be created");
        assertTrue(Files.size(getVmInputFile()) > 0, "VM input file should not be empty");
    }

    @Test
    void shouldCompileOnly_whenRunCompilerButtonIsClicked(FxRobot robot) throws TimeoutException {
        robot.clickOn("#consoleInput").write(TEST_CODE);
        robot.clickOn("#runCompilerButton");

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#compilerOutput").queryAs(TextArea.class).getText();
            return output.toLowerCase().contains("compilation successful");
        });

        assertTrue(Files.exists(getConsoleInputFile()), "Console input file should be created");
        assertTrue(Files.exists(getVmInputFile()), "VM input file should be created");
        assertTrue(robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText().isEmpty(), "VM output should be empty");
    }

    @Test
    void shouldRunVMOnly_whenRunVMButtonIsClicked(FxRobot robot) throws TimeoutException, IOException {
        robot.clickOn("#consoleInput").write(TEST_CODE);
        robot.clickOn("#runCompilerButton");
        waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#compilerOutput").queryAs(TextArea.class).getText().toLowerCase().contains("compilation successful"));

        robot.lookup("#compilerOutput").queryAs(TextArea.class).clear();
        robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).clear();

        robot.clickOn("#runVMButton");

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText();
            return output.contains("Works!");
        });

        assertTrue(robot.lookup("#compilerOutput").queryAs(TextArea.class).getText().isEmpty(), "Compiler output should be empty");
    }

    @Test
    void shouldStopCompiler_whenStopButtonIsClicked(FxRobot robot) throws TimeoutException {
        assertTrue(robot.lookup("#stopCompilerButton").queryButton().isDisabled());
        robot.clickOn("#consoleInput").write("some invalid code that hangs or is slow...");
        robot.clickOn("#runCompilerButton");
        waitFor(1, TimeUnit.SECONDS, () -> !robot.lookup("#stopCompilerButton").queryButton().isDisabled());
        robot.clickOn("#stopCompilerButton");
        waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#stopCompilerButton").queryButton().isDisabled());
        assertTrue(!robot.lookup("#runCompilerButton").queryButton().isDisabled());
    }

    @Test
    void shouldStopVM_whenStopButtonIsClicked(FxRobot robot) throws TimeoutException {
        assertTrue(robot.lookup("#stopVMButton").queryButton().isDisabled());
        robot.clickOn("#consoleInput").write("fn void main () {while (true) {}}");
        robot.clickOn("#runCompilerButton");
        waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#compilerOutput").queryAs(TextArea.class).getText().toLowerCase().contains("compilation successful"));
        robot.clickOn("#runVMButton");
        waitFor(1, TimeUnit.SECONDS, () -> !robot.lookup("#stopVMButton").queryButton().isDisabled());
        robot.clickOn("#stopVMButton");
        waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#stopVMButton").queryButton().isDisabled());
        assertTrue(!robot.lookup("#runVMButton").queryButton().isDisabled());
    }

    @Test
    void shouldShowError_whenRunVMIsClickedWithoutBytecode(FxRobot robot) throws TimeoutException {
        assertFalse(Files.exists(getVmInputFile()));
        robot.clickOn("#runVMButton");
        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText();
            return output.toLowerCase().contains("error reading file");
        });
    }

    @Test
    void shouldDeleteFilesAfterTeardown() throws IOException {
        Files.createFile(getConsoleInputFile());
        Files.createFile(getVmInputFile());
        assertTrue(Files.exists(getConsoleInputFile()));
        assertTrue(Files.exists(getVmInputFile()));
        tearDown();
        assertFalse(Files.exists(getConsoleInputFile()), "Console input file should be deleted after teardown");
        assertFalse(Files.exists(getVmInputFile()), "VM input file should be deleted after teardown");
    }

    @Test
    void shouldDisableAllRunButtons_whenProcessIsRunning(FxRobot robot) throws TimeoutException {
        robot.clickOn("#consoleInput").write("some invalid code that hangs or is slow...");
        robot.clickOn("#runCompilerButton");
        
        waitFor(1, TimeUnit.SECONDS, () -> robot.lookup("#runCompilerButton").queryButton().isDisabled());
        assertTrue(robot.lookup("#runCompilerButton").queryButton().isDisabled());
        assertTrue(robot.lookup("#runCompilerAndVMButton").queryButton().isDisabled());
        assertFalse(robot.lookup("#runVMButton").queryButton().isDisabled());
        
        robot.clickOn("#stopCompilerButton");
        
        waitFor(10, TimeUnit.SECONDS, () -> !robot.lookup("#runCompilerButton").queryButton().isDisabled());
        assertFalse(robot.lookup("#runVMButton").queryButton().isDisabled());
        assertFalse(robot.lookup("#runCompilerAndVMButton").queryButton().isDisabled());
    }

    @Test
    void shouldShowError_whenRunVMIsClickedWithEmptyBytecode(FxRobot robot) throws IOException, TimeoutException {
        Files.createFile(getVmInputFile());
        assertTrue(Files.exists(getVmInputFile()) && Files.size(getVmInputFile()) == 0);

        robot.clickOn("#runVMButton");

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText();
            return output.toLowerCase().contains("error");
        });
    }

    @Test
    void shouldHandleDoubleClickOnRunButton(FxRobot robot) throws TimeoutException {
        robot.clickOn("#consoleInput").write(TEST_CODE);

        robot.doubleClickOn("#runCompilerAndVMButton");

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#compilerOutput").queryAs(TextArea.class).getText();
            return output.toLowerCase().contains("compilation successful");
        });

        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText();
            return output.contains("Works!");
        });

        String log = robot.lookup("#statusOutput").queryAs(TextArea.class).getText();
        long count = log.lines().filter(line -> line.contains("Starting Compiler with command")).count();
        assertEquals(1, count, "Compiler should only be started once on double-click.");
    }

    @Test
    void shouldLoadMainGuiControlsFromFxml() {
        assertNotNull(namespace.get("consoleInput"));
        assertNotNull(namespace.get("consoleInputLineNumbers"));
        assertNotNull(namespace.get("fileMenuButton"));
        assertNotNull(namespace.get("expertModeOption"));
        assertNotNull(namespace.get("runCompilerButton"));
        assertNotNull(namespace.get("runVMButton"));
        assertNotNull(namespace.get("runCompilerAndVMButton"));
        assertNotNull(namespace.get("stopCompilerButton"));
        assertNotNull(namespace.get("stopVMButton"));
        assertNotNull(namespace.get("compilerOutput"));
        assertNotNull(namespace.get("virtualMachineOutput"));
        assertNotNull(namespace.get("statusOutput"));
        assertNotNull(namespace.get("vmStackSizeValueOption"));
        assertNotNull(namespace.get("vmStackSizeKbOption"));
        assertNotNull(namespace.get("vmStackSizeMbOption"));
        assertNotNull(namespace.get("vmDumpOnErrorOption"));
    }

    @Test
    void shouldSaveAndLoadJaverFile(FxRobot robot) throws IOException {
        File testFile = getProjectRoot().resolve("test.javer").toFile();
        
        robot.interact(() -> {
            TextArea consoleInput = robot.lookup("#consoleInput").queryAs(TextArea.class);
            consoleInput.setText(SAVE_LOAD_TEST_CONTENT);
            controller.saveJaverFile(testFile);
            consoleInput.clear();
            controller.loadJaverFile(testFile);
            assertEquals(SAVE_LOAD_TEST_CONTENT, consoleInput.getText());
        });
        
        Files.deleteIfExists(testFile.toPath());
    }

    @Test
    void shouldSaveAndLoadJbcFile(FxRobot robot) throws IOException, TimeoutException {
        File testFile = getProjectRoot().resolve("test.jbc").toFile();

        robot.clickOn("#consoleInput").write(TEST_CODE);
        robot.clickOn("#runCompilerButton");
        waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#compilerOutput").queryAs(TextArea.class).getText().toLowerCase().contains("compilation successful"));

        controller.saveJbcFile(testFile);
        assertTrue(Files.exists(testFile.toPath()));

        Files.deleteIfExists(getVmInputFile());
        controller.loadJbcFile(testFile);

        robot.clickOn("#runVMButton");
        waitFor(10, TimeUnit.SECONDS, () -> {
            String output = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class).getText();
            return output.contains("Works!");
        });

        Files.deleteIfExists(testFile.toPath());
    }

    @Test
    void shouldNotChangeText_whenLoadingJaverFileIsCancelled(FxRobot robot) {
        String initialText = "Initial text.";
        robot.interact(() -> {
            TextArea consoleInput = robot.lookup("#consoleInput").queryAs(TextArea.class);
            consoleInput.setText(initialText);
            controller.loadJaverFile(null);
            assertEquals(initialText, consoleInput.getText());
        });
    }

    @Test
    void shouldNotSaveFile_whenSavingJaverFileIsCancelled(FxRobot robot) {
        File testFile = getProjectRoot().resolve("test.javer").toFile();
        robot.interact(() -> {
            controller.saveJaverFile(null);
            assertFalse(testFile.exists());
        });
    }

    @Test
    void shouldToggleExpertModeOptionsVisibility_whenExpertModeIsToggled(FxRobot robot) throws TimeoutException {
        VBox compilerOptionsBox = robot.lookup("#compilerOptionsBox").queryAs(VBox.class);
        VBox vmOptionsBox = robot.lookup("#vmOptionsBox").queryAs(VBox.class);
        CheckMenuItem expertModeOption = (CheckMenuItem) namespace.get("expertModeOption");
        TextArea virtualMachineOutput = robot.lookup("#virtualMachineOutput").queryAs(TextArea.class);

        assertFalse(compilerOptionsBox.isVisible());
        assertFalse(vmOptionsBox.isVisible());
        assertEquals("Expert Mode: Off", expertModeOption.getText());
        assertEquals(240.0, virtualMachineOutput.getPrefHeight());

        robot.interact(() -> expertModeOption.setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> vmOptionsBox.isVisible());

        assertTrue(compilerOptionsBox.isVisible());
        assertTrue(vmOptionsBox.isVisible());
        assertEquals("Expert Mode: On", expertModeOption.getText());
        assertEquals(80.0, virtualMachineOutput.getPrefHeight());

        robot.interact(() -> expertModeOption.setSelected(false));
        waitFor(2, TimeUnit.SECONDS, () -> !vmOptionsBox.isVisible());

        assertFalse(compilerOptionsBox.isVisible());
        assertFalse(vmOptionsBox.isVisible());
        assertEquals("Expert Mode: Off", expertModeOption.getText());
        assertEquals(240.0, virtualMachineOutput.getPrefHeight());
    }

    @Test
    void shouldUpdateLineNumbers_whenTextIsEntered(FxRobot robot) {
        TextArea lineNumbers = robot.lookup("#consoleInputLineNumbers").queryAs(TextArea.class);

        assertEquals("1", lineNumbers.getText());
        robot.clickOn("#consoleInput").write("line 1\nline 2\nline 3");
        assertEquals("1\n2\n3", lineNumbers.getText());
    }
    
    @Test
    void shouldLogAction_whenExpertModeIsToggled(FxRobot robot) throws TimeoutException {
        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        robot.interact(() -> ((CheckMenuItem) namespace.get("expertModeOption")).setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Expert Mode set to On."));
    }

    @Test
    void shouldAddCompilerOptionsToCommand_whenInExpertMode(FxRobot robot) throws TimeoutException {
        robot.interact(() -> ((CheckMenuItem) namespace.get("expertModeOption")).setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> robot.lookup("#compilerOptionsBox").queryAs(VBox.class).isVisible());

        robot.clickOn("#compilerDumpLexerOption");
        robot.clickOn("#compilerDumpAstOption");
        robot.clickOn("#compilerDumpSymbolTableOption");
        robot.clickOn("#compilerLoggingOption");

        robot.clickOn("#consoleInput").write(TEST_CODE);
        robot.clickOn("#runCompilerButton");

        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Starting Compiler with command"));

        String logText = statusOutput.getText();
        assertTrue(logText.contains("--dump-lexer"));
        assertTrue(logText.contains("--dump-ast"));
        assertTrue(logText.contains("--dump-symboltable"));
        assertTrue(logText.contains("--logging"));
    }

    @Test
    void shouldAddVmOptionsToCommand_whenInExpertMode(FxRobot robot) throws TimeoutException {
        robot.interact(() -> ((CheckMenuItem) namespace.get("expertModeOption")).setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> robot.lookup("#vmOptionsBox").queryAs(VBox.class).isVisible());

        robot.clickOn("#consoleInput").write(TEST_CODE);
        robot.clickOn("#runCompilerButton");
        
        waitFor(10, TimeUnit.SECONDS, () -> robot.lookup("#compilerOutput").queryAs(TextArea.class).getText().toLowerCase().contains("compilation successful"));

        robot.interact(() -> {
            ((RadioButton) namespace.get("vmStackSizeKbOption")).setSelected(true);
            ((CheckBox) namespace.get("vmDumpOnErrorOption")).setSelected(true);
        });

        robot.clickOn("#runVMButton");

        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Starting VM with command"));

        String logText = statusOutput.getText();
        assertTrue(logText.contains("--stack-size"));
        assertTrue(logText.contains("KB"));
        assertTrue(logText.contains("--dump-on-error"));
    }

    @Test
    void shouldUpdateVmStackSizeOptions_whenInExpertMode(FxRobot robot) throws TimeoutException {
        robot.interact(() -> ((CheckMenuItem) namespace.get("expertModeOption")).setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> robot.lookup("#vmOptionsBox").queryAs(VBox.class).isVisible());

        Spinner<Integer> stackSizeSpinner = (Spinner<Integer>) namespace.get("vmStackSizeValueOption");
        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        RadioButton vmStackSizeKbOption = (RadioButton) namespace.get("vmStackSizeKbOption");
        RadioButton vmStackSizeMbOption = (RadioButton) namespace.get("vmStackSizeMbOption");

        robot.interact(() -> {
            assertTrue(vmStackSizeMbOption.isSelected());
            assertEquals(1, stackSizeSpinner.getValue());
            assertEquals(16, ((SpinnerValueFactory.IntegerSpinnerValueFactory) stackSizeSpinner.getValueFactory()).getMax());
        });

        robot.interact(() -> stackSizeSpinner.getValueFactory().setValue(8));
        waitFor(2, TimeUnit.SECONDS, () -> statusOutput.getText().contains("VM stack size set to 8MB."));

        robot.interact(() -> vmStackSizeKbOption.setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> statusOutput.getText().contains("VM stack size unit set to KB."));
        robot.interact(() -> {
            assertEquals(8, stackSizeSpinner.getValue());
            assertEquals(16384, ((SpinnerValueFactory.IntegerSpinnerValueFactory) stackSizeSpinner.getValueFactory()).getMax());
        });

        robot.interact(() -> stackSizeSpinner.getValueFactory().setValue(2048));
        waitFor(2, TimeUnit.SECONDS, () -> statusOutput.getText().contains("VM stack size set to 2048KB."));

        robot.interact(() -> vmStackSizeMbOption.setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> statusOutput.getText().contains("VM stack size unit set to MB."));
        robot.interact(() -> {
            assertEquals(16, stackSizeSpinner.getValue());
            assertEquals(16, ((SpinnerValueFactory.IntegerSpinnerValueFactory) stackSizeSpinner.getValueFactory()).getMax());
        });
    }

    @Test
    void shouldShowError_whenCompilerJarIsMissing(FxRobot robot) throws TimeoutException {
        String originalCompilerJar = System.getProperty("javer.compiler.jar");
        System.setProperty("javer.compiler.jar", "non-existent.jar");

        try {
            robot.clickOn("#consoleInput").write(TEST_CODE);
            robot.clickOn("#runCompilerButton");

            TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
            waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Configured jar does not exist"));
        } finally {
            System.setProperty("javer.compiler.jar", originalCompilerJar);
        }
    }

    @Test
    void shouldShowError_whenLoadingInvalidFileExtension(FxRobot robot) throws IOException, TimeoutException {
        File testFile = getProjectRoot().resolve("test.txt").toFile();
        Files.writeString(testFile.toPath(), "some text");

        String initialText = "Initial text.";
        robot.interact(() -> {
            TextArea consoleInput = robot.lookup("#consoleInput").queryAs(TextArea.class);
            consoleInput.setText(initialText);
            controller.loadJaverFile(testFile);
            assertEquals(initialText, consoleInput.getText());
        });

        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Only .javer files can be loaded as source files."));

        Files.deleteIfExists(testFile.toPath());
    }

    @Test
    void shouldHandleVariousLineEndingsForLineNumbers(FxRobot robot) {
        TextArea lineNumbers = robot.lookup("#consoleInputLineNumbers").queryAs(TextArea.class);
        TextArea consoleInput = robot.lookup("#consoleInput").queryAs(TextArea.class);

        robot.interact(() -> consoleInput.setText("line 1\r\nline 2\nline 3\n"));
        assertEquals("1\n2\n3\n4", lineNumbers.getText());

        robot.interact(() -> consoleInput.setText(""));
        assertEquals("1", lineNumbers.getText());
    }

    @Test
    void shouldLogUiAction_whenCheckBoxIsToggled(FxRobot robot) throws TimeoutException {
        robot.interact(() -> ((CheckMenuItem) namespace.get("expertModeOption")).setSelected(true));
        waitFor(2, TimeUnit.SECONDS, () -> robot.lookup("#compilerOptionsBox").queryAs(VBox.class).isVisible());

        robot.clickOn("#compilerDumpLexerOption");

        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Dump Lexer set to On."));

        robot.clickOn("#compilerDumpLexerOption");
        waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Dump Lexer set to Off."));
    }

    @Test
    void shouldShowCompilationError_whenCompilingInvalidCode(FxRobot robot) throws TimeoutException {
        robot.clickOn("#consoleInput").write("this is not valid javer code");
        robot.clickOn("#runCompilerButton");

        TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
        waitFor(10, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Compiler finished with exit code"));

        TextArea compilerOutput = robot.lookup("#compilerOutput").queryAs(TextArea.class);
        String output = compilerOutput.getText().toLowerCase();
        assertTrue(output.contains("=== error report ==="),
                "Compiler output should show error report on failure. Actual output:\n" + output);
        assertFalse(output.contains("compilation successful"),
                "Compiler output should not show success on failure. Actual output:\n" + output);
    }

    @Test
    void shouldSynchronizeLineNumberScroll_whenInputScrolls(FxRobot robot) {
        TextArea consoleInput = robot.lookup("#consoleInput").queryAs(TextArea.class);
        TextArea lineNumbers = robot.lookup("#consoleInputLineNumbers").queryAs(TextArea.class);

        String longText = IntStream.range(1, 100).mapToObj(i -> "Line " + i).collect(Collectors.joining("\n"));
        robot.interact(() -> consoleInput.setText(longText));

        robot.interact(() -> consoleInput.setScrollTop(50.0));
        assertEquals(50.0, lineNumbers.getScrollTop(), 0.1);

        robot.interact(() -> consoleInput.setScrollTop(0.0));
        assertEquals(0.0, lineNumbers.getScrollTop(), 0.1);
    }

    @Test
    void shouldShowError_whenVmJarIsMissing(FxRobot robot) throws TimeoutException {
        String originalVmJar = System.getProperty("javer.vm.jar");
        System.setProperty("javer.vm.jar", "non-existent.jar");

        try {
            robot.clickOn("#runVMButton");

            TextArea statusOutput = robot.lookup("#statusOutput").queryAs(TextArea.class);
            waitFor(5, TimeUnit.SECONDS, () -> statusOutput.getText().contains("Configured jar does not exist"));
        } finally {
            System.setProperty("javer.vm.jar", originalVmJar);
        }
    }
}
