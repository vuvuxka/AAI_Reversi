package othello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
	
	// The board is one contiguous array for speed
	// Use get/set to read/write on an x/y position
	Tile[] board;
	
	public Board() {
		board = new Tile[8*8];
		Arrays.fill(board, Tile.Empty);
		set(3, 3, Tile.White);
		set(4, 4, Tile.White);
		set(3, 4, Tile.Black);
		set(4, 3, Tile.Black);
	}
	
	private Board(Board b) {
		board = Arrays.copyOf(b.board, 64);
	}
	
	public Board copy() {
		return new Board(this);
	}
	
	// Used to represent one step along each direction 
	private static class Direction {
		int x, y;
		Direction(int dx, int dy) {
			x = dx;
			y = dy;
		}
	}
	
	// Used to loop over each direction
	Direction[] directions = new Direction[] {
		new Direction( 1,  0), 	// East
		new Direction( 0,  1),	// South
		new Direction(-1,  0),	// West
		new Direction( 0, -1),	// North
		new Direction( 1,  1),	// South-East
		new Direction(-1,  1),	// South-West
		new Direction( 1, -1),	// North-East
		new Direction(-1, -1)	// North-West
	};
	
	public Board applyMove(Move move) {
		if(!isValid(move)) {
			//error
			return this;
		}
		// If we get here we can assume that the move is valid.
		
		
		// To make the rest work the same regardless of color
		// I define these:
		Tile myTile = Tile.fromBoolean(move.white);
		Tile otherTile = myTile.reverse();
		
		set(move.x, move.y, myTile);
		
		for(Direction d: directions) {
			int x = move.x + d.x; // Move one step away
			int y = move.y + d.y; // from the move's position
			
			// Search until we reach a tile that is not of
			// the opposite color or we reach the edge of the board
			for(;inside(x, y) && get(x, y) == otherTile; x+= d.x, y+=d.y) {}
			// If we reached the end then
			if(!inside(x,y)) {
				// continue with the next direction
				continue;
			}
			
			// If we found one of the current players tiles
			if(get(x, y) == myTile) {
				// We can color all tiles from (move) to (x, y)  
				draw(move.x, move.y, x, y, d, myTile);
			}
		}
		return this;
	}	
	
	private void draw(int mx, int my, int tx, int ty, Direction d, Tile myTile) {
		int x = mx + d.x; // Move one step away
		int y = my + d.y; // from the move's position
		
		// Search until we reach a tile that is not of
		// the opposite color or we reach the edge of the board
		for(;x != tx || y != ty; x+= d.x, y+=d.y) {
			set(x, y, myTile);
		}
	}

	public boolean isValid(Move move) {
		// The position must be empty before placing a new tile.
		if(!inside(move.x, move.y) || get(move.x, move.y) != Tile.Empty) {
			return false;
		}
		
		// To make the rest work the same regardless of color
		// I define these:
		Tile myTile = Tile.fromBoolean(move.white);
		Tile otherTile = myTile.reverse();
		
		for(Direction d: directions) {
			int x = move.x + d.x; // Move one step away
			int y = move.y + d.y; // from the move's position
			
			//Check if (x, y) is inside
			if(!inside(x,y)) {
				continue; // continue with the next direction
			}
			
			// Tile at (x, y) must be of opposite color
			if(get(x, y) != otherTile) {
				continue; // continue with the next direction
			}
			
			// Search until we reach a tile that is not of
			// the opposite color or we reach the edge of the board
			for(;inside(x, y) && get(x, y) == otherTile; x+= d.x, y+=d.y) {}
			
			// If we reached the end then
			if(!inside(x,y)) {
				continue; // continue with the next direction
			}
			
			// Else we have a valid move if x, y belongs the the current player
			if(get(x, y) == myTile) {
				return true;
			}
			
			// Otherwise we ended up on an empty tile
			// and need to check the next direction
		}
		return false;
	}
	
	// True for all x and y that are on the board, otherwise false.
	private boolean inside(int x, int y) {
		return 0 <= x && x < 8 && 0 <= y && y < 8;
	}

	public boolean anyPossibleMoves(boolean white) {
		// Simple implemenation.
		// Can be optimized.
		Move move = new Move(0,0,white);
		for(move.y = 0; move.y < 8; move.y++) {
			for(move.x = 0; move.x < 8; move.x++) {
				if(isValid(move)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<Move> getPossibleMoves(boolean white) {
		// Simple implemenation.
		// Can be optimized.
		ArrayList<Move> moves = new ArrayList<Move>();
		Move move = new Move(0,0,white);
		for(move.y = 0; move.y < 8; move.y++) {
			for(move.x = 0; move.x < 8; move.x++) {
				if(isValid(move)) {
					moves.add(move.copy());
				}
			}
		}
		return moves;
	}
	
	//Useful for estimating mobility
	public int countPossibleMoves(boolean white) {
		// Simple implemenation.
		// Can be optimized.
		int count = 0;
		Move move = new Move(0,0,white);
		for(move.y = 0; move.y < 8; move.y++) {
			for(move.x = 0; move.x < 8; move.x++) {
				if(isValid(move)) {
					count++;
				}
			}
		}
		return count;
	}
	
	public int[] countBlackWhite() {
		int whiteTiles = 0;
		int blackTiles = 0;
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				switch(get(x, y)) {
				case White: whiteTiles++; break;
				case Black: blackTiles++; break;
				default:
				}
			}
		}
		return new int[]{blackTiles, whiteTiles};
	}
	
	//Positive when white wins, zero for draw, negative when black wins
	public int balancedScore() {
		int[] scores = countBlackWhite();
		int score = scores[1] - scores[0];
		return score;
	}
	
	public Tile get(int x, int y) {
		assert inside(x, y):"Coordinates must be on the board:" + x +", " + y;
		return board[y*8 + x];
	}
	
	private void set(int x, int y, Tile t) {
		assert inside(x, y):"Coordinates must be on the board:" + x +", " + y;
		board[y*8 + x] = t;
	}
	
	public String toString() {
		String b;
		b = "\n\t  ";
		b += "A  B  C  D  E  F  G  H";
		b += "\n";
		for(int l = 0; l < 8; l++) {
			b += "\t" + (l + 1) + " ";
			for(int s = 0; s < 8; s++) {
				b += get(s,l).toString() + "  ";
			}
			b += "\n";
		}
		return b;
	}
	
	public enum Tile {
		Empty, White, Black;
		Tile reverse() {
			switch(this) {
			case White: return Black;
			case Black: return White;
			default:   return Empty;
			}
		}
		
		public String toString() {
			switch(this) {
			case White: return "o";
			case Black: return "x";
			default:	return ".";
			}
		}
		public static Tile fromBoolean(boolean white) {
			if(white) {
				return White;
			}
			return Black;
		}
	}
}