package chessPieces;

import javax.swing.ImageIcon;

// Consider moving setIcon to ChessPiece
// Downside: Makes it downward linking
// Upside: Consolidates code

public abstract class ChessPiece {

	public static final int WHITE = -1;
	public static final int BLACK = -2;
	
	private int side;
	private int value;
	private ImageIcon icon;
	private String name;
	
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
	
	public String toString(){
		return name;
	}
	
	public int getSide(){
		return side;
	}
	
}