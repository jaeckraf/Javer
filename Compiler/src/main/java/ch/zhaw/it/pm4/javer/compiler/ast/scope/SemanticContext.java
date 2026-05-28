package ch.zhaw.it.pm4.javer.compiler.ast.scope;

/**
 * Shared semantic state passed between semantic compiler phases.
 */
public record SemanticContext(GlobalScope globalScope, DataSection dataSection) {
}