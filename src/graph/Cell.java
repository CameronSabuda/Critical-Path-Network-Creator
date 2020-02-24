package graph;

import criticalpath.Task;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Represents how a node in a critical path network would look on an AQA maths exam paper using JavaFX graphics.
 * @author Cameron Sabuda
 */
public class Cell extends Pane {

    private String cellId;


    /**
     * Creates a cell out of a given task, displaying its id, early start time, duration and latest finish time.
     * @param task The task for the cell to be based upon.
     */
    public Cell(Task task) {
        // gives the cell a unique id based on the task's id
        this.cellId = task.getId();

        /**
         * Represents a box to be displayed in the program's frame.
         */
        class Box extends StackPane {
            /**
             * Creates a box of a given width and height that displays the given property in the centre.
             * @param width Width of the box.
             * @param height Height of the box.
             * @param txt The property to display in the centre.
             */
            private Box(int width, int height, Property txt) {
                super();

                Rectangle border = new Rectangle(width, height);
                border.setFill(Color.DARKGREY);
                Rectangle inside = new Rectangle(width - 2, height - 2);
                inside.setFill(Color.LIGHTGREY);

                Text text = new Text();
                if(txt instanceof StringProperty) {
                    text.textProperty().bind(txt);
                }
                else {
                    if (txt instanceof FloatProperty) {
                    text.textProperty().bind(((FloatProperty) txt).asString());
                    }
                }

                text.setFont(Font.font(15));
                super.getChildren().addAll(border,inside, text);


            }
        }

        // create a VBox instance to store all the components of the cell
        VBox wholeCell = new VBox();

        // make a box to contain the id that is the width of the whole cell and half the height
        Box idBox = new Box(120, 40, task.idProperty());

        // create a HBox to store the duration, early start and latest finish
        HBox cellInfo = new HBox();

        // create boxes displaying duration, early start and latest finish
        Box estBox = new Box(40,40, task.earlyStartTimeProperty());//EST = Early Start Time
        Box durationBox = new Box(40,40,task.durationProperty());
        Box lftBox = new Box(40,40, task.latestFinishTimeProperty());

        // put the above 3 boxes in the HBox
        cellInfo.getChildren().addAll(estBox, durationBox, lftBox);

        // put both boxes in the whole cell so all stuff is displayed
        wholeCell.getChildren().addAll(idBox, cellInfo);

        // put wholeCell VBox into the cell pane
        this.getChildren().add(wholeCell);

    }

    /**
     * Gets the cell's unique id
     * @return The cell's id.
     */
    public String getCellId() {
        return cellId;
    }

}