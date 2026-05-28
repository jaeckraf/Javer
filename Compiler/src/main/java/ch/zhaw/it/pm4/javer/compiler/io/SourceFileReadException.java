package ch.zhaw.it.pm4.javer.compiler.io;

public class SourceFileReadException extends RuntimeException {

    public SourceFileReadException(String filePath, Throwable cause) {
        super("Failed to read source file: " + filePath, cause);
    }
}