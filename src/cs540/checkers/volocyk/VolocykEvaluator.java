package cs540.checkers.volocyk;

import static cs540.checkers.CheckersConsts.BLK;
import static cs540.checkers.CheckersConsts.BLK_KING;
import static cs540.checkers.CheckersConsts.BLK_PAWN;
import static cs540.checkers.CheckersConsts.H;
import static cs540.checkers.CheckersConsts.RED;
import static cs540.checkers.CheckersConsts.RED_KING;
import static cs540.checkers.CheckersConsts.RED_PAWN;
import static cs540.checkers.CheckersConsts.W;

import java.util.List;

import cs540.checkers.Evaluator;
import cs540.checkers.Move;
import cs540.checkers.Utils;

public class VolocykEvaluator implements Evaluator {
    public int evaluate(int[] bs) {

        int[] pawns = new int[2], kings = new int[2];
        int redKingPromoCount = 0;
        int blackKingPromoCount = 0;

        for (int i = 0; i < H * W; i++)
        {
            int v = bs[i];
            int row = i / W;
            if (row == 1 && v == RED_PAWN) {
                int cell = i % W;
                if ((cell - 1 > 0 && Utils.canWalk(bs, i, cell -1)) ||
                        (cell + 1 < W && Utils.canWalk(bs,  i,  cell + 1))) {
                        redKingPromoCount++;
                }
            } else if (row == H - 2 && v == BLK_PAWN) {
                int cell = i + W;
                if ((cell - 1 > 0 && Utils.canWalk(bs, i, cell -1)) ||
                        (cell + 1 < W && Utils.canWalk(bs,  i,  cell + 1))) {
                        blackKingPromoCount++;
                }
            }
            switch(v)
            {
                case RED_PAWN:
                case BLK_PAWN:
                    pawns[v % 4] += 1;
                    break;
                case RED_KING:
                case BLK_KING:
                    kings[v % 4] += 1;
                    break;
            }
        }
        
        // Weigh the ability to jump and take pieces heavily
        int redJumpScore = (int)Math.pow(2, getMaxJumps(bs, RED));
        int blkJumpScore = (int)Math.pow(2, getMaxJumps(bs, BLK));
        
        int score = 1 * (pawns[RED] - pawns[BLK]) + 
               3 * (kings[RED] - kings[BLK]) +
               2 * (redKingPromoCount - blackKingPromoCount) +
               (redJumpScore - blkJumpScore);
        return score;
    }
        
    public int getMaxJumps(int[] bs, int side) {
        int max = 0;
        List<Move> list = Utils.findJumpMoves(bs, side);
        for (Move move : list) {
            max = Math.max(max, move.size() - 1);
        }
        return max;
    }
}
