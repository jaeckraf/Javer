package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.CaseLabelAstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;
import ch.zhaw.it.pm4.javer.compiler.ast.scope.DataSection;
import ch.zhaw.it.pm4.javer.compiler.ast.symbol.DataEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.PrimitiveTypeInfo;
import ch.zhaw.it.pm4.javer.compiler.ast.typeinfo.TypeInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends AstNodeVisitorBase {

    private BufferedWriter writer;
    private DataSection dataSection;
    private final Deque<LoopContext> loopContexts = new ArrayDeque<>();
    private int nextLabelId;

    public void generate(CompilationUnit node, String outputFilePath) {
        Path outputFile = Path.of(outputFilePath);
        prepareOutputDirectory(outputFile);

        try (BufferedWriter outputWriter = Files.newBufferedWriter(
                outputFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            writer = outputWriter;
            loopContexts.clear();
            nextLabelId = 0;
            node.accept(this);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write generated code to " + outputFile, exception);
        } finally {
            writer = null;
        }
    }

    protected void writeLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write generated code.", exception);
        }
    }

    private void writeLabel(String label) {
        writeLine(label + ":");
    }

    private String nextLabel(String prefix) {
        return prefix + "_" + nextLabelId++;
    }

    private void prepareOutputDirectory(Path outputFile) {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not create output directory " + parent, exception);
        }
    }

    @Override
    protected void visitDefault(AstNode node) {
        super.visitDefault(node);
    }

    @Override
    public void visit(CompilationUnit node) {
        dataSection = node.getDataSection();
        writeLine(".code");
        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
        writeLine("");
        writeLine(".data");
        dataSection.getEntries().values().forEach(entry -> writeLine(entry.toString()));
        dataSection = null;
    }

    @Override
    public void visit(EnumDeclaration node) {
        super.visit(node);
    }

    @Override
    public void visit(EnumItem node) {
        super.visit(node);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        writeLine("_" + node.getName() + ":");
        writeLine("ENTER, 0");
        node.getBody().accept(this);
    }

    @Override
    public void visit(FunctionParameter node) {
        super.visit(node);
    }

    @Override
    public void visit(StructDeclaration node) {
        super.visit(node);
    }

    @Override
    public void visit(StructField node) {
        super.visit(node);
    }

    @Override
    public void visit(BlockStatement node) {
        node.getStatements().forEach(statement -> statement.accept(this));
    }

    @Override
    public void visit(IfStatement node) {
        String elseLabel = nextLabel("if_else");
        String endLabel = nextLabel("if_end");
        node.getCondition().accept(this);
        writeLine("JUMPF, " + elseLabel);
        node.getThenBranch().accept(this);
        boolean elseBranchPossible = node.getElseBranch() != null;
        if(elseBranchPossible) {
            writeLine("JUMP, " + endLabel);
        }
        writeLabel(elseLabel);
        if(elseBranchPossible) {
            node.getElseBranch().accept(this);
            writeLabel(endLabel);
        }
    }

    @Override
    public void visit(WhileStatement node) {
        String conditionLabel = nextLabel("while_condition");
        String endLabel = nextLabel("while_end");
        writeLabel(conditionLabel);
        node.getCondition().accept(this);
        writeLine("JUMPF, " + endLabel);
        loopContexts.push(new LoopContext(endLabel, conditionLabel));
        node.getBody().accept(this);
        loopContexts.pop();
        writeLine("JUMP, " + conditionLabel);
        writeLabel(endLabel);
    }

    @Override
    public void visit(DoWhileStatement node) {
        String bodyLabel = nextLabel("do_body");
        String conditionLabel = nextLabel("do_condition");
        String endLabel = nextLabel("do_end");
        writeLabel(bodyLabel);
        loopContexts.push(new LoopContext(endLabel, conditionLabel));
        node.getBody().accept(this);
        loopContexts.pop();
        writeLabel(conditionLabel);
        node.getCondition().accept(this);
        writeLine("JUMPT, " + bodyLabel);
        writeLabel(endLabel);
    }

    @Override
    public void visit(ForStatement node) {
        String conditionLabel = nextLabel("for_condition");
        String updateLabel = nextLabel("for_update");
        String endLabel = nextLabel("for_end");
        if (node.getForInit() != null) {
            node.getForInit().accept(this);
        }
        writeLabel(conditionLabel);
        if (node.getCondition() != null) {
            node.getCondition().accept(this);
        } else {
            writeLine("PUSHB, 1");
        }
        writeLine("JUMPF, " + endLabel);
        loopContexts.push(new LoopContext(endLabel, updateLabel));
        node.getBody().accept(this);
        loopContexts.pop();
        writeLabel(updateLabel);
        if (node.getUpdate() != null) {
            node.getUpdate().forEach(expression -> expression.accept(this));
        }
        writeLine("JUMP, " + conditionLabel);
        writeLabel(endLabel);
    }
    
    @Override
    public void visit(SwitchStatement node) {
        String endLabel = nextLabel("switch_end");
        node.getCondition().accept(this);

        TypeInfo conditionType = node.getCondition().getResultingType();
        
        if (conditionType instanceof PrimitiveTypeInfo(var kind)) {
            if (kind == ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind.CHAR) {
                writeLine("C2I");
            } else if (kind == ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind.BOOL) {
                writeLine("B2I");
            }
        }

        Map<SwitchCase, String> caseBodyLabels = new LinkedHashMap<>();
        SwitchCase defaultCase = null;

        for (SwitchCase switchCase : node.getCases()) {
            if (switchCase.isDefault()) {
                defaultCase = switchCase;
            }
            caseBodyLabels.put(switchCase, nextLabel("case_body"));
        }

        String dupInstruction = getDupInstruction(conditionType);
        String eqInstruction = getEqInstruction(conditionType);

        for (SwitchCase switchCase : node.getCases()) {
            if (switchCase.isDefault()) {
                continue;
            }

            String caseBodyLabel = caseBodyLabels.get(switchCase);
            for (CaseLabelAstNode caseLabel : switchCase.getCaseLabels()) {
                writeLine(dupInstruction);
                
                if (caseLabel instanceof LiteralCaseLabel literalCaseLabel) {
                    literalCaseLabel.getLiteral().accept(this);
                    
                    if (conditionType instanceof PrimitiveTypeInfo(var typeKind)) {
                        if (literalCaseLabel.getLiteral().getKind() == LiteralKind.DOUBLE && typeKind == ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind.INT) {
                            writeLine("D2I");
                        } else if (literalCaseLabel.getLiteral().getKind() == LiteralKind.INT && typeKind == ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind.DOUBLE) {
                            writeLine("I2D");
                        }
                    }
                } else if (caseLabel instanceof EnumCaseLabel enumCaseLabel) {
                    writeLine("PUSHI, " + enumCaseLabel.getResolvedEnumValue().getValue());
                }
                
                if (conditionType instanceof PrimitiveTypeInfo(var kind)) {
                    if (kind == ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind.CHAR && (caseLabel instanceof LiteralCaseLabel literalLabel && literalLabel.getLiteral().getKind() == LiteralKind.CHAR)) {
                        writeLine("C2I");
                    } else if (kind == ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveTypeKind.BOOL && (caseLabel instanceof LiteralCaseLabel literalLabel && literalLabel.getLiteral().getKind() == LiteralKind.BOOLEAN)) {
                        writeLine("B2I");
                    }
                }
                
                writeLine(eqInstruction);
                writeLine("JUMPT, " + caseBodyLabel);
            }
        }

        String popInstruction = getPopInstruction(conditionType);
        if (defaultCase != null) {
            writeLine("JUMP, " + caseBodyLabels.get(defaultCase));
        } else {
            writeLine(popInstruction);
            writeLine("JUMP, " + endLabel);
        }

        for (SwitchCase switchCase : node.getCases()) {
            writeLabel(caseBodyLabels.get(switchCase));
            writeLine(popInstruction);
            switchCase.getStatement().accept(this);
            writeLine("JUMP, " + endLabel);
        }

        writeLabel(endLabel);
    }

    @Override
    public void visit(SwitchCase node) {
        super.visit(node);
    }

    @Override
    public void visit(BreakStatement node) {
        if (!loopContexts.isEmpty()) {
            writeLine("JUMP, " + loopContexts.peek().breakLabel());
        }
    }

    @Override
    public void visit(ContinueStatement node) {
        if (!loopContexts.isEmpty()) {
            writeLine("JUMP, " + loopContexts.peek().continueLabel());
        }
    }

    @Override
    public void visit(ReturnStatement node) {
        writeLine("RET");
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(AssignExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(ConditionalExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(BinaryExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(UnaryExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(PostfixExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(CallExpression node) {
        if (node.getFunctionName().equalsIgnoreCase("prints")) {
            node.getArguments().getFirst().accept(this);
            writeLine("HPRINTS");
        } else if (node.getFunctionName().equalsIgnoreCase("printi")) {
            node.getArguments().getFirst().accept(this);
            writeLine("PRINTI");
        } else if (node.getFunctionName().equalsIgnoreCase("printd")) {
            node.getArguments().getFirst().accept(this);
            writeLine("PRINTD");
        } else if (node.getFunctionName().equalsIgnoreCase("printc")) {
            node.getArguments().getFirst().accept(this);
            writeLine("PRINTC");
        } else if (node.getFunctionName().equalsIgnoreCase("printb")) {
            node.getArguments().getFirst().accept(this);
            writeLine("PRINTB");
        }
    }

    @Override
    public void visit(IndexExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(MemberAccessExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(NewExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(ArrayInitExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(NameExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(LiteralExpression<?> node) {
        if (node.getKind() == LiteralKind.STRING) {
            DataEntry entry = dataSection.internString((String) node.getValue());
            writeLine("PUSHR, " + entry.getLabel());
        }
        if (node.getKind() == LiteralKind.BOOLEAN) {
            Boolean b = (Boolean) node.getValue();
            if(b) writeLine("PUSHB, 1");
            else writeLine("PUSHB, 0");
        }
        if (node.getKind() == LiteralKind.INT) {
            writeLine("PUSHI, " + node.getValue());
        }
        if (node.getKind() == LiteralKind.CHAR) {
            int i = (int) ((char)node.getValue());
            writeLine("PUSHC, " + i);
        }
        if (node.getKind() == LiteralKind.DOUBLE) {
            writeLine("PUSHD, " + node.getValue());
        }
        if (node.getKind() == LiteralKind.NULL) {
            writeLine("PUSHI, 0");
        }
    }

    @Override
    public void visit(LiteralCaseLabel node) {
        node.getLiteral().accept(this);
    }

    @Override
    public void visit(EnumCaseLabel node) {
        writeLine("PUSHI, " + node.getResolvedEnumValue().getValue());
    }

    @Override
    public void visit(ArrayType node) {
        super.visit(node);
    }

    @Override
    public void visit(NamedType node) {
        super.visit(node);
    }

    @Override
    public void visit(PrimitiveType node) {
        super.visit(node);
    }

    @Override
    public void visit(VoidType node) {
        super.visit(node);
    }

    @Override
    public void visit(ForInitVarDeclaration node) {
        super.visit(node);
    }

    @Override
    public void visit(ForInitExpressionList node) {
        node.getExpressions().forEach(expressionAstNode -> expressionAstNode.accept(this));
    }

    private record LoopContext(String breakLabel, String continueLabel) {
    }

    private String getDupInstruction(TypeInfo type) {
        if (type instanceof PrimitiveTypeInfo(var kind)) {
            return switch (kind) {
                case DOUBLE -> "DUPD";
                default -> "DUPI";
            };
        }
        return "DUPI";
    }

    private String getEqInstruction(TypeInfo type) {
        if (type instanceof PrimitiveTypeInfo(var kind)) {
            return switch (kind) {
                case DOUBLE -> "DEQ";
                default -> "IEQ";
            };
        }
        return "IEQ";
    }

    private String getPopInstruction(TypeInfo type) {
        if (type instanceof PrimitiveTypeInfo(var kind)) {
            return switch (kind) {
                case DOUBLE -> "POPD";
                default -> "POPI";
            };
        }
        return "POPI";
    }
}