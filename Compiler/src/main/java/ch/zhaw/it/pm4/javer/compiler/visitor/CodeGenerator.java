package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.annotation.JacocoGenerated;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.ArrayType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.NamedType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.PrimitiveType;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.VoidType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends AstNodeVisitorBase {

    private BufferedWriter writer;
    private final List<DataSection> dataSections = new ArrayList<>();
    private int ifLabelCounter = 0;

    private final java.util.Deque<java.util.Map<String, Integer>> scopes = new java.util.ArrayDeque<>();
    private int currentFrameOffset = 0;

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
        writeLine(".code");
        for (DeclarationAstNode declaration : node.getDeclarations()) {
            declaration.accept(this);
        }
        writeLine("");
        writeLine(".data");
        dataSections.forEach(dataSection -> writeLine(dataSection.toString()));
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
        scopes.clear();
        currentFrameOffset = 0;
        
        int[] size = {0};
        node.accept(new AstNodeVisitorBase() {
            @Override
            public void visit(FunctionDeclaration funcNode) {
                if (funcNode.getBody() != null) funcNode.getBody().accept(this);
            }
            @Override
            public void visit(BlockStatement blockNode) {
                for (StatementAstNode stmt : blockNode.getStatements()) {
                    stmt.accept(this);
                }
            }
            @Override
            public void visit(IfStatement ifNode) {
                ifNode.getThenBranch().accept(this);
                if (ifNode.getElseBranch() != null) ifNode.getElseBranch().accept(this);
            }
            @Override
            public void visit(WhileStatement whileNode) {
                whileNode.getBody().accept(this);
            }
            @Override
            public void visit(DoWhileStatement doWhileNode) {
                doWhileNode.getBody().accept(this);
            }
            @Override
            public void visit(ForStatement forNode) {
                if (forNode.getForInit() instanceof ForInitVarDeclaration initVar) {
                    initVar.getVarDeclaration().accept(this);
                }
                forNode.getBody().accept(this);
            }
            @Override
            public void visit(VarDeclarationStatement varNode) {
                size[0] += 4;
            }
        });
        
        writeLine("_" + node.getName() + ":");
        writeLine("ENTER, " + size[0]);
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }
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
        scopes.push(new java.util.HashMap<>());
        node.getStatements().forEach(statement -> statement.accept(this));
        scopes.pop();
    }

    @Override
    public void visit(IfStatement node) {
        int currentLabel = ifLabelCounter++;
        String elseLabel = "if_else_" + currentLabel;
        String endLabel = "if_end_" + currentLabel;

        node.getCondition().accept(this);

        if (node.getCondition() instanceof LiteralExpression<?> lit && lit.getKind() == LiteralKind.INT) {
            writeLine("PUSHI, 0");
            writeLine("IGT");
        }
        
        writeLine("JUMPF, " + elseLabel);
        node.getThenBranch().accept(this);

        if (node.getElseBranch() != null) {
            writeLine("JUMP, " + endLabel);
        }

        writeLine(elseLabel + ":");

        if (node.getElseBranch() != null) {
            node.getElseBranch().accept(this);
            writeLine(endLabel + ":");
        }
    }

    @Override
    public void visit(WhileStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(DoWhileStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(ForStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(SwitchStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(SwitchCase node) {
        super.visit(node);
    }

    @Override
    public void visit(BreakStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(ContinueStatement node) {
        super.visit(node);
    }

    @Override
    public void visit(ReturnStatement node) {
        writeLine("RET");
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        if (node.getInitializer() != null) {
            node.getInitializer().accept(this);
        } else {
            writeLine("PUSHI, 0");
        }
        
        int offset = currentFrameOffset;
        currentFrameOffset += 4;
        
        if (!scopes.isEmpty()) {
            scopes.peek().put(node.getName(), offset);
        }
        
        writeLine("FSTORE4, " + offset);
    }

    @Override
    public void visit(AssignExpression node) {
        node.getValue().accept(this);
        if (node.getTarget() instanceof NameExpression nameExpr) {
            String name = nameExpr.getName();
            Integer offset = null;
            for (java.util.Map<String, Integer> scope : scopes) {
                if (scope.containsKey(name)) {
                    offset = scope.get(name);
                    break;
                }
            }
            if (offset != null) {
                writeLine("FSTORE4, " + offset);
            }
        }
    }

    @Override
    public void visit(ConditionalExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(BinaryExpression node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        switch (node.getOperator()) {
            case ADD -> writeLine("IADD");
            case SUBTRACT -> writeLine("ISUB");
            case MULTIPLY -> writeLine("IMUL");
            case DIVIDE -> writeLine("IDIV");
            case MODULO -> writeLine("IMOD");
            case EQUALS -> writeLine("IEQ");
            case NOT_EQUALS -> writeLine("INE");
            case LESS -> writeLine("ILT");
            case LESS_EQUALS -> writeLine("ILE");
            case GREATER -> writeLine("IGT");
            case GREATER_EQUALS -> writeLine("IGE");
            case SHIFT_LEFT -> writeLine("ISHL");
            case SHIFT_RIGHT -> writeLine("ISHR");
            case BITWISE_AND, AND -> writeLine("IAND");
            case BITWISE_OR, OR -> writeLine("IOR");
            case BITWISE_XOR -> writeLine("IXOR");
            default -> {}
        }
    }

    @Override
    public void visit(UnaryExpression node) {
        node.getOperand().accept(this);
        switch (node.getKind()) {
            case MINUS -> writeLine("INEG");
            case BITWISE_NOT -> writeLine("IINV");
            case LOGICAL_NOT -> {
                writeLine("B2I");
                writeLine("PUSHI, 0");
                writeLine("IEQ");
            }
            default -> {}
        }
    }

    @Override
    public void visit(PostfixExpression node) {
        super.visit(node);
    }

    @Override
    public void visit(CallExpression node) {
        if (node.getFunctionName().equalsIgnoreCase("prints")) {
            node.getArguments().getFirst().accept(this);
            writeLine("DPRINTS, " + "msg");
        } else if(node.getFunctionName().equalsIgnoreCase("printi") || node.getFunctionName().equalsIgnoreCase("print")) {
            node.getArguments().getFirst().accept(this);
            writeLine("PRINTI");
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
        String name = node.getName();
        Integer offset = null;
        for (java.util.Map<String, Integer> scope : scopes) {
            if (scope.containsKey(name)) {
                offset = scope.get(name);
                break;
            }
        }
        if (offset != null) {
            writeLine("FLOAD4, " + offset);
        } else {
            writeLine("PUSHI, 0");
        }
    }

    @Override
    public void visit(LiteralExpression<?> node) {
        if(node.getKind() == LiteralKind.INT) {
            writeLine("PUSHI, " + node.getValue());
        } else if(node.getKind() == LiteralKind.STRING) {
            List<String> values = new ArrayList<>();
            String value = (String) node.getValue();
            for(char c : value.toCharArray()) {
                values.add(String.format("%04X", (int) c));
            }
            values.add(String.format("%04X", 0));
            dataSections.add(new DataSection("msg", "2", values));
        } else if(node.getKind() == LiteralKind.BOOLEAN) {
            writeLine("PUSHB, " + ((Boolean) node.getValue() ? "1" : "0"));
        }
    }

    @Override
    public void visit(LiteralCaseLabel node) {
        super.visit(node);
    }

    @Override
    public void visit(EnumCaseLabel node) {
        super.visit(node);
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
        super.visit(node);
    }

    private record DataSection(String name, String size, List<String> values) {

        @Override
            public String toString() {
                return String.format("%s %s %s", name, size, String.join(",", values));
            }
        }


}
