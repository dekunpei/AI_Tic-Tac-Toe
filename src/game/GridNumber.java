package game;

/**
 * The GridNumber class is used to represent a location.
 */
class GridNumber {
    private int row;
    private int column;
    private final int hash;

    GridNumber(int r, int c) {
        row = r;
        column = c;
        int sum = r+c;
        hash = sum*(sum+1)/2 + r;
    }

    static GridNumber getInvalidObject() {
        return new GridNumber(-1, -1);
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

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof GridNumber)) {
            return false;
        } else if (another == this) {
            return true;
        }
        GridNumber rhs = (GridNumber) another;
        return (this.getRow() == rhs.getRow() &&
                this.getColumn() == rhs.getColumn());
    }

    boolean equals(int r, int c){
        return (this.getRow() == r && this.getColumn() == c);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}