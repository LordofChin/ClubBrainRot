package application.client;

import java.util.HashSet;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import application.core.MapState;
import application.core.User;


public class Map extends StackPane{
	public static Map instance;
	public static Chat chat = Chat.getInstance();
	
    private static Image noInternetMachineImg;
    private static Rectangle noInternetMachine;
    
    private static Image closetImg;
    private static Rectangle closet;
    

	private Map (){
		super();
		
		// set images
		
		// set image for no interenet arcade machine
		noInternetMachineImg = new Image("/application/client/arcade-machine-1.png");
	    noInternetMachine = new Rectangle(150, 200);
	    noInternetMachine.setFill(new ImagePattern(noInternetMachineImg));
	    
	    // set image for the closet
	    closetImg = new Image("/application/client/closet.png");
	    closet = new Rectangle(150, 200);
	    closet.setFill(new ImagePattern(closetImg));
	    
	    // set background image
	    setStyle("-fx-background-image: url('/application/client/background.jpg'); -fx-background-size: cover;");

		}

	
	public static Map getInstance(Map map)
	{
		if (instance == null)
		{
			instance = map;
			map.setAlignment(Pos.CENTER);
			return instance;
		}
		else 
		{
			return instance;
		}
	}
		
	public static Map getInstance()
	{
		if (instance == null)
		{
			Map map = new Map();
			instance = map;
			return map;
		}
		else 
		{
			return instance;
		}
	}
	
	public static void setInstance(MapState state) {
	    Platform.runLater(() -> {
	    	HashSet<User> users = state.users;
	        
	        instance.getChildren().clear();
	        
		    instance.getChildren().addAll(noInternetMachine,closet);
		    StackPane.setAlignment(noInternetMachine, Pos.CENTER_RIGHT);
		    StackPane.setAlignment(closet, Pos.BOTTOM_LEFT);
		    
	        for (User u : users) {
	            //System.out.println("Updating player: " + u.getUsername());
	            
	            Double[] coords = new Double [] {u.x, u.y};
	            Circle circle = new Circle(40); 
	            
	            circle.setTranslateX(coords[0]);
	            circle.setTranslateY(coords[1]);
	            Label lbl = new Label();
	            lbl.setText(u.username);
	            lbl.setTranslateX(coords[0]);
	            lbl.setTranslateY(coords[1]);
	            
	            if (u.moving) {
		            lbl.setTextFill(Color.rgb(u.r,u.g, u.b));
	            	circle.setFill(Color.rgb(255 - u.r, 255 - u.g,255 - u.b));
	            }
	            else {
		            lbl.setTextFill(Color.rgb(255 - u.r,255 - u.g,255 - u.b));
	            	circle.setFill(Color.rgb(u.r,u.g,u.b));
	            }
	            
	            
	            //System.out.println(coords[0]);
	            //System.out.println(coords[1]);
	            
	            instance.getChildren().addAll(circle, lbl);
	           
	            
	        	if(u.getUsername().equals(Main.username))
	        	{
                    Main.color = Color.rgb(u.r, u.g, u.b);
                    Main.user = u;

	        		instance.applyCss();
	        		instance.layout();
	        		if (noInternetMachine.getBoundsInParent().intersects(circle.getBoundsInParent()))
	        		{
	    		    	Label instructionLbl = new Label("No Interenet Game: press e to play");
	    		    	instructionLbl.setTranslateY(-30);
	    		    	instructionLbl.setStyle("-fx-text-fill: limegreen;  -fx-font-family: Menlo; -fx-background-color: black;");

	    		    	
	    	            instance.getChildren().addAll(instructionLbl);
	    	            StackPane.setAlignment(instructionLbl, Pos.BOTTOM_CENTER);
	    	            Main.eAction = "Interenet";
	    		    }
	        		else if (closet.getBoundsInParent().intersects(circle.getBoundsInParent()))
	        		{
	    		    	Label instructionLbl = new Label("clost: press e to change");
	    		    	instructionLbl.setTranslateY(-30);
	    		    	instructionLbl.setStyle("-fx-text-fill: limegreen;  -fx-font-family: Menlo; -fx-background-color: black;");
	    	            instance.getChildren().addAll(instructionLbl);
	    	            StackPane.setAlignment(instructionLbl, Pos.BOTTOM_CENTER);
	    	            Main.eAction = "Closet";
	    		    }
	        		else 
	        		{
	        			Main.eAction = null;
	        		}
	        	}
	        }
	    });
	}
	
	public void add(Circle circle)
	{
		instance.getChildren().add(circle);
	}
	
}
