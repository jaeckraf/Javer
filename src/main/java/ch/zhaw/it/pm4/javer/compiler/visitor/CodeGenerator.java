package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;
import ch.zhaw.it.pm4.javer.vm.Instruction;
import ch.zhaw.it.pm4.javer.vm.OPCode;
import ch.zhaw.it.pm4.javer.vm.Operand;

import java.util.ArrayList;
import java.util.List;

@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends AstNodeVisitorBase<List<Instruction>> {

    private final List<Instruction> instructions = new ArrayList<>();

    public List<Instruction> generate(CompilationUnit rootNode) {
        return visit(rootNode);
    }

    @Override
    public List<Instruction> visit(CompilationUnit node) {
        instructions.clear();
        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }

        if (instructions.isEmpty()) {
            instructions.add(new Instruction(OPCode.HALT));
        }
        return instructions;
    }

    @Override
    public List<Instruction> visit(EnumDeclaration node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(EnumItem node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(FunctionDeclaration node) {
        int sizeBeforeFunction = instructions.size();
        node.getBody().accept(this);

        boolean functionHasHalt = false;
        for (int i = sizeBeforeFunction; i < instructions.size(); i++) {
            if (instructions.get(i).getOperationCode() == OPCode.HALT) {
                functionHasHalt = true;
                break;
            }
        }
        if (!functionHasHalt) {
            instructions.add(new Instruction(OPCode.HALT));
        }
        return instructions;
    }

    @Override
    public List<Instruction> visit(FunctionParameter node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(StructDeclaration node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(StructField node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(BlockStatement node) {
        for (StatementAstNode statement : node.getStatements()) {
            statement.accept(this);
        }
        return instructions;
    }

    @Override
    public List<Instruction> visit(IfStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(WhileStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(DoWhileStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ForStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(SwitchStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(SwitchCase node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(BreakStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ContinueStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ReturnStatement node) {
        if (node.getExpression() != null) {
            node.getExpression().accept(this);
        }
        instructions.add(new Instruction(OPCode.HALT));
        return instructions;
    }

    @Override
    public List<Instruction> visit(VarDeclarationStatement node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(AssignExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ConditionalExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(BinaryExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(UnaryExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(PostfixExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(CallExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(IndexExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(MemberAccessExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(NewExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ArrayInitExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(NameExpression node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(LiteralExpression<?> node) {
        instructions.add(new Instruction(OPCode.PUSH, new Operand<>(node.getValue())));
        return instructions;
    }

    @Override
    public List<Instruction> visit(LiteralCaseLabel node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(EnumCaseLabel node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ArrayType node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(NamedType node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(PrimitiveType node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(VoidType node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ForInitVarDeclaration node) {
        return instructions;
    }

    @Override
    public List<Instruction> visit(ForInitExpressionList node) {
        return instructions;
    }
}
