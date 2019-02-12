package game;

/**
 * The GameMove class is used to represent a game move.
 */
class GameMove {
    private GridNumber location;
    private Player player;

    GameMove(GridNumber aLoc, Player aPlayer) {
        location = aLoc;
        player = aPlayer;
    }

    GridNumber getLocation() {
        return location;
    }

    Player getPlayer() {
        return player;
    }
}
