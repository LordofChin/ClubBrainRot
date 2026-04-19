package application.client;

import java.net.*;
import java.nio.ByteBuffer;

import application.core.*;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    // color sliders and labels
	Slider rSlide;
	Slider gSlide;
	Slider bSlide;
	Label lblRedVal;
	Label lblGreenVal;
	Label lblBlueVal;
    Rectangle preview;


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
            	    	preview = new Rectangle(50, 50, Color.rgb(user.r, user.g, user.b));
            	    	rSlide = new Slider(0,255,user.r);
            	    	gSlide = new Slider(0,255,user.g);
            	    	bSlide = new Slider(0,255,user.b);
            	    	lblRedVal = new Label(String.format("%d",user.r));
            	    	lblGreenVal = new Label(String.format("%d",user.g));
            	    	lblBlueVal = new Label(String.format("%d",user.b));
            	        Dialog<ButtonType> dialog = new Dialog<>();
            	        dialog.setTitle("Customize User");
            			
            			// name field
            	        TextField nameField = new TextField(username);

            	        // grid
            	        GridPane grid = new GridPane();
            	        grid.setHgap(10);
            	        grid.setVgap(10);
            			grid.addRow(0, new Label("Username: "), nameField);
            			grid.addRow(1, new Label("Red: "), rSlide, lblRedVal);
            			grid.addRow(2, new Label("Green: "), gSlide, lblGreenVal);
            			grid.addRow(3, new Label("Blue: "), bSlide, lblBlueVal);
            	        // color preview box with hexcode
            	        Label hexCode = new Label("#000000");
            			
            			VBox root = new VBox(15, preview, hexCode, grid);
            			root.setAlignment(Pos.CENTER);
            			root.setPadding(new Insets(20));

            	        // update colors lambda
            	        Runnable updateColor = () -> {
            	            try {
            	            	int r = (int)rSlide.getValue();
            	            	int g = (int)gSlide.getValue();
            	            	int b = (int)bSlide.getValue();
            	    			lblRedVal.setText(String.valueOf(r));
            	    			lblGreenVal.setText(String.valueOf(g));
            	    			lblBlueVal.setText(String.valueOf(b));
            	    			updateColor();
            	    			String[] colorStringList = {
            	    					Integer.toHexString(r),
            	    					Integer.toHexString(g),
            	    					Integer.toHexString(b)
            	    			};
            	    			for(int i = 0; i < 3; i++) {
            	    				if(colorStringList[i].length() < 2) {
            	    					colorStringList[i] = "0" + colorStringList[i];
            	    				}
            	    			}
            	    			
            	    			hexCode.setText(String.format("#%s%s%s", colorStringList[0], colorStringList[1], colorStringList[2]));

            	                preview.setFill(Color.rgb(r, g, b));
            	            } catch (Exception ignored) {}
            	    	};

            	        // live preview listeners
            	        rSlide.valueProperty().addListener((obs, oldVal, newVal) -> updateColor.run());
            	        gSlide.valueProperty().addListener((obs, oldVal, newVal) -> updateColor.run());
            	        bSlide.valueProperty().addListener((obs, oldVal, newVal) -> updateColor.run());
            	        

            	        dialog.getDialogPane().setContent(root);

            	        ButtonType saveBtn = new ButtonType("Save");
            	        ButtonType cancelBtn = new ButtonType("Close");

            	        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);
            	        
            	        dialog.showAndWait().ifPresent(response -> {
            	            if (response == saveBtn) {

            	              	// collect values
            	                int r = (int)rSlide.getValue();
            	                int g = (int)gSlide.getValue();
            	                int b = (int)bSlide.getValue();

            	                // check username input
            	                String username = nameField.getText();
            	                if (username == null || username.isEmpty()) {
            	                    System.out.println("Username cannot be empty");
            	                    return;
            	                }

            	                // only 20 bytes reserved for username
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
            	            }
            	        });
            	    });
            	}
            }       
        });
       
        Platform.runLater(() -> {
            new Thread(() -> runClient()).start();
        });
    
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
    }
    
	private void updateColor() {
		int red = (int)rSlide.getValue(), 
			green = (int)gSlide.getValue(), 
			blue = (int)bSlide.getValue();
		Color color = Color.rgb(red, green, blue);
		preview.setFill(color);
		rSlide.lookup(".track").setStyle(String.format("-fx-background-color: linear-gradient(to right, rgb(0, %d, %d), rgb(255, %d, %d));", green, blue, green, blue));
		gSlide.lookup(".track").setStyle(String.format("-fx-background-color: linear-gradient(to right, rgb(%d, 0, %d), rgb(%d, 255, %d));", red, blue, red, blue));
		bSlide.lookup(".track").setStyle(String.format("-fx-background-color: linear-gradient(to right, rgb(%d, %d, 0), rgb(%d, %d, 255));", red, green, red, green));
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