package com.datastructures.linear.stack.applied;

import com.datastructures.linear.stack.classic.Stack;

/**
 * Validates that parentheses/brackets in a legacy COBOL source line (PICTURE clauses,
 * {@code COMPUTE} expressions) are balanced before an automated parser attempts to translate
 * it — part of the tooling built for a legacy bank's mainframe-to-microservices modernization effort.
 * A malformed copybook line should fail fast here, not produce a silently wrong translation.
 *
 * <p>This is the textbook stack application (balanced brackets) applied to a real parsing
 * problem: every opening bracket is pushed; every closing bracket must match whatever is on
 * top of the stack, and the stack must be empty again once the line ends.
 */
public final class CopybookBracketValidator {

    public BracketValidationResult validate(String sourceLine) {
        Stack<OpenBracket> openBrackets = new Stack<>();

        for (int position = 0; position < sourceLine.length(); position++) {
            char current = sourceLine.charAt(position);
            if (isOpening(current)) {
                openBrackets.push(new OpenBracket(current, position));
            } else if (isClosing(current)) {
                if (openBrackets.isEmpty()) {
                    return BracketValidationResult.unexpectedClosing(current, position);
                }
                OpenBracket lastOpened = openBrackets.pop();
                char expectedClosing = closingFor(lastOpened.symbol());
                if (expectedClosing != current) {
                    return BracketValidationResult.mismatch(expectedClosing, current, position);
                }
            }
        }

        if (!openBrackets.isEmpty()) {
            OpenBracket unclosed = openBrackets.pop();
            return BracketValidationResult.unclosed(unclosed.symbol(), unclosed.position());
        }
        return BracketValidationResult.ok();
    }

    private boolean isOpening(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    private boolean isClosing(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    private char closingFor(char opening) {
        return switch (opening) {
            case '(' -> ')';
            case '[' -> ']';
            default -> '}';
        };
    }

    private record OpenBracket(char symbol, int position) {
    }
}
