package com.datastructures.linear.stack.classic;

import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StackTest {

    @Test
    void startsEmpty() {
        Stack<String> stack = new Stack<>();

        assertThat(stack.isEmpty()).isTrue();
        assertThat(stack.size()).isZero();
    }

    @Test
    void pushMakesTheStackNonEmptyAndPeekableWithoutRemoving() {
        Stack<String> stack = new Stack<>();

        stack.push("a");

        assertThat(stack.isEmpty()).isFalse();
        assertThat(stack.peek()).isEqualTo("a");
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    void popReturnsElementsInLastInFirstOutOrder() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");
        stack.push("c");

        assertThat(stack.pop()).isEqualTo("c");
        assertThat(stack.pop()).isEqualTo("b");
        assertThat(stack.pop()).isEqualTo("a");
        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    void popOnAnEmptyStackThrows() {
        Stack<String> stack = new Stack<>();

        assertThatThrownBy(stack::pop).isInstanceOf(EmptyStackException.class);
    }

    @Test
    void peekOnAnEmptyStackThrows() {
        Stack<String> stack = new Stack<>();

        assertThatThrownBy(stack::peek).isInstanceOf(EmptyStackException.class);
    }

    @Test
    void growsPastInitialCapacityWithoutLosingOrder() {
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < 100; i++) {
            stack.push(i);
        }

        assertThat(stack.size()).isEqualTo(100);
        for (int i = 99; i >= 0; i--) {
            assertThat(stack.pop()).isEqualTo(i);
        }
    }
}
