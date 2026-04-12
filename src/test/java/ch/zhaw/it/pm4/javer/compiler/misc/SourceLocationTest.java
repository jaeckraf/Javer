package ch.zhaw.it.pm4.javer.compiler.misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SourceLocationTest {

    @Test
    void recordExposesCoordinatesAndFormatsToString() {
        SourceLocation location = new SourceLocation(2, 8, 3);

        assertEquals(2, location.startColumn());
        assertEquals(8, location.endColumn());
        assertEquals(3, location.lineNumber());
        assertEquals("[2 : 8 : 3]", location.toString());
    }
}

