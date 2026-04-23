package application.client;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

class MovementController {

    private BooleanProperty wPressed = new SimpleBooleanProperty();
    private BooleanProperty aPressed = new SimpleBooleanProperty();
    private BooleanProperty sPressed = new SimpleBooleanProperty();
    private BooleanProperty dPressed = new SimpleBooleanProperty();

    private BooleanBinding keyPressed = wPressed.or(aPressed).or(sPressed).or(dPressed);

    private int movementVariable = 2;

    private ImageView shape1;
    private AnchorPane scene;

    public MovementController(ImageView shape1, AnchorPane scene) {
        this.shape1 = shape1;
        this.scene = scene;

        movementSetup();

        keyPressed.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                timer.start();   // start when ANY key is pressed
            } else {
                timer.stop();    // stop when NO keys are pressed
            }
        });

    }

    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long timestamp) {

            if (wPressed.get()) {
                shape1.setLayoutY(shape1.getLayoutY() - movementVariable); // up
            }

            if (sPressed.get()) {
                shape1.setLayoutY(shape1.getLayoutY() + movementVariable); // down
            }

            if (aPressed.get()) {
                shape1.setLayoutX(shape1.getLayoutX() - movementVariable); // left
            }

            if (dPressed.get()) {
                shape1.setLayoutX(shape1.getLayoutX() + movementVariable); // right
            }
        }
    };

    public void movementSetup() {

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) wPressed.set(true);
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) aPressed.set(true);
            if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) sPressed.set(true);
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) dPressed.set(true);
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP) wPressed.set(false);
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) aPressed.set(false);
            if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN) sPressed.set(false);
            if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) dPressed.set(false);
        });
    }
}
