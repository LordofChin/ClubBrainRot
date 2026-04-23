package application.client;

import java.net.*;
import java.nio.ByteBuffer;

import application.core.*;
import javafx.animation.AnimationTimer;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.DatagramPacket;


import java.util.Optional;


public class Main extends Application{
	// application state
	private static Main instance;
	private StackPane mainPane = new StackPane();

	// network connections state
	private static String serverIP;
	protected static InetAddress serverAddress;
    protected static int port = 3478;
    private static DatagramSocket socket;
    private static UdpTransmitter udpT;
    
    // game and user states
    protected static User user;
    protected static String eAction = null;
	private Map map = Map.getInstance();
	private Chat chat = Chat.getInstance();
	protected static Stage stage;
	private boolean wPressed, aPressed, sPressed, dPressed;
	
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

	public static void main(String[] args) 
	{
		serverIP = args[0];
		try {
			socket = new DatagramSocket();			// get any ephemeral port
		} catch (SocketException e) {
			System.out.println("Was not able to establish a socket on any ephemeral port. \nPlease consult with you computer's amdinistrator.\n" + e);				
		}
		udpT = UdpTransmitter.getInstance(socket);	// instantiate UDPTransmitter for all client-side classes
	    Application.launch(args);					// start the application
	}

    @Override
    public void start(Stage stage) {
    	this.stage = stage;
    	try {
			serverAddress = InetAddress.getByName(serverIP);	// get Inet address from the provided server IP
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	
    	// add map and  
    	mainPane.getChildren().addAll(map, chat.getGridPane());
    	StackPane.setAlignment(map, Pos.CENTER);
    	StackPane.setAlignment(chat.getGridPane(), Pos.TOP_LEFT);

    	
        Scene scene = new Scene(mainPane, 800, 600);
	    udpT.signOn(serverAddress, port, showUsernameDialog(),stage);
        
        scene.setOnKeyPressed(event -> {
            String code = event.getCode().getChar().toLowerCase();
            if ("wasd".contains(code)) 
            {
                UdpTransmitter.getInstance().move(serverAddress, port, code.charAt(0));
            }
            if(code.equals("e"))
            {
            	// starts no interenet game 
            	if(eAction.equals("Interenet"))
            	{
            		Platform.runLater(() -> {
            		        new NoInterenetGame(stage);
            		});
            	}
            	

            	if(eAction.equals("Fishing"))
            	{
            		Platform.runLater(() -> {
            			new FishingGame(stage);
            		});
            	}
            	
            	// enters and handles closet
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
            	        dialog.initOwner(stage);

            			
            			// name field
            	        TextField nameField = new TextField(user.getUsername());

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
            	        
            	        // 
            	        dialog.getDialogPane().setContent(root);

            	        // create and add buttons
            	        ButtonType saveBtn = new ButtonType("Save");
            	        ButtonType cancelBtn = new ButtonType("Close");
            	        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);
            	        
            	        // collect form data and transmit to the server upon save
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
                                ByteBuffer dbuf = ByteBuffer.allocate(53);	// use a byte buffer
            	                byte header = 0x05;							// add header of 5 for user updates

            	                //put primitives into the bytebuffer
            	                dbuf.put(header);
            	                dbuf.putInt(r);
            	                dbuf.putInt(g);
            	                dbuf.putInt(b);
            	                
            	                // write username
            	                for (int i = 0; i < 20; i++) {
            	                 	if (username.length() > i) {
            	                        dbuf.putChar(username.charAt(i));
            	                    } else {
            	                    	dbuf.putChar('\0');					// use null 
            	                    }
            	                }
            	                
            	                // update user in the Main method
            	                user.setR(r);
            	                user.setG(g);
            	                user.setB(b);
            	                user.setUsername(username);	

            	                //send the byte array of data to the server
            	                byte[] bytes = dbuf.array();
            	                DatagramPacket dp = new DatagramPacket(bytes, bytes.length, serverAddress, port);
            	                udpT.send(dp);
            	            }
            	        });
            	    });
            	}
            }       
        });
        

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            
            if (code == KeyCode.W || code == KeyCode.UP)    wPressed = true;
            if (code == KeyCode.A || code == KeyCode.LEFT)  aPressed = true;
            if (code == KeyCode.S || code == KeyCode.DOWN)  sPressed = true;
            if (code == KeyCode.D || code == KeyCode.RIGHT) dPressed = true;

            // Handle "E" actions
            if (code == KeyCode.E) {
                handleEAction(); 
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            KeyCode code = event.getCode();
            
            if (code == KeyCode.W || code == KeyCode.UP)    wPressed = false;
            if (code == KeyCode.A || code == KeyCode.LEFT)  aPressed = false;
            if (code == KeyCode.S || code == KeyCode.DOWN)  sPressed = false;
            if (code == KeyCode.D || code == KeyCode.RIGHT) dPressed = false;

            // Handle "E" actions
            if (code == KeyCode.E) {
                handleEAction(); 
            }
        });

        // 3. Start a small AnimationTimer to send the UDP packets smoothly
        AnimationTimer movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (wPressed) UdpTransmitter.getInstance().move(serverAddress, port, 'w');
                if (aPressed) UdpTransmitter.getInstance().move(serverAddress, port, 'a');
                if (sPressed) UdpTransmitter.getInstance().move(serverAddress, port, 's');
                if (dPressed) UdpTransmitter.getInstance().move(serverAddress, port, 'd');
            }
        };
        movementTimer.start();
       
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
	    UdpReceiver udpR = new UdpReceiver(socket, new ClientUdpHandler());
	    udpR.start();
	}
	
	public static String showUsernameDialog() {
	    TextInputDialog dialog = new TextInputDialog("John Doe"); 
	    dialog.setTitle("Login");
	    dialog.setHeaderText("Welcome to the Game!");
	    dialog.setContentText("Please enter your username:");

	    Optional<String> result = dialog.showAndWait();
		user = new User(result.orElse("Anonymous"));							// instatiate user
	    return user.getUsername();
	}
	
	public void handleEAction() 
	{
        	// starts no interenet game 
        	if(eAction.equals("Interenet"))
        	{
        		Platform.runLater(() -> {
        		        new NoInterenetGame(stage);
        		});
        	}
        	

        	if(eAction.equals("Fishing"))
        	{
        		Platform.runLater(() -> {
        			new FishingGame(stage);
        		});
        	}

        	if(eAction.equals("Mole"))
        	{
        		Platform.runLater(() -> {
        			new WhackAMole(stage);
        		});
        	}
        	if(eAction.equals("Dodge"))
        	{
        		Platform.runLater(() -> {
        			DodgeBrainRot game = new DodgeBrainRot(stage);
        			game.showAndWait();
        		});
        	}
        	
        	// enters and handles closet
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
        	        dialog.initOwner(stage);

        			
        			// name field
        	        TextField nameField = new TextField(user.getUsername());

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
        	        
        	        // 
        	        dialog.getDialogPane().setContent(root);

        	        // create and add buttons
        	        ButtonType saveBtn = new ButtonType("Save");
        	        ButtonType cancelBtn = new ButtonType("Close");
        	        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, cancelBtn);
        	        
        	        // collect form data and transmit to the server upon save
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
                            ByteBuffer dbuf = ByteBuffer.allocate(53);	// use a byte buffer
        	                byte header = 0x05;							// add header of 5 for user updates

        	                //put primitives into the bytebuffer
        	                dbuf.put(header);
        	                dbuf.putInt(r);
        	                dbuf.putInt(g);
        	                dbuf.putInt(b);
        	                
        	                // write username
        	                for (int i = 0; i < 20; i++) {
        	                 	if (username.length() > i) {
        	                        dbuf.putChar(username.charAt(i));
        	                    } else {
        	                    	dbuf.putChar('\0');					// use null 
        	                    }
        	                }
        	                
        	                // update user in the Main method
        	                user.setR(r);
        	                user.setG(g);
        	                user.setB(b);
        	                user.setUsername(username);	

        	                //send the byte array of data to the server
        	                byte[] bytes = dbuf.array();
        	                DatagramPacket dp = new DatagramPacket(bytes, bytes.length, serverAddress, port);
        	                udpT.send(dp);
        	            }
        	        });
        	    });
        	}
        	eAction = null;    
	}
}