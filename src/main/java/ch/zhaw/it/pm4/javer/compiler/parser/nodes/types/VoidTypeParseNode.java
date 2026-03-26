package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class VoidTypeParseNode extends TypeParseNode {

    /**
     * {@inheritDoc}
      *
     */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
