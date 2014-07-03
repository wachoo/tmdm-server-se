package com.amalto.core.query.user;

public class StringBuilderPrinter implements UserQueryDumpConsole.DumpPrinter {

    private final StringBuilder conditionToString;

    public StringBuilderPrinter(StringBuilder conditionToString) {
        this.conditionToString = conditionToString;
    }

    @Override
    public void increaseIndent() {
    }

    @Override
    public void print(String message) {
        conditionToString.append(message).append(' ');
    }

    @Override
    public void decreaseIndent() {
    }
}
