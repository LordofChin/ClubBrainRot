package application.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class Controller implements Initializable {

    private Image[] enemyImages;
    @FXML
    private ImageView shape1;
    @FXML
    private AnchorPane scene;
    @FXML
    private Label scoreLabel;

    private int score = 0;

    private Random random = new Random();
    private ArrayList<ImageView> dangerousRectangles = new ArrayList<>();

    private Timeline timeline;        // enemy spawner
    private Timeline collisionTimer;  // collision checker

    private double spawnRate = 0.3; // starting spawn rate

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	
        // load enemy images
    	enemyImages = new Image[]{
    			new Image(getClass().getResource("/assets/enemy1.jpeg").toExternalForm()),
    		    new Image(getClass().getResource("/assets/enemy2.jpeg").toExternalForm()),
    		    new Image(getClass().getResource("/assets/enemy3.jpeg").toExternalForm())
    		};

        // load player image
    	shape1.setImage(
    		    new Image(getClass().getResource("/assets/player.png").toExternalForm())
    		);

        // movement controller
        new MovementController(shape1, scene);

        // enemy spawner
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        setupSpawner();

        // collision + score + win checker
        collisionTimer = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {

                    // WIN CONDITION — 10 seconds survived
                    if (score >= 100) {
                        winGame();
                        return;
                    }

                    // SCORE UPDATE
                    score++;
                    scoreLabel.setText("Score: " + score);

                    // COLLISION CHECKS
                    for (ImageView enemy : new ArrayList<>(dangerousRectangles)) {
                        if (enemy != null) {
                            checkCollision(shape1, enemy);
                        }
                    }
                })
        );
        collisionTimer.setCycleCount(Timeline.INDEFINITE);

        // focus handling
        scene.setFocusTraversable(true);
        scene.requestFocus();
        scene.setOnMouseClicked(e -> scene.requestFocus());
    }

    // WIN GAME POPUP
    private void winGame() {

        timeline.stop();
        collisionTimer.stop();

        for (ImageView e : dangerousRectangles) {
            e.getTransforms().clear();
        }

        Platform.runLater(() -> {
            Alert win = new Alert(Alert.AlertType.INFORMATION);
            win.initOwner(DodgeBrainRot.stage);
            win.setTitle("You Win!");
            win.setHeaderText("Congratulations!");
            win.setContentText("You survived for 10 seconds!");

            win.getButtonTypes().setAll(ButtonType.OK);
            
            
            win.getButtonTypes().setAll(
                    ButtonType.YES,
                    ButtonType.NO
            );

            var result = win.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.YES) {
                restartGame();
            } else {
            	DodgeBrainRot.instance.close();
            }
           
        });
    }

    
    @FXML
    void start(ActionEvent event) {

        // Reset score when starting
        score = 0;
        scoreLabel.setText("Score: 0");

        // Start enemy spawner
        timeline.play();

        // Start score + collision timer
        collisionTimer.play();

        // Make sure focus is on the game
        scene.requestFocus();
    }


    // ENEMY SPAWNER
    private void setupSpawner() {
        KeyFrame frame = new KeyFrame(Duration.seconds(spawnRate), e -> {

            ImageView enemy = createDangerImage();
            scene.getChildren().add(enemy);
            dangerousRectangles.add(enemy);

            // speed up over time
            spawnRate = Math.max(0.1, spawnRate - 0.01);

            timeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.seconds(spawnRate), this::spawnEnemy)
            );
        });

        timeline.getKeyFrames().add(frame);
    }

    private void spawnEnemy(ActionEvent e) {
        ImageView enemy = createDangerImage();
        scene.getChildren().add(enemy);
        dangerousRectangles.add(enemy);
    }

    // COLLISION CHECK
    private void checkCollision(ImageView player, ImageView enemy) {
        if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {

            timeline.stop();
            collisionTimer.stop();

            for (ImageView e : dangerousRectangles) {
                e.getTransforms().clear();
            }

            Platform.runLater(() -> {

                Alert gameOver = new Alert(Alert.AlertType.CONFIRMATION);
                gameOver.initOwner(DodgeBrainRot.stage);
                gameOver.setTitle("Game Over!");
                gameOver.setHeaderText("You got hit!");
                gameOver.setContentText("Would you like to play again?");

                gameOver.getButtonTypes().setAll(
                        ButtonType.YES,
                        ButtonType.NO
                );

                var result = gameOver.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.YES) {
                    restartGame();
                } else {
                	DodgeBrainRot.instance.close();
                }
            });
        }
    }

    // RESET GAME
    private void restartGame() {

        score = 0;
        scoreLabel.setText("Score: 0");

        shape1.setLayoutX(50);
        shape1.setLayoutY(200);

        for (ImageView enemy : dangerousRectangles) {
            scene.getChildren().remove(enemy);
        }
        dangerousRectangles.clear();

        timeline.play();
        collisionTimer.play();

        scene.requestFocus();
    }

    // CREATE ENEMY
    private ImageView createDangerImage() {

        Image img = enemyImages[random.nextInt(enemyImages.length)];

        ImageView enemy = new ImageView(img);
        enemy.setFitWidth(50);
        enemy.setFitHeight(50);
        enemy.setLayoutX(700);

        Platform.runLater(() -> {
            int maxY = (int) scene.getHeight() - 50;
            if (maxY < 0) maxY = 0;
            int y = random.nextInt(maxY);
            enemy.setLayoutY(y);
        });

        TranslateTransition t = new TranslateTransition(Duration.seconds(5), enemy);
        t.setToX(-900);
        t.play();

        t.setOnFinished(e -> {
            scene.getChildren().remove(enemy);
            dangerousRectangles.remove(enemy);
        });

        return enemy;
    }
}