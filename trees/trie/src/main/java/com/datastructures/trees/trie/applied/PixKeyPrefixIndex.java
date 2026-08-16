package com.datastructures.trees.trie.applied;

import com.datastructures.trees.trie.classic.Trie;

/**
 * Validates and autocompletes PIX keys (a BACEN-registered key can be a CPF, an email address,
 * a phone number, or a random UUID-style key) as a user types one into a payment form, without
 * a round trip to the key-directory service on every keystroke. {@link #hasKeyStartingWith}
 * backs an autocomplete-style UI ("does continuing to type this make sense, or is it already a
 * dead end?"), and {@link #isRegisteredKey} is the exact-match pre-check run once the full key
 * is typed. Both cost is proportional to the length of what's been typed so far, not to how
 * many keys BACEN has on file — see this module's benchmark.
 */
public final class PixKeyPrefixIndex {

    private final Trie keys = new Trie();

    public void register(String pixKey) {
        keys.insert(pixKey);
    }

    public boolean isRegisteredKey(String pixKey) {
        return keys.contains(pixKey);
    }

    /** True if any registered PIX key starts with what the user has typed so far. */
    public boolean hasKeyStartingWith(String typedSoFar) {
        return keys.startsWith(typedSoFar);
    }
}
