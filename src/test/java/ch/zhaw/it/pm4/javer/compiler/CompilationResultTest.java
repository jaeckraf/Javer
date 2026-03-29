package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CompilationResultTest {

    @Test
    void record_StoresValuesCorrectly() {
        List<String> errors = List.of("Error 1", "Error 2");
        CompilationUnitParseNode tree = new CompilationUnitParseNode();
        
        CompilationResult result = new CompilationResult(false, errors, tree);
        
        assertFalse(result.success());
        assertEquals(errors, result.errors());
        assertEquals(tree, result.syntaxTree());
    }

    @Test
    void errorsList_IsUnmodifiable() {
        CompilationResult result = new CompilationResult(true, List.of("foo"), null);
        
        assertThrows(UnsupportedOperationException.class, () -> result.errors().add("bar"));
    }
}
