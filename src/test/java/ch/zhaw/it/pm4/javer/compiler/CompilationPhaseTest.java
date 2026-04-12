package ch.zhaw.it.pm4.javer.compiler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilationPhaseTest {

    @Test
    void enumContainsExpectedPhases() {
        assertEquals(CompilationPhase.ARGUMENT_PARSING, CompilationPhase.valueOf("ARGUMENT_PARSING"));
        assertEquals(CompilationPhase.DONE, CompilationPhase.valueOf("DONE"));
        assertTrue(CompilationPhase.values().length >= 5);
    }
}

