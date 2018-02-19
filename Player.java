package othello;

public abstract class Player {
	protected boolean white;
	
	public Player(boolean white) {
		super();
		this.white = white;
	}

	abstract Move play(Board board);
	
}