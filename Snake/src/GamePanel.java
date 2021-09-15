import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements ActionListener, MouseListener{

	static final int SCREEN_WIDTH = 800;
	static final int SCREEN_HEIGHT = 800;
	static final int UNIT_SIZE = 25 ; //Size of unit => 25 pixels
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE; //How many units can be at the same time in the game, to fill the whole screen
	static final int DELAY = 55; //Speed of snake
	
	final int x[] = new int[GAME_UNITS]; //x size/coordinates of snake
	final int y[] = new int[GAME_UNITS]; //y size/coordinates of snake
	int bodyParts = 3; //Start with 3 bodyparts
	int applesEaten; //Score
	int appleX , appleY; //Apple coordinates
	
	char direction = 'R'; //Start by moving right
	boolean running = false; //If game is running it's true
	boolean started = false; //False so the game doesn't start by itself. True only after the user clicks the screen
	boolean noclick = true; //True to display the starting message THEN false after the start of the game to make sure the message won't be displayed again
	Timer timer;
	Random random;
	static boolean gameOn = false; //[Space] to change to false or true. Pausing and resuming the game. 
	
	public GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		this.addMouseListener(this);
		startGame();
	}
	
	public void startGame() {
		//Start the game only after the user clicks the screen
		if(started && noclick) {
			newApple(); //Create an apple
			running = true;
			timer = new Timer(DELAY,this); //Call the ActionListener
			timer.start();
			noclick = false;
		}
	}
	
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_LEFT:
					if(direction != 'R') {
						direction = 'L';
					}
					break;
					
				case KeyEvent.VK_RIGHT:
					if(direction != 'L') {
						direction = 'R';
					}
					break;
					
				case KeyEvent.VK_UP:
					if(direction != 'D') {
						direction = 'U';
					}
					break;
					
				case KeyEvent.VK_DOWN:
					if(direction != 'U') {
						direction = 'D';
					}
					break;
					
				case KeyEvent.VK_SPACE:
					if(GamePanel.gameOn) {
						resume();
					} 
					else if(running){
						pause();
					}
					break;
			}
		}
	}
	
	public void move() {
		for(int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1]; //Move bodyparts of snake one place up e.g. x[3] goes to x[2], x[2] goes to x[1] place, x[1] goes to x[0] place (which is the head of the snake) and x[0] changes with the switch direction
			y[i] = y[i-1]; //Same for y[]
		}
		switch(direction) { //Move head of snake
			case 'U':
				y[0] = y[0] - UNIT_SIZE; //Move bodypart y[0] (head) of snake up
				break;
			case 'D':
				y[0] = y[0] + UNIT_SIZE; //Move bodypart y[0] (head) of snake down
				break;
			case 'L':
				x[0] = x[0] - UNIT_SIZE; //Move bodypart x[0] (head) of snake left
				break;
			case 'R':
				x[0] = x[0] + UNIT_SIZE; //Move bodypart x[0] (head) of snake right
				break;
		}
	}
	
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) { //If head eats apple
			bodyParts++; //Add a part
			applesEaten++; //Add to score
			newApple(); //Create new apple
		}			
	}
	
	public void checkCollisions() {
		// check if head collides with body
		for(int i = bodyParts; i > 0; i--) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}			
		}
		//check if head touches left border
		if(x[0] < 0) {
			running = false;
		}	
		//check if head touches right border
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}	
		//check if head touches top border
		if(y[0] < 0) {
			running = false;
		}
		//check if head touches bottom border
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			g.setColor(Color.RED);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			for(int i = 0; i < bodyParts; i++) {
				if(i == 0) { //Make the head of the snake blue
					g.setColor(Color.BLUE);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else { //Make the body of the snake green
					g.setColor(Color.GREEN);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			//Score text
			g.setColor(Color.CYAN);
			g.setFont(new Font("Ink Free", Font.BOLD, 30));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+ applesEaten))/2, g.getFont().getSize());
			//Game paused text
			if(GamePanel.gameOn == true) {
				g.setFont(new Font("SAN_SERIF", Font.BOLD, 30));
				metrics = getFontMetrics(g.getFont());
				g.drawString("Game Paused", (SCREEN_WIDTH - metrics.stringWidth("Game Paused")) /2, SCREEN_HEIGHT/2);
			}
		}
		else {
			if(!started) { //Start screen text
				g.setColor(Color.RED);
				g.setFont(new Font("SAN_SERIF", Font.BOLD, 30));
				FontMetrics metrics = getFontMetrics(g.getFont());
				g.drawString("[Click] => Begin", (SCREEN_WIDTH - metrics.stringWidth("[Click] => Begin")) /2, SCREEN_HEIGHT/2);
				g.drawString("[Space] => Pause", (SCREEN_WIDTH - metrics.stringWidth("[Space] => Pause")) /2, (SCREEN_HEIGHT/2) + 50);
			}
			else {
				gameOver(g);
			}
		}
	}
	
	public void gameOver(Graphics g) {
		//Final score text
		g.setColor(Color.RED);
		g.setFont(new Font("SAN_SERIF", Font.BOLD, 45));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Your score is: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Your score is: " + applesEaten))/2, (SCREEN_HEIGHT/2) + 50);

		//Game Over Text
		g.setColor(Color.RED);
		g.setFont(new Font("SAN_SERIF", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		started = false;
	}
	
	public void pause() {
		GamePanel.gameOn = true;
		repaint(); //To display message "Game Paused"
		timer.stop(); //Stop game
	}

	public void resume() {
		GamePanel.gameOn = false;
		timer.start(); //Restart game from where it was paused
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		started = true;
		startGame();
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}		
}