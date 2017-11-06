/* CS 2365-002 Project 04 - Derek Fritz
 * 4/22/2017
 * 
 * Project Members:
 * 		Derek Fritz
 * Project Title:
 * 		Battleship
 * Instructions:
 * 		Upon running the program, a window will open that allows for free user interaction.
 * 		In this game, the user and the computer will take turns selecting cells displayed on the
 * 		game board in an attempt to hit each other's hidden ships. Upon hitting a ship, the player's
 * 		board will indicate its location with the generic gray cells turning red. If the guessed cell
 * 		does not contain a ship, it will simply turn blue to mesh with the background.
 * 		
 * 		Both the player and the computer have four ships that are randomly placed on the board with random
 * 		orientations. The ships consist a 5-Cell ship, a 4-Cell ship, a 3-Cell ship, a 2-Cell ship, and a 1-Cell ship.
 * 
 * 		The first side to successfully hit all of their opponent's ships (a total of 14 Cells) will be declared the winner.
 * 		The player will then be allowed to either start the game over, or end the game. Starting over the game creates a
 * 		new randomly-generated game board. Ending the game closes the window and terminates the program.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GUI extends JFrame {
	
	public int xCoord, yCoord = 0;
	public int eGuessX, eGuessY = 0;
	public int winCounter, loseCounter = 0;
	public boolean success = false;
	public boolean menu = false;
	
	boolean[][] enemyShips = new boolean[10][10];
	boolean[][] playerShips = new boolean[10][10];
	boolean[][] guessed = new boolean[10][10];
	boolean[][] eGuessed = new boolean[10][10];
	
	JTextArea display;
	JScrollPane scroll;
	JScrollBar bar;
	JButton replay, end;
	
	public GUI(){
		this.setTitle("Battleship");
		this.setSize(656, 929);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		
		// Attempts to place all four ships, if an issue occurs (ship overlap or out of bounds) then the board is cleared and placement starts over
		for(int i = 2; i < 6; i++){
			boolean completed = shipPlacement(i, enemyShips);
			if(completed == false)
				i = 1;
		}
		for(int i = 2; i < 6; i++){
			boolean completed = shipPlacement(i, playerShips);
			if(completed == false)
				i = 1;
		}
		
		// Creates the paint-components of the game-board
		Board board = new Board();
		this.setContentPane(board);
		
		// Creates the output-display at the bottom of the UI
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 55;
		c.insets = new Insets(713,0,0,0);
		display = new JTextArea("Welcome to Battleship!", 10, 55);
		scroll = new JScrollPane(display, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bar = scroll.getVerticalScrollBar();
		scroll.setVisible(true);
		add(scroll, c);
		revalidate();
		repaint();
		
		// Creates the menu buttons that are displayed upon endgame-conditions
		replay = new JButton("Play Again");
		end = new JButton("End Game");
		c.insets = new Insets(713,0,0,200);
		add(replay,c);
		c.insets = new Insets(713,200,0,0);
		add(end,c);
		replay.setVisible(false);
		end.setVisible(false);
		
		// Creates listener objects to track mouse-based player input
		CoordTracker ctrack = new CoordTracker();
		this.addMouseMotionListener(ctrack);
		Action action = new Action();
		this.addMouseListener(action);
	}
	
	// Randomly places a single ship on the game-board in a random orientation, returns false if the randomly selected traits conflict with other ships
	public boolean shipPlacement(int count, boolean[][] ships){
		Random rand = new Random();
		int indexX = rand.nextInt(10);	// Determines first cell of the ship
		int indexY = rand.nextInt(10);
		int direction = rand.nextInt(4);	// Determines ship orientation; 0 = Up, 1 = Right, 2 = Down, 3 = Left
		try{
			if(direction == 0){
				for(int i = indexY; i > indexY - count; i--){
					// If the current cell already has a ship placed in it, the board is cleared and false is returned to flag a restart
					if(ships[indexX][i] == true){
						for(i = 0; i < 10; i++){
							for(int j = 0; j < 10; j++)
								ships[i][j] = false;
						}
						//shipPlacement(count);
						return false;
					}
					ships[indexX][i] = true;
				}
			}
			if(direction == 1){
				for(int i = indexX; i < indexX + count; i++){
					// If the current cell already has a ship placed in it, the board is cleared and false is returned to flag a restart
					if(ships[i][indexY] == true){
						for(i = 0; i < 10; i++){
							for(int j = 0; j < 10; j++)
								ships[i][j] = false;
						}
						//shipPlacement(count);
						return false;
					}
					ships[i][indexY] = true;
				}
			}
			if(direction == 2){
				for(int i = indexY; i < indexY + count; i++){
					// If the current cell already has a ship placed in it, the board is cleared and false is returned to flag a restart
					if(ships[indexX][i] == true){
						for(i = 0; i < 10; i++){
							for(int j = 0; j < 10; j++)
								ships[i][j] = false;
						}
						//shipPlacement(count);
						return false;
					}
					ships[indexX][i] = true;
				}
			}
			if(direction == 3){
				for(int i = indexX; i > indexX - count; i--){
					// If the current cell already has a ship placed in it, the board is cleared and false is returned to flag a restart
					if(ships[i][indexY] == true){
						for(i = 0; i < 10; i++){
							for(int j = 0; j < 10; j++)
								ships[i][j] = false;
						}
						//shipPlacement(count);
						return false;
					}
					ships[i][indexY] = true;
				}
			}
		}
		// If the random ship placement attempts to go out of bounds, the placements reset and it tries again
		catch(ArrayIndexOutOfBoundsException e){
			for(int i = 0; i < 10; i++){
				for(int j = 0; j < 10; j++)
					ships[i][j] = false;
			}
			//shipPlacement(count);
			return false;
		}
		//System.out.println("Ships have been placed.");
		return true;
	}
	
	// Consists of all of the graphics-class/painted components of the UI
	public class Board extends JPanel{
		
		public void paintComponent(Graphics g){
			g.setColor(Color.BLUE);
			g.fillRect(0, 0, 650, 650);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 650, 650, 250);
			
			for(int x = 0; x < 10; x++){
				for(int y = 0; y < 10; y++){
					g.setColor(Color.LIGHT_GRAY);
					
					// Displays enemy ship positions for testing purposes
					/*if(enemyShips[x][y] == true){
						g.setColor(Color.YELLOW);
					}*/
					// Displays player ship positions for testing purposes
					/*if(playerShips[x][y] == true){
						g.setColor(Color.GREEN);
					}*/
					
					// Changes cell color when clicked based on its contents
					if(guessed[x][y] == true){
						if(enemyShips[x][y] == true){
							g.setColor(Color.RED);
						}
						else
							g.setColor(Color.BLUE);
					}
					// Highlights cell when moused-over
					if(xCoord >= 64*x+10+3 && xCoord <= 64*x+54+10+3 && yCoord >= 64*y+10+27 && yCoord <= 64*y+54+10+27){
						g.setColor(Color.WHITE);
					}
					// Fills out board with 10x10 set of cells
					g.fillRect(64*x+10, 64*y+10, 54, 54);
				}
			}
			
			// Health Bars
			g.setColor(Color.RED);
			g.fillRect(10, 660, 308, 54);	// Player Health
			g.fillRect(330, 660, 308, 54);	// Enemy Health
			g.setColor(Color.BLACK);
			g.fillRect(318-22*loseCounter, 660, 22*loseCounter, 54);
			g.fillRect(638-22*winCounter, 660, 22*winCounter, 54);
			String playerDisplay = "Your Ships: " + (14-loseCounter);
			String enemyDisplay = "Enemy Ships: " + (14-winCounter);
			g.setColor(Color.WHITE);
			g.setFont(new Font("TimesRoman", Font.BOLD, 22));
			g.drawString(playerDisplay, 92, 695);
			g.drawString(enemyDisplay, 402, 695);
		}
		
	}
	
	public class CoordTracker implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			//System.out.println("The mouse was moved.");
			xCoord = e.getX();
			yCoord = e.getY();
		}
		
	}
	
	public class Action implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// Prevents cells from being selected while the End Game Menu is running
			if(menu == false){
				char xToChar = (char) ('A' + cellX());	// Converts the X Coordinate to a char A-J
				int yPlus = cellY() + 1;	// Adds 1 to the Y Coordinate so the range becomes 1-10
				
				if(cellX() != -1 && cellY() != -1){
					// Count a correct guess only if the cell contains an enemy ship and has not been guessed previously
					if(enemyShips[cellX()][cellY()] == true){
						if(guessed[cellX()][cellY()] == false){
							//System.out.println("You have hit an enemy ship at position [" + xToChar + "][" + yPlus + "].");
							writeToDisplay("You have hit an enemy ship at position [" + xToChar + "][" + yPlus + "].");
							winCounter ++;
						}
						else{
							//System.out.println("You targeted position [" + xToChar + "][" + yPlus + "], but you already hit an enemy ship here.");
							writeToDisplay("You targeted position [" + xToChar + "][" + yPlus + "], but you already hit an enemy ship here.");
						}
					}
					else{
						//System.out.println("You targeted position [" + xToChar + "][" + yPlus + "], but there was nothing there.");
						writeToDisplay("You targeted position [" + xToChar + "][" + yPlus + "], but there was nothing there.");
					}
					guessed[cellX()][cellY()] = true;
				}
				// Prevents user from "guessing" at a position outside of a game cell
				else{
					return;
				}
				// Checks if the player has won before initiating the enemy's turn
				if(winCounter == 14){
					endGame(1);
				}
				else
					while(enemyMove() == false);	// After the player has made a guess, the enemy's turn begins
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// Takes string argument and writes to the JTextArea at the bottom of the UI, as well as refreshes the scroll bar so it moves to the bottom
	public void writeToDisplay(String output){
		display.append("\n");
		display.append(output);
		revalidate();
		bar.setValue(bar.getMaximum());
	}
	
	// Provides the X-Coordinate of the cell the mouse is currently in
	public int cellX(){
		for(int x = 0; x < 10; x++){
			for(int y = 0; y < 10; y++){
				if(xCoord >= 64*x+10+3 && xCoord <= 64*x+54+10+3 && yCoord >= 64*y+10+27 && yCoord <= 64*y+54+10+27){
					return x;
				}
			}
		}
		return -1;
	}
	
	// Provides the Y-Coordinate of the cell the mouse is currently in
	public int cellY(){
		for(int x = 0; x < 10; x++){
			for(int y = 0; y < 10; y++){
				if(xCoord >= 64*x+10+3 && xCoord <= 64*x+54+10+3 && yCoord >= 64*y+10+27 && yCoord <= 64*y+54+10+27){
					return y;
				}
			}
		}
		return -1;
	}
	
	// Contains the actions of the enemy's turn, either randomly guessing at a cell or making an educated guess if a previous guess was correct
	public boolean enemyMove(){
		Random rand = new Random();
		// If the guess was correct the previous turn, the enemy will randomly guess at one of the adjacent cells
		if(success == true){
			int direction = rand.nextInt(4);
			if(direction == 0)
				eGuessY --;
			if(direction == 1)
				eGuessX ++;
			if(direction == 2)
				eGuessY ++;
			if(direction == 3)
				eGuessX --;
		}
		else{
			eGuessX = rand.nextInt(10);
			eGuessY = rand.nextInt(10);
		}
		
		try{
			// If the computer has already guessed at the selected cell, the method will start over and select a different cell
			if(eGuessed[eGuessX][eGuessY] == true){
				//enemyMove();
				return false;
			}
		}
		catch(ArrayIndexOutOfBoundsException e){
			//enemyMove();
			return false;
		}
		
		char xToChar = (char) ('A' + eGuessX);	// Converts the X Coordinate to a char A-J
		int yPlus = eGuessY + 1;	// Adds 1 to the Y Coordinate so the range becomes 1-10
		
		if(playerShips[eGuessX][eGuessY] == true){
			//System.out.println("Your opponent has hit your ship at position [" + xToChar + "][" + yPlus + "].");
			writeToDisplay("Your opponent has hit your ship at position [" + xToChar + "][" + yPlus + "].");
			loseCounter ++;
			success = true;
		}
		else{
			//System.out.println("Your opponent targeted position [" + xToChar + "][" + yPlus + "] and missed.");
			writeToDisplay("Your opponent targeted position [" + xToChar + "][" + yPlus + "] and missed.");
			success = false;
		}
		eGuessed[eGuessX][eGuessY] = true;
		
		// Checks to see if the enemy has won before initiating the player's turn
		if(loseCounter == 14){
			endGame(0);
		}
		
		return true;
	}
	
	// Activates the end-game menu, as well as placing a hold on certain mouse input to avoid game continuation
	public void endGame(int winner){
		if(winner == 1){
			//System.out.println("Congratulations! You have sunk all of your opponent's ships. You are the victor.");
			writeToDisplay("Congratulations! You have sunk all of your opponent's ships. You are the victor.");
		}
		else{
			//System.out.println("Your opponent has successfully sunk all of your ships. Better luck next time!");
			writeToDisplay("Your opponent has successfully sunk all of your ships. Better luck next time!");
		}
		menu = true;
		replay.setVisible(true);
		end.setVisible(true);
		scroll.setVisible(false);
		revalidate();
		repaint();
		replay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getSource() == replay){
					dispose();
					Main.reset();
				}
			}
		});
		end.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(e.getSource() == end)
					System.exit(0);
			}
		});
	}
}
