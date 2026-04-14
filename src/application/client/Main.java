package application.client;

import java.net.*;
import java.nio.ByteBuffer;

import application.core.*;
import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.DatagramPacket;


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
    public static String username;
    public static Color color;
    public static User user;
    public static String eAction = null;

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
            if ("wasd".contains(code)) 
            {
                UdpTransmitter.getInstance().move(serverAddress, port, code.charAt(0));
            }
            if(code.equals("e"))
            {
            	if(eAction.equals("Interenet"))
            	{
            		Platform.runLater(() -> {
            		    try {
            		        NoInterenetGame game = new NoInterenetGame(color, username);
            		        Stage gameStage = new Stage();
            		        game.start(gameStage);
            		    } catch (Exception e) {
            		        e.printStackTrace();
            		    }
            		});
            	}
            	else if (eAction.equals("Closet")) {
            	    Platform.runLater(() -> {

            	        Dialog<ButtonType> dialog = new Dialog<>();
            	        dialog.setTitle("Customize User");

            	        TextField rField = new TextField(Integer.toString(user.r));
            	        TextField gField = new TextField(Integer.toString(user.g));
            	        TextField bField = new TextField(Integer.toString(user.b));
            	        TextField nameField = new TextField(username);

            	        GridPane grid = new GridPane();
            	        grid.setHgap(10);
            	        grid.setVgap(10);

            	        grid.add(new Label("Red (0-255):"), 0, 0);
            	        grid.add(rField, 1, 0);

            	        grid.add(new Label("Green (0-255):"), 0, 1);
            	        grid.add(gField, 1, 1);

            	        grid.add(new Label("Blue (0-255):"), 0, 2);
            	        grid.add(bField, 1, 2);

            	        grid.add(new Label("Username:"), 0, 3);
            	        grid.add(nameField, 1, 3);

            	        // Color preview box
            	        Rectangle preview = new Rectangle(50, 50, Color.rgb(user.r, user.g, user.b));
            	        grid.add(new Label("Preview:"), 0, 4);
            	        grid.add(preview, 1, 4);

            	        // update colors lambda
            	        Runnable updateColor = () -> {
            	            try {
            	                int r = Integer.parseInt(rField.getText());
            	                int g = Integer.parseInt(gField.getText());
            	                int b = Integer.parseInt(bField.getText());

            	                if (r >= 0 && r <= 255 &&
            	                    g >= 0 && g <= 255 &&
            	                    b >= 0 && b <= 255) {

            	                    preview.setFill(Color.rgb(r, g, b));
            	                }
            	            } catch (Exception ignored) {}
            	        };

            	        // live preview listeners
            	        rField.textProperty().addListener((obs, oldVal, newVal) -> updateColor.run());
            	        gField.textProperty().addListener((obs, oldVal, newVal) -> updateColor.run());
            	        bField.textProperty().addListener((obs, oldVal, newVal) -> updateColor.run());

            	        dialog.getDialogPane().setContent(grid);

            	        ButtonType saveBtn = new ButtonType("Save");
            	        ButtonType cancelBtn = new ButtonType("Close");

            	        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);

            	        dialog.showAndWait().ifPresent(response -> {
            	            if (response == saveBtn) {

            	                try {
            	                    // check r,g,b input
            	                    int r = Integer.parseInt(rField.getText());
            	                    int g = Integer.parseInt(gField.getText());
            	                    int b = Integer.parseInt(bField.getText());

            	                    if (r < 0 || r > 255 ||
            	                        g < 0 || g > 255 ||
            	                        b < 0 || b > 255) {
            	                        System.out.println("RGB values must be 0-255");
            	                        return;
            	                    }

            	                    // check username input
            	                    String username = nameField.getText();
            	                    if (username == null || username.isEmpty()) {
            	                        System.out.println("Username cannot be empty");
            	                        return;
            	                    }

            	                    if (username.length() > 20) {
            	                        username = username.substring(0, 20);
            	                    }

            	                    // build packet
            	                    ByteBuffer dbuf = ByteBuffer.allocate(53);
            	                    byte header = 0x05;

            	                    dbuf.put(header);
            	                    dbuf.putInt(r);
            	                    dbuf.putInt(g);
            	                    dbuf.putInt(b);

            	                    // write username
            	                    for (int i = 0; i < 20; i++) {
            	                        if (username.length() > i) {
            	                            dbuf.putChar(username.charAt(i));
            	                        } else {
            	                            dbuf.putChar('\0');
            	                        }
            	                    }
            	                    
            	                    Main.color = Color.rgb(r, g, b);
            	                    Main.username = username;	// update username in the Main method

            	                    byte[] bytes = dbuf.array();
            	                    DatagramPacket dp = new DatagramPacket(bytes, bytes.length, serverAddress, port);
            	                    udpT.send(dp);
            	                } catch (NumberFormatException ex) {
            	                    System.out.println("Invalid number input");
            	                }
            	            }
            	        });
            	    });
            	}
            }         
        });
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);

        
        Platform.runLater(() -> {
            new Thread(() -> runClient()).start();
        });
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
	    username = result.orElse("Anonymous");
	    return result.orElse("Anonymous");
	}
}