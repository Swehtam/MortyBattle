import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public class Player extends Entidade {
	
	private double velocidade;
	private String nome;
	private Random rand;
	private Color c;

	public Player(double tamanho, double x, double y, double velocidade, String nome) {
		super(x, y, tamanho);
		this.velocidade = velocidade;
		this.nome = nome;
		rand = new Random();
		c = corPlayer();
	}

	public double getVelocidade() {
		return velocidade;
	}

	/* Método que coloca a velocidade do player, se ela for menor do que o atributo velocidade mínima da Classe Constantes,
	 * então coloca o atributo como velocidade do player, se não deixa a velocidade normal mesmo*/
	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade < Constantes.VELOCIDADE_MIN ? Constantes.VELOCIDADE_MIN : velocidade;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/* Método que coloca o tamanho do player, se ele for menor do que o atributo tamanho mínimo da Classe Constantes
	 * então coloca o atributo como tamanho do player, se não deixa o tamanho normal mesmo*/
	public void setTamanho(double tamanho) {
		this.tamanho = tamanho < Constantes.VELOCIDADE_MIN ? Constantes.VELOCIDADE_MIN : tamanho;
	}
	
	// Método para gerar um cor aleatória para o player
	private Color corPlayer() {
		int r = rand.nextInt(256);
		int g = rand.nextInt(256);
		int b = rand.nextInt(256);
		return new Color(r, g, b);
		
	}
	
	public void drawToScreen(Graphics2D g) {	
		g.setColor(c);
		Ellipse2D shape = new Ellipse2D.Double(getX()-getTamanho()/2, getY()-getTamanho()/2, getTamanho(), getTamanho());
		g.fill(shape);
		
		setLimite(shape.getBounds());
		
		g.drawImage(JogoPanel.playerImage, (int)(getX()-getTamanho()/2), (int)(getY()-getTamanho()/2), (int)(getTamanho()), (int)(getTamanho()), null);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		int width = g.getFontMetrics().stringWidth(getNome());
		g.drawString(getNome(), (int)(getX()-width/2), (int)(getY() + (getTamanho()/2) + 10));
	}
	
	//Método para calculcar se houve um ponto de interseção entre o player e o oponent/objeto
	public boolean intersecao(Entidade e) {
		return Math.sqrt(Math.pow(Math.abs(e.getX()-getX()),2) + Math.pow(Math.abs(e.getY()-getY()),2)) <= getLimite().getWidth()/2 + e.getLimite().getWidth()/2;
	}
	
}
