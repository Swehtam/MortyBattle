import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Menu extends JPanel implements ActionListener {

	private JLabel bemVindoLabel;
	private JLabel projetoLabel;
	private JLabel nomeLabel;
	private JLabel serverLabel;

	private JTextField nomeField;
	private JTextField serverField;

	private JButton iniciarButton;

	private GridBagConstraints gbc;

	private Jogo jogoFrame;

	private Image img;
	private InputStream inputStream;

	public Menu(Jogo jogoFrame) {
		setLayout(new GridBagLayout());
		setBackground(Color.BLACK);
		this.jogoFrame = jogoFrame;
		gbc = new GridBagConstraints();
		carregarImagem();

		bemVindoLabel = new JLabel("Welcome to Morty Battle!");
		bemVindoLabel.setFont(new Font("Impact", Font.BOLD, 35));
		bemVindoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		bemVindoLabel.setForeground(Color.BLACK);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(bemVindoLabel, gbc);

		projetoLabel = new JLabel("LP2 - Final Project");
		projetoLabel.setFont(new Font("Arial", Font.ITALIC, 20));
		projetoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		projetoLabel.setForeground(Color.BLACK);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 50, 0);
		add(projetoLabel, gbc);

		nomeLabel = new JLabel("Which Morty you are?");
		nomeLabel.setFont(new Font("Arial", Font.BOLD, 30));
		nomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		nomeLabel.setForeground(Color.BLACK);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 0, 0, 0);
		add(nomeLabel, gbc);

		nomeField = new JTextField();
		nomeField.setFont(new Font("Arial", Font.BOLD, 20));
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.ipadx = 10;
		gbc.ipady = 10;
		add(nomeField, gbc);

		serverLabel = new JLabel("Enter the server address");
		serverLabel.setFont(new Font("Arial", Font.BOLD, 30));
		serverLabel.setHorizontalAlignment(SwingConstants.CENTER);
		serverLabel.setForeground(Color.BLACK);
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 0, 0, 0);
		add(serverLabel, gbc);

		serverField = new JTextField();
		serverField.setFont(new Font("Arial", Font.BOLD, 20));
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.ipadx = 10;
		gbc.ipady = 10;
		add(serverField, gbc);

		iniciarButton = new JButton("wubba lubba dub dub!");
		iniciarButton.setFont(new Font("Impact", Font.BOLD, 25));
		iniciarButton.addActionListener(this);
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(20, 20, 20, 20);
		gbc.ipadx = 30;
		gbc.ipady = 30;
		add(iniciarButton, gbc);
	}

	// Método para carregar o Background do Menu
	protected void carregarImagem() {
		try {
			inputStream = getClass().getResourceAsStream("/menuBackground.png");
			if(inputStream!=null) img = ImageIO.read(inputStream);
			else System.out.println("Couldn't find background image.");
		} catch (IOException e) {
			System.out.println("Couldn't load background image.");
			e.printStackTrace();
		}
	}

	// Método para pintar imagem no background
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
	}

	// Método para ação do botão
	@Override
	public void actionPerformed(ActionEvent e) {
		if(nomeField.getText().isEmpty()) {
			nomeField.setBackground(Color.PINK);
		} else nomeField.setBackground(Color.WHITE);
		if(serverField.getText().isEmpty()) {
			serverField.setBackground(Color.PINK);
		} else serverField.setBackground(Color.WHITE);
		if(!nomeField.getText().isEmpty() && !serverField.getText().isEmpty()) {
			jogoFrame.iniciarJogo(nomeField.getText(), serverField.getText());
		}
	}

}
