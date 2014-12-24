package chessPieces;

import javax.swing.ImageIcon;

public class Rook extends ChessPiece{

	public Rook(int side) {
		super(6, setIcon(side), "Rook", side);
	}
	
	private static ImageIcon setIcon(int side){
		ImageIcon icon;
		if(side == ChessPiece.WHITE)
			icon = new ImageIcon("ChessPieceImages/rook_white.png");
		else
			icon = new ImageIcon("ChessPieceImages/rook_black.png");
		return icon;
	}

}
