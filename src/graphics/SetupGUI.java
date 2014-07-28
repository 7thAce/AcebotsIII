package graphics;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
public class SetupGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public SetupGUI() {
		initGUI();
	}
	
	public void initGUI()
	{
		GridBagLayout gbl = new GridBagLayout();
    	GridBagConstraints c = new GridBagConstraints();
    	this.setLayout(gbl);
    	
    	JTextField usernameField = new JTextField();
    	JTextField passwordField = new JTextField();
    	JTextField channelsField = new JTextField();
    	JButton continueButton = new JButton("Let's Go!");
    	continueButton.addActionListener(new continueActionListener());
    	JLabel titleText = new JLabel("Acebots III");
    	//Font abf = new Font(null);
    	
    	JLabel uInfoText = new JLabel("Please input your Twitch username:");
    	JLabel pInfoText = new JLabel("Please input your OAUTH password:"); //add ? for oauth
    	JLabel cInfoText = new JLabel("Type the channels you want to join, separated by a ,");
    	titleText.setForeground(new Color(12, 176, 12));
    	uInfoText.setForeground(Color.WHITE);
    	pInfoText.setForeground(Color.WHITE);
    	cInfoText.setForeground(Color.WHITE);
    	usernameField.setBackground(Color.BLACK);
    	usernameField.setForeground(new Color(12, 175, 12));
    	passwordField.setBackground(Color.BLACK);
    	passwordField.setForeground(new Color(12, 175, 12));
    	channelsField.setBackground(Color.BLACK);
    	channelsField.setForeground(new Color(12, 175, 12));
    	
    	this.setSize(500,300);
    	setLocationRelativeTo(getOwner());
    	this.setResizable(false);
    	this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    	this.setTitle("Acebots III Setup");
    	this.setBackground(new Color(6, 30, 6));
    	//this.pack();
    	//set c
    	
		c.gridx = 0; //Location
		c.gridy = 0;
		c.weighty = 0; //Size
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(titleText, c);
		
		c.gridy = 1;
		this.add(new JLabel(" "), c);
		
		c.gridy = 2;
		this.add(uInfoText, c);
		
		c.gridy = 3;
		this.add(usernameField, c);
		
		c.gridy = 4;
		this.add(new JLabel(" "), c);
		
		c.gridy = 5;
		this.add(pInfoText, c);
		
		c.gridy = 6;
		this.add(passwordField, c);
		
		c.gridy = 7;
		this.add(new JLabel(" "), c);
		
		c.gridy = 8;
		this.add(cInfoText, c);
		
		c.gridy = 9;
		this.add(channelsField, c);
		
		c.gridy = 10;
		this.add(new JLabel(" "), c);
		
		c.gridy = 11;
		c.fill = GridBagConstraints.CENTER; //make the button do things!
		this.add(continueButton, c);
		
    	this.setVisible(true);
	}

	private class continueActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			//this.
		}
	}
}
