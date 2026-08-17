package com.datastructures.linear.matrix.classic;

/**
 * A fixed-shape 2D grid backed by a single flat {@code Object[]}, not a Java {@code T[][]}
 * (which is really an array of independently-allocated row arrays with no guaranteed
 * contiguity). Row-major arithmetic (index = row * cols + col) turns {@code get}/{@code set}
 * into a direct array access, and guarantees the whole grid lives in one contiguous block —
 * which is what makes traversal order (row-major vs. column-major) a real, measurable cost
 * difference instead of a wash.
 */
public final class Matrix<T> {

    private final Object[] elements;
    private final int rows;
    private final int cols;

    public Matrix(int rows, int cols) {
        if (rows < 1) {
            throw new IllegalArgumentException("rows must be >= 1");
        }
        if (cols < 1) {
            throw new IllegalArgumentException("cols must be >= 1");
        }
        this.rows = rows;
        this.cols = cols;
        this.elements = new Object[rows * cols];
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    @SuppressWarnings("unchecked")
    public T get(int row, int col) {
        checkBounds(row, col);
        return (T) elements[row * cols + col];
    }

    @SuppressWarnings("unchecked")
    public T set(int row, int col, T value) {
        checkBounds(row, col);
        int index = row * cols + col;
        T previous = (T) elements[index];
        elements[index] = value;
        return previous;
    }

    private void checkBounds(int row, int col) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("row " + row + " out of bounds for " + rows + " rows");
        }
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException("col " + col + " out of bounds for " + cols + " cols");
        }
    }
}
