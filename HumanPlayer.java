package othello;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class HumanPlayer extends Player {
	
	//static because all human players have to share the same terminal.
	static Scanner scan = new Scanner(System.in);
	
	public HumanPlayer(boolean white) {
		super(white);
	}
	
	Pattern movePattern = Pattern.compile("[a-hA-H][1-9]");
	
	@Override
	public Move play(Board board) {
		//An invalid move as initial value.
		Move move = new Move(-1, -1, white);
		boolean once = false;
		if (super.white) System.out.println("- WHITE ");
		else System.out.println("- BLACK ");
		do {
			//Don't print invalid move message the first time.
			if(once) {
				System.out.println("Invalid move, please try again.");
			}
			once = true;
			
			// Ask for move
			System.out.println("Please input your move: ");
			// Scan move
			if(scan.hasNext(movePattern)) {
				String str = scan.next(movePattern);
				
				//Decode
				str = str.toLowerCase();
				int x = str.charAt(0) - 'a';
				int y = str.charAt(1) - '1';
				
				//Create a move object
				move = new Move(x, y, white);
				
			} else if(scan.hasNext()) {
				String str = scan.next();
				if("hint".equalsIgnoreCase(str)) {
					printHint(board);
					once = false;
				} else {
					// Inform the user of the expected input format
					System.out.println("Invalid format. Expected a-h followed by 1-8.");					
				}
				
			} else {
				//Standard input closed.
				System.exit(0);
			}
			
		} while(!board.isValid(move));
		
		// Apply the move 
		board.applyMove(move);
		
		return move;
	}

	private void printHint(Board board) {
		System.out.println("Possible moves:");
		List<Move> moves = board.getPossibleMoves(white);
		int columns = moves.size()/5;
		columns = columns>5?5:columns;
		columns = columns<1?1:columns;
		
		int i=0;
		for(Move move: moves) {
			System.out.print(move);
			System.out.print('\t');
			if(++i%columns == 0) {
				System.out.println();
			}
		}
	}
	
}