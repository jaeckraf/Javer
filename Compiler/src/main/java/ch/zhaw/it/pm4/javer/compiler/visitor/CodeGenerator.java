package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.DataSection;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.*;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.EnumTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.NullTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.StructTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.builtin.BuiltInFunction;
import ch.zhaw.it.pm4.javer.compiler.bytecode.VmLayout;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Emits VM bytecode from a semantically checked AST.
 */
@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends AstNodeVisitorBase {

    private final DiagnosticBag diagnostics;
    private final Path outputFile;
    private final StringBuilder output = new StringBuilder();
    private DataSection dataSection;
    private FunctionEntry currentFunction;
    private final Deque<LoopContext> loopContexts = new ArrayDeque<>();
    private int nextLabelId;
    private boolean outputDirectoryReady = true;
    private boolean failed;

    /**
     * Creates a code generator that reports invariant failures as diagnostics.
     */
    public CodeGenerator(DiagnosticBag diagnostics, String outputFilePath) {
        this.diagnostics = Objects.requireNonNull(diagnostics, "DiagnosticBag must not be null");
        this.outputFile = Path.of(outputFilePath);
        prepareOutputDirectory();
    }

    /**
     * Generates bytecode for a complete compilation unit and writes it to disk.
     *
     * @param node root compilation unit
     */
    public boolean generate(CompilationUnit node) {
        output.setLength(0);
        loopContexts.clear();
        nextLabelId = 0;

        if (!outputDirectoryReady) {
            deleteOutputFile();
            return false;
        }

        try {
            node.accept(this);

            if (failed || diagnostics.hasErrors()) {
                deleteOutputFile();
                return false;
            }

            Files.writeString(
                    outputFile,
                    output.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            return true;
        } catch (IOException exception) {
            report("Could not write bytecode output file: " + outputFile + ".");
            deleteOutputFile();
            return false;
        } finally {
            dataSection = null;
            currentFunction = null;
        }
    }

    protected void writeLine(String line) {
        output.append(line).append(System.lineSeparator());
    }

    private void writeLabel(String label) {
        writeLine(label + ":");
    }

    private String nextLabel(String prefix) {
        return prefix + "_" + nextLabelId++;
    }

    private void prepareOutputDirectory() {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            outputDirectoryReady = false;
            report("Could not create bytecode output directory: " + parent + ".");
        }
    }

    private void deleteOutputFile() {
        try {
            Files.deleteIfExists(outputFile);
        } catch (IOException exception) {
            report("Could not delete incomplete bytecode output file: " + outputFile + ".");
        }
    }

    private void report(String message) {
        failed = true;
        System.err.println(message);
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
    public void visit(FunctionDeclaration node) {
        FunctionEntry function = node.getSymbolEntry();
        currentFunction = function;
        writeLine("_" + node.getName() + ":");
        writeLine("ENTER, " + function.getFrameSizeBytes());
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
            emitReturn(VoidTypeInfo.INSTANCE);
        }
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
        return !(expression instanceof CallExpression call) || !isVoidReturningCall(call);
    }

    private boolean isVoidReturningCall(CallExpression call) {
        FunctionEntry function = call.getResolvedFunction();
        return isVoidLike(function.getReturnType());
    }

    @Override
    public void visit(IfStatement node) {
        String elseLabel = nextLabel("if_else");
        String endLabel = nextLabel("if_end");
        emitAsBoolean(node.getCondition());
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
        emitAsBoolean(node.getCondition());
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
        emitAsBoolean(node.getCondition());
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
            emitAsBoolean(node.getCondition());
        } else {
            writeLine("PUSHI, 1");
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
        writeLine("JUMP, " + loopContexts.peek().breakLabel());
    }

    @Override
    public void visit(ContinueStatement node) {
        writeLine("JUMP, " + loopContexts.peek().continueLabel());
    }

    @Override
    public void visit(ReturnStatement node) {
        ExpressionAstNode expression = node.getExpression();
        TypeInfo returnType = currentFunction.getReturnType();
        if (expression == null) {
            emitReturn(VoidTypeInfo.INSTANCE);
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
        emitStorageAddress(storage);
        emitTyped(node.getInitializer(), storage.getType());
        emitStore(storage.getType());
    }

    @Override
    public void visit(AssignExpression node) {
        TypeInfo type = node.getTarget().getResultingType();
        AssignOperator operator = node.getOperator();
        if (operator == AssignOperator.ASSIGN) {
            emitAddress(node.getTarget());
            emitTyped(node.getValue(), type);
        } else {
            emitAddress(node.getTarget());
            emitAddress(node.getTarget());
            emitLoad(type);
            emitTyped(node.getValue(), type);
            BinaryExpressionKind binaryOperator = compoundAssignToBinary(node);
            emitBinaryOp(binaryOperator, type);
        }
        emitStore(type);
        emitAddress(node.getTarget());
        emitLoad(type);
    }

    private BinaryExpressionKind compoundAssignToBinary(AssignExpression node) {
        return switch (node.getOperator()) {
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
            case ASSIGN, INVALID -> throw new IllegalStateException("Unexpected compound assignment operator: " + node.getOperator());
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
        TypeInfo operandType = binaryOperandType(operator, node);
        if (isLogical(operator)) {
            emitBinaryOp(node, operandType);
            return;
        }
        emitTyped(node.getLeft(), operandType);
        emitTyped(node.getRight(), operandType);
        emitBinaryOp(operator, operandType);
        emitConversion(binaryResultType(operator, operandType), node.getResultingType());
    }

    private TypeInfo binaryOperandType(BinaryExpressionKind operator, BinaryExpression node) {
        if (isLogical(operator)) {
            return PrimitiveTypeInfo.BOOL;
        }
        if (isIntegerOnlyBinary(operator)) {
            return PrimitiveTypeInfo.INT;
        }
        if (operator == BinaryExpressionKind.EQUALS || operator == BinaryExpressionKind.NOT_EQUALS) {
            return equalityOperandType(node);
        }
        TypeInfo left = node.getLeft().getResultingType();
        TypeInfo right = node.getRight().getResultingType();
        if (PrimitiveTypeInfo.DOUBLE.equals(left) || PrimitiveTypeInfo.DOUBLE.equals(right)) {
            return PrimitiveTypeInfo.DOUBLE;
        }
        return PrimitiveTypeInfo.INT;
    }

    private TypeInfo equalityOperandType(BinaryExpression node) {
        TypeInfo left = node.getLeft().getResultingType();
        TypeInfo right = node.getRight().getResultingType();
        if (PrimitiveTypeInfo.DOUBLE.equals(left) || PrimitiveTypeInfo.DOUBLE.equals(right)) {
            return PrimitiveTypeInfo.DOUBLE;
        }
        if (left.equals(right)) {
            return left;
        }
        if (left instanceof NullTypeInfo) {
            return right;
        }
        if (right instanceof NullTypeInfo) {
            return left;
        }
        return PrimitiveTypeInfo.INT;
    }

    private TypeInfo binaryResultType(BinaryExpressionKind operator, TypeInfo operandType) {
        if (isLogical(operator) || isComparison(operator)) {
            return PrimitiveTypeInfo.BOOL;
        }
        return operandType;
    }

    private boolean isLogical(BinaryExpressionKind operator) {
        return switch (operator) {
            case AND, OR -> true;
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
                 BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT,
                 EQUALS, NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> false;
            case INVALID -> throw new IllegalStateException("Unexpected binary operator: " + operator);
        };
    }

    private boolean isIntegerOnlyBinary(BinaryExpressionKind operator) {
        return switch (operator) {
            case MODULO, BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT -> true;
            case ADD, SUBTRACT, MULTIPLY, DIVIDE,
                 AND, OR, EQUALS, NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> false;
            case INVALID -> throw new IllegalStateException("Unexpected binary operator: " + operator);
        };
    }

    private boolean isComparison(BinaryExpressionKind operator) {
        return switch (operator) {
            case EQUALS, NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> true;
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
                 BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT,
                 AND, OR -> false;
            case INVALID -> throw new IllegalStateException("Unexpected binary operator: " + operator);
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
        writeLine("PUSHI, 0");
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
        writeLine("PUSHI, 1");
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
                writeLine("PUSHI, 0");
                writeLine("JUMP, " + endLabel);
                writeLabel(falseLabel);
                writeLine("PUSHI, 1");
                writeLabel(endLabel);
            }
            case PRE_INCREMENT -> emitPrefixStep(node.getOperand(), 1);
            case PRE_DECREMENT -> emitPrefixStep(node.getOperand(), -1);
            case INVALID -> throw new IllegalStateException("Unexpected unary operator: " + node.getKind());
        }
    }

    private void emitPrefixStep(ExpressionAstNode target, int delta) {
        TypeInfo type = target.getResultingType();
        emitAddress(target);
        emitAddress(target);
        emitLoad(type);
        emitNumericLiteralPush(type, delta);
        emitBinaryOp(BinaryExpressionKind.ADD, type);
        emitStore(type);
        emitAddress(target);
        emitLoad(type);
    }

    @Override
    public void visit(PostfixExpression node) {
        ExpressionAstNode target = node.getOperand();
        int delta = switch (node.getKind()) {
            case INCREMENT -> 1;
            case DECREMENT -> -1;
            case INVALID -> throw new IllegalStateException("Unexpected postfix operator: " + node.getKind());
        };
        TypeInfo type = target.getResultingType();
        emitAddress(target);
        emitLoad(type);
        emitAddress(target);
        emitAddress(target);
        emitLoad(type);
        emitNumericLiteralPush(type, delta);
        emitBinaryOp(BinaryExpressionKind.ADD, type);
        emitStore(type);
    }

    @Override
    public void visit(CallExpression node) {
        FunctionEntry function = node.getResolvedFunction();
        if (function.isBuiltIn()) {
            emitPrintBuiltin(node);
            return;
        }
        List<ParameterEntry> parameters = new ArrayList<>(function.getScope().getParameters().values());
        for (int i = 0; i < node.getArguments().size(); i++) {
            ExpressionAstNode argument = node.getArguments().get(i);
            TypeInfo expected = parameters.get(i).getType();
            emitTyped(argument, expected);
        }
        writeLine("CALL, " + function.getLabel() + ", " + function.getParameterBytes());
    }

    @Override
    public void visit(IndexExpression node) {
        emitAddress(node);
        emitLoad(node.getResultingType());
    }

    @Override
    public void visit(MemberAccessExpression node) {
        if (node.getResolvedEnumValue() != null) {
            writeLine("PUSHI, " + node.getResolvedEnumValue().getValue());
            return;
        }
        emitAddress(node);
        emitLoad(node.getResolvedField().getType());
    }

    @Override
    public void visit(NewExpression node) {
        if (node.getResultingType() instanceof StructTypeInfo(
                StructEntry entry
        )) {
            int sizeBytes = entry.getSizeBytes();
            writeLine("PUSHI, " + sizeBytes);
            writeLine("NEW");
            return;
        }
        ArrayTypeInfo arrayType = (ArrayTypeInfo) node.getResultingType();
        TypeInfo elementType = arrayType.elementType();
        int elementSize = memoryBytes(elementType);
        ArrayInitExpression init = node.getArrayInit();

        if (node.getDimensions().isEmpty()) {
            writeLine("PUSHI, " + (init.getElements().size() * elementSize));
        } else {
            emitTyped(node.getDimensions().getFirst(), PrimitiveTypeInfo.INT);
            if (elementSize != VmLayout.BYTE_BYTES) {
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
        ArrayTypeInfo arrayType = (ArrayTypeInfo) node.getResultingType();
        TypeInfo elementType = arrayType.elementType();
        int elementSize = memoryBytes(elementType);
        writeLine("PUSHI, " + (node.getElements().size() * elementSize));
        writeLine("NEW");
        initializeArrayElements(node.getElements(), elementType, elementSize);
    }

    private void initializeArrayElements(List<ExpressionAstNode> elements, TypeInfo elementType, int elementSize) {
        for (int i = 0; i < elements.size(); i++) {
            emitDup(PrimitiveTypeInfo.INT);
            writeLine("PUSHI, " + (i * elementSize));
            writeLine("IADD");
            emitTyped(elements.get(i), elementType);
            emitStore(elementType);
        }
    }

    @Override
    public void visit(NameExpression node) {
        SymbolEntry entry = node.getSymbolEntry();
        if (entry instanceof EnumValueEntry enumValue) {
            writeLine("PUSHI, " + enumValue.getValue());
            return;
        }
        emitFrameLoad((StorageEntry) entry);
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
        FunctionEntry function = node.getResolvedFunction();
        BuiltInFunction builtIn = BuiltInFunction.find(function.getName());
        emitTyped(node.getArguments().getFirst(), builtIn.getParameterType());
        writeLine(builtIn.getVmInstruction());
    }

    private void emitTyped(ExpressionAstNode expression, TypeInfo expectedType) {
        expression.accept(this);
        emitConversion(expression.getResultingType(), expectedType);
    }

    private void emitConversion(TypeInfo from, TypeInfo to) {
        if (from.equals(to)) {
            return;
        }
        if (from instanceof NullTypeInfo && isReferenceType(to)) {
            return;
        }
        PrimitiveTypeKind kind = ((PrimitiveTypeInfo) from).kind();
        PrimitiveTypeKind kind1 = ((PrimitiveTypeInfo) to).kind();
        if (kind == kind1) {
            return;
        }
        switch (kind) {
            case INT -> {
                if (kind1 == PrimitiveTypeKind.DOUBLE) {
                    writeLine("I2D");
                } else {
                    throw new IllegalStateException("Unexpected conversion from " + from + " to " + to);
                }
            }
            case DOUBLE -> {
                if (kind1 == PrimitiveTypeKind.INT) {
                    writeLine("D2I");
                } else {
                    throw new IllegalStateException("Unexpected conversion from " + from + " to " + to);
                }
            }
            default -> throw new IllegalStateException("Unexpected conversion from " + from + " to " + to);
        }
    }

    private void emitAsBoolean(ExpressionAstNode expression) {
        expression.accept(this);
        TypeInfo type = expression.getResultingType();
        if (PrimitiveTypeInfo.DOUBLE.equals(type)) {
            String falseLabel = nextLabel("bool_false");
            String endLabel = nextLabel("bool_end");
            writeLine("DUP, " + VmLayout.DOUBLE_BYTES);
            writeLine("PUSHD, 0.0");
            writeLine("DEQ");
            writeLine("JUMPT, " + falseLabel);
            writeLine("DUP, " + VmLayout.DOUBLE_BYTES);
            writeLine("DEQ");
            writeLine("JUMP, " + endLabel);
            writeLabel(falseLabel);
            writeLine("POP, " + VmLayout.DOUBLE_BYTES);
            writeLine("PUSHI, 0");
            writeLabel(endLabel);
            return;
        }
        if (type instanceof EnumTypeInfo) {
            writeLine("POP, " + VmLayout.WORD_BYTES);
            writeLine("PUSHI, 1");
        }
    }

    private void emitLiteralPush(LiteralExpression<?> node) {
        switch (node.getKind()) {
            case INT -> writeLine("PUSHI, " + node.getValue());
            case DOUBLE -> writeLine("PUSHD, " + formatDouble((Double) node.getValue()));
            case BOOLEAN -> writeLine("PUSHI, " + (Boolean.TRUE.equals(node.getValue()) ? "1" : "0"));
            case CHAR -> writeLine("PUSHI, " + (int) (Character) node.getValue());
            case STRING -> {
                var entry = dataSection.internString((String) node.getValue());
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
            case AND, OR -> throw new IllegalStateException("Logical operators require expression-level emission.");
            case INVALID -> throw new IllegalStateException("Unexpected binary operator: " + operator);
        }
    }

    private void emitBinaryOp(BinaryExpression node, TypeInfo operandType) {
        switch (node.getOperator()) {
            case AND -> emitLogicalAnd(node);
            case OR -> emitLogicalOr(node);
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
                 BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT,
                 LESS, LESS_EQUALS, GREATER, GREATER_EQUALS, EQUALS, NOT_EQUALS ->
                    emitBinaryOp(node.getOperator(), operandType);
            case INVALID -> throw new IllegalStateException("Unexpected binary operator: " + node.getOperator());
        }
    }

    private void emitFrameLoad(StorageEntry storage) {
        emitStorageAddress(storage);
        emitLoad(storage.getType());
    }

    private void emitAddress(ExpressionAstNode expression) {
        if (expression instanceof NameExpression name && name.getSymbolEntry() instanceof StorageEntry storage) {
            emitStorageAddress(storage);
            return;
        }
        if (expression instanceof IndexExpression index) {
            emitIndexAddress(index);
            return;
        }
        emitMemberAddress((MemberAccessExpression) expression);
    }

    private void emitStorageAddress(StorageEntry storage) {
        writeLine("LOCAL, " + storage.getOffsetBytes());
    }

    private void emitIndexAddress(IndexExpression node) {
        ArrayTypeInfo arrayType = (ArrayTypeInfo) node.getTarget().getResultingType();
        TypeInfo elementType = arrayType.elementType();
        node.getTarget().accept(this);
        emitTyped(node.getIndex(), PrimitiveTypeInfo.INT);
        int elementSize = memoryBytes(elementType);
        if (elementSize != VmLayout.BYTE_BYTES) {
            writeLine("PUSHI, " + elementSize);
            writeLine("IMUL");
        }
        writeLine("IADD");
    }

    private void emitMemberAddress(MemberAccessExpression node) {
        FieldEntry field = node.getResolvedField();
        node.getTarget().accept(this);
        if (field.getOffsetBytes() != 0) {
            writeLine("PUSHI, " + field.getOffsetBytes());
            writeLine("IADD");
        }
    }

    private void emitLoad(TypeInfo type) {
        writeLine(memoryWidth(type).loadInstruction());
    }

    private void emitStore(TypeInfo type) {
        writeLine(memoryWidth(type).storeInstruction());
    }

    private void emitDup(TypeInfo type) {
        writeLine("DUP, " + stackBytes(type));
    }

    private void emitPop(TypeInfo type) {
        writeLine("POP, " + stackBytes(type));
    }

    private void emitReturn(TypeInfo type) {
        writeLine("RET, " + returnBytes(type));
    }

    private int memoryBytes(TypeInfo type) {
        return memoryWidth(type).bytes();
    }

    private VmLayout.MemoryWidth memoryWidth(TypeInfo type) {
        return VmLayout.memoryWidth(type);
    }

    private int stackBytes(TypeInfo type) {
        return VmLayout.stackBytes(type);
    }

    private int returnBytes(TypeInfo type) {
        if (isVoidLike(type)) {
            return 0;
        }
        return stackBytes(type);
    }

    private boolean isVoidLike(TypeInfo type) {
        return type instanceof VoidTypeInfo;
    }

    private boolean isReferenceType(TypeInfo type) {
        return PrimitiveTypeInfo.STRING.equals(type)
                || type instanceof ArrayTypeInfo
                || type instanceof StructTypeInfo;
    }

    private record LoopContext(String breakLabel, String continueLabel) {
    }

}
