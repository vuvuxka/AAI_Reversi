package othello;

import java.util.List;

import othello.Board.Tile;

public class AIPlayer extends Player {

	public AIPlayer(boolean white) {
		super(white);
	}

	@Override
	public Move play(Board board) {
		List<Move> possibleMoves = board.getPossibleMoves(white);
		
		Move nextM = null;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		long now = System.currentTimeMillis();
		
		for(Move m: possibleMoves) {
			Board b = board.copy().applyMove(m);
			int value = minimize(b, alpha, beta, 7, now, false);
			if (alpha < value) {
				alpha = value;
				nextM = m;
			}
		}
		
		board.applyMove(nextM);
		return nextM;
	}
	
	private int minimize(Board board, int alpha, int beta, int depth, long time, boolean passed) {
		if (baseCase(depth, time)) {
			return heuristic(board);
		}
		
		List<Move> allMoves = board.getPossibleMoves(!white);
		if (allMoves.isEmpty()) {
			//No possible moves. Must pass.
    		if (passed) {
    			//Opponent had no possible moves either: Game Over
    			return gameOverScore(board);
    		}
    		
			return maximize(board, alpha, beta, depth-1, time, true);
		}
		
		int value = Integer.MAX_VALUE;
        for (Move m: allMoves) {
            Board b = board.copy().applyMove(m);
            value = Math.min(value, maximize(b, alpha, beta, depth-1, time, false));
            beta = Math.min(value, beta);
            if (beta <= alpha) { 
            	break;
            }
        }
        return value;
	}
	
    private int maximize(Board board, int alpha, int beta, int depth, long time, boolean passed) {
    	if(baseCase(depth, time)) {
    		return heuristic(board);
    	}
    	
    	List<Move> allMoves = board.getPossibleMoves(white);
    	if(allMoves.isEmpty()) {
    		//No possible moves. Must pass.
    		if (passed) {
    			//Opponent had no possible moves either: Game Over
    			return gameOverScore(board);
    		}
    		
			return minimize(board, alpha, beta, depth-1, time, true);
		}
    	
        int value = Integer.MIN_VALUE;
        for(Move m: allMoves) {
            Board b = board.copy().applyMove(m);
            value = Math.max(value, minimize(b, alpha, beta, depth-1, time, false));
            alpha = Math.max(value, alpha);
            if (beta <= alpha) {
            	break;
            }
        }
        return value;
    }
    
	private boolean baseCase(int depth, long t)  {
    	// Determine when to stop the search
    	return depth <= 0 || (System.currentTimeMillis() - t) > 10000;
    }
    
	//note: I've just guessed all these numbers.
	//if we could find a source that has better numbers
	//then we could use that instead
	private final static int[] scoreMatrix = {
		30, 0, 3, 3, 3, 3, 0, 30,
		0, -9, 1, 1, 1, 1,-9, 0,
		3,  1, 0, 0, 0, 0, 1, 3,
		3,  1, 0, 0, 0, 0, 1, 3,
		3,  1, 0, 0, 0, 0, 1, 3,
		3,  1, 0, 0, 0, 0, 1, 3,
		0, -9, 1, 1, 1, 1,-9, 0,
		30, 0, 3, 3, 3, 3, 0, 30,
	};
	
	// For indexing the score matrix
	private final static int index(int x, int y) {
		assert 0 <= x && x <= 8 && 0 <= y && y <= 8:
			"Index out of bounds: " + x + ", " + y;
		return y*8 + x;
	}
	
    private int heuristic(Board board) {
    	int score = 0;
    	
    	for(int y = 0; y < 8; y++) {
    		for(int x = 0; x < 8; x++) {
        		Tile tile = board.get(x, y);
				switch(tile) {
				case White:
        			score += scoreMatrix[index(x, y)];
				case Black:
        			score -= scoreMatrix[index(x, y)];
        		default:
        		}
        	}
    	}
    	//Invert score if AI is black
    	score = white?score:-score;
    	
    	//Count AI and opponents moves
    	int myMoves = board.countPossibleMoves(white);
    	int otherMoves = board.countPossibleMoves(!white);
    	int sumMoves = myMoves + otherMoves;
    	
    	//Calculate AI mobility vs. opponent mobility
    	int mobility = 0;
		if(sumMoves != 0) {
			mobility = 100*(myMoves - otherMoves) / sumMoves;	
		}
    	
		// Weight together
    	return score + mobility;
	}
    
    private int gameOverScore(Board board) {
    	// Similar to heuristic but now that the game is over we know who won
    	// So we can make the score a very large positive or negative number
    	// Because avoiding losing or attempting to win is more important
    	// than following any other guess.
    	
    	// Calculate if the AI won
    	int score = board.balancedScore();
    	score = white?score:-score;
		if(score > 0) {
			return 10000;
		} else if(score < 0) {
			return -10000;
		}
		return 0;
    }
}
