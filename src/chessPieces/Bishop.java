package chessPieces;

import javax.swing.ImageIcon;

public class Bishop extends ChessPiece{

	public Bishop(int side) {
		super(3, setIcon(side), "Bishop", side);
	}
	
	private static ImageIcon setIcon(int side){
		ImageIcon icon;
		if(side == ChessPiece.WHITE)
			icon = new ImageIcon("ChessPieceImages/bishop_white.png");
		else
			icon = new ImageIcon("ChessPieceImages/bishop_black.png");
		return icon;
	}
	
}
