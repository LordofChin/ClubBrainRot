package application.client;

import java.net.*;


import java.util.List;
import java.util.Scanner;


import application.core.*;
import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.Optional;


public class Main extends Application{
	private Map map = Map.getInstance();
	private StackPane mainPane = new StackPane();
	private Chat chat = Chat.getInstance();
	private static String serverIP;
	private static Main instance;
	public static InetAddress serverAddress;
    public static int port = 3478;
    private static DatagramSocket socket;
    private static UdpTransmitter udpT;

	public static Main getInstance() 
	{
		if (instance == null) 
		{
			instance = new Main();
		}
		return instance;
	}

	public static void main(String[] args) {
		serverIP = args[0];
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		udpT = UdpTransmitter.getInstance(socket);
	    Application.launch(args);
	}

    @Override
    public void start(Stage stage) {
    	try {
			serverAddress = InetAddress.getByName(serverIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	mainPane.getChildren().addAll(map, chat.getGridPane());
    	StackPane.setAlignment(map, Pos.CENTER);
    	StackPane.setAlignment(chat.getGridPane(), Pos.TOP_LEFT);
    	
        Scene scene = new Scene(mainPane, 800, 600);
	    udpT.signOn(serverAddress, port, showUsernameDialog());
        
        scene.setOnKeyPressed(event -> {
            String code = event.getCode().getChar().toLowerCase();
            if ("wasd".contains(code)) {
                UdpTransmitter.getInstance().move(serverAddress, port, code.charAt(0));
            }
        });

        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> runClient()).start();
    }
	
	private void runClient() {
	    UdpReceiver udpR = new UdpReceiver(socket, new ClientUdpHandler(socket));
	    udpR.start();
	}
	
	public static String showUsernameDialog() {
	    TextInputDialog dialog = new TextInputDialog("John Doe"); 
	    dialog.setTitle("Login");
	    dialog.setHeaderText("Welcome to the Game!");
	    dialog.setContentText("Please enter your username:");

	    Optional<String> result = dialog.showAndWait();
	    
	    return result.orElse("Anonymous");
	}
}