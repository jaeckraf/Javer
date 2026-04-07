package ch.zhaw.it.pm4.javer.compiler;

import ch.zhaw.it.pm4.javer.compiler.misc.PhaseResult;

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

    // private final CompilerOptions options;
    // private final CompilationContext context;
    private CompilationPhase phase;

    public Compiler() {//CompilerOptions options) {
        // this.options = options;
        // this.context = new CompilationContext(options);
        phase = CompilationPhase.ARGUMENT_PARSING;
    }

    public void compile() {
        phase = CompilationPhase.ARGUMENT_PARSING;
        PhaseResult<Boolean> success = argumentParsing();
        if (success != null && success.isSuccess())
            success = setup(success.getPayload());
        if (success != null && success.isSuccess())
            success = lex(success.getPayload());
        if (success != null && success.isSuccess())
            success = parse(success.getPayload());
        if (success != null && success.isSuccess())
            success = checkAst(success.getPayload());
        if (success != null && success.isSuccess())
            success = generateCode(success.getPayload());
        if (success != null && success.isSuccess())
            assemble(success.getPayload());
    }
    private PhaseResult<Boolean> argumentParsing() {
        return null;
    }
    private PhaseResult<Boolean>  setup(Object payload) {
        return null;
    }

    private PhaseResult<Boolean>  lex(Object payload) {
        return null;
    }

    private PhaseResult<Boolean>  parse(Object payload) {
        return null;
    }

    private PhaseResult<Boolean>  checkAst(Object payload) {
        return null;
    }

    private PhaseResult<Boolean>  generateCode(Object payload) {
        return null;
    }

    private PhaseResult<Boolean>  assemble(Object payload) {
        return null;
    }
}