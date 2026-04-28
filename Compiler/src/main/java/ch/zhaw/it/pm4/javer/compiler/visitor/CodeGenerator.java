package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;

@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends VoidAstNodeVisitor {

    public void generate(CompilationUnit node, String outputFilePath) {
        node.accept(this);
    }
}
