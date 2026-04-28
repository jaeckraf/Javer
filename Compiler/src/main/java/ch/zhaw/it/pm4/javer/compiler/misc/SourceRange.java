package ch.zhaw.it.pm4.javer.compiler.misc;

public record SourceRange(SourceLocation start, SourceLocation end) {

    public static final SourceRange UNKNOWN = new SourceRange(
            new SourceLocation(1, 1, 1),
            new SourceLocation(1, 1, 1));

    @Override
    public String toString() {
        return "[%d:%d][%d:%d]".formatted(
                start.lineNumber(),
                start.startColumn(),
                end.lineNumber(),
                end.endColumn());
    }
}
