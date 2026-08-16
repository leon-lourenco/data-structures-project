package com.datastructures.linear.dynamicarray.classic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DynamicArrayTest {

    @Test
    void startsEmpty() {
        DynamicArray<String> array = new DynamicArray<>();

        assertThat(array.isEmpty()).isTrue();
        assertThat(array.size()).isZero();
    }

    @Test
    void addAppendsAndGetReadsBackInOrder() {
        DynamicArray<String> array = new DynamicArray<>();

        array.add("a");
        array.add("b");
        array.add("c");

        assertThat(array.size()).isEqualTo(3);
        assertThat(array.get(0)).isEqualTo("a");
        assertThat(array.get(1)).isEqualTo("b");
        assertThat(array.get(2)).isEqualTo("c");
    }

    @Test
    void growsPastInitialCapacityWithoutLosingElements() {
        DynamicArray<Integer> array = new DynamicArray<>(2);

        for (int i = 0; i < 100; i++) {
            array.add(i);
        }

        assertThat(array.size()).isEqualTo(100);
        assertThat(array.capacity()).isGreaterThan(2);
        for (int i = 0; i < 100; i++) {
            assertThat(array.get(i)).isEqualTo(i);
        }
    }

    @Test
    void setReplacesElementAndReturnsThePreviousOne() {
        DynamicArray<String> array = new DynamicArray<>();
        array.add("original");

        String previous = array.set(0, "replaced");

        assertThat(previous).isEqualTo("original");
        assertThat(array.get(0)).isEqualTo("replaced");
    }

    @Test
    void removeShiftsSubsequentElementsLeft() {
        DynamicArray<String> array = new DynamicArray<>();
        array.add("a");
        array.add("b");
        array.add("c");

        String removed = array.remove(0);

        assertThat(removed).isEqualTo("a");
        assertThat(array.size()).isEqualTo(2);
        assertThat(array.get(0)).isEqualTo("b");
        assertThat(array.get(1)).isEqualTo("c");
    }

    @Test
    void shrinksCapacityAfterDrainingBelowAQuarterFull() {
        DynamicArray<Integer> array = new DynamicArray<>();
        for (int i = 0; i < 1000; i++) {
            array.add(i);
        }
        int grownCapacity = array.capacity();

        for (int i = 999; i >= 20; i--) {
            array.remove(i);
        }

        assertThat(array.capacity()).isLessThan(grownCapacity);
        for (int i = 0; i < array.size(); i++) {
            assertThat(array.get(i)).isEqualTo(i);
        }
    }

    @Test
    void getAndSetAndRemoveRejectOutOfBoundsIndexes() {
        DynamicArray<String> array = new DynamicArray<>();
        array.add("only");

        assertThatThrownBy(() -> array.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> array.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> array.set(5, "x")).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> array.remove(5)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void iteratesInInsertionOrderAndExhaustsCorrectly() {
        DynamicArray<Integer> array = new DynamicArray<>();
        array.add(1);
        array.add(2);
        array.add(3);

        List<Integer> collected = new ArrayList<>();
        for (int value : array) {
            collected.add(value);
        }

        assertThat(collected).containsExactly(1, 2, 3);
    }

    @Test
    void constructorRejectsNonPositiveInitialCapacity() {
        assertThatThrownBy(() -> new DynamicArray<Integer>(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isEmptyReportsFalseOnceAnElementHasBeenAdded() {
        DynamicArray<String> array = new DynamicArray<>();

        array.add("a");

        assertThat(array.isEmpty()).isFalse();
    }

    @Test
    void iteratorNextThrowsOnceExhausted() {
        DynamicArray<Integer> array = new DynamicArray<>();
        array.add(1);
        Iterator<Integer> iterator = array.iterator();
        iterator.next();

        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }
}
