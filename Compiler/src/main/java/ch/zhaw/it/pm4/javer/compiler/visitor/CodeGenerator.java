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

@JacocoGenerated("jacoco-ignore")
public class CodeGenerator extends AstNodeVisitorBase {

    private BufferedWriter writer;

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
        super.visit(node);
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
        super.visit(node);
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
        super.visit(node);
    }

    @Override
    public void visit(IfStatement node) {
        super.visit(node);
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
        super.visit(node);
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
        super.visit(node);
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
        super.visit(node);
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

}
