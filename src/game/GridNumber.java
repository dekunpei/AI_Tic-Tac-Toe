package game;

/**
 * The GridNumber class is used to represent a location.
 */
class GridNumber {
    private int row;
    private int column;

    GridNumber(int r, int c) {
        row = r;
        column = c;
    }

    boolean isValid() {
        return row >=0 && column >=0;
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    boolean isEqual(GridNumber another) {
        return (this.getRow() == another.getRow() &&
                this.getColumn() == another.getColumn());
    }
}