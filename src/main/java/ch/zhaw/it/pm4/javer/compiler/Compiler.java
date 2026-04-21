package ch.zhaw.it.pm4.javer.compiler;

import java.util.List;
import java.util.function.Function;

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

    public String compile() {
        phase = CompilationPhase.ARGUMENT_PARSING;
        PhaseResult<List<Token>> lexingResult = lex(context.getSourceCache().getSourceCode());
        PhaseResult<CompilationUnit> parsingResult = runPhase(lexingResult, this::parse);
        PhaseResult<CompilationUnit> symbolTableResult = runPhase(parsingResult, this::createSymbolTable);
        PhaseResult<CompilationUnit> nameResolutionResult = runPhase(symbolTableResult, this::resolveNames);
        PhaseResult<CompilationUnit> typeCheckingResult = runPhase(nameResolutionResult, this::typeCheck);
        PhaseResult<CompilationUnit> semanticAnalysisResult = runPhase(typeCheckingResult, this::semanticAnalysis);
        PhaseResult<Object> codeGenerationResult = runPhase(semanticAnalysisResult, this::generateCode);
        PhaseResult<Boolean> assemblyResult = runPhase(codeGenerationResult, this::assemble);
        if (isFailed(assemblyResult)) {
            return context.getDiagnosticBag().dumpReport();
        }
        return "Compilation Successful";
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

}