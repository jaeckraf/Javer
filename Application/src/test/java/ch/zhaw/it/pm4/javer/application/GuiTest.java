package ch.zhaw.it.pm4.javer.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitFor;

@ExtendWith(ApplicationExtension.class)
class GuiTest {

    private static final String TEST_CODE = "fn void main () {call prints(\"Works!\");}";

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
        
        Path root = Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
        while (root != null && !Files.exists(root.resolve("pom.xml"))) {
            root = root.getParent();
        }
        if (root != null) {
            System.setProperty("javer.compiler.jar", root.resolve("Compiler/target/Compiler-1.0-SNAPSHOT-all.jar").toString());
            System.setProperty("javer.vm.jar", root.resolve("VM/target/VM-1.0-SNAPSHOT-all.jar").toString());
        }
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

        FXMLLoader fxmlLoader = new FXMLLoader(GuiApplication.class.getResource("gui-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
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
}
