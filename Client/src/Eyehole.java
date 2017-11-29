import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Eyehole extends Rosquinha {
	//------------VARIABLES-----------------

	public Eyehole(double x, double y, double tamanho) {
		super(x, y, tamanho);
		this.setValor(-1*tamanho*Constantes.TAXA_DE_TAMANHO);
	}
	
	// Método para desenhar os eyehole na tela
	public void drawToScreen(Graphics2D g) {
		g.setColor(new Color(0, 0, 0, 0));
		Ellipse2D shape = new Ellipse2D.Double(getX()-getTamanho()/2, getY()-getTamanho()/2, getTamanho(), getTamanho());
		g.fill(shape);

		g.drawImage(JogoPanel.nonfoodImage, (int)(getX()-getTamanho()/1.5), (int)(getY()-getTamanho()/1.5), (int)(getTamanho()*1.5), (int)(getTamanho()*1.5), null);

		setLimite(shape.getBounds());
	}

}