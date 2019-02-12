package game;

import java.util.Random;

/**
 * The AIPlayer class stores the logic of the Tic-Tac-Toe AI player
 */
class AIPlayer {
    private GameState gameState;

    AIPlayer(GameState aGameState) {
        gameState = aGameState;
    }

    GridNumber getGameMove(GridNumber humanMove) {
        GridNumber aiMove = humanMove;
        Random rand = new Random();
        while (gameState.isOccupied(aiMove)) {
            int row = rand.nextInt(3);
            int col = rand.nextInt(3);
            aiMove = new GridNumber(row, col);
        }
        return aiMove;
    }
}
