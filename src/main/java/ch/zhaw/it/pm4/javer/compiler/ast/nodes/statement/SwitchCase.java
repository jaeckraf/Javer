package ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.CaseLabelAstNode;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstNodeVisitor;

import java.util.List;

public final class SwitchCase implements AstNode {

    private final boolean isDefault;
    private final List<CaseLabelAstNode> caseLabels;
    private final StatementAstNode statement;

    public SwitchCase(boolean isDefault, List<CaseLabelAstNode> caseLabels, StatementAstNode statement) {
        this.isDefault = isDefault;
        this.caseLabels = caseLabels;
        this.statement = statement;
    }

    @Override
    public <T> T accept(AstNodeVisitor<T> visitor) {
        return null;
    }
}
