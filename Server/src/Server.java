import java.awt.BorderLayout;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame {
	
	public static List<ClientConnection> clients;
	public static List<Player> playersInGame;
	public static List<Rosquinha> foods;
	
	private Timer spawnFood;
	private GeradorRosquinhas foodSpawner;
	
	public static final int PORT = 24680;
	
	private ServerSocket serverSocket;
	private Socket socket;
	
	ClientConnection newClient;
	Thread newThread;
	
	public static JScrollPane scrollPane;
	public static JTextArea textArea;
	public static int nextID;
	
	public static Image[] foodImage = new Image[5];
	public static Image nonfoodImage;
	public static Image playerImage;
	private InputStream inputStream;

	public Server() {
		
		super("Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400,300);
		setLayout(new BorderLayout());
		
		nextID = 0;
		
		clients = Collections.synchronizedList(new ArrayList<ClientConnection>());
		playersInGame = Collections.synchronizedList(new ArrayList<Player>());
		foods = Collections.synchronizedList(new ArrayList<Rosquinha>());
		
		textArea = new JTextArea(5,20);
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);

		setVisible(true);
		
		loadImages();
		
		foodSpawner = new GeradorRosquinhas();
		for(int i=0; i<Constantes.MAX_NUM_ROSQUINHAS;i++) {
			foodSpawner.gerarRosquinhas();
		}

		spawnFood = new Timer(Constantes.ROSQUINHAS_DELAY, foodSpawner);
		
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			textArea.append("Couldn't create server socket.\n");
			e.printStackTrace();
		}
		
		spawnFood.start();
		
		//POOL DE THREADS
		ExecutorService exec = Executors.newCachedThreadPool();
		
		while(true) {
			try {
				textArea.append("-x-x-x-x-x-x-x-x-x-x-x-x-\nWaiting for connection...\n-x-x-x-x-x-x-x-x-x-x-x-x-\n");
				socket = serverSocket.accept();
				
				textArea.append("New connection from " + socket.getInetAddress().getHostName() + ".\n");
				newClient = new ClientConnection(socket);
				clients.add(newClient);
				
				exec.execute(newClient);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		} catch (IOException e) {
			System.out.println("Couldn't load nonfood image.");
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Server();
	}
	
	public static void addFood(Rosquinha food) {
		foods.add(food);
		synchronized(clients) {
			for(Iterator<ClientConnection> iterator = clients.iterator(); iterator.hasNext();) {
				ClientConnection client = iterator.next();
				client.sendAlert(new Alert("FOOD_ADDED", food));
			}
		}
	}

}

class ClientConnection implements Runnable {
	
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private String name;
	
	private boolean gameIsRunning = true;
	
	private Alert receivedAlert;
	
	public ClientConnection(Socket socket) {
		this.socket = socket;
		setUpStreams();
	}
	
	public void setUpStreams() {
		try {
			Server.textArea.append("Setting up streams for new client...\n");
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			Server.textArea.append("Finished setting up streams.\n");
		} catch (IOException e) {
			Server.textArea.append("ERROR: Setting up streams failed.\n");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(gameIsRunning) {
			try {
				receivedAlert = (Alert) inputStream.readObject();
				
				if(receivedAlert.getType().equals("JOIN")) {
					Player joinedPlayer = (Player) receivedAlert.getData();
					name = joinedPlayer.getNome();
					
					sendAlert(new Alert("FOODS", Server.foods));
					sendAlert(new Alert("OPPONENTS", Server.playersInGame));
					
					Server.playersInGame.add(joinedPlayer);
					
					sendToAllExceptSelf(receivedAlert);
				}
				else if(receivedAlert.getType().equals("CHANGE")) {
					Player playerChanged = (Player) receivedAlert.getData();
					synchronized(Server.playersInGame) {
						for(Iterator<Player> iterator = Server.playersInGame.iterator(); iterator.hasNext();) {
							Player player = iterator.next();
							if(player.getNome().equals(playerChanged.getNome())) {
								iterator.remove();
								Server.playersInGame.add(playerChanged);
								sendToAllExceptSelf(receivedAlert);
								break;
							}
						}
					}
				}
				else if(receivedAlert.getType().equals("FOOD_EATEN")) {
					Rosquinha foodEaten = (Rosquinha) receivedAlert.getData();
					synchronized(Server.foods) {
						for(Iterator<Rosquinha> iterator = Server.foods.iterator(); iterator.hasNext();) {
							Rosquinha food = iterator.next();
							if(food.getX() == foodEaten.getX() && food.getY() == foodEaten.getY()) {
								iterator.remove();
								sendToAllExceptSelf(receivedAlert);
								break;
							}
						}
					}
				}
				else if(receivedAlert.getType().equals("OPPONENT_EATEN")) {
					Player opponentEaten = (Player) receivedAlert.getData();
					synchronized(Server.playersInGame) {
						for(Iterator<Player> iterator = Server.playersInGame.iterator(); iterator.hasNext();) {
							Player player = iterator.next();
							if(player.getNome().equals(opponentEaten.getNome())) {
								iterator.remove();
								break;
							}
						}
					}
					synchronized(Server.clients) {
						for(Iterator<ClientConnection> iterator = Server.clients.iterator(); iterator.hasNext();) {
							ClientConnection client = iterator.next();
							if(client.name.equals(opponentEaten.getNome())) {
								client.sendAlert(new Alert("GAME_OVER", null));
								client.gameOver();
								iterator.remove();
							} else {
								client.sendAlert(receivedAlert);
							}
						}
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				Server.textArea.append("Terminating connection from " + name + "...\n");
				Player playerDisconnected = null;
				synchronized(Server.playersInGame) {
					for(Iterator<Player> iterator = Server.playersInGame.iterator(); iterator.hasNext();) {
						Player player = iterator.next();
						if(player.getNome().equals(name)) {
							playerDisconnected = player;
							iterator.remove();
							break;
						}
					}
				}
				synchronized(Server.clients) {
					for(Iterator<ClientConnection> iterator = Server.clients.iterator(); iterator.hasNext();) {
						ClientConnection client = iterator.next();
						if(client == this) {
							client.gameOver();
							iterator.remove();
						} else {
							if(playerDisconnected != null) client.sendAlert(new Alert("OPPONENT_EATEN", playerDisconnected));
						}
					}
				}
				try {
					outputStream.close();
					inputStream.close();
					socket.close();
				} catch (IOException e1) {
					Server.textArea.append("Error closing connection.");
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void gameOver() {
		gameIsRunning = false;
	}
	
	public synchronized void sendAlert(Alert alertToSend) {
		try {
			outputStream.reset();
			outputStream.writeObject(alertToSend);
			outputStream.flush();
		} catch (IOException e) {
			Server.textArea.append("ERROR: Couldn't send object.\n");
			e.printStackTrace();
		}
	}
	
	public void sendToAllExceptSelf(Alert alertToSend) {
		for(ClientConnection client: Server.clients) {
			if(client!=this)
				client.sendAlert(alertToSend);
		}
	}
}
