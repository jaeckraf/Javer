package ch.zhaw.it.pm4.javer.compiler.visitor;

import ch.zhaw.it.pm4.javer.compiler.misc.diagnostics.DiagnosticBag;

@Deprecated
public class SymbolTableCreation extends SymbolDeclarationVisitor {

    public SymbolTableCreation(DiagnosticBag diagnosticBag) {
        super(diagnosticBag);
    }
}
