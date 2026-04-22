package application.core;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Game extends Dialog<ButtonType>{
	private String name;
	private Stage stage;
	private ButtonType close;
	private Dialog<ButtonType> dialog;
	private int score;
	private Pane pane;

	

	public Game(String name, Stage stage) {
	    this.setTitle(name);
	    this.initOwner(stage);
	    this.pane = new Pane();
	    getDialogPane().setContent(pane);
	    getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
	    getDialogPane().setFocusTraversable(true);
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Stage getStage() {
		return stage;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}


	public ButtonType getClose() {
		return close;
	}


	public void setClose(ButtonType close) {
		this.close = close;
	}


	public Dialog<ButtonType> getDialog() {
		return dialog;
	}


	public void setDialog(Dialog<ButtonType> dialog) {
		this.dialog = dialog;
	}
	
	public Pane getPane() {
		return pane;
	}


	public void setPane(Pane pane) {
		this.pane = pane;
	    getDialogPane().setContent(pane);
	}
}



