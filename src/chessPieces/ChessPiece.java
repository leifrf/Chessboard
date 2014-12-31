package chessPieces;

import javax.swing.ImageIcon;

// Consider moving setIcon to ChessPiece
// Downside: Makes it downward linking
// Upside: Consolidates code

public class ChessPiece implements Cloneable{

	public static final int WHITE = -1;
	public static final int BLACK = -2;
	
	private int side;
	private int value;
	private ImageIcon icon;
	private String name;
	
	@SuppressWarnings("unused")
	private ChessPiece(){};
	
	public ChessPiece(int value, ImageIcon icon, String name, int side){
		this.value = value;
		this.icon = icon;
		this.name = name;
		this.side = side;
	}
	
	public int getValue(){
		return value;
	}
	
	public ImageIcon getIcon(){
		return icon;
	}
	
	public int getSide(){
		return side;
	}
	
	public String toString(){
		String team = "";
		if (side == ChessPiece.WHITE)
			team = "White ";
		else
			team = "Black ";
		return team + name;
	}
	
	public Object clone(){
		return new ChessPiece(value, icon, name, side);
	}
	
}