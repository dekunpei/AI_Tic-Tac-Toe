package game;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * The ButtonClickHandler class routes button presses to game actions.
 */
class ButtonClickHandler implements EventHandler<ActionEvent> {
    private Main game;

    ButtonClickHandler(Main aGame) {
        game = aGame;
    }

    @Override
    public void handle(ActionEvent event) {
        if (!(event.getSource() instanceof Button)) {
            return;
        }
        Button clickedB = (Button) event.getSource();

        if (game.isPlayButton(clickedB)) {
            game.play(clickedB);
        } else if (game.isResetButton(clickedB)) {
            game.reset();
        } else if (game.isUndoButton(clickedB)) {
            game.undo();
        } else if (game.isRedoButton(clickedB)) {
            game.redo();
        }
    }
}
