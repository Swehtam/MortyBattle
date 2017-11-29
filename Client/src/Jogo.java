import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.InputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

public class Jogo extends JFrame {

	private Menu menuPanel;
	private JogoPanel jogoPanel;

	// Classe JogoPanel
	public void iniciarJogo(String nome, String IP) {
		remove(menuPanel);
		jogoPanel = new JogoPanel(nome, IP);
		jogoPanel.setBackground(Color.WHITE);
		add(jogoPanel, BorderLayout.CENTER);
		jogoPanel.setFocusable(true);
		jogoPanel.requestFocus();
		playMusica();
	}

	// Método para fazer a música iniciar quando entrar no jogo
	public void playMusica() {
		try {
			InputStream file = getClass().getResourceAsStream("/music.wav");
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
			clip.loop(-1);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// Classe Menu
	public void mostrarMenu() {
		setTitle("Morty Battle - LP2 Project");
		setSize(Constantes.TAMANHO_DA_TELA, Constantes.TAMANHO_DA_TELA);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		menuPanel = new Menu(this);
		add(menuPanel, BorderLayout.CENTER);

		setVisible(true);
	}

	public static void main(String[] args) {
		new Jogo().mostrarMenu();
	}
}
