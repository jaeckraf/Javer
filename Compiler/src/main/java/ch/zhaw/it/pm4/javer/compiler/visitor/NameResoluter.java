package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.Set;

import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.EnumSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.EnumValueSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.FieldSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.FunctionSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.StorageSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.StructSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.SymbolTable;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.TypeLayout;
import ch.zhaw.it.pm4.javer.compiler.ast.symboltable.VariableSymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.CallExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.MemberAccessExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.NameExpression;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class NameResoluter extends AstNodeVisitorBase {
    private static final Set<String> BUILT_IN_FUNCTIONS = Set.of("printi", "prints", "println");

    private SymbolTable currentScope;
    private FunctionSymbolTableEntry currentFunction;
    private final DiagnosticBag diagnosticBag;

    public NameResoluter(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        currentScope = node.getSymbolTable();
        super.visit(node);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        node.getReturnType().accept(this);

        SymbolTable previousScope = currentScope;
        FunctionSymbolTableEntry previousFunction = currentFunction;
        currentScope = node.getSymbolTable();
        currentFunction = node.getSymbolEntry();

        for (FunctionParameter parameter : node.getParameters()) {
            parameter.accept(this);
        }
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        currentFunction = previousFunction;
        currentScope = previousScope;
    }

    @Override
    public void visit(FunctionParameter node) {
        node.getType().accept(this);
    }

    @Override
    public void visit(StructDeclaration node) {
        SymbolTable previousScope = currentScope;
        currentScope = node.getSymbolTable();

        for (StructField field : node.getFields()) {
            field.accept(this);
        }

        currentScope = previousScope;
    }

    @Override
    public void visit(StructField node) {
        node.getType().accept(this);
    }

    @Override
    public void visit(BlockStatement node) {
        SymbolTable previousScope = currentScope;
        currentScope = node.getSymbolTable();

        super.visit(node);

        currentScope = previousScope;
    }

    @Override
    public void visit(ForStatement node) {
        SymbolTable previousScope = currentScope;
        currentScope = node.getSymbolTable();

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

    @Override
    public void visit(VarDeclarationStatement node) {
        node.getType().accept(this);

        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }

        int sizeBytes = TypeLayout.sizeOf(node.getType());
        int offsetBytes = currentFunction != null ? currentFunction.allocateLocalBytes(sizeBytes) : 0;
        VariableSymbolTableEntry entry = VariableSymbolTableEntry.builder()
            .name(node.getName())
            .type(node.getType())
            .sizeBytes(sizeBytes)
            .offsetBytes(offsetBytes)
            .hasExplicitInitializer(node.getInitializer() != null)
            .defaultValue(TypeLayout.defaultValueOf(node.getType()))
            .build();
        node.setSymbolEntry(entry);

        if (!currentScope.defineLocal(entry)) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
        }
    }

    @Override
    public void visit(NamedType node) {
        SymbolTableEntry entry = switch (node.getKind()) {
            case STRUCT -> currentScope.resolveStruct(node.getName());
            case ENUM -> currentScope.resolveEnum(node.getName());
            case INVALID -> null;
        };

        if (entry == null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined type: " + node.getName());
            return;
        }

        node.setResolvedEntry(entry);
    }

    @Override
    public void visit(NameExpression node) {
        SymbolTableEntry entry = currentScope.resolveVariable(node.getName());
        if (entry != null) {
            node.setSymbolTableEntry(entry);
            return;
        }

        if (currentScope.isAmbiguousEnumValue(node.getName())) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Ambiguous enum value: " + node.getName());
            return;
        }

        entry = currentScope.resolveUniqueEnumValue(node.getName());
        if (entry != null) {
            node.setSymbolTableEntry(entry);
            return;
        }

        diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined symbol: " + node.getName());
    }

    @Override
    public void visit(CallExpression node) {
        for (var argument : node.getArguments()) {
            argument.accept(this);
        }

        FunctionSymbolTableEntry function = currentScope.resolveFunction(node.getFunctionName());
        if (function != null) {
            node.setResolvedFunction(function);
            return;
        }

        if (!BUILT_IN_FUNCTIONS.contains(node.getFunctionName())) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined function: " + node.getFunctionName());
        }
    }

    @Override
    public void visit(MemberAccessExpression node) {
        if (node.getTarget() instanceof NameExpression targetName) {
            EnumSymbolTableEntry enumEntry = currentScope.resolveEnum(targetName.getName());
            if (enumEntry != null) {
                resolveEnumMember(node, enumEntry);
                return;
            }
        }

        node.getTarget().accept(this);

        FieldSymbolTableEntry field = resolveStructField(node);
        if (field != null) {
            node.setResolvedField(field);
        }
    }

    private void resolveEnumMember(MemberAccessExpression node, EnumSymbolTableEntry enumEntry) {
        EnumValueSymbolTableEntry valueEntry = enumEntry.getSymbolTable().resolveEnumValue(node.getMemberName());
        if (valueEntry == null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Enum has no value: " + node.getMemberName());
            return;
        }

        node.setResolvedEnumValue(valueEntry);
        node.setValue(valueEntry.getValue());
    }

    private FieldSymbolTableEntry resolveStructField(MemberAccessExpression node) {
        if (!(node.getTarget() instanceof NameExpression targetName)) {
            return null;
        }

        SymbolTableEntry targetEntry = targetName.getSymbolTableEntry();
        if (!(targetEntry instanceof StorageSymbolTableEntry storageEntry)) {
            return null;
        }

        TypeAstNode targetType = storageEntry.getType();
        if (!(targetType instanceof NamedType namedType)) {
            return null;
        }

        StructSymbolTableEntry structEntry = currentScope.resolveStruct(namedType.getName());
        if (structEntry == null) {
            return null;
        }

        FieldSymbolTableEntry field = structEntry.getSymbolTable().resolveField(node.getMemberName());
        if (field == null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Struct has no field: " + node.getMemberName());
        }
        return field;
    }

    @Override
    public void visit(EnumCaseLabel node) {
        EnumSymbolTableEntry enumEntry = currentScope.resolveEnum(node.getEnumTypeName());
        if (enumEntry == null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Undefined enum: " + node.getEnumTypeName());
            return;
        }

        EnumValueSymbolTableEntry valueEntry = enumEntry.getSymbolTable().resolveEnumValue(node.getEnumValueName());
        if (valueEntry == null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Enum has no value: " + node.getEnumValueName());
            return;
        }

        node.setResolvedEnumValue(valueEntry);
    }
}
