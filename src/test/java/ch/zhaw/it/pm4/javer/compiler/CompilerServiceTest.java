package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompilerServiceTest {

    private CompilerService compilerService;

    @BeforeEach
    void setUp() {
        compilerService = new CompilerService();
    }

    @Test
    void compile_NullInput_ReturnsSuccessfulEmptyResult() {
        CompilationResult result = compilerService.compile(null);
        
        assertTrue(result.success());
        assertTrue(result.errors().isEmpty());
        assertNotNull(result.syntaxTree());
    }

    @Test
    void compile_BlankInput_ReturnsSuccessfulEmptyResult() {
        CompilationResult result = compilerService.compile("   ");
        
        assertTrue(result.success());
        assertTrue(result.errors().isEmpty());
        assertNotNull(result.syntaxTree());
    }

    @Test
    void compile_StubInput_ReturnsSuccessfulResult() {
        // Since we currently have a stub, any input is "successful" (produces EOF and passes)
        CompilationResult result = compilerService.compile("let x = 5;");
        
        assertTrue(result.success());
        assertTrue(result.errors().isEmpty());
        assertNotNull(result.syntaxTree());
    }
}
