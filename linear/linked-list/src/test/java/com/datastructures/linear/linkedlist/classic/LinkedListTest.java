package com.datastructures.linear.linkedlist.classic;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LinkedListTest {

    @Test
    void startsEmpty() {
        LinkedList<String> list = new LinkedList<>();

        assertThat(list.isEmpty()).isTrue();
        assertThat(list.size()).isZero();
    }

    @Test
    void addFirstOnAnEmptyListBecomesTheOnlyElement() {
        LinkedList<String> list = new LinkedList<>();

        list.addFirst("a");

        assertThat(list.get(0)).isEqualTo("a");
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.isEmpty()).isFalse();
    }

    @Test
    void addFirstOnANonEmptyListPrependsIt() {
        LinkedList<String> list = new LinkedList<>();
        list.addFirst("b");

        list.addFirst("a");

        assertThat(toList(list)).containsExactly("a", "b");
    }

    @Test
    void addLastOnAnEmptyListBecomesTheOnlyElement() {
        LinkedList<String> list = new LinkedList<>();

        list.addLast("a");

        assertThat(list.get(0)).isEqualTo("a");
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void addLastOnANonEmptyListAppendsIt() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");

        list.addLast("b");

        assertThat(toList(list)).containsExactly("a", "b");
    }

    @Test
    void insertAfterTheTailBehavesLikeAddLast() {
        LinkedList<String> list = new LinkedList<>();
        LinkedList.Node<String> first = list.addLast("a");

        list.insertAfter(first, "b");

        assertThat(toList(list)).containsExactly("a", "b");
    }

    @Test
    void insertAfterAMiddleNodeSplicesInWithoutShifting() {
        LinkedList<String> list = new LinkedList<>();
        LinkedList.Node<String> a = list.addLast("a");
        list.addLast("c");

        list.insertAfter(a, "b");

        assertThat(toList(list)).containsExactly("a", "b", "c");
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    void removeFirstOnAnEmptyListThrows() {
        LinkedList<String> list = new LinkedList<>();

        assertThatThrownBy(list::removeFirst).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void removeLastOnAnEmptyListThrows() {
        LinkedList<String> list = new LinkedList<>();

        assertThatThrownBy(list::removeLast).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void removeFirstReturnsAndUnlinksTheHead() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("b");

        String removed = list.removeFirst();

        assertThat(removed).isEqualTo("a");
        assertThat(toList(list)).containsExactly("b");
    }

    @Test
    void removeLastReturnsAndUnlinksTheTail() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("b");

        String removed = list.removeLast();

        assertThat(removed).isEqualTo("b");
        assertThat(toList(list)).containsExactly("a");
    }

    @Test
    void removingTheOnlyElementLeavesAnEmptyList() {
        LinkedList<String> list = new LinkedList<>();
        LinkedList.Node<String> only = list.addLast("a");

        list.remove(only);

        assertThat(list.isEmpty()).isTrue();
        assertThat(list.size()).isZero();
    }

    @Test
    void removingAMiddleNodeSplicesItsNeighborsTogether() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        LinkedList.Node<String> b = list.addLast("b");
        list.addLast("c");

        list.remove(b);

        assertThat(toList(list)).containsExactly("a", "c");
    }

    @Test
    void getReturnsTheValueAtTheGivenIndex() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);

        assertThat(list.get(1)).isEqualTo(20);
    }

    @Test
    void getRejectsOutOfBoundsIndexes() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(10);

        assertThatThrownBy(() -> list.get(-1)).isInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> list.get(1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void iteratesHeadToTailAndExhaustsCorrectly() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(1);
        list.addLast(2);

        Iterator<Integer> iterator = list.iterator();
        List<Integer> collected = new ArrayList<>();
        while (iterator.hasNext()) {
            collected.add(iterator.next());
        }

        assertThat(collected).containsExactly(1, 2);
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void headNodeAndTailNodeExposeTheEndsForTraversal() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        assertThat(list.headNode().value()).isEqualTo("a");
        assertThat(list.tailNode().value()).isEqualTo("c");
        assertThat(list.headNode().next().value()).isEqualTo("b");
        assertThat(list.tailNode().prev().value()).isEqualTo("b");
        assertThat(list.headNode().prev()).isNull();
        assertThat(list.tailNode().next()).isNull();
    }

    private static <T> List<T> toList(LinkedList<T> list) {
        List<T> result = new ArrayList<>();
        for (T value : list) {
            result.add(value);
        }
        return result;
    }
}
