package ch.zhaw.it.pm4.javer.vm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VMTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    private String getOutput() {
        return outContent.toString().trim().replace("\r\n", "\n");
    }

    private VM createVMWithCode(String code) throws IOException {
        Path tempFile = Files.createTempFile("vmtest", ".txt");
        Files.writeString(tempFile, code);
        return new VM(tempFile.toAbsolutePath().toString());
    }

    @Test
    void runBasicMath() throws IOException {
        String code = """
                code:
                PUSHCONST 10
                PUSHCONST 5
                ADD
                PRINT
                HALT
                """;
        VM vm = createVMWithCode(code);
        assertEquals("Execution finished.", vm.run());
        assertEquals("15", getOutput());
    }

    @Test
    void runDataSectionAndMemory() throws IOException {
        String code = """
                data:
                42
                
                code:
                PUSHCONST 0
                HLOAD X 0
                PRINT
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("42", getOutput());
    }

    @Test
    void testBranching() throws IOException {
        String correctCode = """
                code:
                PUSHCONST 1
                JT 6
                PUSHCONST 99
                PRINT
                HALT
                PUSHCONST 42
                PRINT
                HALT
                """;
        VM vm2 = createVMWithCode(correctCode);
        vm2.run();
        assertEquals("42", getOutput());
    }

    @Test
    void testFunctionCall() throws IOException {
        String code = """
                code:
                CALL 3
                HALT
                PUSHCONST 123
                PRINT
                RET
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("123", getOutput());
    }

    @Test
    void testPrintChar() throws IOException {
        String code = """
                code:
                PUSHCONST 72
                PRINTC
                PUSHCONST 105
                PRINTC
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("Hi", getOutput());
    }

    @Test
    void testStackOperations() throws IOException {
        String code = """
                code:
                PUSH 10
                PUSHCONST 20
                POP
                PRINT
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("10", getOutput());
    }

    @Test
    void testIntegerMathAndBitwise() throws IOException {
        String code = """
                code:
                PUSHCONST 10
                PUSHCONST 4
                SUB
                PRINT
                PUSHCONST 10
                PUSHCONST 4
                MUL
                PRINT
                PUSHCONST 10
                PUSHCONST 4
                DIV
                PRINT
                PUSHCONST 10
                PUSHCONST 4
                MOD
                PRINT
                PUSHCONST 16
                PUSHCONST 2
                SHIFTR
                PRINT
                PUSHCONST 2
                PUSHCONST 3
                SHIFTL
                PRINT
                PUSHCONST 12
                PUSHCONST 10
                AND
                PRINT
                PUSHCONST 12
                PUSHCONST 10
                OR
                PRINT
                PUSHCONST 12
                PUSHCONST 10
                XOR
                PRINT
                PUSHCONST 12
                BITWISEINVERT
                PRINT
                PUSHCONST 42
                NEGATE
                PRINT
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // SUB: 10 - 4 = 6
        // MUL: 10 * 4 = 40
        // DIV: 10 / 4 = 2
        // MOD: 10 % 4 = 2
        // SHIFTR: 16 >> 2 = 4
        // SHIFTL: 2 << 3 = 16
        // AND: 12 (1100) & 10 (1010) = 8 (1000)
        // OR: 12 | 10 = 14 (1110)
        // XOR: 12 ^ 10 = 6 (0110)
        // BITWISEINVERT: ~12 = -13
        // NEGATE: -42
        String expected = "6\n40\n2\n2\n4\n16\n8\n14\n6\n-13\n-42";
        assertEquals(expected, getOutput());
    }

    @Test
    void testRelationalAndEquality() throws IOException {
        String code = """
                code:
                PUSHCONST 5
                PUSHCONST 10
                LT
                PRINT
                PUSHCONST 10
                PUSHCONST 5
                LE
                PRINT
                PUSHCONST 10
                PUSHCONST 5
                GT
                PRINT
                PUSHCONST 5
                PUSHCONST 10
                GE
                PRINT
                PUSHCONST 5
                PUSHCONST 5
                COMPARE
                PRINT
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // 5 < 10 -> 1
        // 10 <= 5 -> 0
        // 10 > 5 -> 1
        // 5 >= 10 -> 0
        // 5 == 5 -> 1
        assertEquals("1\n0\n1\n0\n1", getOutput());
    }

    @Test
    void testFloatingPointMath() throws IOException {
        String code = """
                code:
                PUSHCONST 2.5
                PUSHCONST 1.5
                ADDF
                PRINT
                PUSHCONST 5.5
                PUSHCONST 2.0
                SUBF
                PRINT
                PUSHCONST 2.0
                PUSHCONST 3.5
                MULF
                PRINT
                PUSHCONST 5.0
                PUSHCONST 2.0
                DIVF
                PRINT
                PUSHCONST 3.14
                NEGATEF
                PRINT
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // ADDF: 2.5 + 1.5 = 4.0
        // SUBF: 5.5 - 2.0 = 3.5
        // MULF: 2.0 * 3.5 = 7.0
        // DIVF: 5.0 / 2.0 = 2.5
        // NEGATEF: -3.14
        String expected = "4.0\n3.5\n7.0\n2.5\n-3.14";
        assertEquals(expected, getOutput());
    }

    @Test
    void testOtherBranching() throws IOException {
        String code = """
                code:
                JMP 4
                PUSHCONST 99
                PRINT
                PUSHCONST 0
                JF 8
                PUSHCONST 88
                PRINT
                PUSHCONST 42
                PRINT
                HALT
                """;
        // Line 1: JMP 4 -> Jumps to Line 4 (PUSHCONST 0)
        // Line 2: PUSHCONST 99 (skipped)
        // Line 3: PRINT (skipped)
        // Line 4: PUSHCONST 0
        // Line 5: JF 8 -> Pops 0 (false), jumps to Line 8
        // Line 6: PUSHCONST 88 (skipped)
        // Line 7: PRINT (skipped)
        // Line 8: PUSHCONST 42
        // Line 9: PRINT (prints 42)
        // Line 10: HALT
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("42", getOutput());
    }

    @Test
    void testHeapMemoryNewAndStore() throws IOException {
        String code = """
                code:
                PUSHCONST 3
                NEW
                PUSHCONST 0
                PUSHCONST 77
                HSTORE X 1
                PUSHCONST 0
                HLOAD X 1
                PRINT
                HALT
                """;
        // PUSHCONST 3
        // NEW (pops 3, pushes heap index 0)
        // PUSHCONST 0
        // PUSHCONST 77
        // HSTORE X 1 (pops value 77, index 0, stores at offset 1)
        // PUSHCONST 0
        // HLOAD X 1 (pops index 0, loads from offset 1, pushes 77)
        // PRINT -> 77
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("77", getOutput());
    }

    @Test
    void testDivisionByZeroInteger() throws IOException {
        String code = """
                code:
                PUSHCONST 10
                PUSHCONST 0
                DIV
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // Fails safely and prints the exact exception message
        assertEquals("Runtime Error at line 3: Division by zero", getOutput());
    }

    @Test
    void testModuloByZeroInteger() throws IOException {
        String code = """
                code:
                PUSHCONST 10
                PUSHCONST 0
                MOD
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("Runtime Error at line 3: Modulo by zero", getOutput());
    }

    @Test
    void testDivisionByZeroFloat() throws IOException {
        String code = """
                code:
                PUSHCONST 5.0
                PUSHCONST 0.0
                DIVF
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("Runtime Error at line 3: Division by zero", getOutput());
    }

    @Test
    void testUnknownInstruction() throws IOException {
        String code = """
                code:
                FAKEINSTRUCTION
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("Unknown instruction at line 1: FAKEINSTRUCTION", getOutput());
    }

    @Test
    void testInvalidJumpTarget() throws IOException {
        String code = """
                code:
                JMP 999
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        assertEquals("Runtime Error at line 1: Jump target out of bounds: line 999", getOutput());
    }

    @Test
    void testMissingReturnAddress() throws IOException {
        String code = """
                code:
                RET
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // Updated to expect the new standard VMRuntimeException format!
        assertEquals("Runtime Error at line 1: Stack underflow on RET: Missing return address", getOutput());
    }

    @Test
    void testStackUnderflowPop() throws IOException {
        String code = """
                code:
                POP
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // POP is on line 1, stack is empty
        assertEquals("Runtime Error at line 1: Stack underflow on POP", getOutput());
    }

    @Test
    void testStackUnderflowBinaryOp() throws IOException {
        String code = """
                code:
                PUSHCONST 10
                ADD
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // ADD is on line 2, but there is only 1 item on the stack
        assertEquals("Runtime Error at line 2: Stack underflow: Not enough operands for binary operation.", getOutput());
    }

    @Test
    void testStackUnderflowFloatBinaryOp() throws IOException {
        String code = """
                code:
                ADDF
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // ADDF is on line 1, stack is empty
        assertEquals("Runtime Error at line 1: Stack underflow: Not enough operands for float binary operation.", getOutput());
    }

    @Test
    void testStackUnderflowCondition() throws IOException {
        String code = """
                code:
                JT 5
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // JT expects a boolean/number to evaluate, but stack is empty
        assertEquals("Runtime Error at line 1: Stack underflow during condition check", getOutput());
    }

    @Test
    void testTypeMismatchPrintc() throws IOException {
        String code = """
                code:
                PUSHCONST "Hello"
                PRINTC
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // PRINTC expects a Number to convert to a char, not a String
        assertEquals("Runtime Error at line 2: Type mismatch: PRINTC expects a Number", getOutput());
    }

    @Test
    void testTypeMismatchNew() throws IOException {
        String code = """
                code:
                PUSHCONST "Five"
                NEW
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // NEW expects a Number for the array size
        assertEquals("Runtime Error at line 2: Type mismatch: NEW expects a Number for size", getOutput());
    }

    @Test
    void testTypeMismatchHStore() throws IOException {
        String code = """
                code:
                PUSHCONST 5
                NEW
                PUSHCONST "InvalidPointer"
                PUSHCONST 42
                HSTORE X 0
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // The pointer index pushed before the value is a String, not a Number
        assertEquals("Runtime Error at line 5: Type mismatch: HSTORE expects Numbers for pointer and value", getOutput());
    }

    @Test
    void testTypeMismatchRet() throws IOException {
        String code = """
                code:
                PUSHCONST 3.14
                RET
                HALT
                """;
        VM vm = createVMWithCode(code);
        vm.run();
        // RET expects an Integer (Program Counter address), not a Float
        assertEquals("Runtime Error at line 2: Type mismatch: Return address on stack is not an Integer", getOutput());
    }
}
