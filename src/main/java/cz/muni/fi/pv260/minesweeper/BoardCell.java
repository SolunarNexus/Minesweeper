package cz.muni.fi.pv260.minesweeper;

public class BoardCell {
    private char value;
    private boolean isFlagged = false;
    private boolean isRevealed = false;
    private boolean isMine = false;

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
        return isMine;
    }

    public void setMine(){
        isMine = true;
        value = 'M';
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

    public void reveal() {
        isRevealed = true;
    }
}
