package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OperandTest {

    @Test
    void getValueReturnsConstructorValue() {
        Operand<Integer> operand = new Operand<>(42);

        assertEquals(42, operand.getValue());
    }
}

