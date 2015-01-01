package chessPieces;

public class King extends ChessPiece implements Castleable{

	private boolean hasMoved = false;
	
	public final long creationTime;
	
	public King(int side) {
		super(100, "King", side);
		this.creationTime = System.currentTimeMillis();
	}
	
	public void setMoved(){
		this.hasMoved = true;
	}
	
	public boolean hasMoved(){
		return hasMoved;
	}
	
	/**
	 * Clone constructor
	 */
	private King(int side, boolean hasMoved){
		this(side);
		this.hasMoved = hasMoved;
	}
	
	public King clone(){
		return new King(side, hasMoved);
	}
}
