package ch.zhaw.it.pm4.javer.compiler.misc.diagnostics;

public enum Severity {
    INFO,       // General information (e.g., optimization applied)
    WARNING,    // Code is valid but potentially problematic (e.g., unused variable)
    ERROR,      // Code is invalid and compilation cannot succeed (e.g., syntax error)
    SEVERE      // Critical internal compiler failure
}
