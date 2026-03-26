package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class StructDeclarationParseNode extends TopLevelParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
