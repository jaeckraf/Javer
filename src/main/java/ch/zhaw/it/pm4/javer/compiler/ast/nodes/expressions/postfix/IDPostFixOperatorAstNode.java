package ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.postfix;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.expressions.primary.IndexAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

public class IDPostFixOperatorAstNode extends AbstractPostFixOperatorAstNode {

    private final String id;

    public IDPostFixOperatorAstNode(String id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @JacocoGenerated("jacoco-ignore")
    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }

}
