import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;

public class GUIV2 extends Application implements EventHandler<ActionEvent> {

    Button button = new Button();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Solar System");
        Group root = new Group();


       button = new Button();
       button.setText("forward");
       button.setTranslateX(50);
       button.setTranslateY(50);

       button.setOnAction(this);

       TitanV5.createPlanets();

       Scene scene = new Scene(root, 500, 500);
       root.getChildren().add(button);
      for(int i=0; i<TitanV5.planets.length; i++){
          root.getChildren().add(TitanV5.planets[i].getCircle());
      }

       primaryStage.setScene(scene);
       primaryStage.show();

 }

    public void handle(ActionEvent event)
    {
        if(event.getSource() == button){

            for(int j=0; j<300; j++){
            //    for(int i=1; i<TitanV5.planets.length; i++){
            //        TitanV5.planets[i].updatePos();
            //    }
                TitanV5.updatePosition();
            }

            for(int i=0; i<TitanV5.planets.length; i++){
                //Titan.planets[i].updatePos();

                Vector2D newPos = TitanV5.planets[i].getPosition();
                System.out.println(newPos.x +"     "+newPos.y);

                TitanV5.planets[i].getCircle().setCenterX(6.25*newPos.x/1.495978707e11 + 250);
                TitanV5.planets[i].getCircle().setCenterY(6.25*newPos.y/1.495978707e11 + 250);
                System.out.println("X -  "+TitanV5.planets[i].getCircle().getCenterX());
                System.out.println("Y -  "+TitanV5.planets[i].getCircle().getCenterY());

            }
            System.out.println();

        }
    }




 public static void main(String[] args) {
     launch(args);
 }
}