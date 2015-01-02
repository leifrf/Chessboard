package chessPieces;

public class Rook extends ChessPiece implements Castleable {

	private boolean hasMoved = false;

	public Rook(int side) {
		super(6, "Rook", side);
	}

	public void setMoved() {
		this.hasMoved = true;
	}

	public boolean hasMoved() {
		return hasMoved;
	}

	/**
	 * Clone constructor
	 */
	private Rook(int side, boolean hasMoved) {
		this(side);
		this.hasMoved = hasMoved;
	}

	public Rook clone() {
		return new Rook(side, hasMoved);
	}

}
