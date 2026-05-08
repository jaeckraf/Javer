package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.HashSet;
import java.util.Set;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.DeclarationAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.EnumItem;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructField;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BlockStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.ForStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.VarDeclarationStatement;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.BlockScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.EnumScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.FunctionScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.GlobalScope;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.StructScope;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumValueEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FieldEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.ParameterEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.VariableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.EnumTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.StructTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class SymbolDeclarationVisitor extends AstNodeVisitorBase {

    private final DiagnosticBag diagnosticBag;
    private GlobalScope globalScope;
    private FunctionScope currentFunctionScope;
    private FunctionEntry currentFunction;
    private BlockScope currentBlock;
    private int nextVariableDeclarationOrder;

    public SymbolDeclarationVisitor(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        globalScope = node.getGlobalScope();

        for (DeclarationAstNode declaration : node.getDeclarations()) {
            registerTopLevelDeclaration(declaration);
        }
        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
    }

    private void registerTopLevelDeclaration(DeclarationAstNode declaration) {
        if (declaration instanceof FunctionDeclaration function) {
            FunctionEntry entry = new FunctionEntry(function.getName());
            FunctionScope scope = new FunctionScope(entry);
            entry.setScope(scope);
            function.setSymbolEntry(entry);
            function.setFunctionScope(scope);
            if (!globalScope.defineFunction(entry)) {
                diagnosticBag.add(function.getSourceRange().start(), Severity.ERROR, "Duplicate function: " + function.getName());
            }
            return;
        }

        if (declaration instanceof StructDeclaration struct) {
            StructEntry entry = new StructEntry(struct.getName());
            StructScope scope = new StructScope();
            entry.setScope(scope);
            struct.setSymbolEntry(entry);
            struct.setStructScope(scope);
            if (!globalScope.defineStruct(entry)) {
                diagnosticBag.add(struct.getSourceRange().start(), Severity.ERROR, "Duplicate struct: " + struct.getName());
            }
            return;
        }

        if (declaration instanceof EnumDeclaration enumDeclaration) {
            EnumEntry entry = new EnumEntry(enumDeclaration.getName());
            EnumScope scope = new EnumScope();
            entry.setScope(scope);
            enumDeclaration.setSymbolEntry(entry);
            enumDeclaration.setEnumScope(scope);
            if (!globalScope.defineEnum(entry)) {
                diagnosticBag.add(enumDeclaration.getSourceRange().start(), Severity.ERROR, "Duplicate enum: " + enumDeclaration.getName());
            }
        }
    }

    @Override
    public void visit(EnumDeclaration node) {
        EnumEntry entry = node.getSymbolEntry();
        EnumScope scope = node.getEnumScope();

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
            EnumValueEntry valueEntry = new EnumValueEntry(
                    item.getName(),
                    entry,
                    value,
                    entry.getElementSizeBytes(),
                    offsetBytes,
                    entry.getDataLabel());
            item.setSymbolEntry(valueEntry);

            if (!scope.defineEnumValue(valueEntry)) {
                diagnosticBag.add(item.getSourceRange().start(), Severity.ERROR, "Duplicate enum item: " + item.getName());
            }

            nextValue = value + 1;
            offsetBytes += entry.getElementSizeBytes();
        }

        entry.setSizeBytes(offsetBytes);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        FunctionScope previousFunctionScope = currentFunctionScope;
        FunctionEntry previousFunction = currentFunction;
        BlockScope previousBlock = currentBlock;
        int previousVariableDeclarationOrder = nextVariableDeclarationOrder;

        currentFunctionScope = node.getFunctionScope();
        currentFunction = node.getSymbolEntry();
        currentBlock = null;
        nextVariableDeclarationOrder = 0;
        currentFunction.setReturnType(resolveType(node.getReturnType()));

        defineParameters(node, currentFunctionScope, currentFunction);
        node.getBody().accept(this);

        currentFunctionScope = previousFunctionScope;
        currentFunction = previousFunction;
        currentBlock = previousBlock;
        nextVariableDeclarationOrder = previousVariableDeclarationOrder;
    }

    private void defineParameters(FunctionDeclaration node, FunctionScope functionScope, FunctionEntry function) {
        int parameterBytes = node.getParameters().stream()
                .map(FunctionParameter::getType)
                .map(this::resolveType)
                .mapToInt(TypeInfo::sizeBytes)
                .sum();
        function.setParameterBytes(parameterBytes);

        int offsetBytes = -parameterBytes;
        for (FunctionParameter parameter : node.getParameters()) {
            TypeInfo type = resolveType(parameter.getType());
            int sizeBytes = type.sizeBytes();
            ParameterEntry entry = new ParameterEntry(parameter.getName(), type, sizeBytes, offsetBytes);
            parameter.setSymbolEntry(entry);

            if (!functionScope.defineParameter(entry)) {
                diagnosticBag.add(parameter.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + parameter.getName());
            }

            offsetBytes += sizeBytes;
        }
    }

    @Override
    public void visit(StructDeclaration node) {
        StructScope structScope = node.getStructScope();

        int offsetBytes = 0;
        for (StructField field : node.getFields()) {
            TypeInfo type = resolveType(field.getType());
            int sizeBytes = type.sizeBytes();
            FieldEntry entry = new FieldEntry(field.getName(), type, sizeBytes, offsetBytes);
            field.setSymbolEntry(entry);

            if (!structScope.defineField(entry)) {
                diagnosticBag.add(field.getSourceRange().start(), Severity.ERROR, "Duplicate field: " + field.getName());
            }

            offsetBytes += sizeBytes;
        }
        structScope.setSizeBytes(offsetBytes);
    }

    @Override
    public void visit(BlockStatement node) {
        BlockScope previousBlock = currentBlock;
        BlockScope blockScope = new BlockScope(currentBlock, currentFunctionScope);
        node.setBlockScope(blockScope);

        if (currentBlock == null) {
            currentFunctionScope.setRootBlock(blockScope);
        } else {
            currentBlock.addChild(blockScope);
        }

        currentBlock = blockScope;
        super.visit(node);
        currentBlock = previousBlock;
    }

    @Override
    public void visit(ForStatement node) {
        BlockScope previousBlock = currentBlock;
        BlockScope forScope = new BlockScope(currentBlock, currentFunctionScope);
        node.setBlockScope(forScope);

        if (currentBlock == null) {
            currentFunctionScope.setRootBlock(forScope);
        } else {
            currentBlock.addChild(forScope);
        }

        currentBlock = forScope;
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
        currentBlock = previousBlock;
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        }

        TypeInfo type = resolveType(node.getType());
        int sizeBytes = type.sizeBytes();
        int offsetBytes = currentFunction != null ? currentFunction.allocateLocalBytes(sizeBytes) : 0;
        VariableEntry entry = new VariableEntry(
                node.getName(),
                type,
                sizeBytes,
                offsetBytes,
                node.getInitializer() != null,
                defaultValueOf(type),
                nextVariableDeclarationOrder++);
        node.setSymbolEntry(entry);

        if (currentBlock != null && !currentBlock.defineVariable(entry)) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, "Duplicate symbol: " + node.getName());
        }
    }

    private TypeInfo resolveType(TypeAstNode type) {
        if (type instanceof PrimitiveType primitiveType) {
            return PrimitiveTypeInfo.of(primitiveType.getKind());
        }
        if (type instanceof ArrayType arrayType) {
            return new ArrayTypeInfo(resolveType(arrayType.getBaseType()));
        }
        if (type instanceof VoidType) {
            return VoidTypeInfo.INSTANCE;
        }
        if (type instanceof NamedType namedType) {
            return resolveNamedType(namedType);
        }
        return UnknownTypeInfo.INSTANCE;
    }

    private TypeInfo resolveNamedType(NamedType namedType) {
        return switch (namedType.getKind()) {
            case STRUCT -> {
                StructEntry entry = globalScope.resolveStruct(namedType.getName());
                if (entry != null) {
                    namedType.setResolvedEntry(entry);
                    yield new StructTypeInfo(entry);
                }
                yield UnknownTypeInfo.INSTANCE;
            }
            case ENUM -> {
                EnumEntry entry = globalScope.resolveEnum(namedType.getName());
                if (entry != null) {
                    namedType.setResolvedEntry(entry);
                    yield new EnumTypeInfo(entry);
                }
                yield UnknownTypeInfo.INSTANCE;
            }
            case INVALID -> UnknownTypeInfo.INSTANCE;
        };
    }

    private static Object defaultValueOf(TypeInfo type) {
        if (type instanceof PrimitiveTypeInfo primitiveType) {
            return switch (primitiveType.kind()) {
                case BOOL -> false;
                case CHAR -> '\0';
                case INT -> 0;
                case DOUBLE -> 0.0;
                case STRING, INVALID -> null;
            };
        }
        return null;
    }
}
