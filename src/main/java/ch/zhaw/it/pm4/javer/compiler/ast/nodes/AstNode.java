package ch.zhaw.it.pm4.javer.compiler.ast.nodes;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceLocation;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

/**
 * This class is the root of all further parse nodes. It defines the accept method for the visitor pattern.
 * */

@JacocoGenerated("Dummy class, remove when implemented.")
public abstract class AstNode {

    private final SourceLocation sourceLocation = null;

    protected AstNode() {
    }

    /**
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the result of the visitor
     * */
    @JacocoGenerated("jacoco-ignore")
    public abstract <T> T accept(AstNodeVisitor<T> visitor);

}
