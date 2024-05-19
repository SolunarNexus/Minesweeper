package cz.muni.fi.pv260.minesweeper;

abstract class BoardCell {
    private char value;
    private boolean isFlagged = false;
    private boolean isRevealed = false;

    public BoardCell(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public boolean isMine(){
        return false;
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void toggleFlag() {
        isFlagged = !isFlagged;
    }

    public boolean isRevealed() {
        return isRevealed;
    }

    public void setRevealed(boolean revealed) {
        isRevealed = revealed;
    }
}
