# Tic Tac Toe service
Web service to play tic-tac-toe game

## Tech stack
1. Language: Java 17
2. Framework: Spring Boot 3

## Gradlew Commands
### Build Command
`./gradlew build` : build the code and generate `.jar` file

### Clean Command
`./gradlew clean` : clean up and delete all build artifact

## Guides

### Run Test Infrastructure

### Pre-requisite:
- Install Docker (refer to https://docs.docker.com/get-docker/)
- Install Docker compose if not included in your installation (refer to https://docs.docker.com/compose/install/)

### How to run:
1. Make sure docker is already running
2. Open a terminal
3. Run `./gradlew clean build` command to create updated `.jar` file
4. Run `cd test-infrastructure` to go to `test-infrastructure` directory
5. Run `docker compose up service`
6. After a few moment you can run `docker ps` to check the containers status
7. Run `docker ps -aq | % { docker rm -f $_ }` for Windows or `docker rm -f $(docker ps -aq)` for Unix System to shut down and delete all containers
