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


public class Map extends StackPane
{
	// isntance state
	public static Map instance;
	
	// map state
	// no interenet arcade machine fields
    private static Image noInternetMachineImg;
    private static Rectangle noInternetMachine;
    
    // fishing game arcade machine fields
    private static Image fishingMachineImg;
    private static Rectangle fishingMachine;
    
    // whack a' mole
    private static Image moleMachineImg;
    private static Rectangle moleMachine;
    
    // closet fields
    private static Image closetImg;
    private static Rectangle closet;

	private Map ()
	{
		// set images
		
		// set image for no interenet arcade machine
		noInternetMachineImg = new Image("/assets/arcade-machine-1.png");
	    noInternetMachine = new Rectangle(150, 200);
	    noInternetMachine.setFill(new ImagePattern(noInternetMachineImg));
	    
		// set image for fishing arcade machine
		fishingMachineImg = new Image("/assets/arcade-machine-2.png");
	    fishingMachine = new Rectangle(200, 150);
	    fishingMachine.setFill(new ImagePattern(fishingMachineImg));
	    
		// set image for whack a mole arcade machine
		moleMachineImg = new Image("/assets/whac-a-mole.png");
	    moleMachine = new Rectangle(200, 150);
	    moleMachine.setFill(new ImagePattern(moleMachineImg));
	    
	    // set image for the closet
	    closetImg = new Image("/assets/closet.png");
	    closet = new Rectangle(150, 200);
	    closet.setFill(new ImagePattern(closetImg));
	    
	    // set background image
	    setStyle("-fx-background-image: url('/assets/background.jpg'); -fx-background-size: cover;");
	}
		
	// singleton design pattern
	public static Map getInstance()
	{
		if (instance == null)
		{
			instance = new Map();
			return instance;
		}
		else 
		{
			return instance;
		}
	}
	
	// allows for the MapState to be updated
	public static void setInstance(MapState state) {
		// pass a runnable to the javafx application to update the javafx scene
	    Platform.runLater(() -> {
	    	// collect your users
	    	HashSet<User> users = state.users;
	        
	    	// end all existent children 
	        instance.getChildren().clear();
	        
	        // add back the pre-rendered interactive objects
		    instance.getChildren().addAll(noInternetMachine, fishingMachine, closet, moleMachine);
		    StackPane.setAlignment(noInternetMachine, Pos.CENTER_RIGHT);
		    StackPane.setAlignment(closet, Pos.BOTTOM_LEFT);
		    fishingMachine.setTranslateY(-250);
		    StackPane.setAlignment(fishingMachine, Pos.BOTTOM_LEFT);
		    moleMachine.setTranslateY(-450);
		    StackPane.setAlignment(moleMachine, Pos.BOTTOM_LEFT);

		    
		    // iterate over the current user list
	        for (User u : users) {
	        	// place circle and label at current users position
	        	Double[] coords = new Double [] {u.x, u.y};
	            Circle circle = new Circle(40); 
	            circle.setTranslateX(coords[0]);
	            circle.setTranslateY(coords[1]);
	            Label lbl = new Label();
	            lbl.setText(u.username);
	            lbl.setTranslateX(coords[0]);
	            lbl.setTranslateY(coords[1]);
	            
	            // animate movement if the user is moving (change their color)
	            if (u.moving) {
		            lbl.setTextFill(Color.rgb(u.r,u.g, u.b));
	            	circle.setFill(Color.rgb(255 - u.r, 255 - u.g,255 - u.b));
	            }
	            else {
		            lbl.setTextFill(Color.rgb(255 - u.r,255 - u.g,255 - u.b));
	            	circle.setFill(Color.rgb(u.r,u.g,u.b));
	            }
	            
	            // add the user and their game tag
	            instance.getChildren().addAll(circle, lbl);
	           
	            // specific actions for if the current user is the client
	        	if(u.getUsername().equals(Main.user.getUsername()))
	        	{
	        		// update user based on server state of user
                    Main.user.setR(u.r);
                    Main.user.setG(u.g);
                    Main.user.setB(u.b);
                    Main.user = u;
                    
                    // reset game javafx application state to handle collisions.
	        		instance.applyCss();
	        		instance.layout();
	        		
	        		// check for collisions 
	        		if (noInternetMachine.getBoundsInParent().intersects(circle.getBoundsInParent()))
	        		{
	        			instructionLabel("No Interent Game: press e to play", "Interenet");
	    		    }
	        		else if (fishingMachine.getBoundsInParent().intersects(circle.getBoundsInParent()))
	        		{
	    		    	instructionLabel("Fishing Game: press e to play", "Fishing");
	    		    }
	        		else if (closet.getBoundsInParent().intersects(circle.getBoundsInParent()))
	        		{
	        			instructionLabel("Closet: press e to change", "Closet");
	    		    }
	        		else if (moleMachine.getBoundsInParent().intersects(circle.getBoundsInParent()))
	        		{
	        			instructionLabel("Whack a' Mole: press e to play", "Mole");
	    		    }
	        		// reset action if no collisions found
	        		else 
	        		{
	        			Main.eAction = null;
	        		}
	        	}
	        }
	    });
	}
	
	// instruct user to use key 'e' to interact
	public static void instructionLabel(String message, String action)
	{
    	Label instructionLbl = new Label(message);
    	instructionLbl.setTranslateY(-30);
    	instructionLbl.setStyle("-fx-text-fill: limegreen;  -fx-font-family: Menlo; -fx-background-color: black;");
        instance.getChildren().addAll(instructionLbl);
        StackPane.setAlignment(instructionLbl, Pos.BOTTOM_CENTER);
        Main.eAction = action;
	}
	
}
