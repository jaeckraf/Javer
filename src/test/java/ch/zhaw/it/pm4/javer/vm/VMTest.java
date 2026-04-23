package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VMTest {

    private VM createVmWithProgram(List<Instruction> program) {
        BytecodeLoader loader = new BytecodeLoader("ignored-path") {
            @Override
            public List<Instruction> getInstructions() {
                return program;
            }
        };
        return new VM(loader);
    }

    @Test
    void runStopsOnHaltInstruction() {
        VM vm = new VM(new BytecodeLoader("ignored-path"));

        assertEquals("Successfully executed program", vm.run());
    }

    @Test
    void runHandlesPushThenHalt() {
        VM vm = createVmWithProgram(List.of(
                new Instruction(OPCode.PUSH, new Operand<>(42)),
                new Instruction(OPCode.HALT)
        ));

        assertEquals("Successfully executed program", vm.run());
    }

    @Test
    void runHandlesPushThenReturn() {
        VM vm = createVmWithProgram(List.of(
                new Instruction(OPCode.PUSH, new Operand<>(42)),
                new Instruction(OPCode.RETURN)
        ));

        assertEquals("Successfully executed program", vm.run());
    }

    @Test
    void runReturnsErrorOnUnknownOpcode() {
        VM vm = createVmWithProgram(List.of(new Instruction(OPCode.ADD)));

        String result = vm.run();
        assertTrue(result.contains("Unknown OPCode: ADD"));
    }
}

