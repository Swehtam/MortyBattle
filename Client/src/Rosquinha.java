import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.concurrent.*;

public class Rosquinha extends Entidade {

	private double valor;

	// Valor aleatório para escolher a imagem  no array
	private int i = ThreadLocalRandom.current().nextInt(0, 5);

	public Rosquinha(double x, double y, double tamanho) {
		super(x, y, tamanho);
		this.setValor(tamanho * Constantes.TAXA_DE_TAMANHO);
	}

	// Método para desenhar as rosquinhas na tela
	public void drawToScreen(Graphics2D g) {
		g.setColor(new Color(0, 0, 0, 0));
		Ellipse2D shape = new Ellipse2D.Double(getX()-getTamanho()/2, getY()-getTamanho()/2, getTamanho(), getTamanho());
		g.fill(shape);

		g.drawImage(JogoPanel.foodImage[i], (int)(getX()-getTamanho()/1.5), (int)(getY()-getTamanho()/1.5), (int)(getTamanho()*2), (int)(getTamanho()*2), null);

		setLimite(shape.getBounds());	
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

}
