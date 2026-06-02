package ch.zhaw.it.pm4.javer.compiler.ast.typeinfo;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.AssignOperator;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.BinaryExpressionKind;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.StructEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TypeRulesTest {

    private static StructTypeInfo structType() {
        return new StructTypeInfo(new StructEntry("Point"));
    }

    private static EnumTypeInfo enumType() {
        return new EnumTypeInfo(new EnumEntry("Color"));
    }

    @Test
    void assignabilityAllowsExactTypesAndNumericConversions() {
        assertAll(
                () -> assertTrue(TypeRules.isAssignable(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.INT)),
                () -> assertTrue(TypeRules.isAssignable(PrimitiveTypeInfo.DOUBLE, PrimitiveTypeInfo.INT)),
                () -> assertTrue(TypeRules.isAssignable(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.DOUBLE)),
                () -> assertFalse(TypeRules.isAssignable(PrimitiveTypeInfo.BOOL, PrimitiveTypeInfo.INT)),
                () -> assertTrue(TypeRules.isAssignable(UnknownTypeInfo.INSTANCE, PrimitiveTypeInfo.BOOL)),
                () -> assertTrue(TypeRules.isAssignable(PrimitiveTypeInfo.BOOL, UnknownTypeInfo.INSTANCE))
        );
    }

    @Test
    void nullIsAssignableOnlyToReferenceTypes() {
        TypeInfo arrayType = new ArrayTypeInfo(PrimitiveTypeInfo.INT);
        TypeInfo structType = structType();
        TypeInfo enumType = enumType();

        assertAll(
                () -> assertTrue(TypeRules.isAssignable(PrimitiveTypeInfo.STRING, NullTypeInfo.INSTANCE)),
                () -> assertTrue(TypeRules.isAssignable(arrayType, NullTypeInfo.INSTANCE)),
                () -> assertTrue(TypeRules.isAssignable(structType, NullTypeInfo.INSTANCE)),
                () -> assertTrue(TypeRules.isAssignable(enumType, NullTypeInfo.INSTANCE)),
                () -> assertFalse(TypeRules.isAssignable(PrimitiveTypeInfo.INT, NullTypeInfo.INSTANCE)),
                () -> assertFalse(TypeRules.isAssignable(PrimitiveTypeInfo.BOOL, NullTypeInfo.INSTANCE))
        );
    }

    @Test
    void conversionIsNotNeededForEqualTypesOrNullToReference() {
        assertAll(
                () -> assertFalse(TypeRules.needsConversion(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.INT)),
                () -> assertFalse(TypeRules.needsConversion(NullTypeInfo.INSTANCE, PrimitiveTypeInfo.STRING)),
                () -> assertTrue(TypeRules.needsConversion(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.DOUBLE)),
                () -> assertTrue(TypeRules.needsConversion(NullTypeInfo.INSTANCE, PrimitiveTypeInfo.INT))
        );
    }

    @Test
    void compoundAssignmentRulesMatchOperatorFamilies() {
        assertAll(
                () -> assertFalse(TypeRules.isNotCompoundAssignable(
                        AssignOperator.ADD_ASSIGN,
                        PrimitiveTypeInfo.INT,
                        PrimitiveTypeInfo.DOUBLE)),
                () -> assertTrue(TypeRules.isNotCompoundAssignable(
                        AssignOperator.MOD_ASSIGN,
                        PrimitiveTypeInfo.DOUBLE,
                        PrimitiveTypeInfo.INT)),
                () -> assertFalse(TypeRules.isNotCompoundAssignable(
                        AssignOperator.LEFT_SHIFT_ASSIGN,
                        PrimitiveTypeInfo.INT,
                        PrimitiveTypeInfo.INT)),
                () -> assertTrue(TypeRules.isNotCompoundAssignable(
                        AssignOperator.BITWISE_AND_ASSIGN,
                        PrimitiveTypeInfo.BOOL,
                        PrimitiveTypeInfo.BOOL)),
                () -> assertTrue(TypeRules.isNotCompoundAssignable(
                        AssignOperator.ASSIGN,
                        PrimitiveTypeInfo.BOOL,
                        PrimitiveTypeInfo.INT))
        );
    }

    @Test
    void compoundCalculationTypeUsesNumericPromotionAndIntegerForBitwiseFamilies() {
        assertAll(
                () -> assertEquals(
                        PrimitiveTypeInfo.DOUBLE,
                        TypeRules.compoundCalculationType(
                                AssignOperator.ADD_ASSIGN,
                                PrimitiveTypeInfo.INT,
                                PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(
                        PrimitiveTypeInfo.INT,
                        TypeRules.compoundCalculationType(
                                AssignOperator.MOD_ASSIGN,
                                PrimitiveTypeInfo.DOUBLE,
                                PrimitiveTypeInfo.INT)),
                () -> assertEquals(
                        PrimitiveTypeInfo.INT,
                        TypeRules.compoundCalculationType(
                                AssignOperator.BITWISE_OR_ASSIGN,
                                PrimitiveTypeInfo.INT,
                                PrimitiveTypeInfo.INT)),
                () -> assertEquals(
                        PrimitiveTypeInfo.BOOL,
                        TypeRules.compoundCalculationType(
                                AssignOperator.ASSIGN,
                                PrimitiveTypeInfo.BOOL,
                                PrimitiveTypeInfo.INT))
        );
    }

    @Test
    void binaryResultsUseOperatorResultTypes() {
        assertAll(
                () -> assertEquals(
                        PrimitiveTypeInfo.DOUBLE,
                        TypeRules.binaryResult(
                                BinaryExpressionKind.ADD,
                                PrimitiveTypeInfo.INT,
                                PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(
                        PrimitiveTypeInfo.BOOL,
                        TypeRules.binaryResult(
                                BinaryExpressionKind.LESS,
                                PrimitiveTypeInfo.INT,
                                PrimitiveTypeInfo.INT)),
                () -> assertEquals(
                        PrimitiveTypeInfo.INT,
                        TypeRules.binaryResult(
                                BinaryExpressionKind.BITWISE_XOR,
                                PrimitiveTypeInfo.INT,
                                PrimitiveTypeInfo.INT)),
                () -> assertSame(
                        UnknownTypeInfo.INSTANCE,
                        TypeRules.binaryResult(
                                BinaryExpressionKind.MULTIPLY,
                                UnknownTypeInfo.INSTANCE,
                                PrimitiveTypeInfo.INT))
        );
    }

    @Test
    void binaryOperatorValidationRejectsInvalidOperandFamilies() {
        TypeInfo stringArray = new ArrayTypeInfo(PrimitiveTypeInfo.STRING);

        assertAll(
                () -> assertFalse(TypeRules.isNotBinaryOperatorAllowed(
                        BinaryExpressionKind.AND,
                        PrimitiveTypeInfo.BOOL,
                        PrimitiveTypeInfo.BOOL)),
                () -> assertTrue(TypeRules.isNotBinaryOperatorAllowed(
                        BinaryExpressionKind.AND,
                        PrimitiveTypeInfo.INT,
                        PrimitiveTypeInfo.BOOL)),
                () -> assertFalse(TypeRules.isNotBinaryOperatorAllowed(
                        BinaryExpressionKind.ADD,
                        PrimitiveTypeInfo.INT,
                        PrimitiveTypeInfo.DOUBLE)),
                () -> assertTrue(TypeRules.isNotBinaryOperatorAllowed(
                        BinaryExpressionKind.ADD,
                        PrimitiveTypeInfo.STRING,
                        PrimitiveTypeInfo.STRING)),
                () -> assertFalse(TypeRules.isNotBinaryOperatorAllowed(
                        BinaryExpressionKind.EQUALS,
                        stringArray,
                        NullTypeInfo.INSTANCE)),
                () -> assertTrue(TypeRules.isNotBinaryOperatorAllowed(
                        BinaryExpressionKind.LESS,
                        PrimitiveTypeInfo.STRING,
                        PrimitiveTypeInfo.STRING))
        );
    }

    @Test
    void equalityComparabilityHandlesNullReferencesEnumsAndNumericTypes() {
        EnumEntry color = new EnumEntry("Color");
        EnumEntry size = new EnumEntry("Size");
        TypeInfo colorType = new EnumTypeInfo(color);
        TypeInfo sameColorType = new EnumTypeInfo(color);
        TypeInfo sizeType = new EnumTypeInfo(size);

        assertAll(
                () -> assertTrue(TypeRules.isEqualityComparable(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.DOUBLE)),
                () -> assertTrue(TypeRules.isEqualityComparable(PrimitiveTypeInfo.STRING, NullTypeInfo.INSTANCE)),
                () -> assertFalse(TypeRules.isEqualityComparable(PrimitiveTypeInfo.INT, NullTypeInfo.INSTANCE)),
                () -> assertTrue(TypeRules.isEqualityComparable(colorType, sameColorType)),
                () -> assertFalse(TypeRules.isEqualityComparable(colorType, sizeType)),
                () -> assertFalse(TypeRules.isEqualityComparable(
                        new ArrayTypeInfo(PrimitiveTypeInfo.INT),
                        new ArrayTypeInfo(PrimitiveTypeInfo.DOUBLE)))
        );
    }

    @Test
    void orderedComparabilityAllowsNumericAndCharPairsOnly() {
        assertAll(
                () -> assertTrue(TypeRules.isOrderedComparable(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.DOUBLE)),
                () -> assertTrue(TypeRules.isOrderedComparable(PrimitiveTypeInfo.CHAR, PrimitiveTypeInfo.CHAR)),
                () -> assertFalse(TypeRules.isOrderedComparable(PrimitiveTypeInfo.CHAR, PrimitiveTypeInfo.INT)),
                () -> assertFalse(TypeRules.isOrderedComparable(PrimitiveTypeInfo.STRING, PrimitiveTypeInfo.STRING))
        );
    }

    @Test
    void equalityOperandTypeSelectsSharedComparisonRepresentation() {
        TypeInfo arrayType = new ArrayTypeInfo(PrimitiveTypeInfo.INT);

        assertAll(
                () -> assertEquals(
                        PrimitiveTypeInfo.DOUBLE,
                        TypeRules.equalityOperandType(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(
                        arrayType,
                        TypeRules.equalityOperandType(NullTypeInfo.INSTANCE, arrayType)),
                () -> assertEquals(
                        PrimitiveTypeInfo.STRING,
                        TypeRules.equalityOperandType(PrimitiveTypeInfo.STRING, NullTypeInfo.INSTANCE)),
                () -> assertEquals(
                        PrimitiveTypeInfo.BOOL,
                        TypeRules.equalityOperandType(PrimitiveTypeInfo.BOOL, PrimitiveTypeInfo.BOOL)),
                () -> assertEquals(
                        PrimitiveTypeInfo.INT,
                        TypeRules.equalityOperandType(PrimitiveTypeInfo.BOOL, PrimitiveTypeInfo.CHAR))
        );
    }

    @Test
    void conditionalResultFindsCommonResultType() {
        TypeInfo structType = structType();

        assertAll(
                () -> assertEquals(
                        PrimitiveTypeInfo.INT,
                        TypeRules.conditionalResult(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.INT)),
                () -> assertEquals(
                        PrimitiveTypeInfo.DOUBLE,
                        TypeRules.conditionalResult(PrimitiveTypeInfo.INT, PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(
                        structType,
                        TypeRules.conditionalResult(NullTypeInfo.INSTANCE, structType)),
                () -> assertSame(
                        UnknownTypeInfo.INSTANCE,
                        TypeRules.conditionalResult(PrimitiveTypeInfo.BOOL, PrimitiveTypeInfo.INT)),
                () -> assertSame(
                        UnknownTypeInfo.INSTANCE,
                        TypeRules.conditionalResult(UnknownTypeInfo.INSTANCE, PrimitiveTypeInfo.INT))
        );
    }

    @Test
    void defaultValuesMatchVmZeroInitializationSemantics() {
        assertAll(
                () -> assertEquals(0, TypeRules.defaultValue(PrimitiveTypeInfo.INT)),
                () -> assertEquals(0.0, TypeRules.defaultValue(PrimitiveTypeInfo.DOUBLE)),
                () -> assertEquals(false, TypeRules.defaultValue(PrimitiveTypeInfo.BOOL)),
                () -> assertEquals('\0', TypeRules.defaultValue(PrimitiveTypeInfo.CHAR)),
                () -> assertNull(TypeRules.defaultValue(PrimitiveTypeInfo.STRING)),
                () -> assertNull(TypeRules.defaultValue(enumType())),
                () -> assertNull(TypeRules.defaultValue(new ArrayTypeInfo(PrimitiveTypeInfo.INT)))
        );
    }

    @Test
    void arrayHelpersWrapAndUnwrapNestedArrayTypes() {
        TypeInfo nestedArray = TypeRules.arrayType(PrimitiveTypeInfo.CHAR, 3);

        assertEquals(new ArrayTypeInfo(new ArrayTypeInfo(new ArrayTypeInfo(PrimitiveTypeInfo.CHAR))), nestedArray);
        assertEquals(PrimitiveTypeInfo.CHAR, TypeRules.leafElementType(nestedArray));
        assertEquals(PrimitiveTypeInfo.INT, TypeRules.arrayType(PrimitiveTypeInfo.INT, 0));
    }
}
