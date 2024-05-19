package cz.muni.fi.pv260.minesweeper;

public class MineCell extends BoardCell {
    public MineCell() {
        super('M');
    }

    @Override
    public boolean isMine() {
        return true;
    }
}
