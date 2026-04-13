package application.core;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class NoInterenetGame extends Application {
	private static Color usercolor;
	private static String username;
	
	public NoInterenetGame (Color usercolor, String username)
	{
		super();
		this.usercolor = usercolor;
		this.username = username;
	}
	
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
    public Scene createGameScene(Stage primaryStage, Scene loseScene) 
    {
    	primaryStage.setFullScreen(true);
    	
        BorderPane pane = new BorderPane();
       
        Scene scene = new Scene(pane, 300, 300);

        // Player
        Circle player = new Circle(15);
        player.setTranslateX(100);
        player.setTranslateY(985);
        player.setFill(usercolor);
        
        // Username
        Label usernameLbl = new Label(username);
        usernameLbl.setTranslateX(100);
        usernameLbl.setTranslateY(985);
        usernameLbl.setFont(new Font("Menlo",8));
        
        Rectangle ground = new Rectangle(5000,20);
        ground.setTranslateX(0);
        ground.setTranslateY(800);
        ground.setFill(Color.BROWN);

        // Obstacles
        Rectangle obstacle = new Rectangle(20, 20);
        obstacle.setTranslateX(300);
        obstacle.setTranslateY(780);
        obstacle.setFill(Color.RED);
        
        Rectangle obstacle2 = new Rectangle(20, 40);
        obstacle2.setTranslateX(800);
        obstacle2.setTranslateY(760);
        obstacle2.setFill(Color.RED);
        
        Rectangle obstacle3 = new Rectangle(10, 40);
        obstacle3.setTranslateX(1200);
        obstacle3.setTranslateY(760);
        obstacle3.setFill(Color.RED);
        
        Rectangle obstacle4 = new Rectangle(20, 30);
        obstacle4.setTranslateX(1800);
        obstacle4.setTranslateY(770);
        obstacle4.setFill(Color.RED);

        pane.getChildren().addAll(player, usernameLbl, obstacle, obstacle2, obstacle3, obstacle4,ground);
        
        double gravity = 0.5;
        double jumpStrength = -8;
        double[] velocityY = {0};
        boolean[] canJump = {true};
        boolean[] gameStart = {false};

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
                    }
                    break;
            }
        });
        
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
            	primaryStage.setFullScreen(true);
            	
            	
                // Gravity
                velocityY[0] += gravity;
                player.setTranslateY(player.getTranslateY() + velocityY[0]);
                usernameLbl.setTranslateY(player.getTranslateY() + velocityY[0]);

                double playerBottom = player.getTranslateY() + player.getRadius();
                double groundTop = ground.getTranslateY();

                if (playerBottom >= groundTop) {
                    player.setTranslateY(groundTop - player.getRadius());
                    usernameLbl.setTranslateY(groundTop - player.getRadius());
                    
                    velocityY[0] = 0;
                    canJump[0] = true;
                } else {
                    canJump[0] = false;
                }

                // Move obstacle to the left
                obstacle.setTranslateX(obstacle.getTranslateX() - obstacleSpeed[0]);
                obstacle2.setTranslateX(obstacle2.getTranslateX() - obstacleSpeed[0]);
                obstacle3.setTranslateX(obstacle3.getTranslateX() - obstacleSpeed[0]);
                obstacle4.setTranslateX(obstacle4.getTranslateX() - obstacleSpeed[0]);
                

                // Reset obstacle if it goes off screen (optional)
                if (obstacle.getTranslateX() + obstacle.getWidth() < 0) 
                {
                    obstacle.setTranslateX(2000); // reset to right side of screen
                }
                
                if (obstacle2.getTranslateX() + obstacle2.getWidth() < 0) 
                {
                    obstacle2.setTranslateX(2000); // reset to right side of screen
                }
                
                if (obstacle3.getTranslateX() + obstacle3.getWidth() < 0) 
                {
                    obstacle3.setTranslateX(2000); // reset to right side of screen
                }
                
                if (obstacle4.getTranslateX() + obstacle4.getWidth() < 0) 
                {
                    obstacle4.setTranslateX(2000); // reset to right side of screen
                }

                // Check collision
                if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent()) || player.getBoundsInParent().intersects(obstacle2.getBoundsInParent()) || player.getBoundsInParent().intersects(obstacle3.getBoundsInParent()) || player.getBoundsInParent().intersects(obstacle4.getBoundsInParent()) ) 
                {
                    primaryStage.setScene(loseScene);
                    stop();
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
    
    public double timer(double time)
    {
		for (int i = 0; i<=100; i++)
		{
			try {
				Thread.sleep(250);
				time = i;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return time;
	}
    

    public static void main(String[] args) 
    {
        launch(args);
    }
}
