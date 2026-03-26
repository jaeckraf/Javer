package ch.zhaw.it.pm4.javer.compiler.parser.nodes.toplevel.enums;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.ParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class EnumItemParseNode extends ParseNode {

    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

