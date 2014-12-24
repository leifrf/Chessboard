package chessPieces;

import javax.swing.ImageIcon;

public class Knight extends ChessPiece{

	public Knight(int side) {
		super(3, setIcon(side), "Knight", side);
	}
	
	private static ImageIcon setIcon(int side){
		ImageIcon icon;
		if(side == ChessPiece.WHITE)
			icon = new ImageIcon("ChessPieceImages/knight_white.png");
		else
			icon = new ImageIcon("ChessPieceImages/knight_black.png");
		return icon;
	}

}
