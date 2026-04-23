package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiagnosticTest {

    @Test
    void gettersAndToStringReflectConstructorArguments() {
        SourceLocation location = new SourceLocation(1, 4, 2);
        Diagnostic diagnostic = new Diagnostic(location, Severity.ERROR, "Type mismatch");

        assertEquals(location, diagnostic.getLocation());
        assertEquals(Severity.ERROR, diagnostic.getSeverity());
        assertEquals("[ERROR] at [1 : 4 : 2]: Type mismatch", diagnostic.toString());
    }
}

