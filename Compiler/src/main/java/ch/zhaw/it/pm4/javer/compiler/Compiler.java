package ch.zhaw.it.pm4.javer.compiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.zhaw.it.pm4.javer.compiler.ast.SymbolTableEntry;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.AstNode;
import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnit;
import ch.zhaw.it.pm4.javer.compiler.lexer.Lexer;
import ch.zhaw.it.pm4.javer.compiler.lexer.Token;
import ch.zhaw.it.pm4.javer.compiler.misc.PhaseResult;
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
    // - evaluate PhaseResults
    // - stop or continue pipeline based on errors
    //
    // Pipeline:
    // args -> setup -> lexing -> parsing -> ast-checks -> code-generation -> assembling -> done

    private final CompilerOptions options;
    private final CompilationContext context;
    private CompilationPhase phase;

    public Compiler(CompilerOptions options) {
        this.options = options;
        SourceCache sourceCache = new SourceCache(options.getInputFilePath());
        this.context = new CompilationContext(options,
                new DiagnosticBag(options.getInputFilePath(), 50, CompilationPhase.COMPILER_SETUP, sourceCache),
                sourceCache);
        phase = CompilationPhase.ARGUMENT_PARSING;
    }

    public static void main(String[] args) {
        System.setProperty("MODULE", "compiler");
        try {
            CompilerOptions options = CompilerOptions.create(args);
            configureLogging(options);
            Compiler compiler = new Compiler(options);
            System.out.println(compiler.compile());
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.exit(2);
        }
    }

    public String compile() {
        StringBuilder output = new StringBuilder();
        phase = CompilationPhase.ARGUMENT_PARSING;
        PhaseResult<List<Token>> lexingResult = lex(context.getSourceCache().getSourceCode());
        if (!isFailed(lexingResult) && options.isDumpLexer()) {
            appendSection(output, "LEXER TOKENS", dumpTokens(lexingResult.getPayload()));
        }

        PhaseResult<CompilationUnit> parsingResult = runPhase(lexingResult, this::parse);
        if (!isFailed(parsingResult) && options.isDumpAst()) {
            appendSection(output, "AST", dumpAst(parsingResult.getPayload()));
        }

        PhaseResult<CompilationUnit> symbolTableResult = runPhase(parsingResult, this::createSymbolTable);
        if (!isFailed(symbolTableResult) && options.isDumpAstSymbolTable()) {
            appendSection(output, "AST SYMBOL TABLE", dumpSymbolTable(symbolTableResult.getPayload()));
        }

        PhaseResult<CompilationUnit> nameResolutionResult = runPhase(symbolTableResult, this::resolveNames);
        PhaseResult<CompilationUnit> typeCheckingResult = runPhase(nameResolutionResult, this::typeCheck);
        PhaseResult<CompilationUnit> semanticAnalysisResult = runPhase(typeCheckingResult, this::semanticAnalysis);
        PhaseResult<Object> codeGenerationResult = runPhase(semanticAnalysisResult, this::generateCode);
        PhaseResult<Boolean> assemblyResult = runPhase(codeGenerationResult, this::assemble);
        if (isFailed(assemblyResult)) {
            return context.getDiagnosticBag().dumpReport();
        }
        output.append("Compilation Successful");
        return output.toString();
    }

    private <T, R> PhaseResult<R> runPhase(PhaseResult<T> previousResult,
                                           Function<T, PhaseResult<R>> phaseRunner) {
        if (isFailed(previousResult)) {
            return PhaseResult.failure();
        }

        return phaseRunner.apply(previousResult.getPayload());
    }

    private boolean isFailed(PhaseResult<?> result) {
        return result == null || !result.isSuccess();
    }

    private PhaseResult<List<Token>> lex(String sourceCode) {
        phase = CompilationPhase.LEXING;
        Lexer lexer = new Lexer(sourceCode, context.getDiagnosticBag());
        List<Token> tokens = lexer.lexSourcecode();
        return new PhaseResult<>(true, tokens);
    }

    private PhaseResult<CompilationUnit> parse(List<Token> tokens) {
        CompilationUnit node = new Parser(tokens, context.getDiagnosticBag()).parse();
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<CompilationUnit> createSymbolTable(CompilationUnit rootNode) {
        phase = CompilationPhase.SYMBOL_TABLE_CREATION;
        new SymbolTableCreation(context.getDiagnosticBag()).visit(rootNode);
        return new PhaseResult<>(!context.getDiagnosticBag().hasErrors(), rootNode);
    }

    private PhaseResult<CompilationUnit> resolveNames(CompilationUnit node) {
        phase = CompilationPhase.NAME_RESOLUTION;
        new NameResoluter().visit(node);
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<CompilationUnit> typeCheck(CompilationUnit node) {
        phase = CompilationPhase.TYPE_CHECKING;
        new TypeChecker().visit(node);
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<CompilationUnit> semanticAnalysis(CompilationUnit node) {
        phase = CompilationPhase.SEMANTIC_ANALYSIS;
        new SemanticChecker().visit(node);
        return new PhaseResult<>(true, node);
    }

    // TODO make code generator return a more specific type than Object
    private PhaseResult<Object> generateCode(CompilationUnit node) {
        phase = CompilationPhase.CODE_GENERATION;
        Object generatedCode = new CodeGenerator().visit(node);
        return new PhaseResult<>(true, generatedCode);
    }

    private PhaseResult<Boolean> assemble(Object payload) {
        phase = CompilationPhase.ASSEMBLING;
        new Assembler().assemble(payload, options.getOutputFilePath());
        return new PhaseResult<>(true, true);
    }

    private static void configureLogging(CompilerOptions options) {
        if (options.isLoggingEnabled()) {
            return;
        }

        LoggerContext loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME).setLevel(Level.OFF);
    }

    private static void appendSection(StringBuilder builder, String title, String content) {
        builder.append("=== ")
                .append(title)
                .append(" ===")
                .append(System.lineSeparator())
                .append(content)
                .append(System.lineSeparator());
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
