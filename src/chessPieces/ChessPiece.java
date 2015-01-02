package chessPieces;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ChessPiece implements Cloneable {

	// Generally accessible side indicators
	// Consider switching to ENUM
	public static final int WHITE = -1;
	public static final int BLACK = -2;

	public final int side;
	public final int value;
	private ImageIcon icon;
	private String name;

	/**
	 * General constructor. Creates a chess piece with the given value, name,
	 * and side. Works directly with the folder res/ChessPieceImages.
	 * 
	 * Although it is possible to create a new chess piece, it will not be
	 * validated by the game.
	 * 
	 * Moves for the piece are determined in the Chessboard class.
	 * 
	 * @param value
	 *            Value of the piece. i.e. Queen = 9.
	 * @param name
	 *            Name of the piece.
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK.
	 */
	public ChessPiece(int value, String name, int side) {
		this.value = value;
		this.name = name;
		this.side = side;
		this.icon = getIcon(name, side);
	}

	/**
	 * Creates the file path based on the name and side. The imageIcon is taken
	 * from this generated path. This is called once, in the constructor.
	 * 
	 * @param name
	 *            Name of the piece
	 * @param side
	 *            ChessPiece.WHITE or ChessPiece.BLACK
	 * @return
	 */
	private ImageIcon getIcon(String name, int side) {
		String sideName = "";
		if (side == ChessPiece.WHITE)
			sideName = "white";
		else
			sideName = "black";
		// Parse path name
		String path = "ChessPieceImages/" + name.toLowerCase() + "_" + sideName
				+ ".png";
		ImageIcon icon = null;
		try {
			// Load image from resources
			icon = new ImageIcon(ImageIO.read(ResourceLoader.load(path)));
		} catch (IOException e) {
			System.out.println("FAILED TO READ ICON");
			e.printStackTrace();
		}
		return icon;
	}

	/**
	 * Accessor for the piece's icon. Icon is ~45px.
	 * 
	 * @return Piece's icon.
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * Gives the string representation of the ChessPiece. Returns side + piece
	 * name i.e. "White Rook"
	 */
	public String toString() {
		String team = "";
		if (side == ChessPiece.WHITE)
			team = "White ";
		else
			team = "Black ";
		return team + name;
	}

	public boolean equals(Object o) {
		boolean equal = true;
		ChessPiece piece = (ChessPiece) o;
		if (this.value == piece.value && this.name == piece.name
				&& this.side == piece.side)
			equal = true;
		else
			equal = false;
		return equal;
	}

	/**
	 * This exists to simplify testMove in Chessboard.
	 */
	public Object clone() {
		return new ChessPiece(value, name, side);
	}

}