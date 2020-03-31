package com.online.game.api;

import com.online.game.api.model.Character;
import com.online.game.api.model.CharacterLevelInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OnlineGameApiApplicationIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReturnCharacterWithFirstLevelAndZeroExperience_whenCreatingNewCharacter() {
        CharacterLevelInfo expected = new CharacterLevelInfo(1, 0);

        Character actualCharacter = createNewCharacter();

        assertEquals(expected, actualCharacter.getLevelInfo());
    }

    @Test
    public void shouldCreateNewCharacterAndReturnIt() {
        Character createdCharacter = createNewCharacter();

        CharacterLevelInfo expected = new CharacterLevelInfo(1, 0);

        CharacterLevelInfo actual = getCharacterInfoById(createdCharacter.getId());

        assertEquals(expected, actual);
        assertEquals(expected, createdCharacter.getLevelInfo());
    }

    @Test
    public void shouldUpgradeCharacterExperience() {
        Character createdCharacter = createNewCharacter();

        CharacterLevelInfo expected = new CharacterLevelInfo(2, 10);

        CharacterLevelInfo actual = upgradeCharacterAndGetResult(createdCharacter.getId(), 110);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldUpgradeCharacterExperienceFromDifferentThreads() throws InterruptedException {
        Character createdCharacter = createNewCharacter();

        CharacterLevelInfo expected = new CharacterLevelInfo(8, 100);

        Callable<CharacterLevelInfo> task = () -> upgradeCharacterAndGetResult(createdCharacter.getId(), 10);
        List<Callable<CharacterLevelInfo>> tasks = getMultipleTasks(task, 200);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 2);
        executorService.invokeAll(tasks)
                .forEach(this::waitFutureResult);

        CharacterLevelInfo actual = getCharacterInfoById(createdCharacter.getId());
        assertEquals(expected, actual);

        executorService.shutdown();
    }

    @Test
    public void shouldReturnBadRequest_whenExperienceValueIsNegative() {
        Character createdCharacter = createNewCharacter();
        ResponseEntity<CharacterLevelInfo> response = upgradeCharacter(createdCharacter.getId(), -1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void shouldReturnNotFound_whenCharacterNotFoundById() {
        ResponseEntity<CharacterLevelInfo> response = upgradeCharacter(100000, 30);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private Character createNewCharacter() {
        return restTemplate.postForObject(getBaseUrl(), null, Character.class);
    }

    private CharacterLevelInfo getCharacterInfoById(int id) {
        return restTemplate.getForObject(getBaseUrlWithId(id), CharacterLevelInfo.class);
    }

    private CharacterLevelInfo upgradeCharacterAndGetResult(int id, int exp) {
        return upgradeCharacter(id, exp).getBody();
    }

    private ResponseEntity<CharacterLevelInfo> upgradeCharacter(int id, int exp) {
        Map<String, Integer> body = new HashMap<>();
        body.put("exp", exp);
        return restTemplate.postForEntity(getBaseUrlWithId(id) + "/exp", body, CharacterLevelInfo.class);
    }

    private String getBaseUrlWithId(int id) {
        return getBaseUrl() + "/" + id;
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/character";
    }

    private List<Callable<CharacterLevelInfo>> getMultipleTasks(Callable<CharacterLevelInfo> task, int count) {
        List<Callable<CharacterLevelInfo>> tasks = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(task);
        }
        return tasks;
    }

    private void waitFutureResult(Future<CharacterLevelInfo> characterLevelInfoFuture) {
        try {
            characterLevelInfoFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}