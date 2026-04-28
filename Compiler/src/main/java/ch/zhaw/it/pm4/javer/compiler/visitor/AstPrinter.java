package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.ast.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.EnumCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.caseLabel.LiteralCaseLabel;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.declaration.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.statement.*;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.type.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AstPrinter extends AstNodeVisitorBase {

    private final List<Boolean> isLastStack = new ArrayList<>();
    private Appendable output;

    public String printToString(CompilationUnit node) {
        StringBuilder builder = new StringBuilder();
        print(node, builder);
        return builder.toString();
    }

    public void print(CompilationUnit node, Appendable output) {
        this.output = output;
        isLastStack.clear();

        try {
            write(nodeName(node));
            writeLine();
            node.accept(this);
        } finally {
            this.output = null;
            isLastStack.clear();
        }
    }

    public void printToFile(CompilationUnit node, String outputFilePath) {
        printToFile(node, Path.of(outputFilePath));
    }

    public void printToFile(CompilationUnit node, Path outputFile) {
        prepareOutputDirectory(outputFile);

        try (BufferedWriter writer = Files.newBufferedWriter(
                outputFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            print(node, writer);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write AST dump to " + outputFile, exception);
        }
    }

    @Override
    public void visit(CompilationUnit node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> symbolTableChild(node.getSymbolTable(), isLast));
        children.add(isLast -> nodesChild("declarations", node.getDeclarations(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(EnumDeclaration node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        children.add(isLast -> nodesChild("items", node.getItems(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(EnumItem node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        if (node.getValue() != null) {
            children.add(isLast -> scalarChild("value", node.getValue(), isLast));
        }
        visitMany(children);
    }

    @Override
    public void visit(FunctionDeclaration node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        children.add(isLast -> labeledNodeChild("returnType", node.getReturnType(), isLast));
        children.add(isLast -> nodesChild("parameters", node.getParameters(), isLast));
        if (node.getBody() != null) {
            children.add(isLast -> labeledNodeChild("body", node.getBody(), isLast));
        }
        visitMany(children);
    }

    @Override
    public void visit(FunctionParameter node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("type", node.getType(), isLast));
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        visitMany(children);
    }

    @Override
    public void visit(StructDeclaration node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        children.add(isLast -> nodesChild("fields", node.getFields(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(StructField node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("type", node.getType(), isLast));
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        visitMany(children);
    }

    @Override
    public void visit(BlockStatement node) {
        nodesChild("statements", node.getStatements(), true);
    }

    @Override
    public void visit(IfStatement node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("condition", node.getCondition(), isLast));
        children.add(isLast -> labeledNodeChild("then", node.getThenBranch(), isLast));
        if (node.getElseBranch() != null) {
            children.add(isLast -> labeledNodeChild("else", node.getElseBranch(), isLast));
        }
        visitMany(children);
    }

    @Override
    public void visit(WhileStatement node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("condition", node.getCondition(), isLast));
        children.add(isLast -> labeledNodeChild("body", node.getBody(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(DoWhileStatement node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("body", node.getBody(), isLast));
        children.add(isLast -> labeledNodeChild("condition", node.getCondition(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(ForStatement node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        if (node.getForInit() != null) {
            children.add(isLast -> labeledNodeChild("init", node.getForInit(), isLast));
        }
        if (node.getCondition() != null) {
            children.add(isLast -> labeledNodeChild("condition", node.getCondition(), isLast));
        }
        children.add(isLast -> nodesChild("update", node.getUpdate(), isLast));
        children.add(isLast -> labeledNodeChild("body", node.getBody(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(SwitchStatement node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("condition", node.getCondition(), isLast));
        children.add(isLast -> nodesChild("cases", node.getCases(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(SwitchCase node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("default", node.isDefault(), isLast));
        children.add(isLast -> nodesChild("labels", node.getCaseLabels(), isLast));
        if (node.getStatement() != null) {
            children.add(isLast -> labeledNodeChild("body", node.getStatement(), isLast));
        }
        visitMany(children);
    }

    @Override
    public void visit(ReturnStatement node) {
        if (node.getExpression() != null) {
            labeledNodeChild("expression", node.getExpression(), true);
        }
    }

    @Override
    public void visit(VarDeclarationStatement node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("type", node.getType(), isLast));
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        if (node.getInitializer() != null) {
            children.add(isLast -> labeledNodeChild("initializer", node.getInitializer(), isLast));
        }
        visitMany(children);
    }

    @Override
    public void visit(AssignExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("target", node.getTarget(), isLast));
        children.add(isLast -> scalarChild("operator", node.getOperator(), isLast));
        children.add(isLast -> labeledNodeChild("value", node.getValue(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(ConditionalExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("condition", node.getCondition(), isLast));
        children.add(isLast -> labeledNodeChild("whenTrue", node.getTrueExpression(), isLast));
        children.add(isLast -> labeledNodeChild("whenFalse", node.getFalseExpression(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(BinaryExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("left", node.getLeft(), isLast));
        children.add(isLast -> scalarChild("operator", node.getOperator(), isLast));
        children.add(isLast -> labeledNodeChild("right", node.getRight(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(UnaryExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("operator", node.getKind(), isLast));
        children.add(isLast -> labeledNodeChild("operand", node.getOperand(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(PostfixExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("target", node.getOperand(), isLast));
        children.add(isLast -> scalarChild("operator", node.getKind(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(CallExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("functionName", quote(node.getFunctionName()), isLast));
        children.add(isLast -> nodesChild("arguments", node.getArguments(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(IndexExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("target", node.getTarget(), isLast));
        children.add(isLast -> labeledNodeChild("index", node.getIndex(), isLast));
        visitMany(children);
    }

    @Override
    public void visit(MemberAccessExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("target", node.getTarget(), isLast));
        children.add(isLast -> scalarChild("member", quote(node.getMemberName()), isLast));
        visitMany(children);
    }

    @Override
    public void visit(NewExpression node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> labeledNodeChild("type", node.getType(), isLast));
        children.add(isLast -> nodesChild("dimensions", node.getDimensions(), isLast));
        if (node.getArrayInit() != null) {
            children.add(isLast -> labeledNodeChild("arrayInit", node.getArrayInit(), isLast));
        }
        visitMany(children);
    }

    @Override
    public void visit(ArrayInitExpression node) {
        nodesChild("elements", node.getElements(), true);
    }

    @Override
    public void visit(NameExpression node) {
        scalarChild("name", quote(node.getName()), true);
    }

    @Override
    public void visit(LiteralExpression<?> node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("kind", node.getKind(), isLast));
        children.add(isLast -> scalarChild("value", quoteValue(node.getValue()), isLast));
        visitMany(children);
    }

    @Override
    public void visit(LiteralCaseLabel node) {
        labeledNodeChild("literal", node.getLiteral(), true);
    }

    @Override
    public void visit(EnumCaseLabel node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("enumTypeName", quote(node.getEnumTypeName()), isLast));
        children.add(isLast -> scalarChild("enumValueName", quote(node.getEnumValueName()), isLast));
        visitMany(children);
    }

    @Override
    public void visit(ArrayType node) {
        labeledNodeChild("baseType", node.getBaseType(), true);
    }

    @Override
    public void visit(NamedType node) {
        List<Consumer<Boolean>> children = new ArrayList<>();
        children.add(isLast -> scalarChild("kind", node.getKind(), isLast));
        children.add(isLast -> scalarChild("name", quote(node.getName()), isLast));
        visitMany(children);
    }

    @Override
    public void visit(PrimitiveType node) {
        scalarChild("kind", node.getKind(), true);
    }

    @Override
    public void visit(ForInitVarDeclaration node) {
        labeledNodeChild("varDeclaration", node.getVarDeclaration(), true);
    }

    @Override
    public void visit(ForInitExpressionList node) {
        nodesChild("expressions", node.getExpressions(), true);
    }

    private void symbolTableChild(SymbolTable symbolTable, boolean isLast) {
        Map<String, SymbolTableEntry> entries = symbolTable.getAllEntries();
        writeBranchLine("symbolTable (" + entries.size() + ")", isLast);
        withChildren(() -> {
            List<Map.Entry<String, SymbolTableEntry>> sortedEntries = entries.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .toList();
            visitMany(sortedEntries, (entry, childIsLast) -> symbolTableEntryChild(entry.getValue(), childIsLast));
        }, isLast);
    }

    private void symbolTableEntryChild(SymbolTableEntry entry, boolean isLast) {
        writeBranchLine(entry.getName() + ": " + entry.getClass().getSimpleName(), isLast);
        withChildren(() -> {
            List<Consumer<Boolean>> children = new ArrayList<>();
            if (entry instanceof VariableSymbolTableEntry variable) {
                children.add(childIsLast -> labeledNodeChild("type", variable.getType(), childIsLast));
                if (variable.getInitializer() != null) {
                    children.add(childIsLast -> labeledNodeChild("initializer", variable.getInitializer(), childIsLast));
                }
            } else if (entry instanceof FunctionSymbolTableEntry function) {
                children.add(childIsLast -> labeledNodeChild("returnType", function.getReturnType(), childIsLast));
                children.add(childIsLast -> nodesChild("parameters", function.getParameters(), childIsLast));
            } else if (entry instanceof StructSymbolTableEntry struct) {
                children.add(childIsLast -> nodesChild("fields", struct.getFields(), childIsLast));
            } else if (entry instanceof EnumSymbolTableEntry enumEntry) {
                children.add(childIsLast -> nodesChild("items", enumEntry.getItems(), childIsLast));
            }
            visitMany(children);
        }, isLast);
    }

    private void scalarChild(String label, Object value, boolean isLast) {
        writeBranchLine(label + ": " + value, isLast);
    }

    private void labeledNodeChild(String label, AstNode node, boolean isLast) {
        if (node == null) {
            writeBranchLine(label + ": <null>", isLast);
            return;
        }

        writeBranchLine(label + " " + nodeName(node), isLast);
        withChildren(() -> node.accept(this), isLast);
    }

    private void nodeChild(AstNode node, boolean isLast) {
        writeBranchLine(nodeName(node), isLast);
        withChildren(() -> node.accept(this), isLast);
    }

    private void nodesChild(String label, List<? extends AstNode> nodes, boolean isLast) {
        int size = nodes == null ? 0 : nodes.size();
        writeBranchLine(label + " (" + size + ")", isLast);
        withChildren(() -> {
            if (nodes != null) {
                visitMany(nodes, this::nodeChild);
            }
        }, isLast);
    }

    private void writeBranchLine(String text, boolean isLast) {
        for (boolean last : isLastStack) {
            write(last ? "    " : "|   ");
        }
        write(isLast ? "`-- " : "|-- ");
        write(text);
        writeLine();
    }

    private void withChildren(Runnable body, boolean isLast) {
        isLastStack.add(isLast);
        body.run();
        isLastStack.remove(isLastStack.size() - 1);
    }

    private static <T> void visitMany(List<T> items, VisitOne<T> visitOne) {
        for (int i = 0; i < items.size(); i++) {
            visitOne.visit(items.get(i), i == items.size() - 1);
        }
    }

    private void visitMany(List<Consumer<Boolean>> children) {
        visitMany(children, Consumer::accept);
    }

    private static String nodeName(AstNode node) {
        return removeAstNodeSuffix(node.getClass().getSimpleName());
    }

    private static String removeAstNodeSuffix(String typeName) {
        return typeName.endsWith("AstNode") ? typeName.substring(0, typeName.length() - "AstNode".length()) : typeName;
    }

    private static String quote(String value) {
        return value == null ? "<null>" : "'" + value.replace("'", "\\'") + "'";
    }

    private static String quoteValue(Object value) {
        return value instanceof String stringValue ? quote(stringValue) : String.valueOf(value);
    }

    private void writeLine() {
        write(System.lineSeparator());
    }

    private void write(String text) {
        try {
            output.append(text);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write AST dump.", exception);
        }
    }

    private static void prepareOutputDirectory(Path outputFile) {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent == null) {
            return;
        }

        try {
            Files.createDirectories(parent);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not create AST dump directory " + parent, exception);
        }
    }

    @FunctionalInterface
    private interface VisitOne<T> {
        void visit(T item, boolean isLast);
    }
}
