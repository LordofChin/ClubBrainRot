package application.client;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


import application.core.Game;

import javafx.fxml.FXMLLoader;

public class DodgeBrainRot extends Game{
	protected static Stage stage;
	protected static Dialog<ButtonType> instance;

	public DodgeBrainRot(Stage stage) {
	    super("Dodge Brain Rot", stage);
		DodgeBrainRot.stage = stage;
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/sample.fxml"));
	        AnchorPane root = loader.load();
	        
	        root.setPrefSize(600, 400); 
	        
	        setPane(root); 

	        this.setOnShown(e -> root.requestFocus());
	        instance = this;

	    } catch (Exception e) {
	        System.out.println("FXML Load Error: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}
