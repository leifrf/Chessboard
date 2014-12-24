package chessPieces;

import javax.swing.ImageIcon;

public class Pawn extends ChessPiece{

	public Pawn(int side) {
		super(1, setIcon(side), "Pawn", side);
	}
	
	private static ImageIcon setIcon(int side){
		ImageIcon icon;
		if(side == ChessPiece.WHITE)
			icon = new ImageIcon("ChessPieceImages/pawn_white.png");
		else
			icon = new ImageIcon("ChessPieceImages/pawn_black.png");
		return icon;
	}

}
