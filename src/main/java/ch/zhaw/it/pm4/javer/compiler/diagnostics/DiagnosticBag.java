package ch.zhaw.it.pm4.javer.compiler.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects and stores diagnostic information (errors/warnings) during compilation.
 */
public class DiagnosticBag {
    private final List<String> errors = new ArrayList<>();

    public void reportError(Token token, String errorMessage) {
        String message = String.format("Error at %s: %s", token.getTokenType(), errorMessage);
        errors.add(message);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void clear() {
        errors.clear();
    }
}
