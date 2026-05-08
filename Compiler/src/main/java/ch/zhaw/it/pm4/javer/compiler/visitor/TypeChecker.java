package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

@Deprecated
public class TypeChecker extends TypeCheckVisitor {

    public TypeChecker() {
        super();
    }

    public TypeChecker(DiagnosticBag diagnosticBag) {
        super(diagnosticBag);
    }
}
