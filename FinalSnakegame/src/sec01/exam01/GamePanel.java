package sec01.exam01;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;
import javax.sound.sampled.*;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	static final int DELAY = 135;
	final int x[] = new int[GAME_UNITS];
	final int y[] = new int[GAME_UNITS];
	int bodyParts = 3;
	int applesEaten = 0;
	int appleX;
	int appleY;
	int highScore = 0;
	char direction = 'D';
	boolean running = false;
	Timer timer;
	Random random;
	Writer writer = null;
	static boolean gameOn = false; 
	
	GamePanel(){
		
		//Play("sound/snakeCollision.wav",  -80);
        Play("sound/snakeMove.wav", -80);
        Play("sound/snakeApple.wav", -80);
        
		//create a text file for the fileReader
		try {
			writer = new FileWriter("C:/Temp/SnakeScore.txt", true);
			
			writer.write(Integer.toString(applesEaten));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
		
		random = new Random();
		this.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setLocation(0, 0);
		this.setBackground(Color.white);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}
	
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
		
	}
	
	public void pause() {
		GamePanel.gameOn = true;
		timer.stop();
	}

	public void resume() {
		GamePanel.gameOn = false;
		timer.start();
	}
	
	public void Play(String fileName, float level)
    {
        try
        {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(fileName));
            Clip clip = AudioSystem.getClip();
            clip.stop();
            clip.open(ais);
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            if (volume != null) {
                volume.setValue(level);     
            }
            clip.start();
            
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
    }

	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			
			//draw Greed line
			int j = 0;
			do {
				g.drawLine(j * UNIT_SIZE, 0, j * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, j * UNIT_SIZE, SCREEN_WIDTH, j * UNIT_SIZE);
				j++;
				
			}while(j < SCREEN_HEIGHT/UNIT_SIZE);
			
			//draw apple
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			//draw snake
			int z = 0;
			while(z < bodyParts) {
				if(z == 0) {
					g.setColor(Color.BLACK);
					g.fillRect(x[z], y[z], UNIT_SIZE, UNIT_SIZE);
					z++;
				}
				else {
					g.setColor(Color.GRAY);
					g.setColor(new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255)));
					g.fillRect(x[z], y[z], UNIT_SIZE, UNIT_SIZE);
					z++;
				}	
			}
			
			//read score and find highest score
			 try {
		            Scanner file = new Scanner(new File("C:/Temp/SnakeScore.txt"));
		            int largest = file.nextInt();

		            while(file.hasNextInt()) {
		                int number = file.nextInt();

		                if(number > largest) {
		                    largest = number;
		                }
		            }

		            file.close();
		            highScore = largest;
		        } catch(IOException e) {
		            System.out.println(e.getMessage());
		        } catch (NoSuchElementException e) {
		        	highScore = 0;
				}
			
			 highScore = (highScore > applesEaten) ? highScore : applesEaten;
			//draw score and highest score
			g.setColor(Color.blue);
			g.setFont( new Font("Helvetica",Font.BOLD, 25));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+ applesEaten + "  HighScore: " + highScore, 
					(SCREEN_WIDTH - metrics.stringWidth("Score: "+ applesEaten
							+ "  HighScore: " + highScore))/2, g.getFont().getSize());
		}
		else {
			gameOver(g);
		}
		
	}
	
	public void newApple(){
		appleX = random.nextInt((int)((SCREEN_WIDTH-50) / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int)((SCREEN_HEIGHT-25) / UNIT_SIZE)) * UNIT_SIZE;
	}
	
	public void move(){
		for(int i = bodyParts; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
		
	}
	public void checkApple() {
		if((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			Play("sound/snakeApple.wav", 0);
			newApple();
		}
	}
	public void checkCollisions() {
		//checks if head collides with body
		for(int i = bodyParts; i > 0; i--) {
			if((x[0] == x[i]) && (y[0] == y[i])) {
				Play("sound/snakeCollision.wav",  -10);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				running = false;
			}
		}
		//check if head touches left border
		if(x[0] < 0) {
			Play("sound/snakeCollision.wav",  -10);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			running = false;
		}
		//check if head touches right border
		if(x[0] > SCREEN_WIDTH - 50) {
			Play("sound/snakeCollision.wav",  -10);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			running = false;
		}
		//check if head touches top border
		if(y[0] < 0) {
			Play("sound/snakeCollision.wav",  -10);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			running = false;
		}
		//check if head touches bottom border
		if(y[0] > SCREEN_HEIGHT - 25) {
			Play("sound/snakeCollision.wav",  -10);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		
		highScore = (highScore > applesEaten) ? highScore : applesEaten;

		//draw score and highest score
		g.setColor(Color.blue);
		g.setFont( new Font("Helvetica",Font.BOLD, 25));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+ applesEaten + "  HighScore: " + highScore, 
				(SCREEN_WIDTH - metrics1.stringWidth("Score: "+ applesEaten
						+ "  HighScore: " + highScore))/2, g.getFont().getSize());
		
		//Score file Out
		try {
			//fileWriter 생성자 두번째값이 false면 덮어쓰기, true면 전의 기록 뒤에 추가
			writer = new FileWriter("C:/Temp/SnakeScore.txt", true);
			String str00 = "\n";
			String scoreOut =  Integer.toString(applesEaten);
			
			writer.write(scoreOut);
			writer.write(str00);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		//Game Over text
		g.setColor(Color.red);
		g.setFont( new Font("Helvetica",Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
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
					Play("sound/snakeMove.wav", -10);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
					Play("sound/snakeMove.wav", -10);
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
					Play("sound/snakeMove.wav", -10);
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
					Play("sound/snakeMove.wav", -10);
				}
				break;
			case KeyEvent.VK_SPACE:
				if(GamePanel.gameOn) {
					resume();
				} else {
					pause();
				}
				break;
			}
		}
	}
}
