package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.CompilationUnitParseNode;
import java.util.List;

/**
 * Immutable record representing the result of a compilation process.
 * 
 * @param success    Whether the compilation was successful (no errors).
 * @param errors     List of error messages produced during compilation.
 * @param syntaxTree The root of the Concrete Syntax Tree (CST) if parsing was attempted.
 */
public record CompilationResult(
    boolean success,
    List<String> errors,
    CompilationUnitParseNode syntaxTree
) {}
