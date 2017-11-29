import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.Serializable;

abstract public class Entidade implements Serializable {
	
	protected double x, y;
	protected double tamanho;
	protected Rectangle limite;
	
	public Entidade(double x, double y, double tamanho) {
		this.x = x;
		this.y = y;
		this.tamanho = tamanho;
		limite = new Rectangle(0, 0, 0, 0);
	}

	public Rectangle getLimite() {
		return limite;
	}
	
	public void setLimite(Rectangle limite) {
		this.limite = limite;
	}
	
	public double getTamanho() {
		return tamanho;
	}

	public void setTamanho(double tamanho) {
		this.tamanho = tamanho;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	abstract public void drawToScreen(Graphics2D g);
	
}
