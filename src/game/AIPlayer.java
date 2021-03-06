package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * The AIPlayer class stores the logic of the Tic-Tac-Toe AI player
 */
class AIPlayer {
    private GameState gameState;
    private static final Player AI = Player.CIRCLE;
    private static final Player HUMAN = Player.CROSS;
    private static final int[][] INDEX_COMP;
    private static final HashMap<GridNumber, ArrayList<GridNumber>> DIAGONAL_COMP;
    private static final HashMap<GridNumber, GridNumber> OPPOSITE_CORNERS;
    private static final ArrayList<GridNumber> LEFT_RIGHT_DIAGONAL;
    private static final ArrayList<GridNumber> RIGHT_LEFT_DIAGONAL;
    private static final ArrayList<GridNumber> CORNERS;
    private static final ArrayList<GridNumber> SIDES;

    static {
        // Initialize complements of an index; complements here are locations on the
        // same row or column
        INDEX_COMP = new int[3][2];
        INDEX_COMP[0][0] = 1;
        INDEX_COMP[0][1] = 2;
        INDEX_COMP[1][0] = 0;
        INDEX_COMP[1][1] = 2;
        INDEX_COMP[2][0] = 0;
        INDEX_COMP[2][1] = 1;

        // Initialize all possible locations
        GridNumber loc00 = new GridNumber(0, 0);
        GridNumber loc01 = new GridNumber(0, 1);
        GridNumber loc02 = new GridNumber(0, 2);
        GridNumber loc10 = new GridNumber(1, 0);
        GridNumber loc11 = new GridNumber(1, 1);
        GridNumber loc12 = new GridNumber(1, 2);
        GridNumber loc20 = new GridNumber(2, 0);
        GridNumber loc21 = new GridNumber(2, 1);
        GridNumber loc22 = new GridNumber(2, 2);

        // Initialize diagonal complements of a move; complements here are locations
        // that are on the same diagonals
        DIAGONAL_COMP = new HashMap<>();
        ArrayList<GridNumber> diagComp00 = new ArrayList<>();
        diagComp00.add(new GridNumber(1, 1));
        diagComp00.add(new GridNumber(2, 2));

        ArrayList<GridNumber> diagComp11 = new ArrayList<>();
        diagComp11.add(new GridNumber(0, 0));
        diagComp11.add(new GridNumber(2, 2));
        diagComp11.add(new GridNumber(0, 2));
        diagComp11.add(new GridNumber(2, 0));

        ArrayList<GridNumber> diagComp22 = new ArrayList<>();
        diagComp22.add(new GridNumber(0, 0));
        diagComp22.add(new GridNumber(1, 1));

        ArrayList<GridNumber> diagComp02 = new ArrayList<>();
        diagComp02.add(new GridNumber(2, 0));
        diagComp02.add(new GridNumber(1, 1));

        ArrayList<GridNumber> diagComp20 = new ArrayList<>();
        diagComp20.add(new GridNumber(0, 2));
        diagComp20.add(new GridNumber(1, 1));

        DIAGONAL_COMP.put(loc00, diagComp00);
        DIAGONAL_COMP.put(loc11, diagComp11);
        DIAGONAL_COMP.put(loc22, diagComp22);
        DIAGONAL_COMP.put(loc02, diagComp02);
        DIAGONAL_COMP.put(loc20, diagComp20);

        // Initialize a lookup table of opposite corners
        OPPOSITE_CORNERS = new HashMap<>();
        OPPOSITE_CORNERS.put(loc00, loc22);
        OPPOSITE_CORNERS.put(loc02, loc20);
        OPPOSITE_CORNERS.put(loc20, loc02);
        OPPOSITE_CORNERS.put(loc22, loc22);

        // Initialize a lookup of moves on each diagonal
        LEFT_RIGHT_DIAGONAL = new ArrayList<>();
        LEFT_RIGHT_DIAGONAL.add(loc00);
        LEFT_RIGHT_DIAGONAL.add(loc11);
        LEFT_RIGHT_DIAGONAL.add(loc22);

        RIGHT_LEFT_DIAGONAL = new ArrayList<>();
        RIGHT_LEFT_DIAGONAL.add(loc02);
        LEFT_RIGHT_DIAGONAL.add(loc11);
        LEFT_RIGHT_DIAGONAL.add(loc20);

        CORNERS = new ArrayList<>();
        CORNERS.add(loc00);
        CORNERS.add(loc02);
        CORNERS.add(loc20);
        CORNERS.add(loc22);

        SIDES = new ArrayList<>();
        SIDES.add(loc01);
        SIDES.add(loc12);
        SIDES.add(loc21);
        SIDES.add(loc10);
    }

    AIPlayer(GameState aGameState) {
        gameState = aGameState;
    }

    private boolean isOccupiedBy(Player player, int r, int c) {
        return gameState.getOccupiedBy(new GridNumber(r, c)) == player;
    }

    private boolean isOccupiedBy(Player player, GridNumber loc) {
        return gameState.getOccupiedBy(loc) == player;
    }

    private boolean isFree(int r, int c) {
        return !gameState.isOccupied(new GridNumber(r, c));
    }

    private boolean isFree(GridNumber move){
        return !gameState.isOccupied(move);
    }

    private boolean isMoveOnRightLeftDiagonal(GridNumber move) {
        return RIGHT_LEFT_DIAGONAL.contains(move);
    }

    private boolean isMoveOnLeftRightDiagonal(GridNumber move) {
        return LEFT_RIGHT_DIAGONAL.contains(move);
    }

    private boolean isMoveOnADiagonal (GridNumber move) {
        return isMoveOnLeftRightDiagonal(move) || isMoveOnRightLeftDiagonal(move);
    }


    private Player getOpponent(Player player) {
        if (player == AI) {
            return HUMAN;
        } else {
            assert(player == HUMAN);
            return AI;
        }
    }

    private int getNumDiagonalThreatsCompletedByMove(Player player, GridNumber move) {
        assert(isFree(move));
        int leftRightDiag = 0;
        int rightLeftDiag = 0;
        if (!isMoveOnADiagonal(move)) {
            return 0;
        }
        ArrayList<GridNumber> diagonalComp = DIAGONAL_COMP.get(move);
        boolean foundPlayerOnLeftRight = false;
        boolean foundOpponentOnLeftRight = false;
        boolean foundPlayerOnRightLeft = false;
        boolean foundOpponentOnRightLeft = false;
        for (GridNumber location : diagonalComp) {

            if (isOccupiedBy(player, location.getRow(), location.getColumn())) {
                if (isMoveOnLeftRightDiagonal(move)) {
                    foundPlayerOnLeftRight = true;
                }
                if (isMoveOnRightLeftDiagonal(move)) {
                    foundPlayerOnRightLeft = true;
                }
            } else if (isOccupiedBy(getOpponent(player), location.getRow(), location.getColumn())) {
                if (isMoveOnLeftRightDiagonal(move)) {
                    foundOpponentOnLeftRight = true;
                }
                if (isMoveOnRightLeftDiagonal(move)) {
                    foundOpponentOnRightLeft = true;
                }
            }
        }
        if(foundPlayerOnLeftRight && !foundOpponentOnLeftRight) {
            leftRightDiag = 1;
        }
        if (foundPlayerOnRightLeft && !foundOpponentOnRightLeft) {
            rightLeftDiag = 1;
        }
        return leftRightDiag + rightLeftDiag;
    }

    private boolean isMoveCompletingTwoInAColumn(Player player, GridNumber move) {
        assert(isFree(move));
        for (int i = 0; i <= 2; i++) {
            int col = move.getColumn();
            int row = move.getRow();
            boolean foundPlayer = false;
            boolean foundOpponent = false;
            for (int k = 0; k < 2; k++) {
                if (isOccupiedBy(player, INDEX_COMP[row][k], col)) {
                    foundPlayer = true;
                } else if (isOccupiedBy(getOpponent(player), INDEX_COMP[row][k], col)) {
                    foundOpponent = true;
                }
            }
            if (foundPlayer && !foundOpponent) {
                return true;
            }
        }
        return false;
    }

    private boolean isMoveCompletingTwoInARow(Player player, GridNumber move) {
        assert(isFree(move));
        for (int i = 0; i <= 2; i++) {
            int col = move.getColumn();
            int row = move.getRow();
            boolean foundPlayer = false;
            boolean foundOpponent = false;
            for (int k = 0; k < 2; k++) {
               if (isOccupiedBy(player, row, INDEX_COMP[col][k])) {
                   foundPlayer = true;
               } else if (isOccupiedBy(getOpponent(player), row, INDEX_COMP[col][k])) {
                   foundOpponent = true;
               }
            }
            if (foundPlayer && !foundOpponent) {
                return true;
            }
        }
        return false;
    }

    private int getMoveThreatAdd(Player player, GridNumber move) {
        int numThreats = 0;
        if (isMoveCompletingTwoInARow(player, move)) {
            numThreats++;
        }
        if (isMoveCompletingTwoInAColumn(player, move)) {
            numThreats++;
        }
        numThreats += getNumDiagonalThreatsCompletedByMove(player, move);
        return numThreats;
    }

    private boolean isMoveForking(Player player, GridNumber move) {
        return getMoveThreatAdd(player, move) >= 2;
    }

    private ArrayList<GridNumber> getAvailableMoves(){
        ArrayList<GridNumber> availableMoves = new ArrayList<>();
        for (int r = 0; r <= 2; r++) {
            for (int c = 0; c <= 2; c++) {
                if(isFree(r, c)) {
                    availableMoves.add(new GridNumber(r, c));
                }
            }
        }
        return availableMoves;
    }

    private ArrayList<GridNumber> getForkingMoves(Player player) {
        ArrayList<GridNumber> forkingMoves = new ArrayList<>();
        ArrayList<GridNumber> availableMoves = getAvailableMoves();
        for (GridNumber move : availableMoves) {
            if (isMoveForking(player, move)) {
                forkingMoves.add(move);
            }
        }
        return forkingMoves;
    }

    private GridNumber getForkingMove() {
        GridNumber move = GridNumber.getInvalidObject();
        ArrayList<GridNumber> forkingMoves = getForkingMoves(AI);
        if (forkingMoves.size() >= 1 ) {
            move = forkingMoves.get(0);
        }
        return move;
    }

    private GridNumber handleTwoForks(ArrayList<GridNumber> availableMoves, ArrayList<GridNumber> forkingMoves){
        // If HUMAN has multiple forking moves available, force the HUMAN to block
        // in a way that he cannot complete the fork
        GridNumber move = GridNumber.getInvalidObject();
        if (forkingMoves.size() < 2) {
            return move;
        }

        for (GridNumber availableMove : availableMoves) {
            gameState.setOccupied(availableMove, AI);
            GridNumber winMove = getWinMove(AI);
            ArrayList<GridNumber> opponentForkingMoves = getForkingMoves(HUMAN);
            if (winMove.isValid() && !opponentForkingMoves.contains(winMove)) {
                move = availableMove;
                gameState.unSetOccupied(availableMove);
                break;
            }
            gameState.unSetOccupied(availableMove);
        }
        return move;
    }

    private GridNumber getForkBlockingMove() {
        GridNumber move;
        ArrayList<GridNumber> availableMoves = getAvailableMoves();
        ArrayList<GridNumber> forkingMoves = getForkingMoves(HUMAN);
        move = handleTwoForks(availableMoves, forkingMoves);
        if (move.isValid()) {
            return move;
        }
        if (forkingMoves.size() > 0) {
            move = forkingMoves.get(0);
        }
        for (GridNumber forkingMove : forkingMoves) {
            if (getMoveThreatAdd(AI, forkingMove) >= 1) {
                move = forkingMove;
                break;
            }
        }
        return move;
    }

    private GridNumber getPlayToCompleteDiagonal(Player player) {
        for (GridNumber move : DIAGONAL_COMP.keySet()) {
            if (move.getRow() == 1 && move.getColumn() == 1) {
                // The complements of the center spans two diagonals, specially handled below
                continue;
            }
            ArrayList<GridNumber> comp = DIAGONAL_COMP.get(move);
            assert(comp.size() == 2);
            if (isFree(move) && isOccupiedBy(player, comp.get(0)) && isOccupiedBy(player, comp.get(1))) {
                return move;
            }
        }
        if (isFree(1, 1) && isOccupiedBy(player, 0, 0) && isOccupiedBy(player, 2, 2)) {
            return new GridNumber(1, 1);
        } else if (isFree(1, 1) && isOccupiedBy(player, 0, 2) && isOccupiedBy(player, 2, 0)) {
            return new GridNumber(1, 1);
        }
        return GridNumber.getInvalidObject();
    }

    private GridNumber getPlayToCompleteColumn(Player player) {
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                if (isFree(r, c) && isOccupiedBy(player, INDEX_COMP[r][0], c) &&
                        isOccupiedBy(player, INDEX_COMP[r][1], c)) {
                    return new GridNumber(r, c);
                }
            }
        }
        return GridNumber.getInvalidObject();
    }

    private GridNumber getPlayToCompleteRow(Player player) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (isFree(r, c) && isOccupiedBy(player, r, INDEX_COMP[c][0]) &&
                        isOccupiedBy(player, r, INDEX_COMP[c][1])) {
                    return new GridNumber(r, c);
                }
            }
        }
        return GridNumber.getInvalidObject();
    }

    private GridNumber getWinMove(Player player) {
        GridNumber winMove = getPlayToCompleteRow(player);
        if (!winMove.isValid()) {
            winMove = getPlayToCompleteColumn(player);
        }
        if(!winMove.isValid()) {
            winMove = getPlayToCompleteDiagonal(player);
        }
        return winMove;
    }

    /* A random move generator for prototyping purposes
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
    */

    private GridNumber getCenterMove() {
        return isFree(1, 1) ? new GridNumber(1, 1) : GridNumber.getInvalidObject();
    }

    private GridNumber getOppositeCornerMove(GridNumber humanMove) {
        GridNumber aiMove = GridNumber.getInvalidObject();
        if (OPPOSITE_CORNERS.containsKey(humanMove) && isFree(OPPOSITE_CORNERS.get(humanMove))) {
            aiMove = OPPOSITE_CORNERS.get(humanMove);
        }
        return aiMove;
    }

    private GridNumber getFromListImpl(ArrayList<GridNumber> moveList) {
        GridNumber aiMove = GridNumber.getInvalidObject();
        ArrayList<GridNumber> availableMoves = new ArrayList<>();
        for (GridNumber move : moveList) {
            if (isFree(move)) {
                availableMoves.add(move);
            }
        }
        if (availableMoves.size() == 1) {
            aiMove = availableMoves.get(0);
        } else if (availableMoves.size() > 1) {
            Random random = new Random();
            int idx = random.nextInt(availableMoves.size());
            aiMove = availableMoves.get(idx);
        }
        return aiMove;
    }

    private GridNumber getEmptySideMove() {
        return getFromListImpl(SIDES);
    }

    private GridNumber getEmptyCornerMove(){
        return getFromListImpl(CORNERS);
    }

    GridNumber getGameMove(GridNumber humanMove) {
        GridNumber aiMove = getWinMove(AI); // 1. Win
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getWinMove(HUMAN); // 2. Block
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getForkingMove(); // 3. Fork
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getForkBlockingMove(); // 4. Block a fork
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getCenterMove(); // 5. Center
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getOppositeCornerMove(humanMove); // 6. Opposite corner
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getEmptyCornerMove(); // 7. Empty corner
        if (aiMove.isValid()) {
            return aiMove;
        }
        aiMove = getEmptySideMove(); // 8. Empty side
        if (aiMove.isValid()) {
            return aiMove;
        }
        assert false : "Detect unhandled situation.";
        return aiMove;
    }
}
