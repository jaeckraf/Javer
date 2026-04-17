package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;

public sealed interface DeclarationAstNode extends AstNode
        permits FunctionDeclaration, StructDeclaration, EnumDeclaration {


}
