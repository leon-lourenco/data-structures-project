package com.datastructures.trees.trie.applied;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PixKeyPrefixIndexTest {

    private final PixKeyPrefixIndex index = new PixKeyPrefixIndex();

    @BeforeEach
    void registerAFewPixKeysOfDifferentTypes() {
        index.register("12345678900"); // CPF-style key
        index.register("leon.gomes@example.com"); // email key
        index.register("+5511999998888"); // phone key
        index.register("a1b2c3d4-e5f6-7890-abcd-ef1234567890"); // random UUID-style key
    }

    @Test
    void anExactlyRegisteredKeyIsRecognizedAsRegistered() {
        assertThat(index.isRegisteredKey("leon.gomes@example.com")).isTrue();
    }

    @Test
    void aKeyThatWasNeverRegisteredIsNotRecognized() {
        assertThat(index.isRegisteredKey("someone-else@example.com")).isFalse();
    }

    @Test
    void autocompleteRecognizesAPartiallyTypedPrefixOfARegisteredKey() {
        assertThat(index.hasKeyStartingWith("leon.")).isTrue();
        assertThat(index.hasKeyStartingWith("+5511")).isTrue();
    }

    @Test
    void autocompleteRejectsAPrefixThatMatchesNoRegisteredKey() {
        assertThat(index.hasKeyStartingWith("99999")).isFalse();
    }

    @Test
    void anEmptyIndexHasNoStartingMatchesAndNoRegisteredKeys() {
        PixKeyPrefixIndex emptyIndex = new PixKeyPrefixIndex();

        assertThat(emptyIndex.hasKeyStartingWith("1")).isFalse();
        assertThat(emptyIndex.isRegisteredKey("12345678900")).isFalse();
    }
}
