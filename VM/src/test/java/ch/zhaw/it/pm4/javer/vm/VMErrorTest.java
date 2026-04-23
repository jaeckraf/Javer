package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VMErrorTest {

    @Test
    void gettersReturnConstructorValues() {
        Instruction instruction = new Instruction(OPCode.HALT);
        VMError error = new VMError("Boom", 7, instruction);

        assertEquals("Boom", error.getMessage());
        assertEquals(7, error.getProgramCounter());
        assertEquals(instruction, error.getInstruction());
    }

    @Test
    void toStringContainsCoreFields() {
        VMError error = new VMError("Boom", 7, new Instruction(OPCode.HALT));

        assertEquals("VMError{message='Boom', programCounter=7, instruction=HALT}", error.toString());
    }
}

