package ch.zhaw.it.pm4.javer.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.lexer.Lexer;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.misc.SourceCache;
import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;
import ch.zhaw.it.pm4.javer.compiler.parser.Parser;
import ch.zhaw.it.pm4.javer.compiler.visitor.Assembler;
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
    // args -> setup -> lexing -> parsing -> ast-checks -> code-generation -> assembling -> done

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
            printSection("AST SYMBOL TABLE", dumpSymbolTable(rootNode));
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
        Object generatedCode = generateCode(rootNode);
        if (stopOnErrors()) {
            return;
        }
        assemble(generatedCode);
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

    // TODO make code generator return a more specific type than Object
    private Object generateCode(CompilationUnit node) {
        enterPhase(CompilationPhase.CODE_GENERATION);
        return new CodeGenerator().generate(node);
    }

    private void assemble(Object payload) {
        enterPhase(CompilationPhase.ASSEMBLING);
        new Assembler().assemble(payload, options.getOutputFilePath());
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
        StringBuilder builder = new StringBuilder();
        appendAstNode(builder, rootNode, 0, new IdentityHashMap<>());
        return builder.toString();
    }

    private static void appendAstNode(
            StringBuilder builder,
            AstNode node,
            int indent,
            IdentityHashMap<AstNode, Boolean> visitedNodes) {

        indent(builder, indent).append(node.getClass().getSimpleName()).append(System.lineSeparator());
        if (visitedNodes.put(node, Boolean.TRUE) != null) {
            indent(builder, indent + 1).append("<already visited>").append(System.lineSeparator());
            return;
        }

        for (Method method : sortedGetterMethods(node.getClass())) {
            Object value = invokeGetter(method, node);
            if (value == null || value instanceof ch.zhaw.it.pm4.javer.compiler.ast.SymbolTable) {
                continue;
            }

            String name = propertyName(method);
            appendAstValue(builder, name, value, indent + 1, visitedNodes);
        }
    }

    private static void appendAstValue(
            StringBuilder builder,
            String name,
            Object value,
            int indent,
            IdentityHashMap<AstNode, Boolean> visitedNodes) {

        if (value instanceof AstNode childNode) {
            indent(builder, indent).append(name).append(":").append(System.lineSeparator());
            appendAstNode(builder, childNode, indent + 1, visitedNodes);
            return;
        }

        if (value instanceof Collection<?> values) {
            indent(builder, indent).append(name).append(":").append(System.lineSeparator());
            for (Object item : values) {
                if (item instanceof AstNode childNode) {
                    appendAstNode(builder, childNode, indent + 1, visitedNodes);
                } else {
                    indent(builder, indent + 1).append(String.valueOf(item)).append(System.lineSeparator());
                }
            }
            return;
        }

        if (isScalar(value)) {
            indent(builder, indent).append(name).append(": ").append(value).append(System.lineSeparator());
        }
    }

    private static String dumpSymbolTable(CompilationUnit rootNode) {
        Map<String, SymbolTableEntry> entries = rootNode.getSymbolTable().getAllEntries();
        if (entries.isEmpty()) {
            return "<empty>";
        }

        return entries.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + entry.getValue().getClass().getSimpleName())
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private static List<Method> sortedGetterMethods(Class<?> type) {
        return java.util.Arrays.stream(type.getMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getName().startsWith("get"))
                .filter(method -> method.getDeclaringClass() != Object.class)
                .sorted(Comparator.comparing(Method::getName))
                .toList();
    }

    private static Object invokeGetter(Method method, Object target) {
        try {
            return method.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            return "<unavailable>";
        }
    }

    private static String propertyName(Method method) {
        String name = method.getName().substring("get".length());
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    private static boolean isScalar(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Character
                || value.getClass().isEnum();
    }

    private static StringBuilder indent(StringBuilder builder, int indent) {
        return builder.append("  ".repeat(indent));
    }

}
