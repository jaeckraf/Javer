package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilationContextTest {

    @Test
    void gettersAndSettersExposeContextState() throws IOException {
        Path input = Files.createTempFile("javer-compilation-context", ".jv");
        Files.writeString(input, "");

        CompilerOptions options = CompilerOptions.create(input.toString(), "out.bin");
        DiagnosticBag diagnostics = new DiagnosticBag(input.toString(), 5, CompilationPhase.COMPILER_SETUP);
        SourceCache sourceCache = new SourceCache(input.toString());

        CompilationContext context = new CompilationContext(options, diagnostics, sourceCache);
        assertEquals(options, context.getOptions());
        assertEquals(diagnostics, context.getDiagnosticBag());
        assertEquals(sourceCache, context.getSourceCache());

        CompilerOptions otherOptions = CompilerOptions.create(input.toString(), "next.bin");
        DiagnosticBag otherDiagnostics = new DiagnosticBag(input.toString(), 1, CompilationPhase.LEXING);
        SourceCache otherSourceCache = new SourceCache(input.toString());

        context.setOptions(otherOptions);
        context.setDiagnostics(otherDiagnostics);
        context.setSourceCache(otherSourceCache);

        assertEquals(otherOptions, context.getOptions());
        assertEquals(otherDiagnostics, context.getDiagnosticBag());
        assertEquals(otherSourceCache, context.getSourceCache());

        Files.deleteIfExists(input);
    }
}

