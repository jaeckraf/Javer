package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.FunctionSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ArrayInitExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.AssignExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.BinaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.CallExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.ConditionalExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.IndexExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.LiteralExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.MemberAccessExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.NewExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.PostfixExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expression.UnaryExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BreakStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ContinueStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.DoWhileStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForInitExpressionList;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForInitVarDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.IfStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ReturnStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.StatementAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchCase;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.SwitchStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.WhileStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NameTypeKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

public class SymbolTableCreation extends AstNodeVisitorBase<Void> {

    private SymbolTable symbolTable;
    private final DiagnosticBag diagnosticBag;

    public SymbolTableCreation(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public Void visit(CompilationUnit node) {
        symbolTable = node.getSymbolTable();

        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(EnumDeclaration node) {
        EnumSymbolTableEntry entry = EnumSymbolTableEntry.builder()
            .name(node.getName())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (EnumItem item : node.getItems()) {
            item.accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(EnumItem node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(new NamedType(NameTypeKind.ENUM, node.getName())) // or enum type if you model it
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(FunctionDeclaration node) {
        FunctionSymbolTableEntry entry = FunctionSymbolTableEntry.builder()
            .name(node.getName())
            .returnType(node.getReturnType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (FunctionParameter param : node.getParameters()) {
            param.accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(FunctionParameter node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(StructDeclaration node) {
        StructSymbolTableEntry entry = StructSymbolTableEntry.builder()
            .name(node.getName())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);

        for (StructField field : node.getFields()) {
            field.accept(this);
        }

        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(StructField node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        symbolTable.addEntry(entry, diagnosticBag);
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(BlockStatement node) {
        for (StatementAstNode statement : node.getStatements()) {
            statement.accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(IfStatement node) {
        node.getThenBranch().accept(this);
        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(WhileStatement node) {
        node.getBody().accept(this);
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(DoWhileStatement node) {
        node.getBody().accept(this);
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ForStatement node) {
        node.getBody().accept(this);
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(SwitchStatement node) {
        for (SwitchCase switchCase : node.getCases()) {
            switchCase.accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(SwitchCase node) {
        if (node.getStatement() != null) {
            node.getStatement().accept(this);
        }
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(BreakStatement node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ContinueStatement node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ReturnStatement node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(VarDeclarationStatement node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .initializer(node.getInitializer()) // or omit entirely if optional
            .build();
        symbolTable.addEntry(entry, diagnosticBag);
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(AssignExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ConditionalExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(BinaryExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(UnaryExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(PostfixExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(CallExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(IndexExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(MemberAccessExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(NewExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ArrayInitExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(NameExpression node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(LiteralExpression<?> node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(LiteralCaseLabel node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(EnumCaseLabel node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ArrayType node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(NamedType node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(PrimitiveType node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(VoidType node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ForInitVarDeclaration node) {
        return null; // visitor has side effects only (symbol table creation)
    }

    @Override
    public Void visit(ForInitExpressionList node) {
        return null; // visitor has side effects only (symbol table creation)
    }
}
