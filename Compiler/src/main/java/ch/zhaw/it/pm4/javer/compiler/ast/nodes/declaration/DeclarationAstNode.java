package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;

/**
 * Marker interface for top-level declarations in a compilation unit.
 */
public sealed interface DeclarationAstNode extends AstNode
        permits FunctionDeclaration, StructDeclaration, EnumDeclaration {


}
