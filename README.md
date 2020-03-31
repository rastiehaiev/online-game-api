# Running tests
```shell script
./gradlew clean test
```

# Running spring boot application
```shell script
./gradlew bootRun
```

# Requests

- Create new character:
```shell script
curl -X POST localhost:9292/character
```
- Update character's experience:
```shell script
curl -X POST localhost:9292/character/1/exp \
    --header "Content-Type: application/json" \
    --data '{"exp":10}'
```
- Get character's info
```shell script
curl localhost:9292/character/{id}
```