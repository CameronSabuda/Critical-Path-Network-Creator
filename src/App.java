import criticalpath.*;
import graph.Graph;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class App extends Application {

    CriticalPathGraph criticalpathgraph;
    Graph graph;

    @Override
    public void start(final Stage primaryStage) {

        // Set the title of the window
        primaryStage.setTitle("Critical Path Network Creator");

        // Create a borderPane to contain all of the user interface elements
        BorderPane userInterface = new BorderPane();
        userInterface.setTop(createToolBar(primaryStage));

        // Initialise the data graph and the view graph
        criticalpathgraph = new CriticalPathGraph();
        graph = new Graph();

        userInterface.setCenter(graph.getCellLayer());

        // Create the window
        Scene scene = new Scene(userInterface, 1280, 720);
        //buttonGrid.setGridLinesVisible(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    // Create the toolbar
    private ToolBar createToolBar(Stage primaryStage) {

        // An internal class to be used to make each button in the toolbar.
        class ToolbarButton extends Button {
            private ToolbarButton(String text) {
                super(text);
                //set image
                Image image = new Image(this.getClass().getResourceAsStream("button-icons/" + text + ".png"));
                this.setGraphic(new ImageView(image));
            }
        }

        //NEW BUTTON
        ToolbarButton newBtn = new ToolbarButton("New");
        newBtn.setOnAction(e -> {
            // basically just restart the app
            this.start(primaryStage);
        });

        //OPEN BUTTON
        ToolbarButton openBtn = new ToolbarButton("Open");
        openBtn.setOnAction(e -> {
            // Open a window that allows the user to select a file from their system to open.
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            // make only .cpg files visible
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Critical Path Graph Files", "*.cpg"));
            File file = fileChooser.showOpenDialog(primaryStage);
            // try to open the file
            if (file != null) {
                try {
                    String fileContents = "";
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        fileContents += line;
                    }
                    reader.close();

                    this.start(primaryStage);
                    openFile(fileContents);

                } catch (Exception ex) {
                    System.err.format("Exception occurred trying to read '%s'.", file.getPath());
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("It would appear that the file you have tried to open is corrupted. Please try opening another file or contacting an administrator for more info.");
                    alert.show();
                }

            }
        });

        //SAVE BUTTON
        ToolbarButton saveBtn = new ToolbarButton("Save");
        saveBtn.setOnAction(e -> {
            // Convert the file into a string that can be put in a file to be saved.
            String fileContents = createFile();
            // Give the user a window that allows them to choose a place for the file to be saved.
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Critical Path Graph Files", "*.cpg"));
            File file = fileChooser.showSaveDialog(primaryStage);

            // try to save the file.
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(fileContents);
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }


        });

        //NEW ACTIVITY BUTTON
        ToolbarButton newActBtn = new ToolbarButton("New Activity");
        newActBtn.setOnAction(e -> {

            // Display a new window that prompts the user for task details so a task can be created.
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setTitle("Add activity");

            GridPane gridpane = new GridPane();
            gridpane.setHgap(10);
            gridpane.setVgap(10);
            gridpane.setPadding(new Insets(10,10,10,10));

            Text title = new Text("Add activity");
            title.setFont(Font.font ("Arial", 18));
            //title.setUnderline(true);
            GridPane.setConstraints(title, 0, 0);

            Text actText = new Text("Activity name:");
            GridPane.setConstraints(actText,0, 1);
            Text durText = new Text("Duration:");
            GridPane.setConstraints(durText, 0, 2);
            Text preText = new Text("Predecessor Activities:");
            GridPane.setConstraints(preText, 0, 3);
            Text preText2 = new Text("(Enter in the format A, B, C etc.)");
            preText2.setFill(Color.GREY);
            GridPane.setConstraints(preText2, 0, 4);

            TextField actTextField = new TextField();
            GridPane.setConstraints(actTextField, 1, 1);
            TextField durTextField = new TextField();
            GridPane.setConstraints(durTextField, 1, 2);
            TextField preTextField = new TextField();
            GridPane.setConstraints(preTextField, 1, 3);
            Button confirmButton = new Button("Add");
            GridPane.setConstraints(confirmButton, 1, 4);
            GridPane.setHalignment(confirmButton, HPos.RIGHT);

            // Try to create a task with the details given.
            confirmButton.setOnAction(event -> {
                try {
                    String predecessorsStr = preTextField.getText();
                    String durationStr = durTextField.getText();
                    String activityStr = actTextField.getText();
                    if (activityStr.contains(",")){
                        throw new Exception("comma in activity name");
                    }

                    ArrayList<Task> predecessors = new ArrayList<>();
                    //Prevent Tasks with string id of "+" being created and added to predecessors
                    if (!predecessorsStr.isEmpty()){
                        for (String str : predecessorsStr.split(",")){
                            str = str.trim();
                            predecessors.add(this.criticalpathgraph.getTask(str));
                        }
                    }
                    if (activityStr.equals("")){
                        throw new Exception(" no activity name");
                    }
                    Task task = new Task(activityStr, Float.parseFloat(durationStr), predecessors);
                    criticalpathgraph.addTask(task);
                    this.addTaskToGraph(task);
                    dialog.close();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("It would appear some of the data you have inputted is invalid. Please try again.");
                    alert.show();
                }
            });


            gridpane.getChildren().addAll(durText,actText, title, actTextField,
                                          durTextField, preTextField, preText,
                                          preText2, confirmButton);



            Scene dialogScene = new Scene(gridpane, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        //DELETE ACTIVITY BUTTON
        ToolbarButton delActBtn = new ToolbarButton("Delete Activity");
        delActBtn.setOnAction(e -> {
            // Show a new window that prompts the user for an activity name for a task to be deleted
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setTitle("Delete activity");

            GridPane gridpane = new GridPane();
            gridpane.setHgap(10);
            gridpane.setVgap(10);
            gridpane.setPadding(new Insets(10,10,10,10));

            Text title = new Text("Delete activity");
            title.setFont(Font.font ("Arial", 18));
            //title.setUnderline(true);
            GridPane.setConstraints(title, 0, 0);

            Text actText = new Text("Activity name:");
            GridPane.setConstraints(actText,0, 1);

            TextField actTextField = new TextField();
            GridPane.setConstraints(actTextField, 1, 1);
            Button confirmButton = new Button("Delete");
            GridPane.setConstraints(confirmButton, 1, 2);
            GridPane.setHalignment(confirmButton, HPos.RIGHT);

            gridpane.getChildren().addAll(actText, actTextField, confirmButton);

            // Try to delete the task, display a dialog box if it failed.
            confirmButton.setOnAction(event -> {
                try{
                    Task task = this.criticalpathgraph.getTask(actTextField.getText());
                    if (task == null){
                        throw new Exception();
                    }
                    removeTaskFromGraph(task);
                    this.criticalpathgraph.deleteTask(task);
                    dialog.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("It would appear that you have entered an invalid activity name, please try again.");
                    alert.show();
                }
            });

            Scene dialogScene = new Scene(gridpane, 250, 100);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        //INFO BUTTON
        ToolbarButton infoBtn = new ToolbarButton("Info");
        infoBtn.setOnAction(e -> {
            // Make a new window that displays the critical paths of the network and the minimum completion time.
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setTitle("Info");
            VBox dialogVBox = new VBox(10);
            dialogVBox.setPadding(new Insets(10));

            String str = "The critical paths of the project are:";
            // Convert the critical paths into strings (could be implemented as a separate method)
            ArrayList<ArrayList<Task>> criticalPaths = this.criticalpathgraph.getCriticalPaths();
            for (ArrayList<Task> cp : criticalPaths){
                str += "\n";
                for (Task task : cp){
                    str += task.getId() + ", ";
                }
                str = str.trim().substring(0, str.length() - 2);
            }
            Text txt1 = new Text(str);
            Text txt2 = new Text("The minimum completion time is: " + this.criticalpathgraph.getEndTask().getLatestFinishTime());

            dialogVBox.getChildren().addAll(txt1, txt2);

            Scene dialogScene = new Scene(dialogVBox, 300, 15*(str.split("\n").length) + 50);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        //TABLE BUTTON
        ToolbarButton tblBtn = new ToolbarButton("Precedence Table");
        tblBtn.setOnAction(e -> {
            // Create a new window that shows the precedence table of the graph
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setTitle("Precedence Table");
            VBox dialogVbox = new VBox(20);
            dialogVbox.setPadding(new Insets(10,10,10,10));

            GridPane table = new GridPane();
            table.setGridLinesVisible(true);//Normally used for debug but allow for an easy table
            table.getColumnConstraints().addAll(new ColumnConstraints(130), new ColumnConstraints(130));
            table.getRowConstraints().add(new RowConstraints(20));

            Text leftTableHeading = new Text(" Activity");
            Text rightTableHeading = new Text(" Predecessors");

            leftTableHeading.setFont(Font.font("Arial", 14));
            rightTableHeading.setFont(Font.font("Arial", 14));

            GridPane.setConstraints(leftTableHeading, 0 ,0 );
            GridPane.setConstraints(rightTableHeading, 1, 0);
            table.getChildren().addAll(leftTableHeading, rightTableHeading);

            // Iterate through all the tasks and add them to the table with the predecessors being displayed in the
            // right column.
            for (int i = 2; i < this.criticalpathgraph.getTasks().size(); i++){
                try{
                    Task task = this.criticalpathgraph.getTasks().get(i);//+1 to avoid _START_ and _END_ activities
                    String preStr = "";

                    if (task.getPredecessors().get(0).getId() == "_START_"){
                        preStr += "None";
                    } else {
                        for (Task tsk : task.getPredecessors()){
                            preStr += tsk.getId() + " ";
                        }
                        preStr = preStr.trim();
                        preStr = preStr.replace(" ", ", ");
                    }

                    Text textLeft = new Text(" " + task.getId());
                    Text textRight = new Text(" " + preStr); //leading whitespace to avoid text cutting into column lines

                    GridPane.setConstraints(textLeft, 0, i+1);
                    GridPane.setConstraints(textRight, 1 , i+1);
                    table.getChildren().addAll(textLeft, textRight);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }


            }

            dialogVbox.getChildren().add(table);

            Scene dialogScene = new Scene(new ScrollPane(dialogVbox), 300, /*table.getRowCount() * 15 + 10*/ 300);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        // Add all the buttons to the toolbar.
        ToolBar toolbar = new ToolBar(
                newBtn,
                openBtn,
                saveBtn,
                new Separator(Orientation.VERTICAL),
                newActBtn,
                delActBtn,
                new Separator(Orientation.VERTICAL),
                infoBtn,
                tblBtn
        );

        toolbar.setOrientation(Orientation.HORIZONTAL);
        return toolbar;

    }

    /**
     * Adds a task to the graph.
     * @param task The task to be added to the graph.
     */
    private void addTaskToGraph(Task task){

        this.graph.beginUpdate();
        // make sure task to be added doesn't share id with required start and end nodes.
        if (!(task.getId().equals("_START_") || task.getId().equals("_END_"))){
            graph.getModel().addCell(task);
            for (Task tsk : task.getPredecessors()){
                if (!tsk.getId().equals("_START_")){
                    graph.getModel().addEdge(tsk.getId(), task.getId());
                }
            }
        }

        this.graph.endUpdate();
    }

    /**
     * Removes a task from the graph.
     * @param task The task to be removed.
     */
    private void removeTaskFromGraph(Task task){
        this.graph.beginUpdate();
        // Ensure that the start/end nodes aren't removed
        if (!(task.getId().equals("_START_") || task.getId().equals("_END_"))){
            graph.getModel().removeCell(task, this.criticalpathgraph);
        }

        this.graph.endUpdate();

    }

    /**
     * Converts the criticalPathGraph object into a string that can be used to restore the object.
     * @return A string version of the criticalPathGraph object.
     */
    private String createFile(){
        String file = "";//[
        for (Task task : this.criticalpathgraph.getTasks()){
            if (!(task.getId().equals("_START_") || task.getId().equals("_END_"))){
                file += task.getId();
                file += ":";
                file += task.getDuration();
                file += ":";//{
                for (Task tsk : task.getPredecessors()){
                    file += tsk.getId();
                    file += ";";
                }
                file += ",";
            }
        }
        return file;
    }

    /**
     * Attempts to create a criticalPathGraph object from a file at a given path.
     * @param file The path of the file to be converted.
     * @throws Exception Thrown if the file can not be opened.
     */
    private void openFile(String file) throws Exception {
        for (String task: file.split(",")){
            String[] data = task.split(":");
            String id = data[0];
            Float duration = Float.parseFloat(data[1]);
            ArrayList<Task> predecessors = new ArrayList<>();
            for (String preTask:data[2].split(";")){
                predecessors.add(criticalpathgraph.getTask(preTask));
            }
            Task tsk = new Task(id, duration, predecessors);
            criticalpathgraph.addTask(tsk);
            addTaskToGraph(tsk);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}