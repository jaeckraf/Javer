package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;

@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends VoidAstNodeVisitor {

    public Object generate(CompilationUnit node) {
        node.accept(this);
        return null;
    }
}
