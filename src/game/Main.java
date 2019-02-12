package game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.util.Random;


/**
 * The Main class creates the JavaFX implementation of the Tic-Tac-Toe application.
 */
public class Main extends Application {
    private static final int PLAY_BUTTON_SIZE = 200;
    private static final String APP_TITLE = "Simple Tic-Tac-Toe";

    private ButtonClickHandler buttonClickHandler;
    private GameState gameState;
    private AIPlayer aiPlayer;

    private Button[][] playBs;
    private Button resetB;
    private Button undoB;
    private Button redoB;

    private HBox[] playRows;
    private VBox playGrid;
    private VBox gameLayout;

    public static void main(String[] args) {
        launch(args);
    }

    // Create all the buttons for the application
    private Pane createButtonGroup() {
        playBs = new Button[3][3];
        playRows = new HBox[3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                playBs[i][j] = new Button();
                playBs[i][j].setPrefSize(PLAY_BUTTON_SIZE, PLAY_BUTTON_SIZE);
                playBs[i][j].setOnAction(buttonClickHandler);
                playBs[i][j].setStyle("-fx-font-size: 12em; -fx-text-overrun: clip;");
            }
        }

        for (int k = 0; k < 3; k++) {
            playRows[k] = new HBox(playBs[k][0], playBs[k][1], playBs[k][2]);
            playRows[k].setStyle("-fx-spacing: 10");

        }
        resetB = new Button("Reset");
        resetB.setStyle("-fx-font-size: 2em;");
        resetB.setOnAction(buttonClickHandler);

        undoB = new Button("Undo");
        undoB.setStyle("-fx-font-size: 2em;");
        undoB.setOnAction(buttonClickHandler);

        redoB = new Button("Redo");
        redoB.setStyle("-fx-font-size: 2em;");
        redoB.setOnAction(buttonClickHandler);

        HBox buttonRow = new HBox(resetB, undoB, redoB);
        buttonRow.setStyle("-fx-spacing: 40; -fx-padding: 0 0 0 20;");

        VBox playGridInner = new VBox(playRows[0], playRows[1], playRows[2]);
        playGridInner.setStyle("-fx-spacing: 10; -fx-background-color: black;");
        playGrid = new VBox(playGridInner);
        playGrid.setStyle(" -fx-padding: 20;");

        gameLayout = new VBox(buttonRow, playGrid);
        gameLayout.setStyle("-fx-spacing: 10");

        return new StackPane(gameLayout);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle(APP_TITLE );
        gameState = new GameState();
        aiPlayer = new AIPlayer(gameState);
        buttonClickHandler = new ButtonClickHandler(this);
        Pane layout = createButtonGroup();
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    boolean isPlayButton(Button b) {
        GridNumber gNum = getPlayButtonCoordinate(b);
        return gNum.isValid();
    }

    boolean isResetButton(Button b) {
        return (resetB == b);
    }

    boolean isUndoButton(Button b) {
        return (undoB == b);
    }

    boolean isRedoButton(Button b) {
        return (redoB == b);
    }

    private boolean isOccupied(Button b) {
        GridNumber gridNum = getPlayButtonCoordinate(b);
        return gameState.isOccupied(gridNum);
    }

    private boolean handleEndedGame() {
        boolean gameEnded = false;
        if (gameState.getHasWinner()) {
            if (gameState.getWinner() == Player.CIRCLE) {
                AlertBox.display("Game Ends", "O won!");
            } else if (gameState.getWinner() == Player.CROSS) {
                AlertBox.display("Game Ends", "X won!");
            }
            gameEnded = true;
            reset();
        } else if (gameState.getIsFull()) {
            AlertBox.display("Game Ends", "Game is tied!");
            gameEnded = true;
            reset();
        }
        return gameEnded;
    }

    private Button getPlayButton(GridNumber move) {
        return playBs[move.getRow()][move.getColumn()];
    }


    void play(Button b) {
        if (isOccupied(b)) {
            return;
        }
        GridNumber humanMove = getPlayButtonCoordinate(b);
        gameState.setMove(humanMove);
        b.setText("X");

        boolean gameEnded = handleEndedGame();
        if (gameEnded) {
            return;
        }

        GridNumber aiMove = aiPlayer.getGameMove(humanMove);
        gameState.setMove(aiMove);
        Button aiMoveB = getPlayButton(aiMove);
        aiMoveB.setText("O");

        handleEndedGame();
    }

    private GridNumber getPlayButtonCoordinate (Button b) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (playBs[i][j] == b) {
                    return new GridNumber(i, j);
                }
            }
        }
        return new GridNumber(-1, -1);
    }

    void reset() {
        gameState.initGame();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                playBs[i][j].setText(null);
            }
        }
    }

    private void undoImpl() {
        GameMove move = gameState.getUndoMove();
        playBs[move.getLocation().getRow()][move.getLocation().getColumn()].setText(null);
        gameState.undo();
    }

    void undo() {
        if (!gameState.canUndo()) {
            return;
        }
        undoImpl();
        undoImpl();
    }

    private void redoImpl() {
        GameMove move = gameState.getRedoMove();
        if (move.getPlayer() == Player.CIRCLE) {
            playBs[move.getLocation().getRow()][move.getLocation().getColumn()].setText("O");
        } else {
            playBs[move.getLocation().getRow()][move.getLocation().getColumn()].setText("X");
        }
        gameState.redo();
    }

    void redo() {
        if (!gameState.canRedo()) {
            return;
        }
        redoImpl();
        redoImpl();
    }
}
