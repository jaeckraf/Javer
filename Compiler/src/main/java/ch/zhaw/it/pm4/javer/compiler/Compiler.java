package ch.zhaw.it.pm4.javer.compiler;

import java.util.List;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.lexer.Lexer;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.parser.Parser;
import ch.zhaw.it.pm4.javer.compiler.visitor.AstPrinter;
import ch.zhaw.it.pm4.javer.compiler.visitor.CodeGenerator;
import ch.zhaw.it.pm4.javer.compiler.visitor.NameResoluter;
import ch.zhaw.it.pm4.javer.compiler.visitor.SemanticChecker;
import ch.zhaw.it.pm4.javer.compiler.visitor.SymbolTableCreation;
import ch.zhaw.it.pm4.javer.compiler.visitor.TypeChecker;

public class Compiler {

    // Orchestrates the full compilation pipeline.
    //
    // Responsibilities:
    // - initialize context
    // - execute phases in order
    // - stop or continue pipeline based on diagnostics
    //
    // Pipeline:
    // args -> setup -> lexing -> parsing -> ast-checks -> code-generation -> done

    private final CompilerOptions options;
    private final CompilationContext context;

    public Compiler(CompilerOptions options) {
        this.options = options;
        SourceCache sourceCache = new SourceCache(options.getInputFilePath());
        this.context = new CompilationContext(options,
                new DiagnosticBag(options.getInputFilePath(), 50, CompilationPhase.COMPILER_SETUP, sourceCache),
                sourceCache);
        enterPhase(CompilationPhase.ARGUMENT_PARSING);
    }

    public static void main(String[] args) {
        System.setProperty("MODULE", "compiler");
        try {
            CompilerOptions options = CompilerOptions.create(args);
            configureLogging(options);
            Compiler compiler = new Compiler(options);
            compiler.compile();
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.exit(2);
        }
    }

    public void compile() {
        enterPhase(CompilationPhase.ARGUMENT_PARSING);

        List<Token> tokens = lex(context.getSourceCache().getSourceCode());
        if (stopOnErrors()) {
            return;
        }
        if (options.isDumpLexer()) {
            printSection("LEXER TOKENS", dumpTokens(tokens));
        }

        CompilationUnit rootNode = parse(tokens);
        if (stopOnErrors()) {
            return;
        }
        if (options.isDumpAst()) {
            printSection("AST", dumpAst(rootNode));
        }

        createSymbolTable(rootNode);
        if (stopOnErrors()) {
            return;
        }
        if (options.isDumpAstSymbolTable()) {
            printSection("AST SYMBOL TABLE", dumpAst(rootNode));
        }

        resolveNames(rootNode);
        if (stopOnErrors()) {
            return;
        }
        typeCheck(rootNode);
        if (stopOnErrors()) {
            return;
        }
        semanticAnalysis(rootNode);
        if (stopOnErrors()) {
            return;
        }
        generateCode(rootNode);
        if (stopOnErrors()) {
            return;
        }
        System.out.println("Compilation Successful");
    }

    private boolean stopOnErrors() {
        if (!context.getDiagnosticBag().hasErrors()) {
            return false;
        }

        System.err.print(context.getDiagnosticBag().dumpReport());
        return true;
    }

    private void enterPhase(CompilationPhase nextPhase) {
        context.getDiagnosticBag().setPhase(nextPhase);
    }

    private List<Token> lex(String sourceCode) {
        enterPhase(CompilationPhase.LEXING);
        Lexer lexer = new Lexer(sourceCode, context.getDiagnosticBag());
        return lexer.lexSourcecode();
    }

    private CompilationUnit parse(List<Token> tokens) {
        enterPhase(CompilationPhase.PARSING);
        return new Parser(tokens, context.getDiagnosticBag()).parse();
    }

    private void createSymbolTable(CompilationUnit rootNode) {
        enterPhase(CompilationPhase.SYMBOL_TABLE_CREATION);
        new SymbolTableCreation(context.getDiagnosticBag()).visit(rootNode);
    }

    private void resolveNames(CompilationUnit node) {
        enterPhase(CompilationPhase.NAME_RESOLUTION);
        new NameResoluter().visit(node);
    }

    private void typeCheck(CompilationUnit node) {
        enterPhase(CompilationPhase.TYPE_CHECKING);
        new TypeChecker().visit(node);
    }

    private void semanticAnalysis(CompilationUnit node) {
        enterPhase(CompilationPhase.SEMANTIC_ANALYSIS);
        new SemanticChecker().visit(node);
    }

    private void generateCode(CompilationUnit node) {
        enterPhase(CompilationPhase.CODE_GENERATION);
        new CodeGenerator().generate(node, options.getOutputFilePath());
    }

    private static void configureLogging(CompilerOptions options) {
        if (options.isLoggingEnabled()) {
            return;
        }

        LoggerContext loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(Level.OFF);
    }

    private static void printSection(String title, String content) {
        System.out.println("=== " + title + " ===");
        System.out.println(content);
    }

    private static String dumpTokens(List<Token> tokens) {
        return tokens.stream()
                .map(Token::toString)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static String dumpAst(CompilationUnit rootNode) {
        return new AstPrinter().printToString(rootNode);
    }

}
