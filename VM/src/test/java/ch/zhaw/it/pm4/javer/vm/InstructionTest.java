package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InstructionTest {

    @Test
    void constructorSetsOpCodeAndOperands() {
        Operand<Integer> left = new Operand<>(1);
        Operand<Integer> right = new Operand<>(2);

        Instruction instruction = new Instruction(OPCode.ADD, left, right);

        assertEquals(OPCode.ADD, instruction.getOperationCode());
        assertEquals(2, instruction.getOperands().size());
        assertEquals(left, instruction.getOperands().get(0));
        assertEquals(right, instruction.getOperands().get(1));
    }

    @Test
    void operandsListIsUnmodifiable() {
        Instruction instruction = new Instruction(OPCode.HALT);

        assertThrows(UnsupportedOperationException.class, () ->
                instruction.getOperands().add(new Operand<>(1))
        );
    }
}

