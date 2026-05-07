package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.HashSet;
import java.util.Set;

import ch.zhaw.it.pm4.javer.compiler.ast.BlockSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.FunctionSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class SymbolTableCreation extends AstNodeVisitorBase {

    private SymbolTable currentScope;
    private final DiagnosticBag diagnosticBag;

    public SymbolTableCreation(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        currentScope = node.getSymbolTable();

        super.visit(node);
    }

    @Override
    public void visit(EnumDeclaration node) {
        EnumSymbolTableEntry entry = EnumSymbolTableEntry.builder()
            .name(node.getName())
            .items(node.getItems())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());

        Set<Integer> usedValues = new HashSet<>();
        int nextValue = 0;

        for (EnumItem item : node.getItems()) {

            int value;

            if (item.getValue() != null) {
                value = item.getValue();
            } else {
                value = nextValue;
            }

            if (usedValues.contains(value)) {
                diagnosticBag.add(
                    item.getSourceRange().start(),
                    Severity.ERROR,
                    "Duplicate enum value: " + value
                );
            }

            usedValues.add(value);

            item.setValue(value);

            nextValue = value + 1;
        }
    }

    @Override
    public void visit(FunctionDeclaration node) {
        SymbolTable functionScope = new SymbolTable(currentScope);
        node.setSymbolTable(functionScope);
        
        FunctionSymbolTableEntry entry = FunctionSymbolTableEntry.builder()
            .name(node.getName())
            .returnType(node.getReturnType())
            .parameters(node.getParameters())
            .scope(functionScope)
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());

        currentScope = functionScope;

        super.visit(node);

        currentScope = currentScope.getParent(); // exit function scope
    }

    @Override
    public void visit(FunctionParameter node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }

    @Override
    public void visit(StructDeclaration node) {
        StructSymbolTableEntry entry = StructSymbolTableEntry.builder()
            .name(node.getName())
            .fields(node.getFields())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());

        super.visit(node);
    }

    @Override
    public void visit(StructField node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .build();

        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }

    @Override
    public void visit(BlockStatement node) {
        SymbolTable blockScope = new SymbolTable(currentScope);
        node.setSymbolTable(blockScope);
        BlockSymbolTableEntry entry = new BlockSymbolTableEntry(blockScope);
        currentScope.addEntry(entry);
        currentScope = blockScope;
        
        super.visit(node);

        currentScope = currentScope.getParent(); // exit block scope
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .initializer(node.getInitializer()) // or omit entirely if optional
            .build();
        if (!currentScope.addEntry(entry))
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
    }
}
