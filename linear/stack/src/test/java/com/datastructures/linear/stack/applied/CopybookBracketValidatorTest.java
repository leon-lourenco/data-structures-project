package com.datastructures.linear.stack.applied;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CopybookBracketValidatorTest {

    private final CopybookBracketValidator validator = new CopybookBracketValidator();

    @Test
    void anEmptyLineIsTriviallyBalanced() {
        assertThat(validator.validate("")).isEqualTo(BracketValidationResult.ok());
    }

    @Test
    void aLineWithNoBracketsIsBalanced() {
        assertThat(validator.validate("MOVE A TO B").valid()).isTrue();
    }

    @Test
    void picClauseParenthesesAreBalanced() {
        BracketValidationResult result = validator.validate("05 CUSTOMER-NAME PIC X(30).");

        assertThat(result.valid()).isTrue();
    }

    @Test
    void nestedMixedBracketTypesAreBalanced() {
        BracketValidationResult result = validator.validate("COMPUTE X = ([A + B] * {C - D})");

        assertThat(result.valid()).isTrue();
    }

    @Test
    void aClosingBracketWithNothingOpenIsRejected() {
        BracketValidationResult result = validator.validate("MOVE A) TO B");

        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("unexpected").contains(")");
    }

    @Test
    void mismatchedBracketTypesAreRejected() {
        BracketValidationResult result = validator.validate("COMPUTE X = (A + B]");

        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("expected").contains("]");
    }

    @Test
    void anUnclosedBracketAtEndOfLineIsRejected() {
        BracketValidationResult result = validator.validate("PIC X(30");

        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("unclosed").contains("(");
    }

    @Test
    void anUnclosedBraceIsAlsoRejected() {
        BracketValidationResult result = validator.validate("COMPUTE X = {A + B");

        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("unclosed");
    }
}
