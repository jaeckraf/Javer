package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeverityTest {

    @Test
    void enumOrderAndNamesStayStable() {
        assertEquals(Severity.INFO, Severity.valueOf("INFO"));
        assertEquals(Severity.SEVERE, Severity.valueOf("SEVERE"));
        assertEquals(4, Severity.values().length);
    }
}

