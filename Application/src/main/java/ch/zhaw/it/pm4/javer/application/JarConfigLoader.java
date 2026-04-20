package ch.zhaw.it.pm4.javer.application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Loads JAR configuration from application.properties and sets system properties.
 * This class handles the initialization of JAR paths for Compiler and VM.
 */
public class JarConfigLoader {
    private static final Logger LOGGER = Logger.getLogger(JarConfigLoader.class.getName());
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String COMPILER_JAR_PROPERTY = "javer.compiler.jar";
    private static final String VM_JAR_PROPERTY = "javer.vm.jar";

    /**
     * Loads JAR configuration from application.properties and sets system properties.
     * Tries multiple fallback strategies if properties are not found.
     */
    public static void loadConfiguration() {
        try {
            Properties props = loadPropertiesFromClasspath();
            
            if (props != null) {
                setSystemProperties(props);
                logLoadedConfiguration();
            } else {
                LOGGER.warning("Could not load application.properties from classpath. " +
                        "JAR paths must be configured via system properties.");
            }
        } catch (Exception e) {
            LOGGER.severe("Error loading JAR configuration: " + e.getMessage());
        }
    }

    /**
     * Loads properties from application.properties in the classpath.
     */
    private static Properties loadPropertiesFromClasspath() throws IOException {
        try (InputStream is = JarConfigLoader.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (is == null) {
                LOGGER.warning("application.properties not found in classpath");
                return null;
            }
            
            Properties props = new Properties();
            props.load(is);
            return props;
        }
    }

    /**
     * Sets system properties from loaded configuration, applying transformations
     * for different build environments (development vs. packaged).
     */
    private static void setSystemProperties(Properties props) {
        String compilerJarPath = props.getProperty(COMPILER_JAR_PROPERTY);
        String vmJarPath = props.getProperty(VM_JAR_PROPERTY);

        if (compilerJarPath != null && !compilerJarPath.isBlank()) {
            String resolvedPath = resolveJarPath(compilerJarPath);
            System.setProperty(COMPILER_JAR_PROPERTY, resolvedPath);
        }

        if (vmJarPath != null && !vmJarPath.isBlank()) {
            String resolvedPath = resolveJarPath(vmJarPath);
            System.setProperty(VM_JAR_PROPERTY, resolvedPath);
        }
    }

    /**
     * Resolves JAR paths, handling both relative and absolute paths.
     * For development: resolves relative to current working directory.
     * For packaged apps: resolves relative to application installation directory.
     */
    private static String resolveJarPath(String jarPath) {
        if (jarPath == null || jarPath.isBlank()) {
            return jarPath;
        }

        Path path = Path.of(jarPath);
        
        // If already absolute and exists, use it as-is
        if (path.isAbsolute() && Files.exists(path)) {
            return path.toAbsolutePath().toString();
        }

        // Try relative to current working directory (development mode)
        Path cwdRelative = Path.of(System.getProperty("user.dir")).resolve(jarPath);
        if (Files.exists(cwdRelative)) {
            return cwdRelative.toAbsolutePath().toString();
        }

        // Try relative to application code location (packaged mode)
        try {
            Path appBase = getApplicationBaseDirectory();
            Path appRelative = appBase.resolve(jarPath);
            if (Files.exists(appRelative)) {
                return appRelative.toAbsolutePath().toString();
            }
        } catch (Exception e) {
            LOGGER.fine("Could not resolve path relative to application base: " + e.getMessage());
        }

        // Return the original path if no resolution was possible
        // GuiController will handle the error
        return jarPath;
    }

    /**
     * Determines the application base directory.
     * For JAR-based applications, this is the directory containing the JAR.
     * For IDE runs, this falls back to the working directory.
     */
    private static Path getApplicationBaseDirectory() throws Exception {
        String codeSourceLocation = JarConfigLoader.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI().getPath();
        
        Path codePath = Path.of(codeSourceLocation);
        
        if (Files.isRegularFile(codePath)) {
            // Running from JAR - return parent directory
            return codePath.getParent();
        } else if (Files.isDirectory(codePath)) {
            // Running from IDE - return code path
            return codePath;
        }
        
        // Fallback to working directory
        return Path.of(System.getProperty("user.dir"));
    }

    /**
     * Logs the configuration that was loaded.
     */
    private static void logLoadedConfiguration() {
        String compilerJar = System.getProperty(COMPILER_JAR_PROPERTY);
        String vmJar = System.getProperty(VM_JAR_PROPERTY);
        
        LOGGER.info("JAR Configuration loaded:");
        LOGGER.info("  Compiler JAR: " + (compilerJar != null ? compilerJar : "not set"));
        LOGGER.info("  VM JAR: " + (vmJar != null ? vmJar : "not set"));
    }
}

