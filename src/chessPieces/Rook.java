package chessPieces;

public class Rook extends ChessPiece implements Castleable{

	private boolean hasMoved = false;
	
	public Rook(int side) {
		super(6, "Rook", side);
	}
	
	public void setMoved(){
		this.hasMoved = true;
	}
	
	public boolean hasMoved(){
		return hasMoved;
	}
}
