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

public class Main implements Runnable{
	
	static GUI gui = new GUI();
	
	public static void main(String[] args) {
		new Thread(new Main()).start();
	}
	
	// Creates a new user interface, including the game board (only used when resetting the game to be played again)
	public static void reset(){
		gui = new GUI();
	}

	@Override
	public void run() {
		while(true){
			gui.repaint();
		}
	}

}
