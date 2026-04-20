package ch.zhaw.it.pm4.javer.compiler.parser;

import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

class ParserTest {

    private static DiagnosticBag diagnostics;
    private static List<Token> tokens;
    private static SourceLocation position;
    private static Parser parser;

    @BeforeAll
    static void setup() {
        diagnostics = mock(DiagnosticBag.class);
        tokens = new ArrayList<>();
        position = null;
        parser = new Parser(tokens, diagnostics);
    }

    @BeforeEach
    void setUp() {
        tokens.clear();
        diagnostics.flush();
        position = null;
    }



}