package de.jeter.chatex.utils;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DomainDictionaryTest {

    private static final Set<String> ENDINGS = Set.of("com", "net", "de", "co.uk");

    @Test
    void matchesKnownTopLevelEnding() {
        assertTrue(DomainDictionary.containsTopLevelEnding("example.com", ENDINGS));
        assertTrue(DomainDictionary.containsTopLevelEnding("example.de", ENDINGS));
    }

    @Test
    void matchesOnLeftAnchoredPrefixOfTheEnding() {
        // The scan builds the ending left-to-right and checks each prefix against
        // the dictionary, so "coma" already matches on its "com" prefix.
        assertTrue(DomainDictionary.containsTopLevelEnding("example.coma", ENDINGS));
    }

    @Test
    void doesNotMatchUnknownEnding() {
        assertFalse(DomainDictionary.containsTopLevelEnding("example.xyz", ENDINGS));
    }

    @Test
    void emptyEndingSetNeverMatches() {
        assertFalse(DomainDictionary.containsTopLevelEnding("example.com", Set.of()));
    }

    @Test
    void usesLastDotSeparatedPartOnly() {
        assertFalse(DomainDictionary.containsTopLevelEnding("com.example.xyz", ENDINGS));
    }
}
