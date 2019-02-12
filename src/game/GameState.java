package game;

import java.util.Stack;

/**
 * The GameState class stores the state of the Tic-Tac-Toe game
 */
class GameState {
    private Player currentPlayer;
    private Player[][] occupied;

    private Stack<GameMove> prevMoves;
    private Stack<GameMove> nextMoves;

    GameState() {
        occupied = new Player[3][3];
        prevMoves = new Stack<>();
        nextMoves = new Stack<>();
        initGame();
    }

    private void chooseNewFirstPlayer() {
        currentPlayer = Player.CROSS;
    }

    private void chooseCurrentPlayer() {
        if (currentPlayer == Player.CIRCLE) {
            currentPlayer = Player.CROSS;
        } else {
            currentPlayer = Player.CIRCLE;
        }
    }

    private void clearOccupiedState() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                occupied[i][j] = Player.UNSET;
            }
        }
    }

    private void clearUndoRedoState() {
        prevMoves.clear();
        nextMoves.clear();
    }

    void initGame() {
        chooseNewFirstPlayer();
        clearOccupiedState();
        clearUndoRedoState();
    }

    private void setOccupied(GridNumber gridNum, Player player) {
        occupied[gridNum.getRow()][gridNum.getColumn()] = player;
    }

    Player getOccupiedBy(GridNumber gridNum) {
        return occupied[gridNum.getRow()][gridNum.getColumn()];
    }

    boolean isOccupied(GridNumber gridNum) {
        return getOccupiedBy(gridNum) != Player.UNSET;
    }

    void setMove(GridNumber gridNum) {
        prevMoves.push(new GameMove(gridNum, currentPlayer));
        nextMoves.clear();
        setOccupied(gridNum, currentPlayer);
        chooseCurrentPlayer();
    }

    Player getCurrentPlayer() {
        return currentPlayer;
    }

    boolean canUndo() {
        return !prevMoves.empty();
    }

    GameMove getUndoMove() {
        return prevMoves.peek();
    }

    void undo() {
        if (prevMoves.empty()) {
            return;
        }

        GameMove move = prevMoves.peek();
        currentPlayer = move.getPlayer();
        occupied[move.getLocation().getRow()][move.getLocation().getColumn()] = Player.UNSET;
        nextMoves.push(move);
        prevMoves.pop();
    }

    boolean canRedo() {
        return !nextMoves.empty();
    }

    GameMove getRedoMove() {
        return nextMoves.peek();
    }

    void redo() {
        if (nextMoves.empty()) {
            return;
        }
        GameMove move = nextMoves.peek();
        if (move.getPlayer() == Player.CIRCLE) {
            currentPlayer = Player.CROSS;
        } else {
            currentPlayer = Player.CIRCLE;
        }
        occupied[move.getLocation().getRow()][move.getLocation().getColumn()] = move.getPlayer();

        prevMoves.push(move);
        nextMoves.pop();
    }

    private boolean checkComboForWin(Player player, Player m1, Player m2, Player m3) {
        return player == m1 && player == m2 && player == m3;
    }

    private boolean getPlayerHasDiagWin(Player player) {
        return checkComboForWin(player, occupied[0][0], occupied[1][1], occupied[2][2]) ||
                checkComboForWin(player, occupied[0][2], occupied[1][1], occupied[2][0]);
    }

    private boolean getPlayerHasColWin(Player player) {
        for (int j = 0; j < 3; j++) {
            if (checkComboForWin(player, occupied[0][j], occupied[1][j], occupied[2][j])) {
                return true;
            }
        }
        return false;
    }

    private boolean getPlayerHasRowWin(Player player) {
        for (int i = 0; i < 3; i++) {
            if (checkComboForWin(player, occupied[i][0], occupied[i][1], occupied[i][2])) {
                return true;
            }
        }
        return false;
    }

    private boolean getPlayerHasWin (Player player) {
        return getPlayerHasRowWin(player) || getPlayerHasColWin(player) || getPlayerHasDiagWin(player);
    }

    boolean getIsFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (occupied[i][j] == Player.UNSET) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean getGameEnded() {
        return getHasWinner() || getIsFull();
    }

    boolean getHasWinner(){
        return getWinner() != Player.UNSET;
    }

    Player getWinner() {
        if (getPlayerHasWin(Player.CIRCLE)) {
            return Player.CIRCLE;
        } else if (getPlayerHasWin(Player.CROSS)) {
            return Player.CROSS;
        }
        return Player.UNSET;
    }



}
