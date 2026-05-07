package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.HashSet;
import java.util.Set;

import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.EnumValueSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.FieldSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.FunctionSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.ParameterSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.TypeLayout;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
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
        SymbolTable enumScope = new SymbolTable(currentScope);
        node.setSymbolTable(enumScope);

        EnumSymbolTableEntry entry = EnumSymbolTableEntry.builder()
            .name(node.getName())
            .symbolTable(enumScope)
            .dataLabel("enum_" + node.getName())
            .elementSizeBytes(4)
            .build();
        node.setSymbolEntry(entry);

        if (!currentScope.defineEnum(entry)) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate enum: " + node.getName());
        }

        SymbolTable previousScope = currentScope;
        currentScope = enumScope;

        Set<Integer> usedValues = new HashSet<>();
        int nextValue = 0;
        int offsetBytes = 0;

        for (EnumItem item : node.getItems()) {
            int value = item.getValue() != null ? item.getValue() : nextValue;

            if (usedValues.contains(value)) {
                diagnosticBag.add(item.getSourceRange().start(), Severity.ERROR, "Duplicate enum value: " + value);
            }
            usedValues.add(value);

            item.setValue(value);
            EnumValueSymbolTableEntry valueEntry = EnumValueSymbolTableEntry.builder()
                .name(item.getName())
                .ownerEnum(entry)
                .value(value)
                .sizeBytes(entry.getElementSizeBytes())
                .offsetBytes(offsetBytes)
                .dataLabel(entry.getDataLabel())
                .build();
            item.setSymbolEntry(valueEntry);

            if (!currentScope.defineEnumValue(valueEntry)) {
                diagnosticBag.add(item.getSourceRange().start(), Severity.ERROR, "Duplicate enum item: " + item.getName());
            }

            nextValue = value + 1;
            offsetBytes += entry.getElementSizeBytes();
        }

        entry.setSizeBytes(offsetBytes);
        currentScope = previousScope;
    }

    @Override
    public void visit(FunctionDeclaration node) {
        SymbolTable functionScope = new SymbolTable(currentScope);
        node.setSymbolTable(functionScope);

        FunctionSymbolTableEntry entry = FunctionSymbolTableEntry.builder()
            .name(node.getName())
            .returnType(node.getReturnType())
            .symbolTable(functionScope)
            .label("_" + node.getName())
            .build();
        node.setSymbolEntry(entry);

        if (!currentScope.defineFunction(entry)) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate function: " + node.getName());
        }

        SymbolTable previousScope = currentScope;
        currentScope = functionScope;

        defineParameters(node, entry);
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        currentScope = previousScope;
    }

    private void defineParameters(FunctionDeclaration node, FunctionSymbolTableEntry currentFunction) {
        int parameterBytes = node.getParameters().stream()
            .mapToInt(parameter -> TypeLayout.sizeOf(parameter.getType()))
            .sum();
        currentFunction.setParameterBytes(parameterBytes);

        int offsetBytes = -parameterBytes;
        for (FunctionParameter parameter : node.getParameters()) {
            int sizeBytes = TypeLayout.sizeOf(parameter.getType());
            ParameterSymbolTableEntry entry = ParameterSymbolTableEntry.builder()
                .name(parameter.getName())
                .type(parameter.getType())
                .sizeBytes(sizeBytes)
                .offsetBytes(offsetBytes)
                .build();
            parameter.setSymbolEntry(entry);

            if (!currentScope.defineParameter(entry)) {
                diagnosticBag.add(parameter.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + parameter.getName());
            }

            offsetBytes += sizeBytes;
        }
    }

    @Override
    public void visit(StructDeclaration node) {
        SymbolTable structScope = new SymbolTable(currentScope);
        node.setSymbolTable(structScope);

        StructSymbolTableEntry entry = StructSymbolTableEntry.builder()
            .name(node.getName())
            .symbolTable(structScope)
            .build();
        node.setSymbolEntry(entry);

        if (!currentScope.defineStruct(entry)) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate struct: " + node.getName());
        }

        SymbolTable previousScope = currentScope;
        currentScope = structScope;

        int offsetBytes = 0;
        for (StructField field : node.getFields()) {
            offsetBytes = defineField(field, offsetBytes);
        }
        entry.setSizeBytes(offsetBytes);

        currentScope = previousScope;
    }

    private int defineField(StructField node, int offsetBytes) {
        int sizeBytes = TypeLayout.sizeOf(node.getType());
        FieldSymbolTableEntry entry = FieldSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .sizeBytes(sizeBytes)
            .offsetBytes(offsetBytes)
            .build();
        node.setSymbolEntry(entry);

        if (!currentScope.defineField(entry)) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate field: " + node.getName());
        }

        return offsetBytes + sizeBytes;
    }

    @Override
    public void visit(BlockStatement node) {
        SymbolTable blockScope = new SymbolTable(currentScope);
        node.setSymbolTable(blockScope);
        currentScope.addChild(blockScope);

        SymbolTable previousScope = currentScope;
        currentScope = blockScope;

        super.visit(node);

        currentScope = previousScope;
    }

    @Override
    public void visit(ForStatement node) {
        SymbolTable forScope = new SymbolTable(currentScope);
        node.setSymbolTable(forScope);
        currentScope.addChild(forScope);

        SymbolTable previousScope = currentScope;
        currentScope = forScope;

        if (node.getForInit() != null) {
            node.getForInit().accept(this);
        }
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }
        if (node.getUpdate() != null) {
            node.getUpdate().forEach(expression -> expression.accept(this));
        }
        node.getBody().accept(this);

        currentScope = previousScope;
    }
}
