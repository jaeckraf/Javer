package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VMTest {

    @Test
    void runStopsOnHaltInstruction() {
        VM vm = new VM(new BytecodeLoader("ignored-path"));

        assertEquals("Successfully executed program", vm.run());
    }
}

