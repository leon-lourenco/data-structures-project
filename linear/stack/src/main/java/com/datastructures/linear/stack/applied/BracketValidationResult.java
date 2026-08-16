package com.datastructures.linear.stack.applied;

/** Outcome of validating one line of legacy source for balanced brackets. */
public record BracketValidationResult(boolean valid, String message) {

    public static BracketValidationResult ok() {
        return new BracketValidationResult(true, "balanced");
    }

    public static BracketValidationResult mismatch(char expectedClosing, char found, int position) {
        return new BracketValidationResult(false,
                "expected '" + expectedClosing + "' but found '" + found + "' at position " + position);
    }

    public static BracketValidationResult unexpectedClosing(char found, int position) {
        return new BracketValidationResult(false, "unexpected '" + found + "' at position " + position);
    }

    public static BracketValidationResult unclosed(char opening, int position) {
        return new BracketValidationResult(false, "unclosed '" + opening + "' opened at position " + position);
    }
}
