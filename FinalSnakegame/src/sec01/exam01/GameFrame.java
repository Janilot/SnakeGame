package sec01.exam01;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame implements ActionListener{

	GamePanel panel = new GamePanel();
	JButton restartButton;
	
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu helpMenu;
	JMenuItem exitItem;
	JMenuItem helpItem;
	
	GameFrame(){
		
		setTitle("SnakeGame");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(590, 723);
		setResizable(false);
		this.setLayout(null);
		
		//Set Image
		ImageIcon image = new ImageIcon("image/snake.png");
		JLabel label = new JLabel();
		label.setIcon(image);
		label.setBounds(150, 600, 100, 60);
		
		//Set menuBar
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		helpMenu = new JMenu("Help");
		
		exitItem = new JMenuItem("Exit");
		helpItem = new JMenuItem("Move with arrow keys, press space to pause");
		
		exitItem.addActionListener(this);
		
		//Set Button
		restartButton = new JButton();
		restartButton.setText("Restart");
		restartButton.setSize(100, 60);
		restartButton.setLocation(250, 600);
		restartButton.addActionListener(this);
		
		panel = new GamePanel();
		
		
		//add to frame
		fileMenu.add(exitItem);
		helpMenu.add(helpItem);
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		
		this.setJMenuBar(menuBar);
		
		this.add(label);
		this.add(restartButton);
		this.add(panel);

		
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == restartButton) {
			this.remove(panel);
			panel = new GamePanel();
			this.add(panel);
			panel.requestFocusInWindow();
			SwingUtilities.updateComponentTreeUI(this);
		}
		
		if(e.getSource() == exitItem) {
			System.exit(0);
		}
		
	}

}
