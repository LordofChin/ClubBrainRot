package application.client;

import java.util.HashSet;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    
    // dodge brain rot
    private static Image dodgeBrainRotMachineImg;
    private static Rectangle dodgeBrainRotMachine;
    
    // closet fields
    private static Image closetImg;
    private static Rectangle closet;
    
	private static Image img = new Image("/assets/player.png");

    
	private Map ()
	{
		// set images
		
		// set image for no interenet arcade machine
		noInternetMachineImg = new Image("/assets/arcade-machine-1.png");
	    noInternetMachine = new Rectangle(150, 200);
	    noInternetMachine.setFill(new ImagePattern(noInternetMachineImg));
	    
		// set image for dodge brain rot arcade machine
		dodgeBrainRotMachineImg = new Image("/assets/dodge-brain-rot.png");
		dodgeBrainRotMachine = new Rectangle(150, 200);
		dodgeBrainRotMachine.setFill(new ImagePattern(dodgeBrainRotMachineImg));
	    
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
		    instance.getChildren().addAll(noInternetMachine, fishingMachine, closet, moleMachine, dodgeBrainRotMachine);
		    StackPane.setAlignment(noInternetMachine, Pos.CENTER_RIGHT);
		    noInternetMachine.setTranslateY(-100);
		    StackPane.setAlignment(dodgeBrainRotMachine, Pos.CENTER_RIGHT);
		    dodgeBrainRotMachine.setTranslateY(100);
		    StackPane.setAlignment(closet, Pos.BOTTOM_LEFT);
		    fishingMachine.setTranslateY(-250);
		    StackPane.setAlignment(fishingMachine, Pos.BOTTOM_LEFT);
		    moleMachine.setTranslateY(-450);
		    StackPane.setAlignment(moleMachine, Pos.BOTTOM_LEFT);

		    
		    // iterate over the current user list
	        for (User u : users) {
	        	// place circle and label at current users position
	        	Double[] coords = new Double [] {u.x, u.y};
	        	ImageView sprite;
	        	sprite = new ImageView(img);
	        	sprite.setFitWidth(120);
	        	sprite.setFitHeight(120);
	            sprite.setTranslateX(coords[0]);
	            sprite.setTranslateY(coords[1]);
	            Label lbl = new Label();
	            lbl.setText(u.username);
	            lbl.setTranslateX(coords[0]);
	            lbl.setTranslateY(coords[1]-70);
		        lbl.setTextFill(Color.rgb(u.r,u.g, u.b));
		        lbl.setStyle("-fx-font-family: Menlo; -fx-background-color: black; -fx-font-size: 24;");


	            
	            // add the user and their game tag
	            instance.getChildren().addAll(sprite, lbl);
	           
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
	        		if (noInternetMachine.getBoundsInParent().intersects(sprite.getBoundsInParent()))
	        		{
	        			instructionLabel("No Interent Game: press e to play", "Interenet");
	    		    }
	        		else if (fishingMachine.getBoundsInParent().intersects(sprite.getBoundsInParent()))
	        		{
	    		    	instructionLabel("Fishing Game: press e to play", "Fishing");
	    		    }
	        		else if (closet.getBoundsInParent().intersects(sprite.getBoundsInParent()))
	        		{
	        			instructionLabel("Closet: press e to change", "Closet");
	    		    }
	        		else if (moleMachine.getBoundsInParent().intersects(sprite.getBoundsInParent()))
	        		{
	        			instructionLabel("Whack a' Mole: press e to play", "Mole");
	    		    }
	        		else if (dodgeBrainRotMachine.getBoundsInParent().intersects(sprite.getBoundsInParent()))
	        		{
	        			instructionLabel("Dodge Brain Rot: press e to play", "Dodge");
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
