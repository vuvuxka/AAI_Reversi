package othello;

import java.util.Scanner;

public class Game implements Runnable {

	public Game(Player white, Player black) {
		super();
		this.white = white;
		this.black = black;

		board = new Board();
		gameOver = false;
		whiteWins = false;
		blackWins = false;
		history = new History();
	}

	Player black;
	Player white;

	boolean gameOver;
	boolean whiteWins;
	boolean blackWins;

	Board board;

	History history;

	public void run() {
		Player current = black;

		// Passed is true when previous player passed.
		boolean passed = false;
		while (!gameOver) {
			
			//Print the board
			System.out.println(history.toString());
			System.out.println(board.toString());
			
			boolean hasValidMoves = board.anyPossibleMoves(current == white);
			if (!hasValidMoves && passed) {
				// Both player have no valid moves. Game Over.
				gameOver = true;
			} else if (!hasValidMoves) {
				// Current player must pass
				System.out.print(current == white ? "white" : "black");
				System.out.println(" has no valid moves and must pass.");
				passed = true;
			} else {
				// Let current player play one move
				Move move = current.play(board);
				history.addMove(move);
				passed = false;
			}

			// Swap current player
			current = ((current == black) ? white : black);
		}

		// Find out who won.
		int[] scores = board.countBlackWhite();
		int whiteTiles = scores[1];
		int blackTiles = scores[0];
		
		if(whiteTiles < blackTiles) {
			System.out.println("Black wins!");
		} else if(whiteTiles > blackTiles) {
			System.out.println("White wins!");
		} else {
			System.out.println("Everyone wins!");
		}
		System.out.println("Score: White " + whiteTiles + " -- " + blackTiles + " Black");
	}

	public static void main(String args[]) {
		Game game = showNewGameMenu();
		(new Thread(game)).start();
	}

	private static Game showNewGameMenu() {
		Player black = null, white = null;
		System.out.println("\t\t\t- Reversi Game -");
		System.out.println("Add a player. C for computer, H for human.");
		
		// Scan move
		black = scanPlayer("Black", false);
		white = scanPlayer("White", true);
		
		System.out.println("--------------------------------------------------");
		System.out.println("\t\tNEW GAME!");
		System.out.println("--------------------------------------------------");
		
		return new Game(white, black);
	}

	private static Player scanPlayer(String name, boolean white) {
		Player player = null;
		do {
			// Ask for type of player: 
			System.out.print("\t"+name+": ");
			if(!scan.hasNext()) {
				//Standard input closed
				System.exit(0);
			}
			String str = scan.next();
			// Determine player type:
			if("c".equalsIgnoreCase(str)) {
				player = new AIPlayer(white);
				
			} else if ("h".equalsIgnoreCase(str)) {
				player = new HumanPlayer(white);
				
			} else {
				// Error: Ask to try again.
				System.out.print("Only 'c' or 'h' are allowed.\n");
			}
		} while(player == null);
		return player;
	}

	public String getHistory() {
		return history.toString();
	}

	private static class History {
		String white = "";
		String black = "";

		public String toString() {
			String h = "\n\t[MOVES HISTORY]\n";
			h += "White: " + white + "\nBlack: " + black;

			return h;
		}

		void addMove(Move m) {
			if (m.white)
				white += " " + m.toString();
			else
				black += " " + m.toString();
		}

	}
	static Scanner scan = new Scanner(System.in);
}