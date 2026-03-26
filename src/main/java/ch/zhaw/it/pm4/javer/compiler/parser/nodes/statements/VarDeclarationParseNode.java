package ch.zhaw.it.pm4.javer.compiler.parser.nodes.statements;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.TopLevelParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class VarDeclarationParseNode extends StatementParseNode {

    /**
     * {@inheritDoc}
     * */
    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
