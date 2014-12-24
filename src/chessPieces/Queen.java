package chessPieces;

import javax.swing.ImageIcon;

public class Queen extends ChessPiece{

	public Queen(int side) {
		super(9, setIcon(side), "Queen", side);
	}
	
	private static ImageIcon setIcon(int side){
		ImageIcon icon;
		if(side == ChessPiece.WHITE)
			icon = new ImageIcon("ChessPieceImages/queen_white.png");
		else
			icon = new ImageIcon("ChessPieceImages/queen_black.png");
		return icon;
	}

}
