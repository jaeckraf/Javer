package ch.zhaw.it.pm4.javer.application;

import ch.zhaw.it.pm4.misc.JaverLogger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Controller for the main GUI. It owns the source input, process output panes,
 * status log pane, compiler/VM process runners, and runtime files used to pass
 * source and bytecode between tools.
 */
public class GuiController {

    public static final String COMPILER = "Compiler";
    public static final String TOOLS = "tools";
    public static final String VM_INPUT_FILE = "VM input file";
    public static final String VM_IS_ALREADY_RUNNING = "VM is already running.";
    public static final String USER_DIR = "user.dir";
    private static final String CONSOLE_INPUT_FILE_NAME = "console-input.javer";
    private static final String VM_INPUT_FILE_NAME = "vm-input.jbc";
    private static final String SOURCE_FILE_EXTENSION = ".javer";
    private static final String BYTECODE_FILE_EXTENSION = ".jbc";
    private static final String SOURCE_FILE_FILTER_DESCRIPTION = "Javer source files (*.javer)";
    private static final String BYTECODE_FILE_FILTER_DESCRIPTION = "Javer bytecode files (*.jbc)";
    private static final double NORMAL_VM_OUTPUT_PREF_HEIGHT = 240.0;
    private static final double EXPERT_VM_OUTPUT_PREF_HEIGHT = 80.0;
    private static final int DEFAULT_STACK_SIZE_VALUE = 1;
    private static final int MIN_STACK_SIZE_VALUE = 1;
    private static final int MAX_STACK_SIZE_KB = 16 * 1024;
    private static final int MAX_STACK_SIZE_MB = 16;
    private static final List<String> TOOL_JAVA_OPTIONS = List.of(
            "-Dfile.encoding=UTF-8",
            "-Dstdout.encoding=UTF-8",
            "-Dstderr.encoding=UTF-8"
    );
    // Packaged release layout inside Application app image
    private static final Path RELEASE_COMPILER_EXE = Path.of("app", TOOLS, COMPILER, "javer-compiler.exe");
    private static final Path RELEASE_VM_EXE = Path.of("app", TOOLS, "VM", "javer-vm.exe");
    private final Path runtimeDirectory = resolveRuntimeDirectory();
    private final Path consoleInputFile = runtimeDirectory.resolve(CONSOLE_INPUT_FILE_NAME);
    private final Path vmInputFile = runtimeDirectory.resolve(VM_INPUT_FILE_NAME);

    private ManagedProcessRunner compilerRunner;
    private ManagedProcessRunner vmRunner;

    @FXML
    private Button runVMButton;

    @FXML
    private Button stopCompilerButton;

    @FXML
    private Button stopVMButton;

    @FXML
    private Button runCompilerAndVMButton;

    @FXML
    private MenuButton fileMenuButton;

    @FXML
    private TextArea compilerOutput;

    @FXML
    private TextArea virtualMachineOutput;

    @FXML
    private TextArea consoleInput;

    @FXML
    private TextArea consoleInputLineNumbers;

    @FXML
    private Button runCompilerButton;

    @FXML
    private TextArea statusOutput;

    @FXML
    private CheckMenuItem expertModeOption;

    @FXML
    private VBox compilerOptionsBox;

    @FXML
    private VBox vmOptionsBox;

    @FXML
    private CheckBox compilerDumpLexerOption;

    @FXML
    private CheckBox compilerDumpAstOption;

    @FXML
    private CheckBox compilerDumpSymbolTableOption;

    @FXML
    private CheckBox compilerLoggingOption;

    @FXML
    private Spinner<Integer> vmStackSizeValueOption;

    @FXML
    private RadioButton vmStackSizeKbOption;

    @FXML
    private RadioButton vmStackSizeMbOption;

    @FXML
    private CheckBox vmDumpOnErrorOption;

    /**
     * Creates the controller instance used by the FXML loader.
     */
    public GuiController() {
        // Required by JavaFX for reflective instantiation; must remain empty.
    }

    private static Path resolveRuntimeDirectory() {
        Path workingDirectory = Path.of(System.getProperty(USER_DIR)).toAbsolutePath().normalize();
        Path codeLocation = getCodeLocation();
        Path packagedRoot = findPackagedAppRoot(codeLocation);
        if (packagedRoot != null) {
            return packagedRoot;
        }

        Path projectRoot = findProjectRoot(workingDirectory);
        if (projectRoot != null) {
            return projectRoot;
        }

        projectRoot = findProjectRoot(codeLocation);
        if (projectRoot != null) {
            return projectRoot;
        }

        return workingDirectory;
    }

    private static Path getCodeLocation() {
        try {
            return Path.of(GuiController.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()).toAbsolutePath().normalize();
        } catch (Exception exception) {
            return Path.of(System.getProperty(USER_DIR)).toAbsolutePath().normalize();
        }
    }

    private static Path findPackagedAppRoot(Path start) {
        Path path = Files.isRegularFile(start) ? start.getParent() : start;
        while (path != null) {
            Path fileName = path.getFileName();
            if (fileName != null
                    && "app".equalsIgnoreCase(fileName.toString())
                    && Files.isDirectory(path.resolve(TOOLS))) {
                return path.getParent();
            }
            path = path.getParent();
        }
        return null;
    }

    private static Path findProjectRoot(Path start) {
        Path path = Files.isRegularFile(start) ? start.getParent() : start;
        while (path != null) {
            if (Files.isRegularFile(path.resolve("pom.xml"))
                    && Files.isDirectory(path.resolve("Application"))
                    && Files.isDirectory(path.resolve(COMPILER))
                    && Files.isDirectory(path.resolve("VM"))) {
                return path;
            }
            path = path.getParent();
        }
        return null;
    }

    /**
     * Initializes process runners, logging integration, and initial button
     * state after FXML injection has completed.
     */
    @FXML
    public void initialize() {
        GuiLogAppender.setConsumer(this::appendStatus);

        JaverLogger.info("GUI initialized");
        JaverLogger.info("Working directory: " + Path.of("").toAbsolutePath().normalize());
        JaverLogger.info("Runtime file directory: " + runtimeDirectory);

        compilerRunner = new ManagedProcessRunner(
                COMPILER,
                this::appendCompilerOutput,
                this::appendCompilerOutput,
                this::updateCompilerButtons
        );

        vmRunner = new ManagedProcessRunner(
                "VM",
                this::appendVMOutput,
                this::appendVMOutput,
                this::updateVMButtons
        );

        updateCompilerButtons(false);
        updateVMButtons(false);
        configureStackSizeOptions();
        bindExpertMode();
        configureExpertModeMenuItem();
        configureUiActionLogging();
        configureSourceLineNumbers();
    }

    /**
     * Saves the current source editor contents as a {@code .javer} file.
     */
    @FXML
    protected void onSaveJaverFileClick() {
        JaverLogger.info("Save Javer Source File selected.");
        File selectedFile = showSaveDialog(
                "Save Javer Source File",
                "program" + SOURCE_FILE_EXTENSION,
                SOURCE_FILE_FILTER_DESCRIPTION,
                SOURCE_FILE_EXTENSION
        );
        if (selectedFile == null) {
            JaverLogger.info("Save Javer Source File canceled.");
            return;
        }

        saveJaverFile(selectedFile);
    }

    /**
     * Saves the contents of the source editor to the specified file.
     * Appends the standard source file extension if it is missing.
     *
     * @param file the target file to save the source code to
     */
    public void saveJaverFile(File file) {
        if (file == null) {
            return;
        }

        Path targetPath = ensureExtension(file.toPath(), SOURCE_FILE_EXTENSION);
        saveTextFile(targetPath, consoleInput.getText(), "Javer source file");
    }

    /**
     * Loads a {@code .javer} file into the source editor.
     */
    @FXML
    protected void onLoadJaverFileClick() {
        JaverLogger.info("Load Javer Source File selected.");
        File selectedFile = showOpenDialog(
                "Load Javer Source File",
                SOURCE_FILE_FILTER_DESCRIPTION,
                SOURCE_FILE_EXTENSION
        );
        if (selectedFile == null) {
            JaverLogger.info("Load Javer Source File canceled.");
            return;
        }

        loadJaverFile(selectedFile);
    }

    /**
     * Loads the contents of the specified file into the source editor.
     * Validates that the file has the correct source file extension before loading.
     *
     * @param file the file containing the source code to load
     */
    public void loadJaverFile(File file) {
        if (file == null) {
            return;
        }

        Path sourcePath = file.toPath().toAbsolutePath().normalize();
        if (!hasExtension(sourcePath, SOURCE_FILE_EXTENSION)) {
            JaverLogger.error("Only .javer files can be loaded as source files.");
            return;
        }

        try {
            consoleInput.setText(Files.readString(sourcePath, StandardCharsets.UTF_8));
            JaverLogger.info("Loaded Javer source file from: " + sourcePath);
        } catch (IOException exception) {
            JaverLogger.error("Failed to load Javer source file: " + exception.getMessage());
        }
    }

    /**
     * Saves the current VM bytecode input as a {@code .jbc} file.
     */
    @FXML
    protected void onSaveJbcFileClick() {
        JaverLogger.info("Save JBC File selected.");
        if (!isBytecodeFileReady()) {
            JaverLogger.error("No JBC file is available. Compile source code or load a .jbc file first.");
            return;
        }

        File selectedFile = showSaveDialog(
                "Save JBC File",
                "output" + BYTECODE_FILE_EXTENSION,
                BYTECODE_FILE_FILTER_DESCRIPTION,
                BYTECODE_FILE_EXTENSION
        );
        if (selectedFile == null) {
            JaverLogger.info("Save JBC File canceled.");
            return;
        }

        saveJbcFile(selectedFile);
    }

    /**
     * Copies the current internal VM input file to the specified target file.
     * Appends the standard bytecode file extension if it is missing.
     *
     * @param file the target file to save the bytecode to
     */
    public void saveJbcFile(File file) {
        if (file == null) {
            return;
        }

        Path targetPath = ensureExtension(file.toPath(), BYTECODE_FILE_EXTENSION);
        copyFile(
                vmInputFile,
                targetPath,
                "Saved JBC file as: ",
                "Failed to save JBC file: "
        );
    }

    /**
     * Loads a {@code .jbc} file as VM input.
     */
    @FXML
    protected void onLoadJbcFileClick() {
        JaverLogger.info("Load JBC File selected.");
        File selectedFile = showOpenDialog(
                "Load JBC File",
                BYTECODE_FILE_FILTER_DESCRIPTION,
                BYTECODE_FILE_EXTENSION
        );
        if (selectedFile == null) {
            JaverLogger.info("Load JBC File canceled.");
            return;
        }

        loadJbcFile(selectedFile);
    }

    /**
     * Copies the specified bytecode file to the internal VM input file location.
     * Validates that the file has the correct bytecode file extension before copying.
     *
     * @param file the file containing the bytecode to load
     */
    public void loadJbcFile(File file) {
        if (file == null) {
            return;
        }

        Path sourcePath = file.toPath().toAbsolutePath().normalize();
        if (!hasExtension(sourcePath, BYTECODE_FILE_EXTENSION)) {
            JaverLogger.error("Only .jbc files can be loaded as bytecode files.");
            return;
        }

        copyFile(
                sourcePath,
                vmInputFile,
                "Loaded JBC file for VM from " + sourcePath + " to ",
                "Failed to load JBC file: "
        );
    }

    /**
     * Writes the editor contents to disk and starts only the compiler process.
     */
    @FXML
    protected void onRunCompilerClick() {
        JaverLogger.info("Run Compiler button pressed.");
        if (compilerRunner.isRunning()) {
            return;
        }

        compilerOutput.clear();

        String inputPath = writeInputToFile(consoleInput.getText());
        if (inputPath == null || !deleteFileIfExists(vmInputFile, VM_INPUT_FILE)) {
            return;
        }

        List<String> command = buildCompilerCommand(inputPath);
        if (command.isEmpty()) {
            return;
        }

        logCommand(COMPILER, command);

        if (compilerRunner.start(command).isEmpty()) {
            JaverLogger.warning("Compiler is already running.");
        }
    }

    /**
     * Starts only the VM process with the latest generated bytecode file.
     */
    @FXML
    protected void onRunVMClick() {
        JaverLogger.info("Run VM button pressed.");
        if (vmRunner.isRunning()) {
            return;
        }

        virtualMachineOutput.clear();

        List<String> command = buildVmCommand();
        if (command.isEmpty()) {
            return;
        }

        logCommand("VM", command);

        if (vmRunner.start(command).isEmpty()) {
            JaverLogger.warning(VM_IS_ALREADY_RUNNING);
        }
    }

    /**
     * Requests termination of the VM process.
     */
    @FXML
    public void onStopVMClick() {
        JaverLogger.info("Stop VM button pressed.");
        vmRunner.stop();
    }

    /**
     * Requests termination of the compiler process.
     */
    @FXML
    public void onStopCompilerClick() {
        JaverLogger.info("Stop Compiler button pressed.");
        compilerRunner.stop();
    }

    /**
     * Stops managed processes, removes runtime source/bytecode files, and
     * detaches the GUI log appender.
     */
    public void shutdown() {
        JaverLogger.info("Application shutdown started.");

        if (compilerRunner != null) {
            compilerRunner.stopAndWait();
        }
        if (vmRunner != null) {
            vmRunner.stopAndWait();
        }

        deleteRuntimeFileIfExists(consoleInputFile, "source input file");
        deleteRuntimeFileIfExists(vmInputFile, VM_INPUT_FILE);
        GuiLogAppender.clearConsumer();
    }

    /**
     * Runs compiler and VM as a sequence. The VM starts only after the compiler
     * has completed successfully and produced a non-empty bytecode file.
     */
    @FXML
    public void onRunCompilerAndVMClick() {
        JaverLogger.info("Run Compiler and VM button pressed.");
        if (compilerRunner.isRunning() || vmRunner.isRunning()) {
            JaverLogger.warning("Compiler or VM is already running.");
            return;
        }

        compilerOutput.clear();
        virtualMachineOutput.clear();

        String inputPath = writeInputToFile(consoleInput.getText());
        if (inputPath == null || !deleteFileIfExists(vmInputFile, VM_INPUT_FILE)) {
            return;
        }

        List<String> compilerCommand = buildCompilerCommand(inputPath);
        List<String> vmCommand = buildVmCommand();

        if (compilerCommand.isEmpty() || vmCommand.isEmpty()) {
            return;
        }

        logCommand(COMPILER, compilerCommand);

        var compilerRun = compilerRunner.start(compilerCommand);
        if (compilerRun.isEmpty()) {
            JaverLogger.warning("Compiler is already running.");
            return;
        }

        compilerRun.get().thenAccept(result -> startVmAfterSuccessfulCompilation(result, vmCommand));
    }

    private List<String> buildCompilerCommand(String inputPath) {
        String compilerOutputPath = bytecodeOutputBasePath(vmInputFile).toAbsolutePath().toString();

        List<String> command = new ArrayList<>();
        Path compilerExe = resolveReleaseExecutable(RELEASE_COMPILER_EXE, COMPILER);
        if (compilerExe != null) {
            command.add(compilerExe.toString());
        } else {
            Path compilerJar = resolveJarFromProperty("javer.compiler.jar");
            if (compilerJar == null) {
                JaverLogger.error("Compiler executable and IDE jar are both unavailable.");
                return List.of();
            }
            addJavaJarCommand(command, compilerJar);
        }

        command.add("--in-file");
        command.add(inputPath);
        command.add("--out-file");
        command.add(compilerOutputPath);
        addCompilerOptions(command);

        return command;
    }

    private void addCompilerOptions(List<String> command) {
        if (!expertModeOption.isSelected()) {
            return;
        }

        if (compilerDumpLexerOption.isSelected()) {
            command.add("--dump-lexer");
        }
        if (compilerDumpAstOption.isSelected()) {
            command.add("--dump-ast");
        }
        if (compilerDumpSymbolTableOption.isSelected()) {
            command.add("--dump-symboltable");
        }
        if (compilerLoggingOption.isSelected()) {
            command.add("--logging");
        }
    }

    private Path bytecodeOutputBasePath(Path bytecodeFile) {
        String fileName = bytecodeFile.getFileName().toString();
        if (!fileName.endsWith(BYTECODE_FILE_EXTENSION)) {
            return bytecodeFile;
        }

        String baseName = fileName.substring(0, fileName.length() - BYTECODE_FILE_EXTENSION.length());
        return bytecodeFile.resolveSibling(baseName);
    }

    private List<String> buildVmCommand() {
        List<String> command = new ArrayList<>();
        Path vmExe = resolveReleaseExecutable(RELEASE_VM_EXE, "VM");
        if (vmExe != null) {
            command.add(vmExe.toString());
        } else {
            Path vmJar = resolveJarFromProperty("javer.vm.jar");
            if (vmJar == null) {
                JaverLogger.error("VM executable and IDE jar are both unavailable.");
                return List.of();
            }
            addJavaJarCommand(command, vmJar);
        }

        if (expertModeOption.isSelected()) {
            addVmOptions(command);
        }
        command.add(vmInputFile.toAbsolutePath().toString());

        return command;
    }

    private void addVmOptions(List<String> command) {
        command.add("--stack-size");
        command.add(vmStackSizeValueOption.getValue() + selectedStackSizeUnit());

        if (vmDumpOnErrorOption.isSelected()) {
            command.add("--dump-on-error");
        }
    }

    private void configureStackSizeOptions() {
        setStackSizeValueFactory(maxStackSizeForSelectedUnit());
        vmStackSizeKbOption.selectedProperty().addListener((observable, wasSelected, selected) -> {
            if (Boolean.TRUE.equals(selected)) {
                setStackSizeValueFactory(MAX_STACK_SIZE_KB);
            }
        });
        vmStackSizeMbOption.selectedProperty().addListener((observable, wasSelected, selected) -> {
            if (Boolean.TRUE.equals(selected)) {
                setStackSizeValueFactory(MAX_STACK_SIZE_MB);
            }
        });
    }

    private void setStackSizeValueFactory(int maxStackSize) {
        Integer currentValue = vmStackSizeValueOption.getValue();
        int stackSize = currentValue == null ? DEFAULT_STACK_SIZE_VALUE : currentValue;
        int clampedStackSize = Math.clamp(stackSize, MIN_STACK_SIZE_VALUE, maxStackSize);

        vmStackSizeValueOption.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                MIN_STACK_SIZE_VALUE,
                maxStackSize,
                clampedStackSize
        ));
    }

    private int maxStackSizeForSelectedUnit() {
        return vmStackSizeKbOption.isSelected() ? MAX_STACK_SIZE_KB : MAX_STACK_SIZE_MB;
    }

    private String selectedStackSizeUnit() {
        return vmStackSizeKbOption.isSelected() ? "KB" : "MB";
    }

    private void addJavaJarCommand(List<String> command, Path jarPath) {
        command.add("java");
        command.addAll(TOOL_JAVA_OPTIONS);
        command.add("-jar");
        command.add(jarPath.toString());
    }

    private void bindExpertMode() {
        compilerOptionsBox.visibleProperty().bind(expertModeOption.selectedProperty());
        compilerOptionsBox.managedProperty().bind(expertModeOption.selectedProperty());
        vmOptionsBox.visibleProperty().bind(expertModeOption.selectedProperty());
        vmOptionsBox.managedProperty().bind(expertModeOption.selectedProperty());
    }

    private void configureExpertModeMenuItem() {
        updateExpertModeMenuItem(expertModeOption.isSelected());
        expertModeOption.selectedProperty().addListener((observable, wasSelected, selected) ->
                updateExpertModeMenuItem(selected));
    }

    private void updateExpertModeMenuItem(boolean enabled) {
        String state = enabled ? "On" : "Off";
        expertModeOption.setText("Expert Mode: " + state);
        virtualMachineOutput.setPrefHeight(enabled ? EXPERT_VM_OUTPUT_PREF_HEIGHT : NORMAL_VM_OUTPUT_PREF_HEIGHT);
    }

    private void configureUiActionLogging() {
        fileMenuButton.setOnShowing(event -> JaverLogger.info("Menu opened."));

        logCheckBoxChanges(compilerDumpLexerOption, "Dump Lexer");
        logCheckBoxChanges(compilerDumpAstOption, "Dump AST");
        logCheckBoxChanges(compilerDumpSymbolTableOption, "Dump Symbol Table");
        logCheckBoxChanges(compilerLoggingOption, "Compiler logging");
        logCheckBoxChanges(vmDumpOnErrorOption, "VM dump on runtime error");

        expertModeOption.selectedProperty().addListener((observable, wasSelected, selected) ->
                JaverLogger.info("Expert Mode set to " + onOff(selected) + "."));
        vmStackSizeValueOption.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                JaverLogger.info("VM stack size set to " + newValue + selectedStackSizeUnit() + ".");
            }
        });
        vmStackSizeKbOption.selectedProperty().addListener((observable, wasSelected, selected) -> {
            if (Boolean.TRUE.equals(selected)) {
                JaverLogger.info("VM stack size unit set to KB.");
            }
        });
        vmStackSizeMbOption.selectedProperty().addListener((observable, wasSelected, selected) -> {
            if (Boolean.TRUE.equals(selected)) {
                JaverLogger.info("VM stack size unit set to MB.");
            }
        });
    }

    private void logCheckBoxChanges(CheckBox checkBox, String label) {
        checkBox.selectedProperty().addListener((observable, wasSelected, selected) ->
                JaverLogger.info(label + " set to " + onOff(selected) + "."));
    }

    private String onOff(boolean selected) {
        return selected ? "On" : "Off";
    }

    private void configureSourceLineNumbers() {
        updateSourceLineNumbers(consoleInput.getText());
        Platform.runLater(this::hideSourceLineNumberScrollBars);
        consoleInput.textProperty().addListener((observable, oldText, newText) -> updateSourceLineNumbers(newText));
        consoleInput.scrollTopProperty().addListener((observable, oldValue, newValue) ->
                syncSourceLineNumberScroll());
    }

    private void hideSourceLineNumberScrollBars() {
        consoleInputLineNumbers.lookupAll(".scroll-bar").stream()
                .filter(ScrollBar.class::isInstance)
                .map(ScrollBar.class::cast)
                .forEach(scrollBar -> {
                    scrollBar.setVisible(false);
                    scrollBar.setManaged(false);
                    scrollBar.setOpacity(0.0);
                });
    }

    private void updateSourceLineNumbers(String sourceText) {
        consoleInputLineNumbers.setText(buildLineNumbers(countSourceLines(sourceText)));
        syncSourceLineNumberScroll();
    }

    private void syncSourceLineNumberScroll() {
        consoleInputLineNumbers.setScrollTop(consoleInput.getScrollTop());
        consoleInputLineNumbers.setScrollLeft(0.0);
    }

    private int countSourceLines(String sourceText) {
        if (sourceText == null || sourceText.isEmpty()) {
            return 1;
        }

        return sourceText.split("\\R", -1).length;
    }

    private String buildLineNumbers(int lineCount) {
        StringBuilder lineNumbers = new StringBuilder(lineCount * 4);
        for (int line = 1; line <= lineCount; line++) {
            if (line > 1) {
                lineNumbers.append('\n');
            }
            lineNumbers.append(line);
        }
        return lineNumbers.toString();
    }

    private void startVmAfterSuccessfulCompilation(
            ManagedProcessRunner.ProcessResult compilerResult,
            List<String> vmCommand
    ) {
        if (!compilerResult.isSuccess()) {
            JaverLogger.warning("Compiler did not finish successfully. VM will not be started.");
            return;
        }

        if (!isBytecodeFileReady()) {
            JaverLogger.error("Compiler finished, but the VM input file is missing or empty: "
                    + vmInputFile.toAbsolutePath());
            return;
        }

        if (vmRunner.isRunning()) {
            JaverLogger.warning(VM_IS_ALREADY_RUNNING);
            return;
        }

        logCommand("VM", vmCommand);
        if (vmRunner.start(vmCommand).isEmpty()) {
            JaverLogger.warning(VM_IS_ALREADY_RUNNING);
        }
    }

    private boolean isBytecodeFileReady() {
        try {
            return Files.isRegularFile(vmInputFile) && Files.size(vmInputFile) > 0;
        } catch (IOException exception) {
            JaverLogger.error("Failed to inspect VM input file: " + exception.getMessage());
            return false;
        }
    }

    private String writeInputToFile(String input) {
        String content = input == null ? "" : input;

        try {
            Files.writeString(
                    consoleInputFile,
                    content,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            JaverLogger.info("Created source input file: " + consoleInputFile.toAbsolutePath());
            return consoleInputFile.toAbsolutePath().toString();
        } catch (IOException exception) {
            JaverLogger.error("Failed to write source input file: " + exception.getMessage());
            return null;
        }
    }

    private void saveTextFile(Path targetPath, String text, String label) {
        Path absoluteTargetPath = targetPath.toAbsolutePath().normalize();
        try {
            Path parent = absoluteTargetPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(
                    absoluteTargetPath,
                    text == null ? "" : text,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            JaverLogger.info("Saved " + label + " as: " + absoluteTargetPath);
        } catch (IOException exception) {
            JaverLogger.error("Failed to save " + label + ": " + exception.getMessage());
        }
    }

    private void copyFile(Path sourcePath, Path targetPath, String successMessage, String failureMessage) {
        try {
            Path absoluteSourcePath = sourcePath.toAbsolutePath().normalize();
            Path absoluteTargetPath = targetPath.toAbsolutePath().normalize();
            Path parent = absoluteTargetPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(absoluteTargetPath) || !Files.isSameFile(absoluteSourcePath, absoluteTargetPath)) {
                Files.copy(absoluteSourcePath, absoluteTargetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            JaverLogger.info(successMessage + absoluteTargetPath);
        } catch (IOException exception) {
            JaverLogger.error(failureMessage + exception.getMessage());
        }
    }

    private File showOpenDialog(String title, String filterDescription, String extension) {
        return createFileChooser(title, filterDescription, extension).showOpenDialog(ownerWindow());
    }

    private File showSaveDialog(String title, String initialFileName, String filterDescription, String extension) {
        FileChooser fileChooser = createFileChooser(title, filterDescription, extension);
        fileChooser.setInitialFileName(initialFileName);
        return fileChooser.showSaveDialog(ownerWindow());
    }

    private FileChooser createFileChooser(String title, String filterDescription, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        defaultFileChooserDirectory().ifPresent(fileChooser::setInitialDirectory);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                filterDescription,
                "*" + extension
        ));
        return fileChooser;
    }

    private Optional<File> defaultFileChooserDirectory() {
        Path directory = runtimeDirectory.toAbsolutePath().normalize();
        if (Files.isDirectory(directory)) {
            return Optional.of(directory.toFile());
        }

        Path workingDirectory = Path.of(System.getProperty(USER_DIR)).toAbsolutePath().normalize();
        if (Files.isDirectory(workingDirectory)) {
            return Optional.of(workingDirectory.toFile());
        }

        return Optional.empty();
    }

    private Window ownerWindow() {
        return consoleInput.getScene() == null ? null : consoleInput.getScene().getWindow();
    }

    private Path ensureExtension(Path path, String extension) {
        if (hasExtension(path, extension)) {
            return path;
        }

        return path.resolveSibling(path.getFileName() + extension);
    }

    private boolean hasExtension(Path path, String extension) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(extension.toLowerCase(Locale.ROOT));
    }

    private boolean deleteFileIfExists(Path path, String label) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.deleteIfExists(path)) {
                JaverLogger.info("Deleted previous " + label + ": " + path.toAbsolutePath());
            }
            return true;
        } catch (IOException e) {
            JaverLogger.error("Failed to delete previous " + label + ": " + e.getMessage());
            return false;
        }
    }

    private void deleteRuntimeFileIfExists(Path path, String label) {
        try {
            if (Files.deleteIfExists(path)) {
                JaverLogger.info("Deleted " + label + ": " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            JaverLogger.warning("Failed to delete " + label + ": " + e.getMessage());
        }
    }

    private Path resolveReleaseExecutable(Path relativePath, String label) {
        Path path = runtimeDirectory.resolve(relativePath).toAbsolutePath().normalize();

        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            JaverLogger.debug(label + " packaged executable not found at: " + path);
            return null;
        }

        JaverLogger.info("Resolved " + label + " executable to: " + path);
        return path;
    }

    private Path resolveJarFromProperty(String propertyName) {
        String value = System.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            JaverLogger.error("Missing system property: " + propertyName);
            JaverLogger.error("Please ensure JarConfigLoader.loadConfiguration() is called at startup or set the property manually.");
            return null;
        }

        Path path = Path.of(value).toAbsolutePath().normalize();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            JaverLogger.error("Configured jar does not exist: " + path);
            JaverLogger.error("Make sure the jar file is present at the expected location.");
            JaverLogger.error("Property '" + propertyName + "' is set to: " + value);
            return null;
        }

        JaverLogger.info("Resolved " + propertyName + " to: " + path.toAbsolutePath());
        return path;
    }

    private void logCommand(String processName, List<String> command) {
        JaverLogger.info("Starting " + processName + " with command:");
        for (String part : command) {
            JaverLogger.info("  " + part);
        }
    }

    private void updateCompilerButtons(boolean running) {
        Platform.runLater(() -> {
            runCompilerButton.setDisable(running);
            stopCompilerButton.setDisable(!running);
            updateRunCompilerAndVMButtonState();
        });
    }

    private void updateVMButtons(boolean running) {
        Platform.runLater(() -> {
            runVMButton.setDisable(running);
            stopVMButton.setDisable(!running);
            updateRunCompilerAndVMButtonState();
        });
    }

    private void updateRunCompilerAndVMButtonState() {
        runCompilerAndVMButton.setDisable(isRunnerRunning(compilerRunner) || isRunnerRunning(vmRunner));
    }

    private boolean isRunnerRunning(ManagedProcessRunner runner) {
        return runner != null && runner.isRunning();
    }

    private void appendCompilerOutput(String text) {
        Platform.runLater(() -> compilerOutput.appendText(text));
    }

    private void appendVMOutput(String text) {
        Platform.runLater(() -> virtualMachineOutput.appendText(text));
    }

    private void appendStatus(String text) {
        Platform.runLater(() -> {
            statusOutput.appendText(text);
            statusOutput.setScrollTop(Double.MAX_VALUE);
        });
    }
}