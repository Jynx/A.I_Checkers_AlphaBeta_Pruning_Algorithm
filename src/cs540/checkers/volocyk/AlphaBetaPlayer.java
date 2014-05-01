/* Don't forget to change this line to cs540.checkers.<username> */
package cs540.checkers.volocyk;
 
import cs540.checkers.*;
 
import static cs540.checkers.CheckersConsts.*;
 
import java.util.*;
 
public class AlphaBetaPlayer extends CheckersPlayer implements GradedCheckersPlayer {
    /** The number of pruned subtrees for the most recent deepening iteration. */
    protected int mIterativePruneCount;
    protected int mTotalPruneCount;
    protected Evaluator mEvaluator;
    int mScore = 0;
    int mLastPrune;
    final int mOtherSide;
 
    public AlphaBetaPlayer(String name, int side) {
        super(name, side);
        // Use SimpleEvaluator to score terminal nodes
        mEvaluator = new SimpleEvaluator();
        mOtherSide = Utils.otherSide(side);
    }
 
    public void calculateMove(int[] bs) {
        int beta = Integer.MAX_VALUE;
        int alpha = Integer.MIN_VALUE;
        int bestScore = Integer.MIN_VALUE;
        mIterativePruneCount = 0;
        mTotalPruneCount = 0;
        Move bestMove = null;
 
        List<Move> possibleMoves = Utils.getAllPossibleMoves(bs, side);
        if (possibleMoves.size() == 0) {
            return;
        }
 
        /* Evaluate this board state */
        for (int x = 1; x <= depthLimit; ++x) {
            bestScore = Integer.MIN_VALUE;
            mIterativePruneCount = 0;
            
            for (Move move : possibleMoves) {
                Stack<Integer> prevBoard = Utils.execute(bs, move);
                int score = minMaxValue(bs, alpha, beta, x, false);
                Utils.revert(bs, prevBoard);
                if (side == BLK) {
                    score = -score;
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
            }
            if (Utils.verbose == true){
                System.out.println("Best Move: " + Utils.reprMove(bestMove) + ", Score: " + bestScore);
                System.out.println("PruneCount: " + mIterativePruneCount + ", Depth: " + x);
            }
            setMove(bestMove);
        }
    }
 
    public int minMaxValue(int[] state, int alpha, int beta, int currDepth, boolean maximizingPlayer) {
        if (currDepth == 0) {
            return mEvaluator.evaluate(state);
        }
        List<Move> possibleMoves = Utils.getAllPossibleMoves(state, maximizingPlayer ? side : mOtherSide);
        if (possibleMoves.size() == 0) {
            return mEvaluator.evaluate(state);
        }
        int count = 0;
        for (Move m : possibleMoves) {
            count++;
            Stack<Integer> prevBoardLayout = Utils.execute(state, m);
            if (maximizingPlayer) {
                alpha = Math.max(alpha, minMaxValue(state, alpha, beta, currDepth - 1, false));
                Utils.revert(state, prevBoardLayout);
                if (alpha > beta) {
                    mLastPrune = alpha;
                    mIterativePruneCount += possibleMoves.size() - count;
                    return alpha;
                }
            } else {
                beta = Math.min(beta, minMaxValue(state, alpha, beta, currDepth - 1, true));
                Utils.revert(state, prevBoardLayout);
                if (beta < alpha) {
                    mLastPrune = beta;
                    mIterativePruneCount += possibleMoves.size() - count;
                    return beta;
                }
            }
        }
        return maximizingPlayer ? alpha : beta;
    }
 
    public int getPruneCount() {
        return mIterativePruneCount;
    }
 
    @Override
    public int getLastPrunedNodeScore() {
        return mLastPrune;
    }
}