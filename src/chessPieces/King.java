package chessPieces;

import javax.swing.ImageIcon;

public class King extends ChessPiece{

	public King(int side) {
		super(100, setIcon(side), "King", side);
	}
	
	private static ImageIcon setIcon(int side){
		ImageIcon icon;
		if(side == ChessPiece.WHITE)
			icon = new ImageIcon("ChessPieceImages/king_white.png");
		else
			icon = new ImageIcon("ChessPieceImages/king_black.png");
		return icon;
	}

}
