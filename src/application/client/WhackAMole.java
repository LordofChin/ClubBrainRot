package application.client;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import application.core.Game;


public class WhackAMole extends Game {
	Label timerLabel;
	static Label scoreLabel;

	static int time;
	protected static int score;
    private List<MoleTile> tiles;
    private TilePane tPane;
    private static VBox vbox;
    
	public WhackAMole(Stage stage) {
		super("Whack A' Mole", stage);
		this.tiles = new ArrayList<>();
		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		time = 0;
		score = 0;
	    vbox.setStyle("-fx-background-image: url('/assets/hallway.png'); -fx-background-size: cover;");

		Thread timerThread = new Thread(() -> {
		    while (time < 10) { 
		        try {
		            Thread.sleep(1000);
		            
		            Platform.runLater(() -> {
		                time++;
		                timerLabel.setText(String.format("time: %d", time));
		            });
		        } catch (InterruptedException e) {
		            break; 
		        }
		    }
		    
		    // Optional: Logic for when time runs out
		    Platform.runLater(() -> {
		    	timerLabel.setText("Game Over!");
		    	vbox.getChildren().clear();
		    	vbox.getChildren().addAll(timerLabel, scoreLabel);
		    });
		    
		});

		timerLabel = new Label("time: " + time);
		scoreLabel = new Label("score: " + score);
        timerLabel.setStyle("-fx-font-family: Menlo; -fx-background-color: black; -fx-font-size: 24;");
        scoreLabel.setStyle("-fx-font-family: Menlo; -fx-background-color: black; -fx-font-size: 24;");
        timerLabel.setTextFill(Color.GREEN);
        scoreLabel.setTextFill(Color.GREEN);

		
		timerThread.setDaemon(true);
		timerThread.start();
		this.tPane = new TilePane();
		setPane(vbox);
        getPane().setPrefSize(400, 600);

        // create tiles
        for (int i = 0; i < 9; i++) {
            MoleTile tile = new MoleTile(new Image("assets/mole.png"));

            tile.setOnHit(() -> {
                spawnNextMole(); // player triggers next mole
            });

            tiles.add(tile);
            tPane.getChildren().add(tile);
        }

        this.setOnShown(e -> {
            getDialogPane().requestFocus();
            Platform.runLater(() -> getDialogPane().requestFocus());
        });
        vbox.getChildren().addAll(timerLabel, tPane, scoreLabel);
        // start first mole
        spawnNextMole();

        showAndWait();
    }

    private void spawnNextMole() {
        // hide all
        for (MoleTile t : tiles) {
            t.hideMole();
        }

        // pick random tile
        int index = (int)(Math.random() * tiles.size());
        tiles.get(index).showMole();
    }

}

class MoleTile extends StackPane {

    private ImageView imageView;
    private boolean isMole = false;

    private Runnable onHit; // callback to Driver

    public MoleTile(Image image) {
        imageView = new ImageView(image);
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);

        setStyle("-fx-background-color: lightgray;");

        getChildren().add(imageView);

        // click logic
        setOnMouseClicked(e -> {
            if (isMole) {
                hideMole();
                WhackAMole.score++;
                WhackAMole.scoreLabel.setText("score: " + WhackAMole.score);
                if (onHit != null) {
                    onHit.run(); // notify Driver
                }
            }
        });

        hideMole(); // start hidden
    }

    public void showMole() {
        isMole = true;
        imageView.setVisible(true);
    }

    public void hideMole() {
        isMole = false;
        imageView.setVisible(false);
    }

    public boolean isMole() {
        return isMole;
    }

    public void setOnHit(Runnable onHit) {
        this.onHit = onHit;
    }
}
