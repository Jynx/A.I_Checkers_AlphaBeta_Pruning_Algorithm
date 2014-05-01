package cs540.checkers.volocyk;
import cs540.checkers.Utils;

public class Board {
    final int[] mBoard;
    final boolean mMaximizing;

    public Board(int[] bs, boolean maximizing) {
        mBoard = bs.clone();
        mMaximizing = maximizing;
    }

    /**
     * Returns a string representation of this board state.
     * @return      a string representation of this board state
     */
    public String toString()
    {
        return Utils.reprBoardState(mBoard);
    }
    
    public boolean equals(Object object) {
        if (object instanceof Board) {
            return equals((Board)object);
        } else {
            return (Object)this == object;
        }
    }

    public boolean equals(Board board)
    {
        if (this == board) {
            return true;
        } else if (mMaximizing == board.mMaximizing) {
            return Utils.equalsBoardState(mBoard, board.mBoard);
        }
        return false;
    }
}
