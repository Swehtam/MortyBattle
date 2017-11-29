import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import java.io.IOException;
import java.io.InputStream;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class JogoPanel extends JPanel implements ActionListener, MouseListener, KeyListener {

	private long tStart;

	private Player player;
	private List<Player> opponents;
	private List<Rosquinha> foods;

	private double viewX;
	private double viewY;

	private Timer gameTimer;

	private Multiplayer network;
	private Thread receiver;
	private Alert receivedAlert;

	private JPanel alignmentPanel;
	private DefaultListModel<String> listModel;
	private JList<String> leaderboard;

	private boolean mouseIsPressed;
	private int keyPressed;
	private String name;

	private Random rand;
	private Point spawnPoint;

	private JLabel warningLabel;

	private final String HOST;

	private Image backgroundImage;
	public static Image[] foodImage = new Image[5];  
	public static Image nonfoodImage;
	public static Image playerImage;
	private InputStream inputStream;

	public JogoPanel(String name, final String HOST) {
		this.name = name;
		this.HOST = HOST;

		setLayout(new BorderLayout());		

		loadImages();

		rand = new Random();

		gameTimer = new Timer(Constantes.JOGO_DELAY, this);

		addMouseListener(this);
		addKeyListener(this);

		setUpGame();
	}

	private void loadImages(){
		try {
			inputStream = getClass().getResourceAsStream("/morty.png");
			if(inputStream!=null) playerImage = ImageIO.read(inputStream);
			else System.out.println("Couldn't find player image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/eyehole.png");
			if(inputStream!=null) nonfoodImage = ImageIO.read(inputStream);
			else System.out.println("Couldn't find nonfood image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/blue.png");
			if(inputStream!=null) foodImage[0] = ImageIO.read(inputStream);
			else System.out.println("Couldn't find blue food image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/green.png");
			if(inputStream!=null) foodImage[1] = ImageIO.read(inputStream);
			else System.out.println("Couldn't find green food image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/pink.png");
			if(inputStream!=null) foodImage[2] = ImageIO.read(inputStream);
			else System.out.println("Couldn't find pink food image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/purple.png");
			if(inputStream!=null) foodImage[3] = ImageIO.read(inputStream);
			else System.out.println("Couldn't find purple food image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/yellow.png");
			if(inputStream!=null) foodImage[4] = ImageIO.read(inputStream);
			else System.out.println("Couldn't find yellow food image.");
			inputStream = null;

			inputStream = getClass().getResourceAsStream("/background.png");
			if(inputStream!=null) backgroundImage = ImageIO.read(inputStream);
			else System.out.println("Couldn't find background image.");
			inputStream = null;
		} catch (IOException e) {
			System.out.println("Couldn't load some image.");
			e.printStackTrace();
		}
	}

	private void setUpGame() {
		mouseIsPressed = false;
		keyPressed = 0;
		spawnPoint = randomPoint();

		foods = Collections.synchronizedList(new ArrayList<Rosquinha>());
		opponents = Collections.synchronizedList(new ArrayList<Player>());
		player = new Player(Constantes.TAMANHO_INICIAL, spawnPoint.getX(), spawnPoint.getY(), Constantes.VELOCIDADE_INICIAL, name);
		viewX = spawnPoint.getX() - Constantes.TAMANHO_DA_TELA/2;
		viewY = spawnPoint.getY() - Constantes.TAMANHO_DA_TELA/2;

		network = new Multiplayer(HOST);

		network.writeAlert(new Alert("JOIN", player));

		receiver = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					receivedAlert = network.readAlert();

					if(receivedAlert.getType().equals("FOODS")) {
						foods = (List<Rosquinha>) receivedAlert.getData();
					} 
					else if(receivedAlert.getType().equals("OPPONENTS")) {
						List<Player> opponentList = (List<Player>) receivedAlert.getData();
						opponents = opponentList;
					} 
					else if(receivedAlert.getType().equals("JOIN")) {
						opponents.add((Player) receivedAlert.getData());
					} 
					else if(receivedAlert.getType().equals("CHANGE")) {
						Player playerChanged = (Player) receivedAlert.getData();
						synchronized(opponents) {
							for(Iterator<Player> iterator = opponents.iterator(); iterator.hasNext();) {
								Player opponent = iterator.next();
								if(opponent.getNome().equals(playerChanged.getNome())) {
									iterator.remove();
									opponents.add(playerChanged);
									break;
								}
							}
						}
					} 
					else if(receivedAlert.getType().equals("FOOD_EATEN")) {
						Rosquinha foodEaten = (Rosquinha) receivedAlert.getData();
						synchronized(foods) {
							for(Iterator<Rosquinha> iterator = foods.iterator(); iterator.hasNext();) {
								Rosquinha food = iterator.next();
								if(food.getX() == foodEaten.getX() && food.getY() == foodEaten.getY()) {
									iterator.remove();
									break;
								}
							}
						}
					}
					else if(receivedAlert.getType().equals("FOOD_ADDED")) {
						Rosquinha foodAdded = (Rosquinha) receivedAlert.getData();
						foods.add(foodAdded);
					} 
					else if (receivedAlert.getType().equals("OPPONENT_EATEN")) {
						Player opponentEaten = (Player) receivedAlert.getData();
						synchronized(opponents) {
							for(Iterator<Player> iterator = opponents.iterator(); iterator.hasNext();) {
								Player opponent = iterator.next();
								if(opponent.getNome().equals(opponentEaten.getNome())) {
									iterator.remove();
									break;
								}
							}
						}
					}			
					else if(receivedAlert.getType().equals("GAME_OVER")) {
						gameOver(false);
						break;
					}
				}
			}
		});
		receiver.start();

		alignmentPanel = new JPanel();
		alignmentPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		alignmentPanel.setOpaque(false);
		add(alignmentPanel, BorderLayout.NORTH);
		listModel = new DefaultListModel<String>();
		leaderboard = new JList<>(listModel);
		leaderboard.setFont(new Font("Arial", Font.PLAIN, 18));
		leaderboard.setBackground(Color.LIGHT_GRAY);
		alignmentPanel.add(leaderboard, BorderLayout.NORTH);

		warningLabel = new JLabel("Get back into the arena!");
		warningLabel.setFont(new Font("Arial", Font.BOLD, 50));
		warningLabel.setForeground(Color.RED);
		warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
		warningLabel.setVerticalAlignment(SwingConstants.CENTER);

		gameTimer.start();

		tStart = System.currentTimeMillis();
	}

	public void gameOver(boolean won) {
		gameTimer.stop();
		network.closeConnection();
		remove(alignmentPanel);
		String[] options = {"Oh no!"};
		JOptionPane.showOptionDialog(this, "You lost!\nYou were eaten in " + (int)(System.currentTimeMillis() - tStart)/1000 + " s.", "GAME OVER.", JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		closeGame();
	}

	public void closeGame() {
		JOptionPane.showMessageDialog(this, "Thanks for playing!");
		System.exit(0);
	}

	private Color randomColor() {
		int r = rand.nextInt(256);
		int g = rand.nextInt(256);
		int b = rand.nextInt(256);
		return new Color(r,g,b);
	}

	private Point randomPoint() {
		return new Point(rand.nextInt(Constantes.TAMANHO_DO_JOGO+1), rand.nextInt(Constantes.TAMANHO_DO_JOGO+1));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(-1*viewX, -1*viewY);

		/*---DRAW BOUNDARY---*/
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(5));
		g2.drawRect(0, 0, Constantes.TAMANHO_DO_JOGO, Constantes.TAMANHO_DO_JOGO);

		/*---DRAW BACKGROUND IMAGE---*/
		if(backgroundImage!=null)
			g2.drawImage(backgroundImage, 0, 0, Constantes.TAMANHO_DO_JOGO, Constantes.TAMANHO_DO_JOGO, 0, 0, backgroundImage.getWidth(null), backgroundImage.getHeight(null), null);

		/*---DRAW OPPONENTS AND CHECK COLLISION---*/
		synchronized(opponents) {
			for(Iterator<Player> iterator = opponents.iterator(); iterator.hasNext();) {
				Player opponent = iterator.next();
				opponent.drawToScreen(g2);
				if(player.getTamanho() > opponent.getTamanho() && player.intersecao(opponent)) {
					iterator.remove();
					network.writeAlert(new Alert("OPPONENT_EATEN", opponent));

					player.setTamanho(player.getTamanho() + opponent.getTamanho()/2);
					player.setVelocidade(player.getVelocidade()/1.005);

					network.writeAlert(new Alert("CHANGE", player));
				}
			}
		}

		/*---DRAW FOODS AND CHECK COLLISION---*/
		synchronized(foods) {
			for(Iterator<Rosquinha> iterator = foods.iterator(); iterator.hasNext();) {
				Rosquinha food = iterator.next();
				if(isOnScreen(food)) {
					food.drawToScreen(g2);
					if(player.intersecao(food)) {
						iterator.remove();
						network.writeAlert(new Alert("FOOD_EATEN", food));

						if(!(food instanceof Eyehole)) {
							player.setTamanho(player.getTamanho() + food.getValor()/2);
							player.setVelocidade(player.getVelocidade()/1.002);
						} else {
							player.setTamanho(player.getTamanho() + food.getValor()*4);
						}

						network.writeAlert(new Alert("CHANGE", player));						
					}
				}
			}
		}

		// Desenha o player
		player.drawToScreen(g2);

		g2.translate(viewX, viewY);
	}

	private boolean isOnScreen(Entidade e) {
		return e.getX()+e.getLimite().getWidth()/2 > viewX && e.getX()-e.getLimite().getWidth()/2 < viewX + getWidth() && e.getY()+e.getLimite().getHeight()/2 > viewY && e.getY()-e.getLimite().getHeight()/2 < viewY+getHeight();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(keyPressed != 0) {
			switch(keyPressed) {
			case 1:
				moveTowards(player.getX(), player.getY()-1);
				break;
			case 2:
				moveTowards(player.getX()+1, player.getY());
				break;
			case 3:
				moveTowards(player.getX(), player.getY()+1);
				break;
			case 4:
				moveTowards(player.getX()-1, player.getY());
				break;
			}

			network.writeAlert(new Alert("CHANGE", player));
		} else if(mouseIsPressed) {
			Point2D mousePosition = getMousePosition();
			if(mousePosition == null) return;

			moveTowards(mousePosition.getX() + viewX, mousePosition.getY() + viewY);

			network.writeAlert(new Alert("CHANGE", player));
		}

		/*---SORT LEADERBOARD---*/
		listModel.clear();
		List<Player> players = new ArrayList<Player>();
		players.add(player);
		synchronized(opponents){
			for(Iterator<Player> iterator = opponents.iterator(); iterator.hasNext();){
				Player opponent = iterator.next();
				players.add(opponent);
			}
		}
		Collections.sort(players, new Comparator<Player>() {
			public int compare(Player c1, Player c2) {
				if(c2.getTamanho() > c1.getTamanho()) return 1;
				else if(c2.getTamanho() == c1.getTamanho()) return 0;
				else return -1;
			}
		});
		for(Player player: players) {
			listModel.addElement(player.getNome() + " : " + BigDecimal.valueOf(player.getTamanho()).setScale(1, RoundingMode.HALF_UP).doubleValue());
		}

		repaint();
	}

	public void moveTowards(double x, double y) {
		double dx = x - player.getX();
		double dy = y - player.getY();
		double distance = Math.sqrt(dx*dx + dy*dy);

		double movementX = player.getVelocidade()/10*dx/distance;
		double movementY = player.getVelocidade()/10*dy/distance;

		player.setX(player.getX() + movementX);
		player.setY(player.getY() + movementY);

		viewX += movementX;
		viewY += movementY;

		if(player.getX()-player.getTamanho()/2 < 0) { player.setX(player.getTamanho()/2); viewX = player.getTamanho()/2 - Constantes.TAMANHO_DA_TELA/2; }
		if(player.getX()+player.getTamanho()/2 > Constantes.TAMANHO_DO_JOGO) { player.setX(Constantes.TAMANHO_DO_JOGO-player.getTamanho()/2); viewX = Constantes.TAMANHO_DO_JOGO-player.getTamanho()/2 - Constantes.TAMANHO_DA_TELA/2; }
		if(player.getY()-player.getTamanho()/2 < 0) { player.setY(player.getTamanho()/2); viewY = player.getTamanho()/2 - Constantes.TAMANHO_DA_TELA/2; }
		if(player.getY()+player.getTamanho()/2 > Constantes.TAMANHO_DO_JOGO) { player.setY(Constantes.TAMANHO_DO_JOGO-player.getTamanho()/2); viewY = Constantes.TAMANHO_DO_JOGO-player.getTamanho()/2 - Constantes.TAMANHO_DA_TELA/2; }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseIsPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseIsPressed = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		int c = e.getKeyCode();
		if(c == KeyEvent.VK_LEFT){
			keyPressed = 4;
		}
		if(c == KeyEvent.VK_UP){
			keyPressed = 1;
		}
		if(c == KeyEvent.VK_RIGHT){
			keyPressed = 2;
		}
		if(c == KeyEvent.VK_DOWN){
			keyPressed = 3;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyPressed = 0;
	}
}
