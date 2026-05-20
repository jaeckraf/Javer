package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.DataSection;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.DataEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumValueEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.FunctionEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.ParameterEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StorageEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.SymbolEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/**
 * Emits VM bytecode from a semantically checked AST.
 */
@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends AstNodeVisitorBase {

    private BufferedWriter writer;
    private DataSection dataSection;
    private FunctionEntry currentFunction;
    private final Deque<LoopContext> loopContexts = new ArrayDeque<>();
    private int nextLabelId;

    /**
     * Creates a code generator with no active output writer.
     */
    public CodeGenerator() {
    }

    /**
     * Generates bytecode for a complete compilation unit and writes it to disk.
     *
     * @param node           root compilation unit
     * @param outputFilePath target bytecode file path
     */
    public void generate(CompilationUnit node, String outputFilePath) {
        Path outputFile = Path.of(outputFilePath);
        prepareOutputDirectory(outputFile);

        try (BufferedWriter outputWriter = Files.newBufferedWriter(
                outputFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer = outputWriter;
            loopContexts.clear();
            nextLabelId = 0;
            node.accept(this);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write generated code to " + outputFile, exception);
        } finally {
            writer = null;
        }
    }

    protected void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write generated code.", exception);
        }
    }

    private void writeLabel(String label) {
        writeLine(label + ":");
    }

    private String nextLabel(String prefix) {
        return prefix + "_" + nextLabelId++;
    }

    private void prepareOutputDirectory(Path outputFile) {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not create output directory " + parent, exception);
        }
    }

    @Override
    protected void visitDefault(AstNode node) {
        super.visitDefault(node);
    }

    @Override
    public void visit(CompilationUnit node) {
        dataSection = node.getDataSection();
        writeLine(".code");
        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
        writeLine("");
        writeLine(".data");
        dataSection.getEntries().values().forEach(entry -> writeLine(entry.toString()));
        dataSection = null;
    }

    @Override
    public void visit(EnumDeclaration node) {
        super.visit(node);
    }

    @Override
    public void visit(EnumItem node) {
        super.visit(node);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        FunctionEntry function = node.getSymbolEntry();
        currentFunction = function;
        writeLine("_" + node.getName() + ":");
        writeLine("ENTER, " + (function == null ? 0 : function.getFrameSizeBytes()));
        node.getBody().accept(this);
        if (!endsWithReturn(node.getBody())) {
            emitFallthroughReturn(function);
        }
        currentFunction = null;
    }

    private boolean endsWithReturn(BlockStatement body) {
        List<StatementAstNode> statements = body.getStatements();
        if (statements.isEmpty()) {
            return false;
        }
        return statements.getLast() instanceof ReturnStatement;
    }

    private void emitFallthroughReturn(FunctionEntry function) {
        TypeInfo returnType = function.getReturnType();
        if (isVoidLike(returnType)) {
            writeLine("RET");
            return;
        }
        emitZeroFor(returnType);
        emitReturn(returnType);
    }

    @Override
    public void visit(FunctionParameter node) {
        super.visit(node);
    }

    @Override
    public void visit(StructDeclaration node) {
        super.visit(node);
    }

    @Override
    public void visit(StructField node) {
        super.visit(node);
    }

    @Override
    public void visit(BlockStatement node) {
        for (StatementAstNode statement : node.getStatements()) {
            emitStatement(statement);
        }
    }

    private void emitStatement(StatementAstNode statement) {
        if (statement instanceof ExpressionAstNode expression) {
            expression.accept(this);
            discardExpressionResult(expression);
            return;
        }
        statement.accept(this);
    }

    private void discardExpressionResult(ExpressionAstNode expression) {
        TypeInfo type = expression.getResultingType();
        if (!leavesValueOnStack(expression, type)) {
            return;
        }
        emitPop(type);
    }

    private boolean leavesValueOnStack(ExpressionAstNode expression, TypeInfo type) {
        if (isVoidLike(type)) {
            return false;
        }
        if (expression instanceof CallExpression call && isVoidReturningCall(call)) {
            return false;
        }
        if (expression instanceof AssignExpression assign && assign.getTarget() instanceof IndexExpression) {
            return false;
        }
        return true;
    }

    private boolean isVoidReturningCall(CallExpression call) {
        FunctionEntry function = call.getResolvedFunction();
        if (function != null) {
            return isVoidLike(function.getReturnType());
        }
        return isPrintBuiltin(call.getFunctionName());
    }

    @Override
    public void visit(IfStatement node) {
        String elseLabel = nextLabel("if_else");
        String endLabel = nextLabel("if_end");
        node.getCondition().accept(this);
        writeLine("JUMPF, " + elseLabel);
        node.getThenBranch().accept(this);
        boolean elseBranchPossible = node.getElseBranch() != null;
        if (elseBranchPossible) {
            writeLine("JUMP, " + endLabel);
        }
        writeLabel(elseLabel);
        if (elseBranchPossible) {
            node.getElseBranch().accept(this);
            writeLabel(endLabel);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        String conditionLabel = nextLabel("while_condition");
        String endLabel = nextLabel("while_end");
        writeLabel(conditionLabel);
        node.getCondition().accept(this);
        writeLine("JUMPF, " + endLabel);
        loopContexts.push(new LoopContext(endLabel, conditionLabel));
        node.getBody().accept(this);
        loopContexts.pop();
        writeLine("JUMP, " + conditionLabel);
        writeLabel(endLabel);
    }

    @Override
    public void visit(DoWhileStatement node) {
        String bodyLabel = nextLabel("do_body");
        String conditionLabel = nextLabel("do_condition");
        String endLabel = nextLabel("do_end");
        writeLabel(bodyLabel);
        loopContexts.push(new LoopContext(endLabel, conditionLabel));
        node.getBody().accept(this);
        loopContexts.pop();
        writeLabel(conditionLabel);
        node.getCondition().accept(this);
        writeLine("JUMPT, " + bodyLabel);
        writeLabel(endLabel);
    }

    @Override
    public void visit(ForStatement node) {
        String conditionLabel = nextLabel("for_condition");
        String updateLabel = nextLabel("for_update");
        String endLabel = nextLabel("for_end");
        if (node.getForInit() != null) {
            node.getForInit().accept(this);
        }
        writeLabel(conditionLabel);
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        } else {
            writeLine("PUSHB, 1");
        }
        writeLine("JUMPF, " + endLabel);
        loopContexts.push(new LoopContext(endLabel, updateLabel));
        node.getBody().accept(this);
        loopContexts.pop();
        writeLabel(updateLabel);
        if (node.getUpdate() != null) {
            for (ExpressionAstNode update : node.getUpdate()) {
                emitStatement(update);
            }
        }
        writeLine("JUMP, " + conditionLabel);
        writeLabel(endLabel);
    }

    @Override
    public void visit(SwitchStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(SwitchCase node) {
        super.visit(node);
    }

    @Override
    public void visit(BreakStatement node) {
        if (!loopContexts.isEmpty()) {
            writeLine("JUMP, " + loopContexts.peek().breakLabel());
        }
    }

    @Override
    public void visit(ContinueStatement node) {
        if (!loopContexts.isEmpty()) {
            writeLine("JUMP, " + loopContexts.peek().continueLabel());
        }
    }

    @Override
    public void visit(ReturnStatement node) {
        ExpressionAstNode expression = node.getExpression();
        TypeInfo returnType = currentFunction == null ? VoidTypeInfo.INSTANCE : currentFunction.getReturnType();
        if (expression == null || isVoidLike(returnType)) {
            writeLine("RET");
            return;
        }
        emitTyped(expression, returnType);
        emitReturn(returnType);
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        if (node.getInitializer() == null) {
            return;
        }
        StorageEntry storage = node.getSymbolEntry();
        emitTyped(node.getInitializer(), storage.getType());
        emitFrameStore(storage);
    }

    @Override
    public void visit(AssignExpression node) {
        if (node.getTarget() instanceof IndexExpression indexTarget) {
            emitIndexAssign(indexTarget, node);
            return;
        }
        StorageEntry storage = storageOf(node.getTarget());
        if (storage == null) {
            return;
        }
        TypeInfo type = storage.getType();
        AssignOperator operator = node.getOperator();
        if (operator == AssignOperator.ASSIGN) {
            emitTyped(node.getValue(), type);
        } else {
            emitFrameLoad(storage);
            emitTyped(node.getValue(), type);
            emitBinaryOp(compoundAssignToBinary(operator), type);
        }
        emitDup(type);
        emitFrameStore(storage);
    }

    private void emitIndexAssign(IndexExpression target, AssignExpression node) {
        if (node.getOperator() != AssignOperator.ASSIGN) {
            return;
        }
        StorageEntry storage = storageOf(target.getTarget());
        if (storage == null || !(storage.getType() instanceof ArrayTypeInfo(TypeInfo elementType))) {
            return;
        }
        emitFrameLoad(storage);
        emitTyped(target.getIndex(), PrimitiveTypeInfo.INT);
        int elementSize = sizeOf(elementType);
        if (elementSize != 1) {
            writeLine("PUSHI, " + elementSize);
            writeLine("IMUL");
        }
        emitTyped(node.getValue(), elementType);
        emitHeapStore(elementType);
    }

    private BinaryExpressionKind compoundAssignToBinary(AssignOperator operator) {
        return switch (operator) {
            case ADD_ASSIGN -> BinaryExpressionKind.ADD;
            case SUB_ASSIGN -> BinaryExpressionKind.SUBTRACT;
            case MUL_ASSIGN -> BinaryExpressionKind.MULTIPLY;
            case DIV_ASSIGN -> BinaryExpressionKind.DIVIDE;
            case MOD_ASSIGN -> BinaryExpressionKind.MODULO;
            case BITWISE_OR_ASSIGN -> BinaryExpressionKind.BITWISE_OR;
            case BITWISE_AND_ASSIGN -> BinaryExpressionKind.BITWISE_AND;
            case BITWISE_XOR_ASSIGN -> BinaryExpressionKind.BITWISE_XOR;
            case LEFT_SHIFT_ASSIGN -> BinaryExpressionKind.SHIFT_LEFT;
            case RIGHT_SHIFT_ASSIGN -> BinaryExpressionKind.SHIFT_RIGHT;
            case ASSIGN, INVALID -> BinaryExpressionKind.INVALID;
        };
    }

    @Override
    public void visit(ConditionalExpression node) {
        String falseLabel = nextLabel("cond_false");
        String endLabel = nextLabel("cond_end");
        TypeInfo resultType = node.getResultingType();
        emitAsBoolean(node.getCondition());
        writeLine("JUMPF, " + falseLabel);
        emitTyped(node.getTrueExpression(), resultType);
        writeLine("JUMP, " + endLabel);
        writeLabel(falseLabel);
        emitTyped(node.getFalseExpression(), resultType);
        writeLabel(endLabel);
    }

    @Override
    public void visit(BinaryExpression node) {
        BinaryExpressionKind operator = node.getOperator();
        if (operator == BinaryExpressionKind.AND) {
            emitLogicalAnd(node);
            return;
        }
        if (operator == BinaryExpressionKind.OR) {
            emitLogicalOr(node);
            return;
        }
        TypeInfo operandType = binaryOperandType(operator, node);
        emitTyped(node.getLeft(), operandType);
        emitTyped(node.getRight(), operandType);
        emitBinaryOp(operator, operandType);
        emitConversion(binaryResultType(operator, operandType), node.getResultingType());
    }

    private TypeInfo binaryOperandType(BinaryExpressionKind operator, BinaryExpression node) {
        if (isIntegerOnlyBinary(operator)) {
            return PrimitiveTypeInfo.INT;
        }
        TypeInfo left = node.getLeft().getResultingType();
        TypeInfo right = node.getRight().getResultingType();
        if (PrimitiveTypeInfo.DOUBLE.equals(left) || PrimitiveTypeInfo.DOUBLE.equals(right)) {
            return PrimitiveTypeInfo.DOUBLE;
        }
        return PrimitiveTypeInfo.INT;
    }

    private TypeInfo binaryResultType(BinaryExpressionKind operator, TypeInfo operandType) {
        if (isComparison(operator)) {
            return PrimitiveTypeInfo.BOOL;
        }
        return operandType;
    }

    private boolean isIntegerOnlyBinary(BinaryExpressionKind operator) {
        return switch (operator) {
            case MODULO, BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT -> true;
            default -> false;
        };
    }

    private boolean isComparison(BinaryExpressionKind operator) {
        return switch (operator) {
            case EQUALS, NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> true;
            default -> false;
        };
    }

    private void emitLogicalAnd(BinaryExpression node) {
        String falseLabel = nextLabel("and_false");
        String endLabel = nextLabel("and_end");
        emitAsBoolean(node.getLeft());
        writeLine("JUMPF, " + falseLabel);
        emitAsBoolean(node.getRight());
        writeLine("JUMP, " + endLabel);
        writeLabel(falseLabel);
        writeLine("PUSHB, 0");
        writeLabel(endLabel);
    }

    private void emitLogicalOr(BinaryExpression node) {
        String trueLabel = nextLabel("or_true");
        String endLabel = nextLabel("or_end");
        emitAsBoolean(node.getLeft());
        writeLine("JUMPT, " + trueLabel);
        emitAsBoolean(node.getRight());
        writeLine("JUMP, " + endLabel);
        writeLabel(trueLabel);
        writeLine("PUSHB, 1");
        writeLabel(endLabel);
    }

    @Override
    public void visit(UnaryExpression node) {
        switch (node.getKind()) {
            case PLUS -> node.getOperand().accept(this);
            case MINUS -> {
                node.getOperand().accept(this);
                TypeInfo type = node.getOperand().getResultingType();
                writeLine(PrimitiveTypeInfo.DOUBLE.equals(type) ? "DNEG" : "INEG");
            }
            case BITWISE_NOT -> {
                node.getOperand().accept(this);
                writeLine("IINV");
            }
            case LOGICAL_NOT -> {
                emitAsBoolean(node.getOperand());
                String falseLabel = nextLabel("not_false");
                String endLabel = nextLabel("not_end");
                writeLine("JUMPF, " + falseLabel);
                writeLine("PUSHB, 0");
                writeLine("JUMP, " + endLabel);
                writeLabel(falseLabel);
                writeLine("PUSHB, 1");
                writeLabel(endLabel);
            }
            case PRE_INCREMENT -> emitPrefixStep(node.getOperand(), 1);
            case PRE_DECREMENT -> emitPrefixStep(node.getOperand(), -1);
            case INVALID -> {
            }
        }
    }

    private void emitPrefixStep(ExpressionAstNode target, int delta) {
        StorageEntry storage = storageOf(target);
        TypeInfo type = storage.getType();
        emitFrameLoad(storage);
        emitNumericLiteralPush(type, delta);
        emitBinaryOp(BinaryExpressionKind.ADD, type);
        emitDup(type);
        emitFrameStore(storage);
    }

    @Override
    public void visit(PostfixExpression node) {
        StorageEntry storage = storageOf(node.getOperand());
        int delta = node.getKind() == PostfixOperationKind.INCREMENT ? 1 : -1;
        TypeInfo type = storage.getType();
        emitFrameLoad(storage);
        emitDup(type);
        emitNumericLiteralPush(type, delta);
        emitBinaryOp(BinaryExpressionKind.ADD, type);
        emitFrameStore(storage);
    }

    @Override
    public void visit(CallExpression node) {
        if (isPrintBuiltin(node.getFunctionName())) {
            emitPrintBuiltin(node);
            return;
        }
        FunctionEntry function = node.getResolvedFunction();
        List<ParameterEntry> parameters = new ArrayList<>(function.getScope().getParameters().values());
        for (int i = 0; i < node.getArguments().size(); i++) {
            ExpressionAstNode argument = node.getArguments().get(i);
            TypeInfo expected = i < parameters.size()
                    ? parameters.get(i).getType()
                    : argument.getResultingType();
            emitTyped(argument, expected);
        }
        writeLine("CALL, " + function.getLabel() + ", " + function.getParameterBytes());
    }

    @Override
    public void visit(IndexExpression node) {
        StorageEntry storage = storageOf(node.getTarget());
        if (storage == null || !(storage.getType() instanceof ArrayTypeInfo(TypeInfo elementType))) {
            return;
        }
        emitFrameLoad(storage);
        emitTyped(node.getIndex(), PrimitiveTypeInfo.INT);
        int elementSize = sizeOf(elementType);
        if (elementSize != 1) {
            writeLine("PUSHI, " + elementSize);
            writeLine("IMUL");
        }
        emitHeapLoad(elementType);
    }

    private void emitHeapLoad(TypeInfo type) {
        int size = sizeOf(type);
        writeLine(switch (size) {
            case 1 -> "HLOAD1";
            case 2 -> "HLOAD2";
            case 8 -> "HLOAD8";
            default -> "HLOAD4";
        });
    }

    private void emitHeapStore(TypeInfo type) {
        int size = sizeOf(type);
        writeLine(switch (size) {
            case 1 -> "HSTORE1";
            case 2 -> "HSTORE2";
            case 8 -> "HSTORE8";
            default -> "HSTORE4";
        });
    }

    @Override
    public void visit(MemberAccessExpression node) {
        if (node.getResolvedEnumValue() != null) {
            writeLine("PUSHI, " + node.getResolvedEnumValue().getValue());
        }
    }

    @Override
    public void visit(NewExpression node) {
        if (!(node.getResultingType() instanceof ArrayTypeInfo(TypeInfo elementType))) {
            return;
        }
        int elementSize = sizeOf(elementType);
        ArrayInitExpression init = node.getArrayInit();

        if (node.getDimensions().isEmpty()) {
            if (init == null) {
                return;
            }
            writeLine("PUSHI, " + (init.getElements().size() * elementSize));
        } else {
            emitTyped(node.getDimensions().getFirst(), PrimitiveTypeInfo.INT);
            if (elementSize != 1) {
                writeLine("PUSHI, " + elementSize);
                writeLine("IMUL");
            }
        }
        writeLine("NEW");

        if (init != null) {
            initializeArrayElements(init.getElements(), elementType, elementSize);
        }
    }

    @Override
    public void visit(ArrayInitExpression node) {
        TypeInfo elementType = node.getResultingType() instanceof ArrayTypeInfo(TypeInfo type)
                ? type
                : UnknownTypeInfo.INSTANCE;
        int elementSize = sizeOf(elementType);
        writeLine("PUSHI, " + (node.getElements().size() * elementSize));
        writeLine("NEW");
        initializeArrayElements(node.getElements(), elementType, elementSize);
    }

    private void initializeArrayElements(List<ExpressionAstNode> elements, TypeInfo elementType, int elementSize) {
        for (int i = 0; i < elements.size(); i++) {
            writeLine("DUPI");
            writeLine("PUSHI, " + (i * elementSize));
            emitTyped(elements.get(i), elementType);
            emitHeapStore(elementType);
        }
    }

    @Override
    public void visit(NameExpression node) {
        SymbolEntry entry = node.getSymbolEntry();
        if (entry instanceof StorageEntry storage) {
            emitFrameLoad(storage);
            return;
        }
        if (entry instanceof EnumValueEntry enumValue) {
            writeLine("PUSHI, " + enumValue.getValue());
        }
    }

    @Override
    public void visit(LiteralExpression<?> node) {
        emitLiteralPush(node);
    }

    @Override
    public void visit(ForInitExpressionList node) {
        for (ExpressionAstNode expression : node.getExpressions()) {
            emitStatement(expression);
        }
    }

    private void emitPrintBuiltin(CallExpression node) {
        if (node.getArguments().isEmpty()) {
            return;
        }
        ExpressionAstNode argument = node.getArguments().getFirst();
        switch (node.getFunctionName().toLowerCase()) {
            case "printb" -> {
                emitTyped(argument, PrimitiveTypeInfo.BOOL);
                writeLine("PRINTB");
            }
            case "printc" -> {
                emitTyped(argument, PrimitiveTypeInfo.CHAR);
                writeLine("PRINTC");
            }
            case "printi" -> {
                emitTyped(argument, PrimitiveTypeInfo.INT);
                writeLine("PRINTI");
            }
            case "printd" -> {
                emitTyped(argument, PrimitiveTypeInfo.DOUBLE);
                writeLine("PRINTD");
            }
            case "prints" -> emitPrintString(argument);
            default -> {
            }
        }
    }

    private void emitPrintString(ExpressionAstNode argument) {
        if (argument instanceof LiteralExpression<?> literal && literal.getKind() == LiteralKind.STRING) {
            DataEntry entry = dataSection.internString((String) literal.getValue());
            writeLine("DPRINTS, " + entry.getLabel());
            return;
        }
        argument.accept(this);
        writeLine("HPRINTS");
    }

    private boolean isPrintBuiltin(String name) {
        return switch (name.toLowerCase()) {
            case "printb", "printc", "printi", "printd", "prints" -> true;
            default -> false;
        };
    }

    private void emitTyped(ExpressionAstNode expression, TypeInfo expectedType) {
        expression.accept(this);
        emitConversion(expression.getResultingType(), expectedType);
    }

    private void emitConversion(TypeInfo from, TypeInfo to) {
        if (from == null || to == null || from.equals(to)) {
            return;
        }
        if (!(from instanceof PrimitiveTypeInfo(PrimitiveTypeKind kind)) || !(to instanceof PrimitiveTypeInfo(
                PrimitiveTypeKind kind1
        ))) {
            return;
        }
        if (kind == kind1) {
            return;
        }
        switch (kind) {
            case INT -> {
                if (kind1 == PrimitiveTypeKind.DOUBLE) {
                    writeLine("I2D");
                } else if (kind1 == PrimitiveTypeKind.BOOL) {
                    writeLine("I2B");
                } else if (kind1 == PrimitiveTypeKind.CHAR) {
                    writeLine("I2C");
                }
            }
            case DOUBLE -> {
                if (kind1 == PrimitiveTypeKind.INT) {
                    writeLine("D2I");
                }
            }
            case BOOL -> {
                if (kind1 == PrimitiveTypeKind.INT) {
                    writeLine("B2I");
                }
            }
            case CHAR -> {
                if (kind1 == PrimitiveTypeKind.INT) {
                    writeLine("C2I");
                }
            }
            default -> {
            }
        }
    }

    private void emitAsBoolean(ExpressionAstNode expression) {
        expression.accept(this);
        TypeInfo type = expression.getResultingType();
        if (type instanceof PrimitiveTypeInfo(PrimitiveTypeKind kind) && kind == PrimitiveTypeKind.INT) {
            writeLine("I2B");
        }
    }

    private void emitLiteralPush(LiteralExpression<?> node) {
        switch (node.getKind()) {
            case INT -> writeLine("PUSHI, " + node.getValue());
            case DOUBLE -> writeLine("PUSHD, " + formatDouble((Double) node.getValue()));
            case BOOLEAN -> writeLine("PUSHB, " + (Boolean.TRUE.equals(node.getValue()) ? "1" : "0"));
            case CHAR -> writeLine("PUSHC, " + (int) (Character) node.getValue());
            case STRING -> {
                DataEntry entry = dataSection.internString((String) node.getValue());
                writeLine("PUSHR, " + entry.getLabel());
            }
            case NULL -> writeLine("PUSHI, 0");
        }
    }

    private String formatDouble(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.format(Locale.ROOT, "%.1f", value);
        }
        return Double.toString(value);
    }

    private void emitNumericLiteralPush(TypeInfo type, int value) {
        if (PrimitiveTypeInfo.DOUBLE.equals(type)) {
            writeLine("PUSHD, " + formatDouble(value));
            return;
        }
        writeLine("PUSHI, " + value);
    }

    private void emitBinaryOp(BinaryExpressionKind operator, TypeInfo operandType) {
        boolean isDouble = PrimitiveTypeInfo.DOUBLE.equals(operandType);
        switch (operator) {
            case ADD -> writeLine(isDouble ? "DADD" : "IADD");
            case SUBTRACT -> writeLine(isDouble ? "DSUB" : "ISUB");
            case MULTIPLY -> writeLine(isDouble ? "DMUL" : "IMUL");
            case DIVIDE -> writeLine(isDouble ? "DDIV" : "IDIV");
            case MODULO -> writeLine("IMOD");
            case BITWISE_AND -> writeLine("IAND");
            case BITWISE_OR -> writeLine("IOR");
            case BITWISE_XOR -> writeLine("IXOR");
            case SHIFT_LEFT -> writeLine("ISHL");
            case SHIFT_RIGHT -> writeLine("ISHR");
            case LESS -> writeLine(isDouble ? "DLT" : "ILT");
            case LESS_EQUALS -> writeLine(isDouble ? "DLE" : "ILE");
            case GREATER -> writeLine(isDouble ? "DGT" : "IGT");
            case GREATER_EQUALS -> writeLine(isDouble ? "DGE" : "IGE");
            case EQUALS -> writeLine(isDouble ? "DEQ" : "IEQ");
            case NOT_EQUALS -> writeLine(isDouble ? "DNE" : "INE");
            case AND, OR, INVALID -> {
            }
        }
    }

    private void emitFrameLoad(StorageEntry storage) {
        int size = sizeOf(storage.getType());
        writeLine(switch (size) {
            case 1 -> "FLOAD1, " + storage.getOffsetBytes();
            case 2 -> "FLOAD2, " + storage.getOffsetBytes();
            case 8 -> "FLOAD8, " + storage.getOffsetBytes();
            default -> "FLOAD4, " + storage.getOffsetBytes();
        });
    }

    private void emitFrameStore(StorageEntry storage) {
        int size = sizeOf(storage.getType());
        writeLine(switch (size) {
            case 1 -> "FSTORE1, " + storage.getOffsetBytes();
            case 2 -> "FSTORE2, " + storage.getOffsetBytes();
            case 8 -> "FSTORE8, " + storage.getOffsetBytes();
            default -> "FSTORE4, " + storage.getOffsetBytes();
        });
    }

    private void emitDup(TypeInfo type) {
        int size = sizeOf(type);
        writeLine(switch (size) {
            case 1 -> "DUPB";
            case 2 -> "DUPC";
            case 8 -> "DUPD";
            default -> "DUPI";
        });
    }

    private void emitPop(TypeInfo type) {
        int size = sizeOf(type);
        writeLine(switch (size) {
            case 1 -> "POPB";
            case 2 -> "POPC";
            case 8 -> "POPD";
            default -> "POPI";
        });
    }

    private void emitReturn(TypeInfo type) {
        if (isVoidLike(type)) {
            writeLine("RET");
            return;
        }
        int size = sizeOf(type);
        writeLine(switch (size) {
            case 1 -> "RETB";
            case 2 -> "RETC";
            case 8 -> "RETD";
            default -> "RETI";
        });
    }

    private void emitZeroFor(TypeInfo type) {
        if (PrimitiveTypeInfo.DOUBLE.equals(type)) {
            writeLine("PUSHD, 0.0");
            return;
        }
        int size = sizeOf(type);
        writeLine(switch (size) {
            case 1 -> "PUSHB, 0";
            case 2 -> "PUSHC, 0";
            default -> "PUSHI, 0";
        });
    }

    private StorageEntry storageOf(ExpressionAstNode expression) {
        if (expression instanceof NameExpression name && name.getSymbolEntry() instanceof StorageEntry storage) {
            return storage;
        }
        return null;
    }

    private int sizeOf(TypeInfo type) {
        if (type == null) {
            return 4;
        }
        int size = type.sizeBytes();
        return size <= 0 ? 4 : size;
    }

    private boolean isVoidLike(TypeInfo type) {
        return type instanceof VoidTypeInfo || type instanceof UnknownTypeInfo || type == null;
    }

    private record LoopContext(String breakLabel, String continueLabel) {
    }

}
