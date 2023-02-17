package es.ces.dint.snake;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelloApplication extends Application {

    private static final float OBJECT_SIZE = 20;
    private static final float SNAKE_SPEED = OBJECT_SIZE * .5f;
    Pane pane;
    Rectangle snakeHead = new Rectangle(OBJECT_SIZE, OBJECT_SIZE) {{
        setFill(Color.rgb(0,255, 0));
    }};
    Circle apple = new Circle(OBJECT_SIZE/2, OBJECT_SIZE/2, OBJECT_SIZE/2) {{
        setFill(Color.WHITE);
    }};
    List<Rectangle> snakeBody = new ArrayList<>() {{
        add(snakeHead);
    }};
    private final Random r = new Random();
    private Direction snakeDirection = Direction.RIGHT;
    private float screenSizeX, screenSizeY;
    SimpleIntegerProperty points = new SimpleIntegerProperty(0);
    Label scoreTF = new Label(){{
        textProperty().bind(Bindings.concat("Score: ").concat(points));
        setAlignment(Pos.CENTER_LEFT);
        setFont(Font.font(20));
        setTextFill(Color.WHITE);
//        setVisible(false);
    }};
    Label pauseTF = new Label("PAUSED"){{
        setAlignment(Pos.CENTER_RIGHT);
        setFont(Font.font(40));
        setTextFill(Color.WHITE);
        setVisible(false);
    }};

    @Override
    public void start(Stage stage) throws IOException {
        pane = new Pane(
                snakeHead,
                apple,
                scoreTF,
                pauseTF
        ) {{
            requestFocus();
            setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
            widthProperty().addListener((observable, oldValue, newValue) -> {
                screenSizeX = newValue.floatValue();
                float centerX = screenSizeX / 2;
                centerX -= centerX % OBJECT_SIZE;
                snakeHead.setLayoutX(centerX);
                pauseTF.setLayoutX(centerX);
                scoreTF.setLayoutX(10);
            });
            heightProperty().addListener((observable, oldValue, newValue) -> {
                screenSizeY = newValue.floatValue();
                float centerY = screenSizeY / 2;
                centerY -= centerY % OBJECT_SIZE;
                snakeHead.setLayoutY(centerY);
                pauseTF.setLayoutY(centerY);
                scoreTF.setLayoutY(10);
                appleNewPos();
            });
        }};

        Scene scene = new Scene(pane, 1080, 720) {{
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(30), event -> {

                for (int i = snakeBody.size() - 1; i > 0; i--) {
                    Rectangle rectangle = snakeBody.get(i);
                    Rectangle nextRectangle = snakeBody.get(i - 1);
                    rectangle.setX(nextRectangle.getX());
                    rectangle.setY(nextRectangle.getY());
                }

                switch (snakeDirection) {
                    case UP -> snakeHead.setY(snakeHead.getY() - SNAKE_SPEED);
                    case DOWN -> snakeHead.setY(snakeHead.getY() + SNAKE_SPEED);
                    case LEFT -> snakeHead.setX(snakeHead.getX() - SNAKE_SPEED);
                    case RIGHT -> snakeHead.setX(snakeHead.getX() + SNAKE_SPEED);
                }

                // onCollision
                Shape snake2Apple = Shape.intersect(snakeHead, apple);
                if (!snake2Apple.getBoundsInLocal().isEmpty()) {
                    appleEaten();
                }
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            setOnKeyPressed(keyEvent -> {
                switch (keyEvent.getCode()) {
                    case UP, W -> snakeDirection = Direction.UP;
                    case DOWN, S -> snakeDirection = Direction.DOWN;
                    case LEFT, A -> snakeDirection = Direction.LEFT;
                    case RIGHT, D -> snakeDirection = Direction.RIGHT;
                    case ESCAPE -> {
                        if (timeline.getStatus() == Animation.Status.RUNNING) {
                            timeline.pause();
//                            scoreTF.setVisible(true);
                            pauseTF.setVisible(true);
                        } else {
                            timeline.play();
//                            scoreTF.setVisible(false);
                            pauseTF.setVisible(false);
                        }
                    }
                }
            });
        }};
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();
    }

    private void appleEaten() {
        appleNewPos();
        Rectangle nextBodySegment = new Rectangle(OBJECT_SIZE, OBJECT_SIZE) {{
            setFill(Color.GREEN);
            setLayoutX(snakeBody.get(snakeBody.size() - 1).getLayoutX());
            setLayoutY(snakeBody.get(snakeBody.size() - 1).getLayoutY());
            setX(snakeBody.get(snakeBody.size() - 1).getX());
            setY(snakeBody.get(snakeBody.size() - 1).getY());
        }};
        points.set(points.get() + 1);
        pane.getChildren().add(nextBodySegment);
        snakeBody.add(nextBodySegment);
        System.out.println("Apple Eaten. Current Length: " + snakeBody.size());
    }

    private void appleNewPos() {
        float newX = r.nextFloat(screenSizeX);
        float newY = r.nextFloat(screenSizeY);

        newX -= newX % OBJECT_SIZE;
        newY -= newY % OBJECT_SIZE;

        apple.setLayoutX(newX);
        apple.setLayoutY(newY);
    }

    public static void main(String[] args) {
        launch();
    }
}

enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}