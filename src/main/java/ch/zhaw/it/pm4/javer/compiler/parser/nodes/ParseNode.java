package ch.zhaw.it.pm4.javer.compiler.parser.nodes;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.parser.visitor.ParseNodeVisitor;

/**
 * This class is the root of all further parse nodes. It defines the accept method for the visitor pattern.
 * */

@JacocoGenerated("Dummy class, remove when implemented.")
public abstract class ParseNode {

    /**
     * @param visitor the visitor to accept
     * @param <T>     the return type of the visitor
     * @return the result of the visitor
     * */
    @JacocoGenerated("jacoco-ignore")
    public abstract <T> T accept(ParseNodeVisitor<T> visitor);

}
