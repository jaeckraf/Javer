package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.ast.nodes.CompilationUnitAstNode;
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

import java.util.List;
import java.util.function.Function;

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

        this.context = new CompilationContext(options, new DiagnosticBag(options.getInputFilePath(), 50, CompilationPhase.COMPILER_SETUP), new SourceCache(options.getInputFilePath()));
        phase = CompilationPhase.ARGUMENT_PARSING;
    }

    public String compile() {
        phase = CompilationPhase.ARGUMENT_PARSING;
        PhaseResult<List<Token>> lexingResult = lex(context.getSourceCache().getSourceCode());
        PhaseResult<CompilationUnitAstNode> parsingResult = runPhase(lexingResult, this::parse);
        PhaseResult<CompilationUnitAstNode> symbolTableResult = runPhase(parsingResult, this::createSymbolTable);
        PhaseResult<CompilationUnitAstNode> nameResolutionResult = runPhase(symbolTableResult, this::resolveNames);
        PhaseResult<CompilationUnitAstNode> typeCheckingResult = runPhase(nameResolutionResult, this::typeCheck);
        PhaseResult<CompilationUnitAstNode> semanticAnalysisResult = runPhase(typeCheckingResult, this::semanticAnalysis);
        PhaseResult<Object> codeGenerationResult = runPhase(semanticAnalysisResult, this::generateCode);
        PhaseResult<Boolean> assemblyResult = runPhase(codeGenerationResult, this::assemble);
        PhaseResult<Boolean> completionResult = runPhase(assemblyResult, this::done);

        if (isFailed(completionResult)) {
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


    private PhaseResult<List<Token>>  lex(String payload) {
        Lexer lexer = new Lexer(payload);
        List<Token> tokens = lexer.lexSourcecode();
        return new PhaseResult<>(true, tokens);
    }

    private PhaseResult<CompilationUnitAstNode>  parse(List<Token> tokens) {
        CompilationUnitAstNode node = new Parser(tokens).parse();
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<CompilationUnitAstNode> createSymbolTable(CompilationUnitAstNode payload) {
        new SymbolTableCreation().visit(payload);
        return new PhaseResult<>(true, payload);
    }

    private PhaseResult<CompilationUnitAstNode> resolveNames(CompilationUnitAstNode node) {
        phase = CompilationPhase.NAME_RESOLUTION;
        new NameResoluter().visit(node);
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<CompilationUnitAstNode> typeCheck(CompilationUnitAstNode node) {
        phase = CompilationPhase.TYPE_CHECKING;
        new TypeChecker().visit(node);
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<CompilationUnitAstNode> semanticAnalysis(CompilationUnitAstNode node) {
        phase = CompilationPhase.SEMANTIC_ANALYSIS;
        new SemanticChecker().visit(node);
        return new PhaseResult<>(true, node);
    }

    private PhaseResult<Object> generateCode(CompilationUnitAstNode node) {
        phase = CompilationPhase.CODE_GENERATION;
        Object generatedCode = new CodeGenerator().visit(node);
        return new PhaseResult<>(true, generatedCode);
    }

    private PhaseResult<Boolean> assemble(Object payload) {
        phase = CompilationPhase.ASSEMBLING;
        new Assembler().assemble(payload);
        return new PhaseResult<>(true, true);
    }

    private PhaseResult<Boolean> done(Boolean payload) {
        phase = CompilationPhase.DONE;
        return new PhaseResult<>(true, payload != null && payload);
    }
}