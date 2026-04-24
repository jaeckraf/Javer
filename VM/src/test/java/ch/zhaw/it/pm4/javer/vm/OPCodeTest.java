package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OPCodeTest {

    @Test
    void enumContainsExpectedOperationCodes() {
        assertNotNull(OPCode.valueOf("HALT"));
        assertNotNull(OPCode.valueOf("RETURN"));
        assertNotNull(OPCode.valueOf("ADD"));
        assertNotNull(OPCode.valueOf("NEW"));
        assertTrue(OPCode.values().length >= 10);
    }

    @Test
    void valueOfResolvesEveryEnumName() {
        for (OPCode code : OPCode.values()) {
            assertEquals(code, OPCode.valueOf(code.name()));
        }
    }
}

