package ch.zhaw.it.pm4.javer.vm;

import java.util.Arrays;
import java.util.List;

public class Instruction {
    private OPCode operationCode;
    private List<Operand> operands;

    public Instruction(OPCode operationCode, Operand ... operands) {
        this.operationCode = operationCode;
        this.operands = Arrays.stream(operands).toList();
    }

    public OPCode getOperationCode() {
        return operationCode;
    }

    public List<Operand> getOperands() {
        return operands;
    }



}
