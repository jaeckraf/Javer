package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.LiteralKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ReturnStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;
import ch.zhaw.it.pm4.javer.vm.Instruction;
import ch.zhaw.it.pm4.javer.vm.OPCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeGeneratorTest {

    @Test
    void functionWithLiteralReturn_generatesPushAndHalt() {
        ReturnStatement returnStatement = ReturnStatement
                .builder(new LiteralExpression<>(LiteralKind.INT, 42))
                .build();
        BlockStatement body = new BlockStatement(List.of(returnStatement));
        FunctionDeclaration function = new FunctionDeclaration(new VoidType(), "main", List.of(), body);
        CompilationUnit unit = new CompilationUnit(List.of(function));

        List<Instruction> instructions = new CodeGenerator().visit(unit);

        assertEquals(2, instructions.size());
        assertEquals(OPCode.PUSH, instructions.getFirst().getOperationCode());
        assertEquals(42, instructions.getFirst().getOperands().getFirst().getValue());
        assertEquals(OPCode.HALT, instructions.get(1).getOperationCode());
    }

    @Test
    void functionWithoutReturn_generatesImplicitHalt() {
        BlockStatement body = new BlockStatement(List.of());
        FunctionDeclaration function = new FunctionDeclaration(new VoidType(), "main", List.of(), body);
        CompilationUnit unit = new CompilationUnit(List.of(function));

        List<Instruction> instructions = new CodeGenerator().visit(unit);

        assertEquals(1, instructions.size());
        assertEquals(OPCode.HALT, instructions.getFirst().getOperationCode());
    }
}

