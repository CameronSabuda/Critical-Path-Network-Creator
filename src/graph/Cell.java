package graph;

import criticalpath.Task;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Cell extends Pane {

    private String cellId;


    public Cell(Task task) {
        this.cellId = task.getId();

        class Box extends StackPane {
            private Box(int width, int height, Property txt){
                super();

                Rectangle border = new Rectangle(width, height);
                border.setFill(Color.DARKGREY);
                Rectangle inside = new Rectangle(width - 2, height - 2);
                inside.setFill(Color.LIGHTGREY);

                Text text = new Text();
                if(txt instanceof StringProperty){
                    text.textProperty().bind(txt);
                } else {if(txt instanceof FloatProperty){
                    text.textProperty().bind(((FloatProperty) txt).asString());
                }}

                text.setFont(Font.font(15));
                super.getChildren().addAll(border,inside, text);


            }
        }

        VBox wholeCell = new VBox();

        Box idBox = new Box(120, 40, task.idProperty());

        HBox cellInfo = new HBox();

        Box estBox = new Box(40,40, task.earlyStartTimeProperty());//EST = Early Start Time
        Box durationBox = new Box(40,40,task.durationProperty());
        Box lftBox = new Box(40,40, task.latestFinishTimeProperty());

        cellInfo.getChildren().addAll(estBox, durationBox, lftBox);

        wholeCell.getChildren().addAll(idBox, cellInfo);

        this.getChildren().add(wholeCell);

    }

    public String getCellId() {
        return cellId;
    }

}