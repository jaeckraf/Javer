package ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration;

import java.util.List;

public final class StructDeclaration implements DeclarationAstNode {

     private final String name;
     private final List<StructField> fields;

    public StructDeclaration(String name, List<StructField> fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
     public <T> T accept(ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor<T> visitor) {
          return null;
     }
}
