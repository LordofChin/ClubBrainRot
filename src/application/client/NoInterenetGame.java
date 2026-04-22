package application.client;

import javafx.geometry.Pos;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NoInterenetGame extends Application 
{
    private static Image tralalero_tralala_img;
    private static Rectangle tralalero_tralala;
    private static Image tung_tung_tung_sahur_img;
    private static Rectangle tung_tung_tung_sahur;
    private static Rectangle il_cacto_hipopotamo;
    private static Image il_cacto_hipopotamo_img;
    private static Rectangle la_vaca_saturno_saturnita;
    private static Image la_vaca_saturno_saturnita_img;    
	
    @Override
    public void start(Stage primaryStage) 
    {

        // Build the lose scene
        Scene loseScene = createLoseScene(primaryStage);

        // Build the first game scene
        Scene gameScene = createGameScene(primaryStage, loseScene);

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("No Internet Game");
        primaryStage.show();
    }

    // Method to create a new game scene
    public Scene createGameScene(Stage primaryStage, Scene previousScene) 
    {
    	boolean [] skip = {false};
    	
    	if (skip [0] == false)
        {
        	Alert howToPlay = new Alert((Alert.AlertType.CONFIRMATION));
            howToPlay.setTitle("How To Play");
            howToPlay.setHeaderText("Press SPACE to start the game and to jump over the obsticles, every Jump = 5 points!!");
    		howToPlay.setContentText("Want To Play?");
    		
    		howToPlay.getButtonTypes().clear();
    		
    		ButtonType playTheGameButton = new ButtonType("Play");
    		ButtonType returnToLobbyButton = new ButtonType("Return to Lobby");
    		
    		howToPlay.getButtonTypes().addAll(playTheGameButton, returnToLobbyButton);
    		
    		howToPlay.showAndWait().ifPresent(response -> {
    			if(response == playTheGameButton)
    			{
    				howToPlay.close();
    			}
    			else 
    			{
    				primaryStage.setScene(previousScene);
    			}
    		});
        }
    	
        BorderPane pane = new BorderPane();
       
        Scene scene = new Scene(pane, 300, 300);

        // Player
        Circle player = new Circle(15);
        player.setTranslateX(100);
        player.setTranslateY(985);
        Color color = Color.rgb(Main.user.getR(), Main.user.getB(), Main.user.getG());
        player.setFill(color);
        
        // Username
        Label usernameLbl = new Label();
        usernameLbl.setText(Main.user.getUsername());
        
        usernameLbl.translateXProperty().bind(
        	    player.translateXProperty()
        	        .subtract(usernameLbl.widthProperty().divide(2))
        	);
        usernameLbl.translateYProperty().bind(
        	    player.translateYProperty()
        	        .subtract(usernameLbl.heightProperty().divide(2))
        	);
        usernameLbl.setFont(new Font("Menlo",8));
        usernameLbl.setTextFill(color.invert());
        
        Rectangle ground = new Rectangle(5000,20);
        ground.setTranslateX(0);
        ground.setTranslateY(800);
        ground.setFill(Color.BROWN);

        // Obstacles
        Rectangle obstacle = new Rectangle(20, 20);
        obstacle.setTranslateX(300);
        obstacle.setTranslateY(780);
        obstacle.setFill(Color.RED);
        obstacle.setVisible(false);
		tralalero_tralala_img = new Image("/assets/tralalero-tralala.png");
		tralalero_tralala = new Rectangle(100, 70);
	    tralalero_tralala.setFill(new ImagePattern(tralalero_tralala_img));
        tralalero_tralala.setTranslateX(260);
        tralalero_tralala.setTranslateY(755);
        
	    
        Rectangle obstacle2 = new Rectangle(20, 40);
        obstacle2.setTranslateX(800);
        obstacle2.setTranslateY(760);
        obstacle2.setFill(Color.RED);
        obstacle2.setVisible(false);
        il_cacto_hipopotamo_img = new Image("/assets/il-cacto-hipopotamo.png");
        il_cacto_hipopotamo = new Rectangle(60, 70);
        il_cacto_hipopotamo.setFill(new ImagePattern((il_cacto_hipopotamo_img)));
        il_cacto_hipopotamo.setTranslateX(780);
        il_cacto_hipopotamo.setTranslateY(740);

        
        Rectangle obstacle3 = new Rectangle(10, 40);
        obstacle3.setTranslateX(1200);
        obstacle3.setTranslateY(760);
        obstacle3.setFill(Color.RED);
        obstacle3.setVisible(false);
        tung_tung_tung_sahur_img = new Image("/assets/tung-tung-tung-sahur.png");
        tung_tung_tung_sahur = new Rectangle(70, 70);
        tung_tung_tung_sahur.setFill(new ImagePattern(tung_tung_tung_sahur_img));
        tung_tung_tung_sahur.setTranslateX(1170);
        tung_tung_tung_sahur.setTranslateY(745);
        
        Rectangle obstacle4 = new Rectangle(20, 30);
        obstacle4.setTranslateX(1800);
        obstacle4.setTranslateY(770);
        obstacle4.setFill(Color.RED);
        obstacle4.setVisible(false);
        la_vaca_saturno_saturnita_img = new Image("/assets/la-vaca-saturno-saturnita.png");
        la_vaca_saturno_saturnita = new Rectangle(70, 60);
        la_vaca_saturno_saturnita.setFill(new ImagePattern(la_vaca_saturno_saturnita_img));
        la_vaca_saturno_saturnita.setTranslateX(1775);
        la_vaca_saturno_saturnita.setTranslateY(750);

        pane.getChildren().addAll(player, obstacle, obstacle2, obstacle3, obstacle4, ground, usernameLbl, tralalero_tralala, tung_tung_tung_sahur, il_cacto_hipopotamo,la_vaca_saturno_saturnita);
        
        double gravity = 0.5;
        double jumpStrength = -8;
        double[] velocityY = {0};
        boolean[] canJump = {true};
        boolean[] gameStart = {false};
        int[] score = {5};

        // Key movement
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE :
                	if(!gameStart[0])
                	{
                		gameStart[0] = true;
                	}
                    else if (canJump[0]) 
                	{
                        velocityY[0] = jumpStrength;
                        score[0] +=5;
                    }
                    break;
            }
        });
        
        Label showScore = new Label("SCORE " + score[0]);
        pane.setCenter(showScore);
        
        double[] obstacleSpeed = {5};
        double speedIncrease = 0.005;
        
     // Game loop
        new AnimationTimer() {
            @Override
            public void handle(long now) 
            {
            	primaryStage.setFullScreen(true);
            	
            	if(!gameStart[0])
            	{
            		return;
            	}
            	
            	 Label showScore = new Label("SCORE " + score[0]);
                 pane.setCenter(showScore);
            	
            	
                // Gravity
                velocityY[0] += gravity;
                player.setTranslateY(player.getTranslateY() + velocityY[0]);

                double playerBottom = player.getTranslateY() + player.getRadius();
                double groundTop = ground.getTranslateY();

                if (playerBottom >= groundTop) {
                    player.setTranslateY(groundTop - player.getRadius());
                    
                    velocityY[0] = 0;
                    canJump[0] = true;
                } else {
                    canJump[0] = false;
                }

                // Move obstacle to the left
                obstacle.setTranslateX(obstacle.getTranslateX() - obstacleSpeed[0]);
                tralalero_tralala.setTranslateX(tralalero_tralala.getTranslateX() - obstacleSpeed[0]);
                obstacle2.setTranslateX(obstacle2.getTranslateX() - obstacleSpeed[0]);
                il_cacto_hipopotamo.setTranslateX(il_cacto_hipopotamo.getTranslateX() - obstacleSpeed[0]);
                obstacle3.setTranslateX(obstacle3.getTranslateX() - obstacleSpeed[0]);
                tung_tung_tung_sahur.setTranslateX(tung_tung_tung_sahur.getTranslateX() - obstacleSpeed[0]);
                obstacle4.setTranslateX(obstacle4.getTranslateX() - obstacleSpeed[0]);
                la_vaca_saturno_saturnita.setTranslateX(la_vaca_saturno_saturnita.getTranslateX() - obstacleSpeed[0]);

                

                // Reset obstacle if it goes off screen (optional)
                if (obstacle.getTranslateX() + obstacle.getWidth() < 0) 
                {
                    obstacle.setTranslateX(2000); // reset to right side of screen
                    tralalero_tralala.setTranslateX(1960);
                }
                
                if (obstacle2.getTranslateX() + obstacle2.getWidth() < 0) 
                {
                    obstacle2.setTranslateX(2000); // reset to right side of screen
                    il_cacto_hipopotamo.setTranslateX(1980);
                }
                
                if (obstacle3.getTranslateX() + obstacle3.getWidth() < 0) 
                {
                    obstacle3.setTranslateX(2000); // reset to right side of screen
                    tung_tung_tung_sahur.setTranslateX(1970);
                }
                
                if (obstacle4.getTranslateX() + obstacle4.getWidth() < 0) 
                {
                    obstacle4.setTranslateX(2000); // reset to right side of screen
                    la_vaca_saturno_saturnita.setTranslateX(1975);
                }

                // Check collision
                if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent()) || player.getBoundsInParent().intersects(obstacle2.getBoundsInParent()) || player.getBoundsInParent().intersects(obstacle3.getBoundsInParent()) || player.getBoundsInParent().intersects(obstacle4.getBoundsInParent()) ) 
                {
                    stop();
                    Platform.runLater(() -> {
                    	
                    	Alert lost = new Alert((Alert.AlertType.CONFIRMATION));
                        lost.setTitle("Game Over");
                        lost.setHeaderText("Total Score: " + score[0]);
                		lost.setContentText("Try again?");
                		
                		lost.getButtonTypes().clear();
                		
                		ButtonType playButton = new ButtonType("Play Again");
                		ButtonType returnButton = new ButtonType("Return to Lobby");
                		
                		lost.getButtonTypes().addAll(playButton, returnButton);
                		
                		lost.showAndWait().ifPresent(response -> {
                			if(response == playButton)
                			{
                				skip[0] = true;
                				primaryStage.setScene(createGameScene(primaryStage, previousScene));
                			}
                			else 
                			{
                				primaryStage.setScene(previousScene);
                			}
                		});
                    	
                    	
                    });
                }
                
                obstacleSpeed[0] += speedIncrease;
            }
        }.start();

        return scene;
    }

    // Method to create lose scene
    public Scene createLoseScene(Stage primaryStage) 
    {
    	primaryStage.setFullScreen(true);
        BorderPane losePane = new BorderPane();

        Label oof = new Label("You Lost");
        Button playAgain = new Button("Play Again");

        VBox layout = new VBox(10);
        layout.getChildren().addAll(oof, playAgain);
        layout.setAlignment(Pos.CENTER);

        losePane.setCenter(layout);

        Scene loseScene = new Scene(losePane, 300, 300);

        // Play Again button rebuilds a new game scene
        playAgain.setOnAction(e -> {
            Scene newGame = createGameScene(primaryStage, loseScene);
            primaryStage.setScene(newGame);
        });

        return loseScene;
    }
    

    public static void main(String[] args) 
    {
        launch(args);
    }
}
