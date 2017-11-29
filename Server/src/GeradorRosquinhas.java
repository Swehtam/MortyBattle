import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GeradorRosquinhas implements ActionListener {
	
	private Random rand;
	
	public GeradorRosquinhas() {
		rand = new Random();
	}
	
	// Método para retornar posição das rosquinhas 
	private Point aleatorioPoint() {
		return new Point(rand.nextInt(Constantes.TAMANHO_DO_JOGO+1), rand.nextInt(Constantes.TAMANHO_DO_JOGO+1));
	}
	
	// Método para determinar se vai gerar uma rosquinha ou um eyehole, eyeholes tem 5% de chance de serem gerados 
	public void gerarRosquinhas() {
		if(Server.foods.size() < Constantes.MAX_NUM_ROSQUINHAS) {
			if (rand.nextDouble()*2 < 1.9)
				Server.addFood(new Rosquinha(aleatorioPoint().getX(), aleatorioPoint().getY(), rand.nextDouble()*10+8));
			else
				Server.addFood(new Eyehole(aleatorioPoint().getX(), aleatorioPoint().getY(), rand.nextDouble()*10+20));
		}
	}

	// Método que usa o delay para gerar mais rosquinhas/eyeholes depois de serem devorados
	@Override
	public void actionPerformed(ActionEvent e) {
		gerarRosquinhas();
	}
	
}
