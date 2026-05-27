package dev.maurice.chess.api.service;

import dev.maurice.chess.api.dto.CreateGameRequest;
import dev.maurice.chess.api.dto.GameResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Test
    void createGameShouldUseActiveColorFromFullFen() {
        GameResponse response = gameService.createGame(
                new CreateGameRequest(
                        "WHITE",
                        "7k/6Q1/5K2/8/8/8/8/8 b - - 0 1"
                )
        );

        assertEquals("BLACK", response.sideToMove());
        assertEquals("CHECKMATE", response.status());
    }

    @Test
    void createGameShouldDefaultSideToMoveToWhiteForPiecePlacementOnlyFen() {
        GameResponse response = gameService.createGame(
                new CreateGameRequest(
                        "WHITE",
                        "7k/6Q1/5K2/8/8/8/8/8"
                )
        );

        assertEquals("WHITE", response.sideToMove());
        assertEquals("ACTIVE", response.status());
    }
}
