package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{
	  
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 600;
	 public static final int HEIGHT = 400;
	 final int FPS = 60;
	 Thread gameThread;
	 Board board = new Board();
	 Mouse mouse = new Mouse();
	 
	 //PIECES
	 public static ArrayList<Piece> pieces = new ArrayList<>();
	 public static ArrayList<Piece> simpieces = new ArrayList<>();
	 Piece activeP, checkingP;
	 public static Piece castlingP;
	 ArrayList<Piece> promoPieces = new ArrayList<>();
	 
	 //COLOR
	 public static final int WHITE = 0;
	 public static final int BLACK = 1;
	 int currentColor = WHITE;
	 
	 //BOOLEANS
	 boolean canMove;
	 boolean validSquare;
	 boolean promotion;
	 boolean gameover;
	 boolean stalemate;
	 
	 public GamePanel() {
		 setPreferredSize(new Dimension(WIDTH,HEIGHT));
		 setBackground(Color.GREEN);
		 addMouseMotionListener(mouse);
		 addMouseListener(mouse);
		 
		 setPieces();
		 //testPromotion();
		 copyPieces(pieces, simpieces);
	 }
	 public void LaunchGamae() {
		 gameThread = new Thread(this);
		 gameThread.start();
	 }
	 public void setPieces() {
		 
		 //WHITE team
		 pieces.add(new Pawn(WHITE,0,6));
		 pieces.add(new Pawn(WHITE,1,6));
		 pieces.add(new Pawn(WHITE,2,6));
		 pieces.add(new Pawn(WHITE,3,6));
		 pieces.add(new Pawn(WHITE,4,6));
		 pieces.add(new Pawn(WHITE,5,6));
		 pieces.add(new Pawn(WHITE,6,6));
		 pieces.add(new Pawn(WHITE,7,6));
         pieces.add(new Rook(WHITE,0,7));
         pieces.add(new Rook(WHITE,7,7));
		 pieces.add(new Knight(WHITE,1,7));
		 pieces.add(new Knight(WHITE,6,7));
		 pieces.add(new Bishop(WHITE,2,7));
		 pieces.add(new Bishop(WHITE,5,7));
		 pieces.add(new Queen(WHITE,3,7));
		 pieces.add(new King(WHITE,4,7));
		 
		 
		 //BLACK team
		pieces.add(new Pawn(BLACK,0,1));
		 pieces.add(new Pawn(BLACK,1,1));
		 pieces.add(new Pawn(BLACK,2,1));
		 pieces.add(new Pawn(BLACK,3,1));
		 pieces.add(new Pawn(BLACK,4,1));
		 pieces.add(new Pawn(BLACK,5,1));
		 pieces.add(new Pawn(BLACK,6,1));
		 pieces.add(new Pawn(BLACK,7,1));
		 pieces.add(new Rook(BLACK,0,0));
		 pieces.add(new Rook(BLACK,7,0));
		 pieces.add(new Knight(BLACK,1,0));
		 pieces.add(new Knight(BLACK,6,0));
		 pieces.add(new Bishop(BLACK,2,0));
		 pieces.add(new Bishop(BLACK,5,0));
		 pieces.add(new Queen(BLACK,3,0));
		 pieces.add(new King(BLACK,4,0));
		 
	 }
	 
	 public void testPromotion() {
		 pieces.add(new Pawn(WHITE,0,4));
		 pieces.add(new Pawn(BLACK,7,4));
	 }
	 
	 private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		 
		 target.clear();
		 for(int i = 0;i < source.size();i++) {
			 target.add(source.get(i));
		 }
	 }
	 
	 private boolean canPromote() {
		 
		 if(activeP.type == Type.PAWN) {
			 if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
				 promoPieces.clear();
				 promoPieces.add(new Rook(currentColor, 9, 2));
				 promoPieces.add(new Knight(currentColor, 9, 3));
				 promoPieces.add(new Bishop(currentColor, 9, 4));
				 promoPieces.add(new Queen(currentColor, 9, 5));
				 return true;
				 
			 }
		 }
		 return false;
	 }
	 private void update() {
		 
		 if(promotion) {
			 promoting();
		 }
		 else if(gameover == false && stalemate == false){
			 //mouse button is pressed//
			 if(mouse.pressed) {
				 if(activeP == null) {
					 
					 for(Piece piece : simpieces) {
						 
						 if(piece.color == currentColor &&
								 piece.col == mouse.x/Board.SQUARE_SIZE && 
								 piece.row == mouse.y/Board.SQUARE_SIZE) {
							 
							 activeP = piece;
						 }
								 
					 }
				 }
				 else {
					 
					 simulate();
				 }
			 }
			 //mouse is released//
			 if(mouse.pressed == false) {
				 
				 if(activeP != null) {
					 
					 if(validSquare) {
						 //move is confirmed//
						 
						 //update the piece list in case a piece has been captured and remove during the simulation//
						 copyPieces(simpieces, pieces);
						 activeP.updatePosition();
						 if(castlingP != null) {
							 castlingP.updatePosition();
						 }
						 
						 if(isKingInCheck() && isCheckmate()) {
							 //possible game over//
							 gameover = true;
						 }
						 else if(isStalemate() && isKingInCheck() == false) {
							 stalemate = true;
						 }
						 else { // the game continues//
							 if(canPromote()) {
								 promotion = true;
							 }
							 else {
								//when move is confirmed and change turns//
								 changePlayer();
							 }
						 }
					
			 
					 }
					 else {
						 
						 //the move is not valid so reset//
						 copyPieces(pieces, simpieces);
						 activeP.resetPosition();
						 activeP = null;
					 } 
				 }
			 }
		 }
     }
	 
	 private boolean isIllegal(Piece King) {
		 
		 
		 if(King.type == Type.KING) {
			 for(Piece piece : simpieces) {
				 if(piece != King && piece.color != King.color && piece.canMove(King.col, King.row)) {
					 return true;
				 }
			 }
		 }
		 return false;
	 }
	 
	 private boolean isKingInCheck() {
		 
		 Piece King = getKing(true);
		
		 if(activeP.canMove(King.col, King.row)) {
			 checkingP = activeP;
			 return true;
		 }
		 else {
			 checkingP = null;
		 }
		 return false;
	 }
	 
	 private Piece getKing(boolean opponent) {
		 
		 Piece King = null;
		 
		 for(Piece piece : simpieces) {
			 if(opponent) {
				 if(piece.type == Type.KING && piece.color != currentColor) {
					 King = piece;
				 }
			 }
			 else {
				 if(piece.type == Type.KING && piece.color == currentColor) {
					 King = piece;
				 }
			 }
		 }
		 return King;
	 }
	 
	 private void checkCastling() {
		 
		 if(castlingP != null) {
			 if(castlingP.col == 0) {
				 castlingP.col += 3;
			 }
			 else if(castlingP.col == 7) {
				 castlingP.col -= 2;
			 }
			 castlingP.x = castlingP.getX(castlingP.col);
		 }
	 }
	 
	 private boolean isStalemate() {
		 
		 int count = 0;
		 //count the number of pieces//
		 for(Piece piece : simpieces) {
			 if(piece.color != currentColor) {
				 count++;
			 }
		 }
		 //only the king is left//
		 if(count == 1) {
			 if(kingCanMove(getKing(true)) == false) {
				 return true;
			 }
		 }
		 
		 
		 return false;
	 }
	 
	 private void simulate() {
		 
		 canMove = false;
		 validSquare = false;
		 
		 //reset the piece list in every loop
		 //for restoring the removed piece
		 copyPieces(pieces, simpieces);
		 
		 //reset the castling piece position//
		 if(castlingP != null) {
			 castlingP.col = castlingP.preCol;
			 castlingP.x = castlingP.getX(castlingP.col);
			 castlingP = null;
		 }
		 //if a piece is picked ,update its position
		 activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
		 activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
		 activeP.col = activeP.getCol(activeP.x);
		 activeP.row = activeP.getRow(activeP.y);
		 
		 //check if the piece is hovering over reachable square//
		 if(activeP.canMove(activeP.col, activeP.row)) {
			 
			 canMove = true;
			 
			 //if hitting a piece remove that piece//
			 if(activeP.hittingP != null) {
				 simpieces.remove(activeP.hittingP.getIndex());
			 }
			 
			 checkCastling();
			 
			 if(isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
				 validSquare = true;
			 }
			 
		 }
	 }
	 
	 private void promoting() {
		 
		 if(mouse.pressed) {
			 for(Piece piece : promoPieces) {
				 if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE) {
					 switch(piece.type) {
					 case ROOK : simpieces.add(new Rook(currentColor, activeP.col, activeP.row)); break;
					 case KNIGHT : simpieces.add(new Knight(currentColor, activeP.col, activeP.row)); break;
					 case BISHOP : simpieces.add(new Bishop(currentColor, activeP.col, activeP.row)); break;
					 case QUEEN : simpieces.add(new Queen(currentColor, activeP.col, activeP.row)); break;
					default : break;
					 }
					 simpieces.remove(activeP.getIndex());
					 copyPieces(simpieces, pieces);
					 activeP = null;
					 promotion = false;
					 changePlayer();
				 }
			 }
		 }
	 }
	 
	 private boolean opponentCanCaptureKing() {
		 
		 Piece  King = getKing(false);
		 
		 for(Piece piece : simpieces) {
			 if(piece.color != King.color && piece.canMove(King.col, King.row)) {
				 return true;
			 }
		 }
		 
		 return false;
	 }
	 
	 private boolean isCheckmate() {
		 
		 Piece King = getKing(true);
		 
		 if(kingCanMove(King)) {
			 return false;
		 }
		 else {
			 //check if you can block the attack with your piece//
			 
			 //checking the position of the king in check//
			 int colDiff = Math.abs(checkingP.col - King.col);
			 int rowDiff = Math.abs(checkingP.row - King.row);
			 
			 if(colDiff == 0) {
				 //the checking piece is attacking vertically//
				 if(checkingP.row < King.row) {
					 //the checking piece is above the king//
					 for(int row = checkingP.row;row < King.row;row++) {
						 for(Piece piece : simpieces) {
							 if(piece != King && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								 return false;
							 }
						 }
					 }
				 }
				 if(checkingP.row > King.row) {
					 //the checking piece is below the king//
					 for(int row = checkingP.row;row > King.row;row--) {
						 for(Piece piece : simpieces) {
							 if(piece != King && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
								 return false;
							 }
						 }
					 }
				 }
			 }
			 else if(rowDiff == 0) {
				 //the checking piece is attacking horizontally//
				 if(checkingP.col < King.col) {
					 //the checking piece is to the left//
					 for(int col = checkingP.col;col < King.col;col++) {
						 for(Piece piece : simpieces) {
							 if(piece != King && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								 return false;
							 }
						 }
					 }
				 }
				 if(checkingP.col > King.col) {
					 //the checking piece is to right//
					 for(int col = checkingP.col;col > King.col;col--) {
						 for(Piece piece : simpieces) {
							 if(piece != King && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
								 return false;
							 }
						 }
					 }
				 }
			 }
			 else if(colDiff == rowDiff) {
				 //the checking piece is attacking diagonally//
				 if(checkingP.row < King.row) {
					 //the checking piece is above the king//
					 if(checkingP.col < King.col) {
						 //the checking piece is in upper left//
						 for(int col = checkingP.col, row = checkingP.row; col < King.col; col++,row++) {
							 for(Piece piece : simpieces) {
								 if(piece != King && piece.color != currentColor && piece.canMove(col,  row)) {
									 return false;
								 }
							 }
						 }
					 }
					 if(checkingP.col > King.col) {
						 //the checking piece is in upper right//
						 for(int col = checkingP.col, row = checkingP.row; col > King.col; col--,row++) {
							 for(Piece piece : simpieces) {
								 if(piece != King && piece.color != currentColor && piece.canMove(col,  row)) {
									 return false;
								 }
							 }
						 }
						 
					 }
				 }
				 if(checkingP.row > King.row) {
					 //the checking piece is below the king//
					 if(checkingP.col < King.col) {
						 //the checking piece is in lower left//
						 for(int col = checkingP.col, row = checkingP.row; col < King.col; col++,row--) {
							 for(Piece piece : simpieces) {
								 if(piece != King && piece.color != currentColor && piece.canMove(col,  row)) {
									 return false;
								 }
							 }
						 }
						 
					 }
					 if(checkingP.col > King.col) {
						 //the checking piece is in lower right//
						 for(int col = checkingP.col, row = checkingP.row; col > King.col; col--,row--) {
							 for(Piece piece : simpieces) {
								 if(piece != King && piece.color != currentColor && piece.canMove(col,  row)) {
									 return false;
								 }
							 }
						 }
						 
					 }
				 }
			 }
			 else {
				 // the checking piece is Knight//
				 
			 }
			 
		 }
		 
		 return true;
	 }
	 private boolean kingCanMove(Piece King) {
		 
		 //simulate if there is any square which the king can move to //
		 if(isValidMove(King,-1,-1)) return true;
		 if(isValidMove(King,0,-1)) return true;
		 if(isValidMove(King,1,-1)) return true;
		 if(isValidMove(King,-1,0)) return true;
		 if(isValidMove(King,1,0)) return true;
		 if(isValidMove(King,-1,1)) return true;
		 if(isValidMove(King,0,1)) return true;
		 if(isValidMove(King,1,1)) return true;
		 
		 
		 return false;
	 }
	 private boolean isValidMove(Piece King, int colPlus, int rowPlus) {
		 
		 boolean isValidMove = false;
		 
		 //update the king position for a second//
		 King.col += colPlus;
		 King.row += rowPlus;
		 
		 if(King.canMove(King.col, King.row)) {
			 
			 if(King.hittingP != null) {
				 simpieces.remove(King.hittingP.getIndex());
			 }
			 if(isIllegal(King) == false) {
				 isValidMove = true;
			 }
		 }
		 
		 //reset the king position and restore the removed piece//
		 King.resetPosition();
		 copyPieces(pieces, simpieces);
		 
		 return isValidMove;
	 }
	 public void paintComponent(Graphics g) {
		 super.paintComponent(g);
		 
		 Graphics2D g2 = (Graphics2D)g;
		 //BOARD
		 board.draw(g2);
		 
		 //PIECES
		 for(Piece p : simpieces) {
			 p.draw(g2);
		 }
		 
		 if(activeP != null) {
			 
			 if(canMove) {
				 
				 if(isIllegal(activeP) || opponentCanCaptureKing()) {
					 g2.setColor(Color.red);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					 g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				 }
				 else {
					 g2.setColor(Color.green);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					 g2.fillRect(activeP.col*Board.SQUARE_SIZE, activeP.row*Board.SQUARE_SIZE,
							Board.SQUARE_SIZE, Board.SQUARE_SIZE);
					 g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f));
				 }
				 
			 }
			 
			 //draw the active piece in the end so it wont be hidden by the board
			 activeP.draw(g2);
		 }
		 
		 //status messages//
		 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		 g2.setFont(new Font("Book Antique",Font.PLAIN,25));
		 g2.setColor(Color.white);
		 
		 if(promotion) {
			 g2.drawString("promote to : ", 420, 75);
			 for(Piece piece : promoPieces) {
				 g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
						 Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			 }
		 }
		 else {
			 //display message//
			 if(currentColor == WHITE) {
				
				 g2.drawString("White's turn", 440, 275);
				 if(checkingP != null && checkingP.color == BLACK) {
					 g2.setColor(Color.red);
					 g2.drawString("the white king ", 425, 300);
					 g2.drawString("is in check!", 440, 320);
				 }
			 }
			 else {
				
				 g2.drawString("Black's turn", 440, 125);

				 if(checkingP != null && checkingP.color == WHITE) {
					 g2.setColor(Color.red);
					 g2.drawString("the black king ", 425, 150);
					 g2.drawString("is in check!", 440, 170);
				 }
			 }
		 }
		
		 if(gameover) {
			 String s = "";
			 if(currentColor == WHITE) {
				 s = "White Wins";
			 }
			 else {
				 s = "Black Wins";
			 }
			 g2.setFont(new Font("Arial", Font.PLAIN, 45));
			 g2.setColor(Color.GREEN);
			 g2.drawString(s, 100, 210);
		 }
		 
		 if(stalemate) {
			 g2.setFont(new Font("Arial", Font.PLAIN, 45));
			 g2.setColor(Color.MAGENTA);
			 g2.drawString("stalemate", 100, 210);
		 }
	 }
	 private void changePlayer() {
		 
		 if(currentColor == WHITE) {
			 currentColor = BLACK;
			 //reset black's two stepped status//
			 for(Piece piece : pieces) {
				 if(piece.color == BLACK) {
					 piece.twoStepped = false;
				 }
			 }
		 }
		 else {
			 currentColor = WHITE;
		 }
		 activeP = null;
	 }
	 
	 public void run() {
		  
		 //GAMELOOP
		 double drawInterval = 1000000000/FPS;
		 double delta = 0;
		 long lastTime = System.nanoTime();
		 long currentTime;
		 
		 while(gameThread != null) {
			 
			 currentTime = System.nanoTime();
			 
			 delta += (currentTime - lastTime)/drawInterval;
			 lastTime = currentTime;
			 
			 if(delta >= 1) {
				 update();
				 repaint();
				 delta--;
			 }
		 }
    }
}
