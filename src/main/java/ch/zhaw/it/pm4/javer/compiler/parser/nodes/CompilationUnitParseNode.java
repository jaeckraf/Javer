package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public class CompilationUnitParseNode extends ParseNode {

    @Override
    public <T> T accept(ParseNodeVisitor<T> visitor) {
        return visitor.visit(this);
    }

}

