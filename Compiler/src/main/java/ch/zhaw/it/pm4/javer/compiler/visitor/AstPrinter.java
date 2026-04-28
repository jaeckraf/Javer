package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;

public class AstPrinter extends VoidAstNodeVisitor {

    public void print(CompilationUnit node) {
        node.accept(this);
    }
}
