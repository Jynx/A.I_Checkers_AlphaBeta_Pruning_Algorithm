package cs540.checkers.volocyk;

import static cs540.checkers.CheckersConsts.BLK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import cs540.checkers.CheckersPlayer;
import cs540.checkers.Evaluator;
import cs540.checkers.GradedCheckersPlayer;
import cs540.checkers.Move;
import cs540.checkers.Utils;

public class VolocykPlayer extends CheckersPlayer implements GradedCheckersPlayer {
    /** The number of pruned subtrees for the most recent deepening iteration. */
    protected int mIterativePruneCount;
    protected Evaluator mEvaluator;
    int mScore = 0;
    int mLastPrune;
    HashMap<Board, Integer> mSavedStates = null;
    final int mOtherSide;
        
    public VolocykPlayer(String name, int side) {
        super(name, side);
        mEvaluator = new VolocykEvaluator();
        mOtherSide = Utils.otherSide(side);
    }

    public void calculateMove(int[] bs) {        
        int beta = Integer.MAX_VALUE;
        int alpha = Integer.MIN_VALUE;
        int bestScore = Integer.MIN_VALUE;
        mIterativePruneCount = 0;
        Move bestMove = null;
        mSavedStates = new HashMap<Board, Integer>();

        List<Move> possibleMoves = Utils.getAllPossibleMoves(bs, side);
        if (possibleMoves.size() == 0) {
            return;
        }

        ArrayList<MoveScore> moves = new ArrayList<MoveScore>();

        // Sort the moves to consider the best moves first (increases the number of prunings)
        for (Move move : possibleMoves) {
            Stack<Integer> rv = Utils.execute(bs, move);
            moves.add(new MoveScore(move, mEvaluator.evaluate(bs), true));
            Utils.revert(bs, rv);
        }
        Collections.sort(moves);

        /* Evaluate this board state */
        for (int x = 1; x <= depthLimit; ++x) {
            bestScore = Integer.MIN_VALUE;
            mIterativePruneCount = 0;
            mSavedStates.clear();
            
            for (MoveScore move : moves) {
                Stack<Integer> prevBoard = Utils.execute(bs, move.mMove);
                int score = minMaxValue(bs, alpha, beta, x, false);
                Utils.revert(bs, prevBoard);
                if (side == BLK) {
                    score = -score;
                }
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move.mMove;
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
            int value = mEvaluator.evaluate(state);
            mSavedStates.put(new Board(state, maximizingPlayer), value);
            return value;
        }
        List<Move> possibleMoves = Utils.getAllPossibleMoves(state, maximizingPlayer ? side : mOtherSide);
        if (possibleMoves.size() == 0) {
            int value = mEvaluator.evaluate(state);
            mSavedStates.put(new Board(state, maximizingPlayer), value);
            return value;
        }
        
        int count = 0;
        for (Move move : possibleMoves) {
            count++;
            Stack<Integer> prevBoardLayout = new Stack<Integer>();
            prevBoardLayout = Utils.execute(state, move);
            
            Board b = new Board(state, maximizingPlayer);
            Integer score = mSavedStates.get(b);
            if (score != null) {
                // Ignore boards that have already been considered
                return score;
            }
                                
            if (maximizingPlayer) {
                alpha = Math.max(alpha, minMaxValue(state, alpha, beta, currDepth - 1, false));
                Utils.revert(state, prevBoardLayout);
                if (alpha > beta) {
                    mLastPrune = beta;
                    mIterativePruneCount += possibleMoves.size() - count;
                    return alpha;
                }
            } else {
                beta = Math.min(beta, minMaxValue(state, alpha, beta, currDepth - 1, true));
                Utils.revert(state, prevBoardLayout);
                if (beta < alpha) {
                    mLastPrune = alpha;
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
