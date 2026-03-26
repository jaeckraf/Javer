package ch.zhaw.it.pm4.javer.compiler.parser.nodes.types;

import ch.zhaw.it.pm4.javer.compiler.parser.nodes.ParseNode;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

public abstract class TypeParseNode extends ParseNode {

    @Override
    abstract public <T> T accept(ParseNodeVisitor<T> visitor);

}

