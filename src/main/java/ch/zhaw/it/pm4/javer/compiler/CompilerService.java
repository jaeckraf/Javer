package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.diagnostics.JaverLogger;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenPosition;
import ch.zhaw.it.pm4.javer.compiler.lexer.TokenType;
import ch.zhaw.it.pm4.javer.compiler.parser.Parser;
import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
import ch.zhaw.it.pm4.javer.compiler.diagnostics.DiagnosticBag;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class that orchestrates the compiler pipeline.
 * It coordinates the steps of Lexing, Parsing, and potentially Semantic Analysis and Code Generation.
 */
public class CompilerService {

    private final DiagnosticBag diagnostics;

    public CompilerService() {
        this.diagnostics = new DiagnosticBag();
    }

    /**
     * Compiles raw source code and returns a comprehensive Result object.
     * 
     * @param sourceCode The source code string to compile.
     * @return A {@link CompilationResult} containing success/failure status and error messages.
     */
    public CompilationResult compile(String sourceCode) {
        JaverLogger.info("Compilation process started.");
        
        if (sourceCode == null || sourceCode.isBlank()) {
            JaverLogger.warning("Empty source code provided. Compilation aborted.");
            return new CompilationResult(true, List.of(), new CompilationUnitParseNode());
        }

        diagnostics.clear();

        // Step 1: Lexing (Stub)
        // In the future, this will be replaced by a real Lexer instance.
        List<Token> tokens = lexStub(sourceCode);
        JaverLogger.info("Lexing stage completed (Stub used).");

        // Step 2: Parsing
        Parser parser = new Parser(tokens, diagnostics);
        CompilationUnitParseNode syntaxTree = parser.parse();
        JaverLogger.info("Parsing stage completed.");

        boolean success = !diagnostics.hasErrors();
        List<String> errors = diagnostics.getErrors();
        
        if (success) {
            JaverLogger.info("Compilation successful.");
        } else {
            JaverLogger.info("Compilation failed with " + errors.size() + " errors.");
        }

        return new CompilationResult(success, errors, syntaxTree);
    }

    /**
     * Stub method representing the Lexer until a real one is integrated.
     * It currently ignores all source code and just returns an EOF token.
     */
    private List<Token> lexStub(String sourceCode) {
        List<Token> tokens = new ArrayList<>();
        // Real lexing logic would tokenize sourceCode here.
        // For integration purposes, we just return an EOF token so the Parser can finish successfully.
        tokens.add(new Token(TokenType.SPECIAL_END_OF_FILE, "", new TokenPosition(1, 1, 1)));
        return tokens;
    }
}
