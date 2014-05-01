package cs540.checkers.volocyk;

import cs540.checkers.Move;

public class MoveScore implements Comparable<MoveScore> {
    final Move mMove;
    final int mScore;
    final boolean mMax;
    public MoveScore(Move move, int score, boolean max) {
        mMove = move;
        mScore = score;
        mMax = max;
    }
    
    @Override
    public int compareTo(MoveScore another) {
        if (mScore > another.mScore) {
            return mMax ? -1 : 1;
        }            
        else if (mScore < another.mScore) {
           return mMax ? 1 : -1;
        } else {
          return 0;
          }
    }      
}
