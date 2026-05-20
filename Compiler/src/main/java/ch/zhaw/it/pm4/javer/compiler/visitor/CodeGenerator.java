package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.DataSection;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.*;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.EnumTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.StructTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.builtin.BuiltInFunction;
import ch.zhaw.it.pm4.javer.compiler.bytecode.VmLayout;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

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

    private void report(AstNode node, String message) {
        diagnostics.add(node.getSourceRange().start(), Severity.ERROR, message);
    }

    private void report(String message) {
        failed = true;
        System.err.println(message);
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
            emitReturn(VoidTypeInfo.INSTANCE, null);
            return;
        }
        report("Cannot generate fallthrough return for non-void function '" + function.getName() + "'.");
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
        emitPop(type, expression);
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
            emitReturn(VoidTypeInfo.INSTANCE, node);
            return;
        }
        if (isVoidLike(returnType)) {
            report(node, "Void function '" + currentFunction.getName() + "' cannot return a value.");
            return;
        }
        emitTyped(expression, returnType);
        emitReturn(returnType, node);
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        if (node.getInitializer() == null) {
            return;
        }
        StorageEntry storage = node.getSymbolEntry();
        emitStorageAddress(storage);
        emitTyped(node.getInitializer(), storage.getType());
        emitStore(storage.getType(), node);
    }

    @Override
    public void visit(AssignExpression node) {
        TypeInfo type = node.getTarget().getResultingType();
        AssignOperator operator = node.getOperator();
        if (operator == AssignOperator.ASSIGN) {
            if (!emitAddress(node.getTarget())) {
                return;
            }
            emitTyped(node.getValue(), type);
        } else {
            if (!emitAddress(node.getTarget())) {
                return;
            }
            if (!emitAddress(node.getTarget())) {
                return;
            }
            emitLoad(type, node);
            emitTyped(node.getValue(), type);
            BinaryExpressionKind binaryOperator = compoundAssignToBinary(node);
            if (binaryOperator == BinaryExpressionKind.INVALID) {
                return;
            }
            emitBinaryOp(binaryOperator, type, node);
        }
        emitStore(type, node);
        if (!emitAddress(node.getTarget())) {
            return;
        }
        emitLoad(type, node);
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
            case ASSIGN, INVALID -> {
                report(node, "Cannot generate compound assignment for operator: " + node.getOperator() + ".");
                yield BinaryExpressionKind.INVALID;
            }
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
        emitBinaryOp(operator, operandType, node);
        emitConversion(binaryResultType(operator, operandType), node.getResultingType(), node);
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
            case INVALID -> false;
        };
    }

    private boolean isIntegerOnlyBinary(BinaryExpressionKind operator) {
        return switch (operator) {
            case MODULO, BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT -> true;
            case ADD, SUBTRACT, MULTIPLY, DIVIDE,
                 AND, OR, EQUALS, NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> false;
            case INVALID -> false;
        };
    }

    private boolean isComparison(BinaryExpressionKind operator) {
        return switch (operator) {
            case EQUALS, NOT_EQUALS, LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> true;
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
                 BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT,
                 AND, OR -> false;
            case INVALID -> false;
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
            case INVALID -> report(node, "Cannot generate invalid unary operator.");
        }
    }

    private void emitPrefixStep(ExpressionAstNode target, int delta) {
        TypeInfo type = target.getResultingType();
        if (!emitAddress(target)) {
            return;
        }
        if (!emitAddress(target)) {
            return;
        }
        emitLoad(type, target);
        emitNumericLiteralPush(type, delta);
        emitBinaryOp(BinaryExpressionKind.ADD, type, target);
        emitStore(type, target);
        if (!emitAddress(target)) {
            return;
        }
        emitLoad(type, target);
    }

    @Override
    public void visit(PostfixExpression node) {
        ExpressionAstNode target = node.getOperand();
        int delta = switch (node.getKind()) {
            case INCREMENT -> 1;
            case DECREMENT -> -1;
            case INVALID -> {
                report(node, "Cannot generate invalid postfix operator.");
                yield 0;
            }
        };
        TypeInfo type = target.getResultingType();
        if (!emitAddress(target)) {
            return;
        }
        emitLoad(type, node);
        if (!emitAddress(target)) {
            return;
        }
        if (!emitAddress(target)) {
            return;
        }
        emitLoad(type, node);
        emitNumericLiteralPush(type, delta);
        emitBinaryOp(BinaryExpressionKind.ADD, type, node);
        emitStore(type, node);
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
        if (!emitAddress(node)) {
            return;
        }
        emitLoad(node.getResultingType(), node);
    }

    @Override
    public void visit(MemberAccessExpression node) {
        if (node.getResolvedEnumValue() != null) {
            writeLine("PUSHI, " + node.getResolvedEnumValue().getValue());
            return;
        }
        if (!emitAddress(node)) {
            return;
        }
        emitLoad(node.getResolvedField().getType(), node);
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
        // TODO has been checked in typechecker
        if (!(node.getResultingType() instanceof ArrayTypeInfo(TypeInfo elementType))) {
            report(node, "Cannot generate new expression because the resulting type is not a struct or array.");
            return;
        }
        int elementSize = memoryBytes(elementType, node);
        ArrayInitExpression init = node.getArrayInit();

        if (node.getDimensions().isEmpty()) {
            if (init == null) {
                report(node, "Cannot generate array allocation without dimensions or initializer.");
                return;
            }
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
        if (!(node.getResultingType() instanceof ArrayTypeInfo arrayType)) {
            report(node, "Cannot generate array initializer because its resulting type is not an array.");
            return;
        }
        TypeInfo elementType = arrayType.elementType();
        int elementSize = memoryBytes(elementType, node);
        writeLine("PUSHI, " + (node.getElements().size() * elementSize));
        writeLine("NEW");
        initializeArrayElements(node.getElements(), elementType, elementSize);
    }

    private void initializeArrayElements(List<ExpressionAstNode> elements, TypeInfo elementType, int elementSize) {
        for (int i = 0; i < elements.size(); i++) {
            emitDup(PrimitiveTypeInfo.INT, elements.get(i));
            writeLine("PUSHI, " + (i * elementSize));
            writeLine("IADD");
            emitTyped(elements.get(i), elementType);
            emitStore(elementType, elements.get(i));
        }
    }

    @Override
    public void visit(NameExpression node) {
        SymbolEntry entry = node.getSymbolEntry();
        if (entry instanceof StorageEntry storage) {
            emitFrameLoad(storage, node);
            return;
        }
        if (entry instanceof EnumValueEntry enumValue) {
            writeLine("PUSHI, " + enumValue.getValue());
            return;
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
        FunctionEntry function = node.getResolvedFunction();
        BuiltInFunction builtIn = BuiltInFunction.find(function.getName());
        emitTyped(node.getArguments().getFirst(), builtIn.getParameterType());
        writeLine(builtIn.getVmInstruction());
    }

    private void emitTyped(ExpressionAstNode expression, TypeInfo expectedType) {
        expression.accept(this);
        emitConversion(expression.getResultingType(), expectedType, expression);
    }

    private void emitConversion(TypeInfo from, TypeInfo to, AstNode context) {
        if (from.equals(to)) {
            return;
        }
        if (from instanceof UnknownTypeInfo || to instanceof UnknownTypeInfo) {
            report(context, "Cannot generate conversion involving unresolved type: " + from + " to " + to + ".");
            return;
        }
        if (!(from instanceof PrimitiveTypeInfo(PrimitiveTypeKind kind)) || !(to instanceof PrimitiveTypeInfo(
                PrimitiveTypeKind kind1
        ))) {
            report(context, "Cannot generate conversion from " + from + " to " + to + ".");
            return;
        }
        if (kind == kind1) {
            return;
        }
        switch (kind) {
            case INT -> {
                if (kind1 == PrimitiveTypeKind.DOUBLE) {
                    writeLine("I2D");
                } else {
                    report(context, "Cannot generate conversion from " + from + " to " + to + ".");
                }
            }
            case DOUBLE -> {
                if (kind1 == PrimitiveTypeKind.INT) {
                    writeLine("D2I");
                } else {
                    report(context, "Cannot generate conversion from " + from + " to " + to + ".");
                }
            }
            default -> report(context, "Cannot generate conversion from " + from + " to " + to + ".");
        }
    }

    private void emitAsBoolean(ExpressionAstNode expression) {
        expression.accept(this);
        TypeInfo type = expression.getResultingType();
        if (PrimitiveTypeInfo.DOUBLE.equals(type)) {
            writeLine("PUSHD, 0.0");
            writeLine("DNE");
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

    private void emitBinaryOp(BinaryExpressionKind operator, TypeInfo operandType, AstNode context) {
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
            // wrong
            case AND, OR -> report(context, "Cannot generate logical operator as a primitive binary instruction.");
            case INVALID -> report(context, "Cannot generate invalid binary operator.");
        }
    }

    private void emitBinaryOp(BinaryExpression node, TypeInfo operandType) {
        switch (node.getOperator()) {
            case AND -> emitLogicalAnd(node);
            case OR -> emitLogicalOr(node);
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO,
                 BITWISE_AND, BITWISE_OR, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT,
                 LESS, LESS_EQUALS, GREATER, GREATER_EQUALS, EQUALS, NOT_EQUALS ->
                    emitBinaryOp(node.getOperator(), operandType, node);
            case INVALID -> report(node, "Cannot generate invalid binary operator.");
        }
    }

    private void emitFrameLoad(StorageEntry storage, AstNode context) {
        emitStorageAddress(storage);
        emitLoad(storage.getType(), context);
    }

    private boolean emitAddress(ExpressionAstNode expression) {
        if (expression instanceof NameExpression name && name.getSymbolEntry() instanceof StorageEntry storage) {
            emitStorageAddress(storage);
            return true;
        }
        if (expression instanceof IndexExpression index) {
            return emitIndexAddress(index);
        }
        if (expression instanceof MemberAccessExpression member && member.getResolvedField() != null) {
            return emitMemberAddress(member);
        }
        return false;
    }

    private void emitStorageAddress(StorageEntry storage) {
        writeLine("LOCAL, " + storage.getOffsetBytes());
    }

    private boolean emitIndexAddress(IndexExpression node) {
        ArrayTypeInfo arrayType = (ArrayTypeInfo) node.getTarget().getResultingType();
        TypeInfo elementType = arrayType.elementType();
        node.getTarget().accept(this);
        emitTyped(node.getIndex(), PrimitiveTypeInfo.INT);
        int elementSize = memoryBytes(elementType, node);
        if (elementSize != VmLayout.BYTE_BYTES) {
            writeLine("PUSHI, " + elementSize);
            writeLine("IMUL");
        }
        writeLine("IADD");
        return true;
    }

    private boolean emitMemberAddress(MemberAccessExpression node) {
        FieldEntry field = node.getResolvedField();
        node.getTarget().accept(this);
        if (field.getOffsetBytes() != 0) {
            writeLine("PUSHI, " + field.getOffsetBytes());
            writeLine("IADD");
        }
        return true;
    }

    private void emitLoad(TypeInfo type, AstNode context) {
        writeLine(memoryWidth(type, context).loadInstruction());
    }

    private void emitStore(TypeInfo type, AstNode context) {
        writeLine(memoryWidth(type, context).storeInstruction());
    }

    private void emitDup(TypeInfo type, AstNode context) {
        writeLine("DUP, " + stackBytes(type, context));
    }

    private void emitPop(TypeInfo type, AstNode context) {
        writeLine("POP, " + stackBytes(type, context));
    }

    private void emitReturn(TypeInfo type, AstNode context) {
        writeLine("RET, " + returnBytes(type, context));
    }

    private int memoryBytes(TypeInfo type, AstNode context) {
        return memoryWidth(type, context).bytes();
    }

    private VmLayout.MemoryWidth memoryWidth(TypeInfo type, AstNode context) {
        return VmLayout.memoryWidth(type);
    }

    private int stackBytes(TypeInfo type, AstNode context) {
        if (!hasStackRepresentation(type)) {
            report(context, "Cannot generate stack operation for type: " + type + ".");
            return VmLayout.WORD_BYTES;
        }
        return VmLayout.stackBytes(type);
    }

    private int returnBytes(TypeInfo type, AstNode context) {
        if (isVoidLike(type)) {
            return 0;
        }
        return stackBytes(type, context);
    }

    private boolean hasStackRepresentation(TypeInfo type) {
        if (PrimitiveTypeInfo.DOUBLE.equals(type)) {
            return true;
        }
        if (type instanceof PrimitiveTypeInfo(PrimitiveTypeKind kind)) {
            return switch (kind) {
                case BOOL, CHAR, INT, STRING -> true;
                case DOUBLE, INVALID -> false;
            };
        }
        return type instanceof EnumTypeInfo || type instanceof ArrayTypeInfo || type instanceof StructTypeInfo;
    }

    private boolean isVoidLike(TypeInfo type) {
        return type instanceof VoidTypeInfo;
    }

    private record LoopContext(String breakLabel, String continueLabel) {
    }

}
