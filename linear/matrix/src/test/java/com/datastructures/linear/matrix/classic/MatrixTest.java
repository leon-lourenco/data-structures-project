package com.datastructures.linear.matrix.classic;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MatrixTest {

    @Test
    void reportsTheDimensionsItWasConstructedWith() {
        Matrix<String> matrix = new Matrix<>(3, 5);

        assertThat(matrix.rows()).isEqualTo(3);
        assertThat(matrix.cols()).isEqualTo(5);
    }

    @Test
    void everyCellStartsUnset() {
        Matrix<String> matrix = new Matrix<>(2, 2);

        assertThat(matrix.get(0, 0)).isNull();
        assertThat(matrix.get(1, 1)).isNull();
    }

    @Test
    void setThenGetReturnsTheStoredValue() {
        Matrix<String> matrix = new Matrix<>(2, 2);

        matrix.set(1, 0, "value");

        assertThat(matrix.get(1, 0)).isEqualTo("value");
    }

    @Test
    void setReturnsThePreviousValueAtThatCell() {
        Matrix<String> matrix = new Matrix<>(2, 2);
        matrix.set(0, 0, "original");

        String previous = matrix.set(0, 0, "replaced");

        assertThat(previous).isEqualTo("original");
        assertThat(matrix.get(0, 0)).isEqualTo("replaced");
    }

    @Test
    void cellsAreIndependentAcrossRowsAndColumns() {
        Matrix<Integer> matrix = new Matrix<>(3, 3);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                matrix.set(row, col, row * 10 + col);
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                assertThat(matrix.get(row, col)).isEqualTo(row * 10 + col);
            }
        }
    }

    @Test
    void getRejectsAnOutOfBoundsRowOrColumn() {
        Matrix<String> matrix = new Matrix<>(2, 2);

        assertThatThrownBy(() -> matrix.get(-1, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> matrix.get(2, 0)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> matrix.get(0, -1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> matrix.get(0, 2)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void setRejectsAnOutOfBoundsRowOrColumn() {
        Matrix<String> matrix = new Matrix<>(2, 2);

        assertThatThrownBy(() -> matrix.set(-1, 0, "x")).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> matrix.set(0, 2, "x")).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void constructorRejectsNonPositiveRows() {
        assertThatThrownBy(() -> new Matrix<String>(0, 3)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Matrix<String>(-1, 3)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructorRejectsNonPositiveCols() {
        assertThatThrownBy(() -> new Matrix<String>(3, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
