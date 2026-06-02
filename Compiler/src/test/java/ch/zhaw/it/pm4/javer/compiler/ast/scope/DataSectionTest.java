package ch.zhaw.it.pm4.javer.compiler.ast.scope;

import ch.zhaw.it.pm4.javer.compiler.ast.symbol.DataEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.EnumValueEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataSectionTest {

    private static EnumEntry enumEntryWithValues(String name, EnumValueEntry... values) {
        EnumEntry enumEntry = new EnumEntry(name);
        EnumScope scope = new EnumScope();
        enumEntry.setScope(scope);

        for (EnumValueEntry value : values) {
            EnumValueEntry ownedValue = new EnumValueEntry(
                    value.getName(),
                    enumEntry,
                    value.getValue(),
                    value.getSizeBytes(),
                    value.getOffsetBytes(),
                    enumEntry.getDataLabel()
            );
            scope.defineEnumValue(ownedValue);
        }

        return enumEntry;
    }

    private static EnumValueEntry enumValue(String name, int value, int offsetBytes) {
        return new EnumValueEntry(name, null, value, 4, offsetBytes, "");
    }

    @Test
    void internStringDeduplicatesAndEncodesUtf16PayloadWithLengthPrefix() {
        DataSection dataSection = new DataSection();

        DataEntry first = dataSection.internString("Hi");
        DataEntry second = dataSection.internString("Hi");

        assertSame(first, second);
        assertEquals("string_0", first.getLabel());
        assertEquals("string_0 1 02,00,00,00,48,00,69,00", first.toString());
        assertEquals(1, dataSection.getEntries().size());
    }

    @Test
    void internStringAssignsStableLabelsByInsertionOrder() {
        DataSection dataSection = new DataSection();

        DataEntry first = dataSection.internString("A");
        DataEntry second = dataSection.internString("B");

        assertEquals("string_0", first.getLabel());
        assertEquals("string_1", second.getLabel());
        assertIterableEquals(List.of("string_0", "string_1"), dataSection.getEntries().keySet());
    }

    @Test
    void addConstantUsesTypeSizeAsElementWidth() {
        DataSection dataSection = new DataSection();

        DataEntry intEntry = dataSection.addConstant("answer", PrimitiveTypeInfo.INT, 42);
        DataEntry boolEntry = dataSection.addConstant("flag", PrimitiveTypeInfo.BOOL, true);

        assertEquals("answer 4 42", intEntry.toString());
        assertEquals("flag 1 true", boolEntry.toString());
        assertSame(intEntry, dataSection.getEntries().get("answer"));
        assertSame(boolEntry, dataSection.getEntries().get("flag"));
    }

    @Test
    void addConstantClampsExplicitElementWidthToAtLeastOneByte() {
        DataSection dataSection = new DataSection();

        DataEntry entry = dataSection.addConstant("zeroWidth", PrimitiveTypeInfo.INT, List.of("AA"), 0);

        assertEquals("zeroWidth 1 AA", entry.toString());
    }

    @Test
    void addConstantWithSameLabelReplacesEntry() {
        DataSection dataSection = new DataSection();

        DataEntry first = dataSection.addConstant("value", PrimitiveTypeInfo.INT, 1);
        DataEntry second = dataSection.addConstant("value", PrimitiveTypeInfo.INT, 2);

        assertNotSame(first, second);
        assertSame(second, dataSection.getEntries().get("value"));
        assertEquals(1, dataSection.getEntries().size());
        assertEquals("value 4 2", dataSection.getEntries().get("value").toString());
    }

    @Test
    void addEnumValuesEncodesValuesAsUnsignedFourByteHex() {
        DataSection dataSection = new DataSection();
        EnumEntry enumEntry = enumEntryWithValues(
                "Sign",
                enumValue("NEGATIVE", -1, 0),
                enumValue("POSITIVE", 42, 4)
        );

        dataSection.addEnumValues(enumEntry);

        assertEquals(
                "enum_Sign_values 4 FFFFFFFF,0000002A",
                dataSection.getEntries().get("enum_Sign_values").toString()
        );
    }

    @Test
    void addEnumValuesIgnoresMissingOrEmptyScopes() {
        DataSection dataSection = new DataSection();
        EnumEntry withoutScope = new EnumEntry("WithoutScope");
        EnumEntry empty = new EnumEntry("Empty");
        empty.setScope(new EnumScope());

        dataSection.addEnumValues(withoutScope);
        dataSection.addEnumValues(empty);

        assertTrue(dataSection.getEntries().isEmpty());
    }

    @Test
    void addEnumValuesIsIdempotentForExistingDataLabel() {
        DataSection dataSection = new DataSection();
        EnumEntry enumEntry = enumEntryWithValues("Color", enumValue("RED", 1, 0));

        dataSection.addEnumValues(enumEntry);
        dataSection.addEnumValues(enumEntry);

        assertEquals(1, dataSection.getEntries().size());
        assertEquals("enum_Color_values 4 00000001", dataSection.getEntries().get("enum_Color_values").toString());
    }

    @Test
    void addArrayTemplateEncodesPrimitiveValuesUsingMemoryWidth() {
        DataSection dataSection = new DataSection();

        DataEntry bools = dataSection.addArrayTemplate(PrimitiveTypeInfo.BOOL, Arrays.asList(true, false, null));
        DataEntry chars = dataSection.addArrayTemplate(PrimitiveTypeInfo.CHAR, List.of('A', '\u20AC'));
        DataEntry ints = dataSection.addArrayTemplate(PrimitiveTypeInfo.INT, Arrays.asList(1, -1, null));
        DataEntry doubles = dataSection.addArrayTemplate(PrimitiveTypeInfo.DOUBLE, Arrays.asList(1.5, null));

        assertEquals("array_template_0 1 01,00,00", bools.toString());
        assertEquals("array_template_1 2 0041,20AC", chars.toString());
        assertEquals("array_template_2 4 00000001,FFFFFFFF,00000000", ints.toString());
        assertEquals("array_template_3 8 3FF8000000000000,0000000000000000", doubles.toString());
    }

    @Test
    void addArrayTemplateFallsBackToZeroForUnsupportedStaticValues() {
        DataSection dataSection = new DataSection();

        DataEntry entry = dataSection.addArrayTemplate(PrimitiveTypeInfo.INT, List.of("not-static"));

        assertEquals("array_template_0 4 00000000", entry.toString());
    }
}
