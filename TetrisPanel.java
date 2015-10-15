package Version7;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.*;

/* This class works as the "game outside of the actual game". It handles the interactions between
 * Tetris and TetrisPiece, and handles all user input to pass to TetrisPiece and screen output.
 * It calls for a new instance of Tetris when a new game starts, and calls for new TetrisPiece
 * instances at a time interval. When a piece is falling or a command is given, this class passes
 * the board information to the piece to determine whether it has collided with anything. If the
 * current instance of Tetris shows that the player has lost, this class halts all processes and
 * lets the player start the game again.
 * */

public class TetrisPanel extends Panel implements KeyListener, MouseListener
{
	
	private static final int SHIFT_R = KeyEvent.VK_RIGHT;
	private static final int SHIFT_L = KeyEvent.VK_LEFT;
	private static final int HARD_DROP = KeyEvent.VK_SPACE;
	private static final int ROTATE_CW = KeyEvent.VK_X;
	private static final int ROTATE_CCW = KeyEvent.VK_Z;
	private static final int HOLD = KeyEvent.VK_SHIFT;
	private static final int SOFT_DROP = KeyEvent.VK_DOWN;
	private static final int FIRM_DROP = KeyEvent.VK_CONTROL;
	private static final int EXIT = KeyEvent.VK_ESCAPE;
	private static final int[] idleCounts = {5000, 4000, 3000, 2000, 1000, 500};
	

	private long idleCounter;	//Counts the amount of time since the last piece drop in milliseconds
	private int piecesDropped;	//Total number of pieces dropped
	private int heldType;	//Shape of piece that is in the "hold" box
	private boolean alreadyHeld;	//If the hold mechanic has been used for the current falling piece
	private boolean lostGame;	//Have you lost the game
	private int [] dropQueue = {1,2,3,4,5,6,7};	//Queue of piece types to drop
	
	private BufferedImage osi = null;	//BufferedImage for double-buffered graphics
	private Graphics osg = null;	//Graphics off-screen for double-buffered graphics
	private Timer t;	// Timer
	private TetrisPiece currPiece;	//The current Tetris piece
	private Color backColor = Color.GRAY;	// a background color of the panel
	private Dimension curDim = null;	// a current Dimensions of the panel
	private Tetris tetris;	//The current game of Tetris
	
	public TetrisPanel()	//constructors
	{
		addKeyListener(this);
		addMouseListener(this);
		randomize(dropQueue);
		tetris = new Tetris();
		piecesDropped = 0;
		createPiece();
		idleCounter = 0;
		heldType = -1;
		alreadyHeld = false;
		lostGame = false;
		
		
		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				if (idleCounter % (idleCounts[tetris.getLevel()]/20) == 0)	//Drops pieces at rate according to level
				{
					currPiece.fall(tetris.board);
					repaint();
				}
				idleCounter++;				
			}
		}, 0, 1);
		System.out.println("Level 1");
	}
	
	public void createPiece()	//Makes a new piece according to the current position of dropqueue, reshuffles the drop queue if you've reached the end
	{
		currPiece = new TetrisPiece(dropQueue[piecesDropped%7], piecesDropped, 0);
		piecesDropped++;
		if (piecesDropped%7 == 0)
			randomize(dropQueue);
	}
	 
	//KeyListener methods
	public void keyPressed(KeyEvent ke)	//react to key commands
	{
		int keyCode = ke.getKeyCode();
		if (!lostGame)
		{
			switch (keyCode)
			{
				case SHIFT_L:
					currPiece.shiftL(tetris.board);
					break;
				case SHIFT_R:
					currPiece.shiftR(tetris.board);
					break;
				case HARD_DROP:
					currPiece.hardDrop(tetris.board);
					idleCounter = 0;
					break;
				case ROTATE_CW:
					currPiece.rotateCW(tetris.board);
					break;
				case ROTATE_CCW:
					currPiece.rotateCCW(tetris.board);
					break;
				case SOFT_DROP:
					currPiece.softDrop(tetris.board);
					break;
				case FIRM_DROP:
					currPiece.firmDrop(tetris.board);
					break;
				case HOLD:
					if (!alreadyHeld)
					{
						int tempHeld = heldType;
						heldType = currPiece.getType()/10;
						if (tempHeld == -1)
							createPiece();
						else
							currPiece = new TetrisPiece(tempHeld, piecesDropped, 0);
						alreadyHeld = true;
					}
					break;
				case EXIT:
					System.exit(0);
					break;
			}
			repaint();
		}
	}
	public void keyReleased(KeyEvent ke){}
	public void keyTyped(KeyEvent ke){}
	
	public void mouseClicked(MouseEvent me){	//Reacts to mouse (For resetting the game when you lose)
		if (lostGame)
			this.resetGame();
	}
	
	public void mouseEntered(MouseEvent me){}
	public void mouseExited(MouseEvent me){}
	public void mousePressed(MouseEvent me){}
	public void mouseReleased(MouseEvent me){}
	
	public void paint(Graphics g)	//System-triggered painting
	{
		curDim = getSize();
		
		osi = new BufferedImage(curDim.width, curDim.height,
								BufferedImage.TYPE_INT_RGB);
		osg = osi.getGraphics();
		
		update(g);
	}
	
	public void update(Graphics g)	//Draws the current configuration of the game and piece onto the screen
	{
		osg.setColor(backColor);
		osg.fillRect(0, 0, curDim.width, curDim.height);
		tetris.displayBoard(osg, curDim.width, curDim.height);	// Fill the board with current configuration
		tetris.displayNextWindow(osg, dropQueue[(piecesDropped)%7], curDim.width, curDim.height);	//Display the next coming piece
		tetris.displayHoldWindow(osg, heldType, curDim.width, curDim.height);
		boolean newPiece = currPiece.display(osg, tetris.board, curDim.width, curDim.height);	//Display the moving, non-finalized piece on the board
		tetris.drawLines(osg, curDim.width, curDim.height);	//Draw the grid of the board
		g.drawImage(osi, 0, 0, this);
		if (tetris.lostGame())	//terminate if hidden rows are filled, display game over screen
		{
			gameOverScreen(osg, g);
		}
		else
		{
			tetris.clearRows();
			if (newPiece)	//Drop a new piece if your current piece is locked into grid
			{
				createPiece();
				alreadyHeld = false;
			}
		}	
	}
	
	private void gameOverScreen(Graphics osg, Graphics g)	//Draws the game over piece
	{
		osg.setColor(Color.BLACK);
		osg.fillRect(0, 0, curDim.width, curDim.height);
		osg.setColor(Color.WHITE);
		int fontSize = 50;
		osg.setFont(new Font("Arial", Font.BOLD, fontSize));
	 	osg.drawString("Game Over", 50, curDim.height/2 - fontSize/2);
	 	osg.setFont(new Font("Arial", Font.BOLD, 36));
	 	osg.drawString("Click to restart, press ESC to close", 50, curDim.height/2 - fontSize/2 + 75);
	 	g.drawImage(osi, 0, 0, this);
		lostGame = true;
		t.cancel();
	}
	
	private void resetGame()	//Resets the game after you lose (basically reconstruct)
	{
		tetris = new Tetris();
		piecesDropped = 0;
		randomize(dropQueue);
		createPiece();
		idleCounter = 0;
		heldType = -1;
		alreadyHeld = false;
		lostGame = false;

		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				if (idleCounter % (idleCounts[tetris.getLevel()]/20) == 0)
				{
					currPiece.fall(tetris.board);
					repaint();
				}
				idleCounter++;				
			}
		}, 0, 1);
		System.out.println("Level 1");
		
		repaint();
	}
	
	static void randomize(int[] ar)	//Fisher-Yates shuffle
	{
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
	    }
	  }
}