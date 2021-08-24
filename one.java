import javax.imageio.ImageIO;
import javax.swing.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
// Youxin(Sissy) He; ISU final assignment - flappy bird; Jan. 18, 2020
// This program allows the user to play the classic game "flappy bird" 
// This code sets the first and second screen depends on theuser's choice, set all the background stuff such as the pipes and make the red/blue bird goes up and down, try to pass the pipes(ground) without touching them
@SuppressWarnings("serial")
public class one extends JPanel implements KeyListener, Runnable  {
	//introduction comment
	static Thread thread;
	static JFrame frame;
	int FPS = 60;
	int screenWidth = 400;
	int screenHeight = 500;
	int xb = 0;
	int choice;
	static int rectw;
	static int recth;
	static int points = 0;
	static int bestScore = 0;
	static int startOrNot = 1;
	static int oneOrTwo = 1;
	static int rect1x;
	static int rect1y;
	static Rectangle rect1;
	static Rectangle rect2;
	static Rectangle[] pipes = new Rectangle[16];
	static Rectangle coin1;
	boolean jump1;
	boolean airborne1 = true;
	boolean jumpAgain1 = false;
	boolean jump2;
	boolean airborne2 = true;
	boolean jumpAgain2 = false;
	static boolean lose1 = false;	
	static boolean lose2 = false;	
	double jumpSpeed = 15;		
	static double yVel1 = 0;
	static double yVel2 = 0;
	double gravity = 3.0;
	static double speedb = 2.0;
	static double speedp = 2.0;
	static BufferedImage background;
	static Image red;
	static Image blue;
	static Image top;
	static Image pop;
	static AudioClip backgroundMusic;
	AudioClip coinSound;
	static JLabel score;
	static JLabel score2;
	static JLabel title;
	static JButton start; 
	static Timer scoreTimer;
	static TimerTask addScore;
	// To create a new timer and the timer task that would add score after certain amount of time
	// None
	// None
	public static void newTimerClass () {
		scoreTimer = new Timer();
		addScore = new TimerTask () {
			public void run() {
				points++;
				// To update the score only for rect1
//				  if (oneOrTwo == 1) {
//					   score.setText ("Score: " + points);	
//				   } 
//				   if (oneOrTwo == 2){
//	  				   score.setText ("RED: " + points);	
//	 				   score2.setText ("BLUE: " + points);	
//				   }
//				if (oneOrTwo == 1) {
					score.setText ("Score: " + points);	
				// To update the score for both rect1 and rect2
//				} else {
//					score.setText ("RED: " + points);	
//					score2.setText ("BLUE: " + points);	
//				}
			}	
		};
		// To add the timer task to timer, call it 4 seconds later after the game start and then call it every 2.6 seconds
		scoreTimer.scheduleAtFixedRate(addScore, 4000, 2600);
	}
	
	public one() {
		// To  create the frame
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
		// To set jump and lose to false 
		// The game would start and continue
		jump1 = false;
		jump2 = false;
		lose1 = false;
		lose2 = false;		
		// To create and start a thread to run the program
		thread = new Thread(this);
		thread.start();		
		// To create items for player menu
		JMenuItem onePlayer, twoPlayer;
		onePlayer = new JMenuItem("SINGLE");
		twoPlayer = new JMenuItem("DOUBLE");
		// To set the mode to single player by changing the variable oneOrTwo to 1
		onePlayer.setActionCommand ("one");
		onePlayer.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
					oneOrTwo = 1;
			}
		});		
		// To set the mode to double players by changing the variable oneOrTwo to 2
		twoPlayer.setActionCommand ("two");
		twoPlayer.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
					oneOrTwo = 2;
			}
		});		
		// To create the items for BGM menu
		JMenuItem onOption, offOption;
		onOption = new JMenuItem ("ON");
		offOption = new JMenuItem ("OFF");
		// To loop the BGM when the user chooses "on"
		onOption.setActionCommand ("ON");
		onOption.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
					backgroundMusic.loop ();
			}
		});	
		// To stop looping the BGM when the user chooses "off"
		offOption.setActionCommand ("OFF");
		offOption.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				backgroundMusic.stop ();
		}
		});
		// To create one item for help 
		JMenuItem help;
		help = new JMenuItem("How");
		help.setActionCommand ("HELP");
		// To show how to play the game on massage dialog when the user clicks "how"
		help.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				// To create a pane that holds the message dialog
				JOptionPane pane = new JOptionPane ("Single: Red rectangle only - Press L\n" + "Double: Red Rectangle - Press L\n" + "              Blue rectangle - Press A");
				JDialog helpDialog = pane.createDialog((JFrame)null, "Information");
				// To set the location for the dialog
				helpDialog.setLocation (70, 230);
				helpDialog.setVisible(true);
			}
		});
		// To create the menu for player and add the items
		JMenu PLAYER = new JMenu ("PLAYER");
		PLAYER.add(onePlayer);
		PLAYER.addSeparator();
		PLAYER.add(twoPlayer);
		// To creat the menu for BGM and add the items
		JMenu BGM = new JMenu ("BGM");	
		BGM.add(onOption);
		BGM.addSeparator();
		BGM.add(offOption);
		// To create the menu for help and add the item
		JMenu HELP = new JMenu ("HELP");
		HELP.add(help);
		// To create the main menu that holds all the small menus
		JMenu setting = new JMenu ("Setting");
		setting.add(PLAYER);
		setting.add(BGM);
		setting.add(HELP);
		// To create a menu bar that holds the main menu "setting"
		JMenuBar mainMenu = new JMenuBar();
		mainMenu.add(setting);
		// To add the menu bar on the frame
		frame.setJMenuBar(mainMenu);
		// To wait until the BGM finishes downloading
		MediaTracker tracker = new MediaTracker (this);
		backgroundMusic = Applet.newAudioClip (getCompleteURL ("play.wav"));
		coinSound = Applet.newAudioClip (getCompleteURL ("coin.wav"));
		try
		{
		    tracker.waitForAll ();
		}
			catch (InterruptedException e)
		{
		}
	}

    @Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub		
	}

    // To move rect1/rect2 vertically
    // keyEvent e - press A or L
    // None
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		// When key A is pressed
		if (key == KeyEvent.VK_A) {
			// rect2 jumps and falls
			jump2 = true;
			// Whenever A is pressed, rect2 jumps again
	    	jumpAgain2 = true;
	    	airborne2 = true;
	    	
			move ();
			keepInBound();			
			this.repaint();			
			try {
				Thread.sleep(100/FPS);
			} catch(Exception e1) {
				e1.printStackTrace();
			}
		}
		// When key L is pressed
		if (key == KeyEvent.VK_L) {
			// rect1 jumps and falls
			jump1 = true;
			// Whenever L is pressed, rect1 jumps again
	    	jumpAgain1 = true;
	    	airborne1 = true;
	    	
			move ();
			keepInBound();			
			this.repaint();			
			try {
				Thread.sleep(100/FPS);
			} catch(Exception e1) {
				e1.printStackTrace();
			}   
		}
	}

	// To stop rect1/rect2 jumping when the keys are released
	//keyEvent e - releases A or L
    // None 
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		// When key A is relased
		if (key == KeyEvent.VK_A) {
			// rect2 stops jumping
			jump2 = false;
		}
		// When key L is pressed
		if (key == KeyEvent.VK_L) {
			// rect1 stops jumping
			jump1 = false;
		}
	}
	
	// To check if rect1/rect2 intersects with the pipes or ground
    public void lose () {
    	// Check the pipes in the array pipes one by one each time
    	for (int i = 0; i < pipes.length; i++) {
    		// Check if rect1 intersects with the pipe or touches the ground
	    	if (rect1.intersects(pipes[i]) || rect1.y == 480) {
	    		// if rect1 does, then user1(RED) loses
	    		lose1 = true;
		    }
	    	// Check if rect2 intersects with the pipe or touches the ground at double-players mode
	    	if (oneOrTwo == 2) {
	    		if (rect2.intersects(pipes[i]) || rect2.y== 480) {
	    			// if rect2 does, then user2(BLUE) loses
	    			lose2 = true;
	    		}
	    	}
    	}
    }
    
    public void checkCoin () {
    	// To check if rect1 gets the coin
    	if (rect1.intersects(coin1)) {
    		// To play the coin sound
    		coinSound.play ();
    		// The size of the rects can not be less than 7 x 7
    		if (rectw >= 7 && recth >= 7) {
    			// rect1 becomes smaller if it gets the coin
    			rect1 = new Rectangle (rect1.x, rect1.y, rectw-= 1, recth-= 1);
    		}
    	}
    	// To check if rect2 gets the coin at double-players mode
    	if (oneOrTwo == 2) {
	    	if (rect2.intersects(coin1)) {
	    		if (rectw >= 7 && recth >= 7) {
	    			// rect2 becomes smaller if it gets the coin
	    			rect2 = new Rectangle (rect2.x, rect2.y, rectw-= 1, recth-= 1);
	    		}
	    	}
    	}
    	
    	// When rect1/rect2 get the coin or the coin passes the bounder, it disappaers and is reset to somewhere else
    	if (rect1.intersects(coin1) || rect2.intersects(coin1) || coin1.x < -10) {
    		int coinx = (int) (Math.random()*401) + 600;
    		int coiny = (int) (Math.random()*201) + 200;
    		// To reset the coin to a random place
    		coin1 = new Rectangle (coinx, coiny, 10, 10);
    		// To check if the coin intersects any pipes
    		for (int i = 0; i < pipes.length; i++) {
	    		if (coin1.intersects(pipes[i])) {
	    			// if it does, move it a little forward and upward 
	    			coin1 = new Rectangle (coinx -= 20, coiny-=20, 10, 10);
	    			// To check from the first pipe again to make sure that the coin is not intersecting with any pipe
	    			i = -1;
	    		} 
    	    }
    	} 
    }
    
    // To run the program, intialize the screen and animate the game
    // None
    // None
	public void run() {	
		// To intialize the screen and make rect1/rect2 stay at their initialized positions at the beginning
		initialize();
		jump1 = false;
		airborne1 = false;
		jump2 = false;
		airborne2 = false;
		
		while(true) {
			// To move the background picture, pipes and coin
			update();
			pipesMove ();
			coinMove ();
			
			// To move rect1/rect2
			move ();
			keepInBound();
			// To check if the user gets the coin or not
			checkCoin ();
			// To check if the user lose or not(if rect1/rect2 touches the pipes/ground)
			lose();
			
			this.repaint();
			try {
				Thread.sleep(1000/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			// if the user loses the game...
			if (lose1 == true || lose2 == true) {
				// the timer and timer task trhat are used to count the score is cancelled - stoping counting score
				scoreTimer.cancel ();
				addScore.cancel ();
				// To find the best score the user had
				if (points >= bestScore) {
					bestScore = points;
				}				
				// To show the option dialog that provides two choices - restart or exit
				Object[] options = {"Restart", "Exit"};
				if (oneOrTwo == 1) {
					// single player mode - show the score and best score of the user
					choice = JOptionPane.showOptionDialog (this, "YOUR SCORE: " + points + "\nBEST  SCORE: " + bestScore,
	 			          "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
	 			          options, null);
				} else {
					// double players mode - show who wins the game
					if (lose2 == true) {
						choice = JOptionPane.showOptionDialog (this, "RED WINS!",
		 			    "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
		 			     options, null);
					} else {
					    choice = JOptionPane.showOptionDialog (this, "BLUE WINS!",
		 			    "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
		 			    options, null);							  
					}	
				}
				
			   // if the user choose to restart, it initalizes erevything again just like what it did
 			   if (choice == JOptionPane.YES_OPTION) {
 				   // To reset the point to zero and the text
 				  Object[] modeOptions = {"Single", "Double"};
 				  int mode = JOptionPane.showOptionDialog (this, "Choose Your Mode",
	 			          "Game Over", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
	 			          modeOptions, null);
 				  if (mode == JOptionPane.NO_OPTION) {
 					 oneOrTwo = 2; 
 				  } else {
 					  oneOrTwo = 1;
 				  }
 				  
 				  // To initailize the variables to make the program run again 
 				  lose1 = false;
 				  lose2 = false;
 				  jump1 = false;
 				  jump2 = false;
 				  // To make rect1/rect2 stays at the initailized position until the keys are pressed
 				  yVel1 = 0;
 				  yVel2 = 0;
 				  initialize();
 				  if (oneOrTwo == 2) {
	 				  rect2.x = 100;
	 				  rect2.y = 235;
 				  }
 				  points = 0;
 				  score.setText ("Score: " + points);
 				  newTimerClass ();
 			   	  rect1.x = 130;
				  rect1.y = 265;		  
				  // To run the program again
 				  thread = new Thread(this);
 				  thread.start();
			    } else {
			    	// if the user chooses exit, it exits
			    	System.exit (0);
			    }
 			    return;  
			}
			this.repaint();			
			try {
				Thread.sleep(1000/FPS);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// To paint the screen and repaint/updates it depends on the choice of the user
	// Graphic g to draw on the screen
	// None
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		// if the game has not started yet..
		if (startOrNot == 1) {
			// To set the background picture and loop it
			initialize();
			update();
			g.drawImage(background, xb, 0, 1200, 500, this);
			g.drawImage(background, xb + 1200, 0, 1200, 500, this);
		} else {
			// if the user chooses to start, set everything(including rect1/rect2, the birds, pipes, background picture and coin) to be ready to pley
		    // To set background
			g.drawImage(background, xb, 0, 1200, 500, this);
			g.drawImage(background, xb + 1200, 0, 1200, 500, this);
			Color pipeColor = new Color(100, 200, 100);
			Color coinColor = new Color(250, 200, 100);
			g2.setColor(pipeColor);
			// To set all the pipes
			for (int i = 0; i < 8; i++) {
				g2.drawImage(pop, pipes[i].x, pipes[i].y, pipes[i].width, pipes[i].height, this);		
			}
			for (int i = 8; i < 16; i++) {
				g2.drawImage(top, pipes[i].x, pipes[i].y, 90, 30, this);
			}
					// To set the red bird at the same position with rect1, having same width and height
					// When rect1 moves or changes, red bird moves or changes excatly the same way
			if (oneOrTwo == 1) {
				g2.drawImage(red, rect1.x, rect1.y, rectw, recth, this);
			} else {
				g2.drawImage(red, rect1.x, rect1.y, rectw, recth, this);
				// To set the blue bird at the same position with rect2, having same width and height
				// When rect2 moves or changes, blue bird moves or changes excatly the same way
			    g2.drawImage(blue, rect2.x, rect2.y, rectw, recth, this);
			}
			// To set the coin
			g2.setColor(coinColor);
			g2.fill(coin1);
		}
	}
	
	// To move rect1 and rect2 vertically
	// None
	// None
	public void move() {
		// if L is pressed, rect1 jumps again
		if (jumpAgain1) {
			airborne1 = true;
			yVel1 = jumpSpeed;
			jumpAgain1 = false;
		}		
		// When rect1 falls down, the distance it travelled vertically is negative
		if(airborne1) { 
			yVel1 -= gravity;
		}else {
			// When rect1 jumps up, the distance it travelled vertically is positive
			if(jump1) { // up
				airborne1 = true;
				yVel1 = jumpSpeed;
			}
		}
		// if A is pressed, rect2 jumps again
		if (oneOrTwo == 2) {
			if (jumpAgain2) {
				airborne2 = true;
				yVel2 = jumpSpeed;
				jumpAgain2 = false;
			}
			// rect2 falls down
			if(airborne2) { 
				yVel2 -= gravity;
			}else {
				// rect2 jumps up
				if(jump2) { 
					airborne2 = true;
					yVel2 = jumpSpeed;
				}
			}
			// To reset the y of rect2
			// fall - minus negative is plus positive - going downward
			// up - minus positive - going upward
			rect2.y -= yVel2;
		}
		// To reset the y of rect1
		rect1.y -= yVel1;
	}
	
	// To make sure that rect1/rect2 is in bound
	// None
	// None
	public void keepInBound() {
		// the x of rect1/rect2 is not goin to change - only check the y of rect1/rect2
		// To check if rect1 is out of the top bounder
		if(rect1.y < 0) {
			// reset it to be in the bounder, among the top
			rect1.y = 0;
			yVel1 = 0;
		// To check if rect1 is out of the bottom bounder
		}else if(rect1.y > 480) {
			// reset it to be in the bounder, among the bottom
			rect1.y = 480;
			yVel1 = 0;
		}
		// to check the same thing for rect2 
		if (oneOrTwo == 2) {
			if(rect2.y < 0) { 
				rect2.y = 0;
				yVel2 = 0;
			}else if(rect2.y > 480) {
				rect2.y = 480;
				yVel2 = 0;
			}
		}
	}

	// To initailize the second screen
	// None
	// None
	public static void initialize() {
		// To initialized the array of the pipe rectangles
		pipes[0] = new Rectangle (300, 0, 70, 170);
		pipes[1] = new Rectangle (300, 380, 70, 120);
		pipes[2] = new Rectangle (450, 0, 70, 270);
		pipes[3] = new Rectangle (450, 480, 70, 20);
		pipes[4] = new Rectangle (600, 0, 70, 120);
		pipes[5] = new Rectangle (600, 350, 70, 150);
		pipes[6] = new Rectangle (750, 0, 70, 220);
		pipes[7] = new Rectangle (750, 430, 70, 70);
		pipes[8] = new Rectangle (290, 170, 90, 30);
		pipes[9] = new Rectangle (290, 350, 90, 30);
		pipes[10] = new Rectangle (440, 270, 90, 30);
		pipes[11] = new Rectangle (440, 450, 90, 30);
		pipes[12] = new Rectangle (590, 120, 90, 30);
		pipes[13] = new Rectangle (590, 320, 90, 30);
		pipes[14] = new Rectangle (740, 220, 90, 30);
		pipes[15] = new Rectangle (740, 400, 90, 30);
		// To initalize the size and location of rect1, rect2 and the coin
		rectw = 25; recth = 25;
		rect1 = new Rectangle(130, 265, rectw, recth);
		rect2 = new Rectangle(100, 235, rectw, recth);
		coin1 = new Rectangle (400, 300, 10, 10);
		try {
			// To download the background picture, blue bird image and red bird image
			background = ImageIO.read(new File("bgp.jpg"));
			red = ImageIO.read(new File("red5.png"));
			blue = ImageIO.read(new File("blue5.png"));
			top = ImageIO.read(new File("top.jpg"));
			pop = ImageIO.read(new File ("pop.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// To make the background picturee keep moving
	// None
	// None
	public void update() {
		xb -= speedb;
		// if the background picture passes the left bounder, reset it back to zero again and keep moving
		if(xb < -1200) {
			xb = 0;
		}
	}
	
	// To move the pipes and reset them to keep them moving 
	// None
	// None
	public void pipesMove() {
		// To make pipes move toward left
		for(int i = 0; i < pipes.length; i++) {
			pipes[i].x -= speedp;
		}	
		int a = (int) (Math.random()*300)+1;
		// To reset the pipes back if it passes the left bounder
		if (pipes[0].x < -70 && pipes[1].x < -70) {
			pipes[0] = new Rectangle (530, 0, 70, a);
			pipes[1] = new Rectangle (530, a+200, 70, 500);
			pipes[8] = new Rectangle (520, a, 90, 30);
		    pipes[9] = new Rectangle (520, a+170, 90, 30);
		    // To reduce the gap between the pipes(up and down pipes) up to a certain height
		    if (pipes[8].y <= 230) {
			    pipes[0].height += 5;
			    pipes[8].y += 5;
		    }
		}
		// Other three if statements do the same thing
		if (pipes[2].x < -70 && pipes[3].x < -70) {
			a = (int) (Math.random()*300)+1;
			pipes[2] = new Rectangle (530, 0, 70, a);
			pipes[3] = new Rectangle (530, a+200, 70, 500);
			pipes[10] = new Rectangle (520, a, 90, 30);
		    pipes[11] = new Rectangle (520, a+170, 90, 30);
		    if (pipes[10].y <= 340) {
			    pipes[2].height += 5;
			    pipes[10].y += 5;
		    }
		}
		if (pipes[4].x < -70 && pipes[5].x < -70) {
			a = (int) (Math.random()*300)+1;
			pipes[4] = new Rectangle (530, 0, 70, a);
			pipes[5] = new Rectangle (530, a+200, 70, 500);
			pipes[12] = new Rectangle (520, a, 90, 30);
		    pipes[13] = new Rectangle (520, a+170, 90, 30);
		    if (pipes[12].y <= 180) {
			    pipes[4].height += 5;
			    pipes[12].y += 5;
	        }
		}		
		if (pipes[6].x < -70 && pipes[7].x < -70) {
			a = (int) (Math.random()*300)+1;
			pipes[6] = new Rectangle (530, 0, 70, a);
			pipes[7] = new Rectangle (530, a+200, 70, 500);
			pipes[14] = new Rectangle (520, a, 90, 30);
		    pipes[15] = new Rectangle (520, a+170, 90, 30);
		    if (pipes[14].y <= 280) {
			    pipes[6].height += 5;
			    pipes[14].y += 5;
		    }
		}

	}
	
	// To move the coin with the pipe
	// None
	// None
	public void coinMove() {
		coin1.x -= speedp;
	}		
	
	// To get the compelete URL of the imput file
	// String the name of the input file
	// URL of the file
	public URL getCompleteURL (String fileName)
	{
		try
		{
			return new URL ("file:" + System.getProperty ("user.dir") + "/" + fileName);
		}
		catch (MalformedURLException e)
		{
			System.err.println (e.getMessage ());
		}
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		// To create a new fame and panel
		frame = new JFrame ("Flappy Rectangle");
		one myPanel = new one ();
		myPanel.setLayout(null);
		// To set the first screen that appears
	    if (startOrNot == 1) {
	    	// To play the BGM
	    	backgroundMusic.loop ();
	    	// To set the title
			title = new JLabel("FLAPPY BIRD");
			title.setLocation(70, 150);
			title.setSize(400, 100);
			Font font = new Font ("Malgun Gothic", Font.PLAIN, 40);
			title.setFont(font);
			// To set the botton "play"
			start = new JButton ("PLAY");
			start.setLocation(150, 270);
			start.setSize(80, 40);
			myPanel.add(title);
			myPanel.add(start);
			// When the user chooses to play...
			start.setActionCommand("startPlay");
			start.addActionListener(new ActionListener () {
				public void actionPerformed(ActionEvent e) { 
				// To clean the previous screen
				   myPanel.removeAll(); 
				   if (oneOrTwo == 1) {
					   // single player mode - to set the label to count the score
					   score = new JLabel ("SCORE: " + points);
					   score.setBounds(100,20,100,100);
					   score.setFont(score.getFont().deriveFont(18.0f));
					   myPanel.add (score);
				   } else {
					   // double players mode - to set the labels that count score for both rect1 and rect2
					   score = new JLabel ("RED: " + points);   
					   score.setBounds(100,20,100,100);
					   score.setFont(score.getFont().deriveFont(18.0f));  
					   score2 = new JLabel ("BLUE: " + points);
					   score2.setBounds(100,50,100,100);
					   score2.setFont(score.getFont().deriveFont(18.0f));  
					   myPanel.add (score);
					   myPanel.add (score2);
				   }
				   // change the screen to the second one by changing the boolean variable startOrNot to 2
				   startOrNot = 2;
				   // To create a new timer and timer task that counts the score(s)
				   newTimerClass ();
				} 
			});
		}
	    // To add the panel to the frame and make it visible
		frame.add(myPanel);
		// to add key listener
		frame.addKeyListener(myPanel);
		frame.setFocusable (true);
		frame.setFocusTraversalKeysEnabled(false);
		frame.setVisible(true);
		frame.pack();
		// To make the screen fixed in certain size
		frame.setResizable(false);
		// To close when the user click "x"
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}