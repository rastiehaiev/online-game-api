package com.online.game.api.service;

import com.online.game.api.configuration.CharacterLevelConfiguration;
import com.online.game.api.model.CharacterLevelInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharacterLevelManagementServiceTest {

    private static CharacterLevelManagementService characterLevelManagementService;

    @BeforeAll
    public static void setUp() {
        Map<Integer, Integer> configuration = new LinkedHashMap<>();
        configuration.put(1, 100);
        configuration.put(2, 200);
        configuration.put(3, 300);
        configuration.put(4, 400);
        configuration.put(5, null);
        CharacterLevelConfiguration characterLevelConfiguration = new CharacterLevelConfiguration(configuration);
        characterLevelManagementService = new CharacterLevelManagementService(characterLevelConfiguration);
    }

    @Test
    public void shouldNotUpdateLevel_whenLackExperience() {
        CharacterLevelInfo given = new CharacterLevelInfo(1, 10);

        CharacterLevelInfo expected = new CharacterLevelInfo(1, 30);

        CharacterLevelInfo actual = characterLevelManagementService.upgrade(given, 20);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldLevelUp_whenHasMoreThanRequiredExperience() {
        CharacterLevelInfo given = new CharacterLevelInfo(1, 10);

        CharacterLevelInfo expected = new CharacterLevelInfo(2, 30);

        CharacterLevelInfo actual = characterLevelManagementService.upgrade(given, 120);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldLevelUp_whenHasExperienceEqualToRequiredExperience() {
        CharacterLevelInfo given = new CharacterLevelInfo(1, 10);

        CharacterLevelInfo expected = new CharacterLevelInfo(2, 0);

        CharacterLevelInfo actual = characterLevelManagementService.upgrade(given, 90);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldTwiceLevelUp_whenHasEnoughOfExperience() {
        CharacterLevelInfo given = new CharacterLevelInfo(1, 10);

        CharacterLevelInfo expected = new CharacterLevelInfo(3, 60);

        CharacterLevelInfo actual = characterLevelManagementService.upgrade(given, 350);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldOnlyUpdateExperience_whenReachedTopLevel() {
        CharacterLevelInfo given = new CharacterLevelInfo(5, 10);

        CharacterLevelInfo expected = new CharacterLevelInfo(5, 1010);

        CharacterLevelInfo actual = characterLevelManagementService.upgrade(given, 1000);

        assertEquals(expected, actual);
    }
}