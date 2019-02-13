package game;

import java.util.Random;

/**
 * The AIPlayer class stores the logic of the Tic-Tac-Toe AI player
 */
class AIPlayer {
    private GameState gameState;
    private static final Player AI = Player.CIRCLE;
    private static final Player HUMAN = Player.CROSS;

    AIPlayer(GameState aGameState) {
        gameState = aGameState;
    }

    boolean isOccupiedBy(Player player, int r, int c) {
        return gameState.getOccupiedBy(new GridNumber(r, c)) == player;
    }

    boolean isOccupied(int r, int c) {
        return gameState.isOccupied(new GridNumber(r, c));
    }

    private GridNumber getPlayToCompleteDiagonal(Player player) {
        if (!isOccupied(2, 2) && isOccupiedBy(player, 0, 0) && isOccupiedBy(player, 1, 1)) {
            return new GridNumber(2, 2);
        } else if (!isOccupied(1, 1) && isOccupiedBy(player, 0, 0) && isOccupiedBy(player, 2, 2)) {
            return new GridNumber(1, 1);
        } else if (!isOccupied(0, 0) && isOccupiedBy(player, 1, 1) && isOccupiedBy(player, 2, 2)) {
            return new GridNumber(0, 0);
        } else if (!isOccupied(2, 0) && isOccupiedBy(player, 0, 2) && isOccupiedBy(player, 1, 1)) {
            return new GridNumber(2, 0);
        } else if (!isOccupied(2, 0) && isOccupiedBy(player, 0, 2) && isOccupiedBy(player, 2, 0)) {
            return new GridNumber(1, 1);
        } else if (!isOccupied(0, 2) && isOccupiedBy(player, 1, 1) && isOccupiedBy(player, 2, 0)) {
            return new GridNumber(0, 2);
        }
        return GridNumber.getInvalidObject();
    }

    private GridNumber getPlayToCompleteColumn(Player player) {
        for (int i = 0; i < 3; i++) {
            if (!isOccupied(2, i) && isOccupiedBy(player, 0, i) && isOccupiedBy(player, 1, i)) {
                return new GridNumber(2, i);
            } else if (!isOccupied(1, i) && isOccupiedBy(player, 0, i) && isOccupiedBy(player, 2, i)) {
                return new GridNumber(1, i);
            } else if (!isOccupied(0, i) && isOccupiedBy(player, 1, i) && isOccupiedBy(player, 2, i)) {
                return new GridNumber(0, i);
            }
        }
        return GridNumber.getInvalidObject();
    }

    private GridNumber getPlayToCompleteRow(Player player) {
        for (int i = 0; i < 3; i++) {
            if (!isOccupied(i, 2) && isOccupiedBy(player, i, 0) && isOccupiedBy(player, i, 1)) {
                return new GridNumber(i, 2);
            } else if (!isOccupied(i, 1) && isOccupiedBy(player, i, 0) && isOccupiedBy(player, i, 2)) {
                return new GridNumber(i, 1);
            } else if (!isOccupied(i, 0) && isOccupiedBy(player, i, 1) && isOccupiedBy(player, i, 2)) {
                return new GridNumber(i, 0);
            }
        }
        return GridNumber.getInvalidObject();
    }

    private GridNumber getWinMove(Player player) {
        GridNumber winMove = GridNumber.getInvalidObject();
        winMove = getPlayToCompleteRow(player);
        if (!winMove.isValid()) {
            winMove = getPlayToCompleteColumn(player);
        }
        if(!winMove.isValid()) {
            winMove = getPlayToCompleteDiagonal(player);
        }
        return winMove;
    }

    private GridNumber getRandomMove() {
        GridNumber aiMove = GridNumber.getInvalidObject();
        Random rand = new Random();
        while (!aiMove.isValid() || gameState.isOccupied(aiMove)) {
            int row = rand.nextInt(3);
            int col = rand.nextInt(3);
            aiMove = new GridNumber(row, col);
        }
        return aiMove;
    }

    GridNumber getGameMove() {
        GridNumber aiMove = getWinMove(AI); // 1. Win
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getWinMove(HUMAN); // 2. Block
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getRandomMove();
        assert(aiMove.isValid());
        return aiMove;
    }
}
