package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.StructDeclaration;

/**
 * Computes layout metadata such as struct byte sizes after type resolution.
 */
public class LayoutVisitor extends AstNodeVisitorBase {

    /**
     * Creates a layout pass.
     */
    public LayoutVisitor() {
    }

    @Override
    public void visit(StructDeclaration node) {
        if (node.getStructScope() != null && node.getSymbolEntry() != null) {
            node.getStructScope().setSizeBytes(node.getStructScope().getFields().values().stream()
                    .mapToInt(field -> field.getSizeBytes())
                    .sum());
        }
        super.visit(node);
    }
}
