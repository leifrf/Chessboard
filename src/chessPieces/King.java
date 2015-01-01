package chessPieces;

public class King extends ChessPiece implements Castleable{

	private boolean hasMoved = false;
	
	public King(int side) {
		super(100, "King", side);
	}
	
	public void setMoved(){
		this.hasMoved = true;
	}
	
	public boolean hasMoved(){
		return hasMoved;
	}
}
