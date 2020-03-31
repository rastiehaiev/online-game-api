package com.online.game.api.configuration.parser;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterLevelConfigurationInnerParserTest {

    private final CharacterLevelConfigurationInnerParser parser = new CharacterLevelConfigurationInnerParser();

    @Test
    public void shouldSuccessfullyParseValidConfiguration() {
        List<String> given = Arrays.asList(
                "1+   |   100",
                "3+   |   200",
                "5   |   -");

        Map<Integer, Integer> expected = new LinkedHashMap<>();
        expected.put(1, 100);
        expected.put(2, 100);
        expected.put(3, 200);
        expected.put(4, 200);
        expected.put(5, null);

        Map<Integer, Integer> actual = parser.parse(given.stream());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldThrowIllegalStateException_whenConfigurationIsEmpty() {
        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(Stream.of())
        );
        assertEquals("Characters Level configuration is empty.", thrown.getMessage());
    }

    @Test
    public void shouldThrowIllegalStateException_whenConfigurationRowHasInvalidFormat() {
        List<String> given = Arrays.asList(
                "1+   |   100",
                "3+   |   200 | 100");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(given.stream())
        );
        assertEquals("Each row in the configuration should have 2 columns separated by a pipe (|).", thrown.getMessage());
    }

    @Test
    public void shouldThrowIllegalStateException_whenExperienceHasBeenSetForTopLevelConfig() {
        List<String> given = Arrays.asList(
                "1+   |   100",
                "3    |   200");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(given.stream())
        );
        assertEquals("Experience value should not be set for the top level configuration.", thrown.getMessage());
    }

    @Test
    public void shouldThrowIllegalStateException_whenConfigurationEntriesAreNotSorted() {
        List<String> given = Arrays.asList(
                "1+    |   100",
                "5+    |   200",
                "3+    |   150");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(given.stream())
        );
        assertEquals("Invalid configuration: entries must be sorted by level.", thrown.getMessage());
    }

    @Test
    public void shouldThrowIllegalStateException_whenLevelValueIsNegative() {
        List<String> given = Arrays.asList(
                "1+     |   100",
                "-5+    |   200");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(given.stream())
        );
        assertEquals("Level string should be a positive number.", thrown.getMessage());
    }

    @Test
    public void shouldThrowIllegalStateException_whenExperienceValueIsNegative() {
        List<String> given = Arrays.asList(
                "1+   |   100",
                "5    |   -200");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(given.stream())
        );
        assertEquals("Experience string should be a positive number.", thrown.getMessage());
    }

    @Test
    public void shouldThrowIllegalStateException_whenExperienceValueIsNotSetForIntermediateLevel() {
        List<String> given = Arrays.asList(
                "1+   |   100",
                "3+   |   -",
                "5    |   200");

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> parser.parse(given.stream())
        );
        assertEquals("Experience value must be set for the intermediate level configuration.", thrown.getMessage());
    }
}