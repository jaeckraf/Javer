package ch.zhaw.it.pm4.javer.compiler.visitor;

import java.util.Set;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionDeclaration;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.FunctionParameter;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.TypeAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.GlobalScope;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.*;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.ArrayTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.EnumTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.StructTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.UnknownTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.VoidTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.Severity;

public class TypeCheckVisitor extends AstNodeVisitorBase {

    private static final Set<String> VOID_BUILT_INS = Set.of("printi", "prints", "println");

    private final DiagnosticBag diagnosticBag;
    private GlobalScope globalScope;
    private TypeInfo currentFunctionReturnType = UnknownTypeInfo.INSTANCE;

    public TypeCheckVisitor() {
        this(null);
    }

    public TypeCheckVisitor(DiagnosticBag diagnosticBag) {
        this.diagnosticBag = diagnosticBag;
    }

    @Override
    public void visit(CompilationUnit node) {
        globalScope = node.getGlobalScope();
        super.visit(node);
    }

    @Override
    public void visit(LiteralExpression<?> node) {
        node.setResultingType(switch (node.getKind()) {
            case INT -> PrimitiveTypeInfo.INT;
            case DOUBLE -> PrimitiveTypeInfo.DOUBLE;
            case BOOLEAN -> PrimitiveTypeInfo.BOOL;
            case STRING -> PrimitiveTypeInfo.STRING;
            case CHAR -> PrimitiveTypeInfo.CHAR;
            case NULL -> UnknownTypeInfo.INSTANCE;
        });
    }

    @Override
    public void visit(NameExpression node) {
        SymbolEntry entry = node.getSymbolEntry();
        if (entry instanceof StorageEntry storageEntry) {
            node.setResultingType(storageEntry.getType());
            return;
        }
        if (entry instanceof EnumValueEntry enumValueEntry) {
            node.setResultingType(new EnumTypeInfo(enumValueEntry.getOwnerEnum()));
            return;
        }
        node.setResultingType(UnknownTypeInfo.INSTANCE);
    }

    //Function Declaration

    public void visit(FunctionDeclaration node) {
        TypeInfo previous = currentFunctionReturnType;

        currentFunctionReturnType = node.getSymbolEntry() != null
                ? node.getSymbolEntry().getReturnType()
                : resolveType(node.getReturnType());
        super.visit(node);
        currentFunctionReturnType = previous;
    }

    @Override
    public void visit(FunctionParameter node) {
        node.getType().accept(this);

        TypeInfo resolved = resolveType(node.getType());
        if (resolved instanceof UnknownTypeInfo) {
            if (diagnosticBag != null) {
                diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR,
                        "Undefined type for parameter: " + node.getName());
            }
        }
    }

    @Override
    public void visit(ReturnStatement node) {
        super.visit(node);

        TypeInfo actualReturnType = node.getExpression() == null
                ? VoidTypeInfo.INSTANCE
                : node.getExpression().getResultingType();

        TypeInfo expectedReturnType = currentFunctionReturnType == null
                ? UnknownTypeInfo.INSTANCE
                : currentFunctionReturnType;

        // void function darf keinen Wert returnen
        if (expectedReturnType instanceof VoidTypeInfo && node.getExpression() != null) {
            report(node, "Void function must not return a value.");
            return;
        }

        // non-void function muss Wert returnen
        if (!(expectedReturnType instanceof VoidTypeInfo) && node.getExpression() == null) {
            report(node, "Missing return value. Expected: " + expectedReturnType);
            return;
        }

        // Typprüfung
        if (!isAssignable(expectedReturnType, actualReturnType)) {
            report(node, "Return type mismatch. Expected: " + expectedReturnType + ", actual: " + actualReturnType);
        }
    }

    private boolean isAssignable(TypeInfo expected, TypeInfo actual) {
        if (expected instanceof UnknownTypeInfo || actual instanceof UnknownTypeInfo) {
            return true;
        }

        if (expected.equals(actual)) {
            return true;
        }

        return PrimitiveTypeInfo.DOUBLE.equals(expected) && PrimitiveTypeInfo.INT.equals(actual);
    }

    private void report(AstNode node, String message) {
        if (diagnosticBag != null) {
            diagnosticBag.add(node.getSourceRange().start(), Severity.ERROR, message);
        }
    }


    //Function Call

    @Override
    public void visit(CallExpression node) {
        super.visit(node);

        FunctionEntry function = node.getResolvedFunction();
        if (function == null) {
            if (VOID_BUILT_INS.contains(node.getFunctionName())) {
                node.setResultingType(VoidTypeInfo.INSTANCE);
                return;
            }
            node.setResultingType(UnknownTypeInfo.INSTANCE);
            return;
        }

        var parameters = function.getScope().getParameters().values();

        if (node.getArguments().size() != parameters.size()) {
            report(node, "Argument count mismatch for function " + node.getFunctionName());
            node.setResultingType(UnknownTypeInfo.INSTANCE);
            return;
        }

        int index = 0;
        for (var parameter : parameters) {
            TypeInfo expected = parameter.getType();
            TypeInfo actual = node.getArguments().get(index).getResultingType();

            if (!isAssignable(expected, actual)) {
                report(node, "Argument " + (index + 1) + " type mismatch: expected "
                        + expected + ", got " + actual);
            }
            index++;
        }

        node.setResultingType(function.getReturnType());
    }


    @Override
    public void visit(BinaryExpression node) {
        super.visit(node);

        TypeInfo left = node.getLeft().getResultingType();
        TypeInfo right = node.getRight().getResultingType();

        TypeInfo result = switch (node.getOperator()) {
            case OR, AND -> {
                if (!isBoolean(left) || !isBoolean(right)) {
                    report(node, "Logical operator requires boolean operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.BOOL;
            }

            case EQUALS, NOT_EQUALS -> {
                if (!isComparable(left, right)) {
                    report(node, "Equality operator requires compatible operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.BOOL;
            }

            case LESS, LESS_EQUALS, GREATER, GREATER_EQUALS -> {
                if (!isNumeric(left) || !isNumeric(right)) {
                    report(node, "Comparison operator requires numeric operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.BOOL;
            }

            case ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO -> {
                if (!isNumeric(left) || !isNumeric(right)) {
                    report(node, "Arithmetic operator requires numeric operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield numericResult(left, right);
            }

            case BITWISE_OR, BITWISE_AND, BITWISE_XOR, SHIFT_LEFT, SHIFT_RIGHT -> {
                if (!isInteger(left) || !isInteger(right)) {
                    report(node, "Bitwise and shift operators require integer operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.INT;
            }

            case INVALID -> UnknownTypeInfo.INSTANCE;
        };

        node.setResultingType(result);
    }

    private boolean isBoolean(TypeInfo type) {
        return PrimitiveTypeInfo.BOOL.equals(type);
    }

    private boolean isInteger(TypeInfo type) {
        return PrimitiveTypeInfo.INT.equals(type);
    }

    private boolean isNumeric(TypeInfo type) {
        return PrimitiveTypeInfo.INT.equals(type) || PrimitiveTypeInfo.DOUBLE.equals(type);
    }

    private boolean isComparable(TypeInfo left, TypeInfo right) {
        if (left instanceof UnknownTypeInfo || right instanceof UnknownTypeInfo) {
            return true;
        }

        if (left.equals(right)) {
            return true;
        }

        // falls ihr int/double-Mischung erlauben wollt
        return isNumeric(left) && isNumeric(right);
    }


    @Override
    public void visit(AssignExpression node) {
        super.visit(node);

        TypeInfo targetType = node.getTarget() == null
                ? UnknownTypeInfo.INSTANCE
                : node.getTarget().getResultingType();

        TypeInfo valueType = node.getValue() == null
                ? UnknownTypeInfo.INSTANCE
                : node.getValue().getResultingType();

        if (!isAssignableTarget(node.getTarget())) {
            report(node, "Left side of assignment is not assignable.");
            node.setResultingType(UnknownTypeInfo.INSTANCE);
            return;
        }

        TypeInfo assignedValueType = switch (node.getOperator()) {
            case ASSIGN -> valueType;

            case ADD_ASSIGN, SUB_ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN -> {
                if (!isNumeric(targetType) || !isNumeric(valueType)) {
                    report(node, "Arithmetic assignment requires numeric operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield numericResult(targetType, valueType);
            }

            case BITWISE_OR_ASSIGN, BITWISE_AND_ASSIGN, BITWISE_XOR_ASSIGN,
                 LEFT_SHIFT_ASSIGN, RIGHT_SHIFT_ASSIGN -> {
                if (!isInteger(targetType) || !isInteger(valueType)) {
                    report(node, "Bitwise/shift assignment requires integer operands.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.INT;
            }

            case INVALID -> UnknownTypeInfo.INSTANCE;
        };

        if (!isAssignable(targetType, assignedValueType)) {
            report(node, "Cannot assign " + assignedValueType + " to " + targetType + ".");
            node.setResultingType(UnknownTypeInfo.INSTANCE);
            return;
        }

        node.setResultingType(targetType);
    }


    private boolean isAssignableTarget(ExpressionAstNode target) {
        return target instanceof NameExpression
                || target instanceof MemberAccessExpression
                || target instanceof IndexExpression;
    }

    @Override
    public void visit(IfStatement node) {
        super.visit(node);
        checkConditionType(node.getCondition(), node, "If", false);
    }

    @Override
    public void visit(WhileStatement node) {
        super.visit(node);
        checkConditionType(node.getCondition(), node, "While", false);
    }

    @Override
    public void visit(DoWhileStatement node) {
        super.visit(node);
        checkConditionType(node.getCondition(), node, "Do-while", false);
    }

    @Override
    public void visit(ForStatement node) {
        super.visit(node);
        checkConditionType(node.getCondition(), node, "For", true);
    }


    @Override
    public void visit(ConditionalExpression node) {
        super.visit(node);

        checkConditionType(node.getCondition(), node, "Conditional expression", false);

        TypeInfo trueType = node.getTrueExpression() == null
                ? UnknownTypeInfo.INSTANCE
                : node.getTrueExpression().getResultingType();
        TypeInfo falseType = node.getFalseExpression() == null
                ? UnknownTypeInfo.INSTANCE
                : node.getFalseExpression().getResultingType();

        node.setResultingType(trueType.equals(falseType) ? trueType : UnknownTypeInfo.INSTANCE);
    }


    private boolean isConditionType(TypeInfo type) {
        if (type instanceof UnknownTypeInfo) {
            return true; // vermeidet Fehler-Kaskade
        }
        return PrimitiveTypeInfo.BOOL.equals(type)
                || PrimitiveTypeInfo.INT.equals(type)
                || PrimitiveTypeInfo.DOUBLE.equals(type);
    }

    private void checkConditionType(ExpressionAstNode condition, AstNode owner, String context, boolean allowMissingCondition) {
        if (condition == null) {
            if (!allowMissingCondition) {
                report(owner, context + " condition is missing.");
            }
            return;
        }

        TypeInfo conditionType = condition.getResultingType();
        if (!isConditionType(conditionType)) {
            report(owner, context + " condition must be bool, int, or double, but was: " + conditionType);
        }
    }



    @Override
    public void visit(UnaryExpression node) {
        super.visit(node);

        TypeInfo operandType = node.getOperand() == null
                ? UnknownTypeInfo.INSTANCE
                : node.getOperand().getResultingType();

        TypeInfo result = switch (node.getKind()) {
            case LOGICAL_NOT -> {
                if (!isBoolean(operandType)) {
                    report(node, "Logical not requires boolean operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.BOOL;
            }

            case BITWISE_NOT -> {
                if (!isInteger(operandType)) {
                    report(node, "Bitwise not requires integer operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield PrimitiveTypeInfo.INT;
            }

            case MINUS, PLUS -> {
                if (!isNumeric(operandType)) {
                    report(node, "Unary " + node.getKind() + " requires int or double operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield operandType; // int bleibt int, double bleibt double
            }

            case PRE_INCREMENT, PRE_DECREMENT -> {
                if (!isNumeric(operandType)) {
                    report(node, "Pre increment/decrement requires int or double operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                if (!isAssignableTarget(node.getOperand())) {
                    report(node, "Pre increment/decrement requires assignable operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield operandType;
            }

            case INVALID -> UnknownTypeInfo.INSTANCE;
        };

        node.setResultingType(result);
    }


    @Override
    public void visit(PostfixExpression node) {
        super.visit(node);

        TypeInfo operandType = node.getOperand() == null
                ? UnknownTypeInfo.INSTANCE
                : node.getOperand().getResultingType();

        TypeInfo result = switch (node.getKind()) {
            case INCREMENT, DECREMENT -> {
                if (!isNumeric(operandType)) {
                    report(node, "Post increment/decrement requires int or double operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                if (!isAssignableTarget(node.getOperand())) {
                    report(node, "Post increment/decrement requires assignable operand.");
                    yield UnknownTypeInfo.INSTANCE;
                }
                yield operandType;
            }

            case INVALID -> UnknownTypeInfo.INSTANCE;
        };

        node.setResultingType(result);
    }

    @Override
    public void visit(IndexExpression node) {
        super.visit(node);
        if (node.getTarget().getResultingType() instanceof ArrayTypeInfo arrayType) {
            node.setResultingType(arrayType.elementType());
            return;
        }
        node.setResultingType(UnknownTypeInfo.INSTANCE);
    }

    @Override
    public void visit(MemberAccessExpression node) {
        super.visit(node);
        if (node.getResolvedField() != null) {
            node.setResultingType(node.getResolvedField().getType());
            return;
        }
        if (node.getResolvedEnumValue() != null) {
            node.setResultingType(new EnumTypeInfo(node.getResolvedEnumValue().getOwnerEnum()));
            return;
        }
        node.setResultingType(UnknownTypeInfo.INSTANCE);
    }

    @Override
    public void visit(NewExpression node) {
        super.visit(node);
        TypeInfo type = resolveType(node.getType());
        if (!node.getDimensions().isEmpty() || node.getArrayInit() != null) {
            node.setResultingType(new ArrayTypeInfo(type));
            return;
        }
        node.setResultingType(type);
    }

    @Override
    public void visit(ArrayInitExpression node) {
        super.visit(node);
        TypeInfo elementType = node.getElements().isEmpty()
                ? UnknownTypeInfo.INSTANCE
                : node.getElements().getFirst().getResultingType();
        node.setResultingType(new ArrayTypeInfo(elementType));
    }

    private TypeInfo numericResult(TypeInfo left, TypeInfo right) {
        if (PrimitiveTypeInfo.DOUBLE.equals(left) || PrimitiveTypeInfo.DOUBLE.equals(right)) {
            return PrimitiveTypeInfo.DOUBLE;
        }
        if (left instanceof UnknownTypeInfo || right instanceof UnknownTypeInfo) {
            return UnknownTypeInfo.INSTANCE;
        }
        return PrimitiveTypeInfo.INT;
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
        SymbolEntry resolvedEntry = namedType.getResolvedEntry();
        if (resolvedEntry instanceof StructEntry structEntry) {
            return new StructTypeInfo(structEntry);
        }
        if (resolvedEntry instanceof EnumEntry enumEntry) {
            return new EnumTypeInfo(enumEntry);
        }

        if (globalScope == null) {
            return UnknownTypeInfo.INSTANCE;
        }

        return switch (namedType.getKind()) {
            case STRUCT -> {
                StructEntry entry = globalScope.resolveStruct(namedType.getName());
                yield entry == null ? UnknownTypeInfo.INSTANCE : new StructTypeInfo(entry);
            }
            case ENUM -> {
                EnumEntry entry = globalScope.resolveEnum(namedType.getName());
                yield entry == null ? UnknownTypeInfo.INSTANCE : new EnumTypeInfo(entry);
            }
            case INVALID -> UnknownTypeInfo.INSTANCE;
        };
    }
}
