package application.client;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.Random;

import application.core.Game;

public class FishingGame extends Game {

    private double hookX = 185;
    private double hookY = 0;

    private boolean dropping = false;

    private final double hookSpeed = 6;

    private int score = 0;

    private Rectangle hook;

    private ArrayList<ImageView> fishList = new ArrayList<>();
    private Image[] fishImages;
    private Random rand = new Random();

    private boolean left, right, space;

    private AnimationTimer loop;

    public FishingGame(Stage stage) {
    	super("Fishing Game", stage);    	
        getPane().setPrefSize(400, 600);

        // background water
        getPane().setStyle("-fx-background-color: linear-gradient(to bottom, lightblue, darkblue);");

        // hook
        hook = new Rectangle(10, 30, Color.BLACK);
        Image hookImg = new Image(getClass().getResource("/assets/hook.jpg").toExternalForm());
        hook.setFill(new ImagePattern (hookImg));
        hook.setTranslateX(hookX);
        hook.setTranslateY(hookY);

        getPane().getChildren().add(hook);
        
        fishImages = new Image[] {
        		new Image(getClass().getResource("/assets/enemy1.jpeg").toExternalForm()),
    		    new Image(getClass().getResource("/assets/enemy2.jpeg").toExternalForm()),
    		    new Image(getClass().getResource("/assets/enemy3.jpeg").toExternalForm())
    		};

        // fish spawn
        for (int i = 0; i < 3; i++) {

            ImageView fish = new ImageView(fishImages[rand.nextInt(fishImages.length)]);
            fish.setFitWidth(40);
            fish.setFitHeight(25);

            fish.setTranslateX(rand.nextInt(300));
            fish.setTranslateY(200 + i * 100);

            fishList.add(fish);
        }

        getPane().getChildren().addAll(fishList);

        setupControls();
        startGameLoop();
        
        this.setOnShown(e -> {
            getDialogPane().requestFocus();
            Platform.runLater(() -> getDialogPane().requestFocus());
        });
        
        showAndWait();
    }

    private void setupControls() {
        DialogPane dialogPane = getDialogPane();
        
        // Ensure the pane can receive focus
        dialogPane.setFocusTraversable(true);
        Platform.runLater(() -> dialogPane.requestFocus());

        // Use EventFilters on the DialogPane itself
        dialogPane.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.SPACE) space = true;
            if (e.getCode() == KeyCode.LEFT) left = true;
            if (e.getCode() == KeyCode.RIGHT) right = true;
            
            // Prevent the key from triggering dialog buttons (like Enter/Esc)
            e.consume(); 
        });

        dialogPane.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode() == KeyCode.SPACE) space = false;
            if (e.getCode() == KeyCode.LEFT) left = false;
            if (e.getCode() == KeyCode.RIGHT) right = false;
            e.consume();
        });
    }

    private void startGameLoop() {

        loop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                updatePlayer();
                updateHook();
                updateFish();
                checkCollisions();
            }
        };

        loop.start();
    }

    private void updatePlayer() {

        if (left) hookX -= 5;
        if (right) hookX += 5;

        hookX = Math.max(0, Math.min(360, hookX));
    }

    private void updateHook() {

        if (space && hookY == 0) {
            dropping = true;
        }

        if (dropping) {
            hookY += hookSpeed;
        } else if (hookY > 0) {
            hookY -= hookSpeed;
        }

        if (hookY > 520) {
            dropping = false;
        }

        if (hookY < 0) hookY = 0;

        hook.setTranslateX(hookX);
        hook.setTranslateY(hookY);
    }

    private void updateFish() {

        for (ImageView fish : fishList) {

            fish.setTranslateX(fish.getTranslateX() + 2);

            if (fish.getTranslateX() > 400) {
                fish.setTranslateX(-50);
                fish.setTranslateY(200 + rand.nextInt(300));
            }
        }
    }

    private void checkCollisions() {

        Rectangle hookBounds = new Rectangle(hookX, hookY, 10, 30);

        for (ImageView fish : fishList) {

            if (hookBounds.getBoundsInParent().intersects(fish.getBoundsInParent())) {

                score++;

                fish.setTranslateX(-50);
                fish.setTranslateY(200 + rand.nextInt(300));

                dropping = false;
                hookY = 0;

                System.out.println("Score: " + score);
            }
        }
    }

    public void stopGame() {
        loop.stop();
    }

    public int getScore() {
        return score;
    }
}
