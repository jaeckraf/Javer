package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class VMTest {

    @Test
    void runStopsOnHaltInstruction() {
        BytecodeLoader mockLoader = Mockito.mock(BytecodeLoader.class);
        when(mockLoader.getInstructions()).thenReturn(List.of(
                new Instruction(OPCode.HALT)
        ));
        VM vm = new VM(mockLoader);
        assertEquals("Successfully executed program", vm.run());
    }

    @Test
    void runHandlesWhileLoop() {
        BytecodeLoader mockLoader = Mockito.mock(BytecodeLoader.class);
        when(mockLoader.getInstructions()).thenReturn(List.of(
                new Instruction(OPCode.WHILE_START),
                new Instruction(OPCode.WHILE_END),
                new Instruction(OPCode.HALT)
        ));
        VM vm = new VM(mockLoader);
        assertEquals("Successfully executed program", vm.run());
    }

    @Test
    void runHandlesForLoop() {
        BytecodeLoader mockLoader = Mockito.mock(BytecodeLoader.class);
        when(mockLoader.getInstructions()).thenReturn(List.of(
                new Instruction(OPCode.FOR_START),
                new Instruction(OPCode.FOR_END),
                new Instruction(OPCode.HALT)
        ));
        VM vm = new VM(mockLoader);
        assertEquals("Successfully executed program", vm.run());
    }

    @Test
    void runHandlesNestedLoops() {
        BytecodeLoader mockLoader = Mockito.mock(BytecodeLoader.class);
        when(mockLoader.getInstructions()).thenReturn(List.of(
                new Instruction(OPCode.WHILE_START),
                new Instruction(OPCode.FOR_START),
                new Instruction(OPCode.FOR_END),
                new Instruction(OPCode.WHILE_END),
                new Instruction(OPCode.HALT)
        ));
        VM vm = new VM(mockLoader);
        assertEquals("Successfully executed program", vm.run());
    }
}