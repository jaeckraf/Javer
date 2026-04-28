package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import java.util.List;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;

@JacocoGenerated("Skeleton only, remove when fully implemented")
public final class StructDeclaration implements DeclarationAstNode {

     private final String name;
     private final List<StructField> fields;

    public StructDeclaration(String name, List<StructField> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<StructField> getFields() {
        return fields;
    }

    @Override
     public <T> T accept(ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor<T> visitor) {
        return visitor.visit(this);
     }
}
