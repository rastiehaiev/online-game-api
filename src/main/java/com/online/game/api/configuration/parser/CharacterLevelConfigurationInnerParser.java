package com.online.game.api.configuration.parser;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class CharacterLevelConfigurationInnerParser {

    public Map<Integer, Integer> parse(Stream<String> streamOfLines) {

        List<CharacterLevelConfigurationEntry> configurationEntries = streamOfLines
                .map(this::splitLineToValidColumns)
                .map(this::trimColumns)
                .map(this::toCharacterLevelConfigurationEntry)
                .collect(toList());

        if (CollectionUtils.isEmpty(configurationEntries)) {
            throw new IllegalStateException("Characters Level configuration is empty.");
        }

        List<Pair<Integer, Integer>> configurationPairs = streamOfConsecutivePairs(configurationEntries)
                .map(this::mapOnLevel)
                .map(this::expandByLevelValues)
                .flatMap(List::stream)
                .collect(toList());

        return toLinkedHashMap(configurationPairs);
    }

    private String[] splitLineToValidColumns(String line) {
        String[] columns = line.split("\\|");
        if (columns.length != 2) {
            throw new IllegalStateException("Each row in the configuration should have 2 columns separated by a pipe (|).");
        }
        return columns;
    }

    private String[] trimColumns(String[] entry) {
        return Stream.of(entry).map(String::trim).toArray(String[]::new);
    }

    private CharacterLevelConfigurationEntry toCharacterLevelConfigurationEntry(String[] entry) {
        return new CharacterLevelConfigurationEntry(entry[0], parseExperienceValue(entry[1]));
    }

    private <T> Stream<Pair<T, T>> streamOfConsecutivePairs(List<T> list) {
        // this is a workaround, because Java Streams API does not have 'zip' method
        List<Pair<T, T>> pairs = new LinkedList<>();
        list.stream().reduce((current, next) -> {
            pairs.add(new Pair<>(current, next));
            return next;
        });
        pairs.add(new Pair<>(list.get(list.size() - 1), null));
        return pairs.stream();
    }

    private List<Pair<Integer, Integer>> expandByLevelValues(Pair<Level, Integer> pair) {
        return pair.key.get().stream()
                .map(level -> new Pair<>(level, pair.value))
                .collect(toList());
    }

    private Pair<Level, Integer> mapOnLevel(Pair<CharacterLevelConfigurationEntry, CharacterLevelConfigurationEntry> pair) {
        CharacterLevelConfigurationEntry left = pair.key;
        Level level = getLevel(left, pair.value);
        return new Pair<>(level, left.experience);
    }

    private Level getLevel(CharacterLevelConfigurationEntry left, CharacterLevelConfigurationEntry right) {
        String lowerBoundString = left.level;
        if (right == null) {
            return getTopLevel(left, lowerBoundString);
        }
        if (left.experience == null) {
            throw new IllegalStateException("Experience value must be set for the intermediate level configuration.");
        }
        int lowerBound = parseLevel(lowerBoundString);
        int upperBound = parseLevel(right.level);
        if (lowerBound > upperBound) {
            throw new IllegalStateException("Invalid configuration: entries must be sorted by level.");
        }
        return new LevelRange(lowerBound, upperBound);
    }

    private Level getTopLevel(CharacterLevelConfigurationEntry left, String lowerBoundString) {
        if (left.experience != null) {
            throw new IllegalStateException("Experience value should not be set for the top level configuration.");
        }
        return new SingleLevel(parseLevel(lowerBoundString));
    }

    private int parseLevel(String levelString) {
        if (levelString.endsWith("+")) {
            levelString = levelString.substring(0, levelString.length() - 1);
        }
        return assertPositiveNumber(levelString, "Level");
    }

    private Integer parseExperienceValue(String value) {
        if (value.equals("-")) {
            return null;
        }
        return assertPositiveNumber(value, "Experience");
    }

    private int assertPositiveNumber(String value, String name) {
        int experience = NumberUtils.toInt(value, 0);
        if (experience <= 0) {
            throw new IllegalStateException(name + " string should be a positive number.");
        }
        return experience;
    }

    private Map<Integer, Integer> toLinkedHashMap(List<Pair<Integer, Integer>> configurationPairs) {
        return configurationPairs.stream()
                .collect(LinkedHashMap::new, (map, pair) -> map.put(pair.key, pair.value), LinkedHashMap::putAll);
    }

    @RequiredArgsConstructor
    private static class Pair<K, V> {
        private final K key;
        private final V value;
    }

    private interface Level {

        List<Integer> get();
    }

    @RequiredArgsConstructor
    private static class LevelRange implements Level {
        private final int from;
        private final int to;


        @Override
        public List<Integer> get() {
            return IntStream.range(from, to).boxed().collect(toList());
        }
    }

    @RequiredArgsConstructor
    private static class SingleLevel implements Level {
        private final int value;

        @Override
        public List<Integer> get() {
            return Collections.singletonList(value);
        }
    }

    @RequiredArgsConstructor
    private static class CharacterLevelConfigurationEntry {
        private final String level;
        private final Integer experience;
    }
}
