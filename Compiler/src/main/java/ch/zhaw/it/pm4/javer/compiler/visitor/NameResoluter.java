package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

@Deprecated
public class NameResoluter extends NameResolutionVisitor {

    public NameResoluter(DiagnosticBag diagnosticBag) {
        super(diagnosticBag);
    }
}
