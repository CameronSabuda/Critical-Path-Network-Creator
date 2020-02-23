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

    CriticalPathGraph criticalpathgraph = new CriticalPathGraph();
    Graph graph = new Graph();

    @Override
    public void start(final Stage primaryStage) {

        primaryStage.setTitle("Critical Path Network Creator");

        BorderPane userInterface = new BorderPane();
        userInterface.setTop(createToolBar(primaryStage));

        criticalpathgraph = new CriticalPathGraph();
        graph = new Graph();

        userInterface.setCenter(graph.getCellLayer());

        Scene scene = new Scene(userInterface, 1280, 720);
        //buttonGrid.setGridLinesVisible(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    private ToolBar createToolBar(Stage primaryStage){

        class ToolbarButton extends Button{
            private ToolbarButton(String text){
                super(text);
                //set image
                Image image = new Image(this.getClass().getResourceAsStream("button-icons/"+text+".png"));
                this.setGraphic(new ImageView(image));
            }
        }

        //NEW BUTTON
        ToolbarButton newBtn = new ToolbarButton("New");
        newBtn.setOnAction(e -> {
            this.start(primaryStage);
        });

        //OPEN BUTTON
        ToolbarButton openBtn = new ToolbarButton("Open");
        openBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Critical Path Graph Files", "*.cpg"));
            File file = fileChooser.showOpenDialog(primaryStage);
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
            String fileContents = createFile();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Critical Path Graph Files", "*.cpg"));
            File file = fileChooser.showSaveDialog(primaryStage);

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


            gridpane.getChildren().addAll(durText,actText, title, actTextField, durTextField, preTextField, preText, preText2, confirmButton);



            Scene dialogScene = new Scene(gridpane, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        //DELETE ACTIVITY BUTTON
        ToolbarButton delActBtn = new ToolbarButton("Delete Activity");
        delActBtn.setOnAction(e -> {
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
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            dialog.setTitle("Info");
            VBox dialogVbox = new VBox(10);
            dialogVbox.setPadding(new Insets(10));

            String str = "The critical paths of the project are:";
            ArrayList<ArrayList<Task>> criticalPaths = this.criticalpathgraph.getCriticalPaths();
            for (ArrayList<Task> cp : criticalPaths){
                str += "\n";
                for (Task task : cp){
                    str += task.getId() + ", ";
                }
                str = str.trim().substring(0, str.length() - 2);
            }
            Text txt1 = new Text(str);
            Text txt2 = new Text("The minimum completion time is: " + this.criticalpathgraph.getTask("_END_").getLatestFinishTime());

            dialogVbox.getChildren().addAll(txt1, txt2);

            Scene dialogScene = new Scene(dialogVbox, 300, 15*(str.split("\n").length) + 50);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        //TABLE BUTTON
        ToolbarButton tblBtn = new ToolbarButton("Precedence Table");
        tblBtn.setOnAction(e -> {
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

    private void addTaskToGraph(Task task){

        this.graph.beginUpdate();
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

    private void removeTaskFromGraph(Task task){
        this.graph.beginUpdate();
        if (!(task.getId().equals("_START_") || task.getId().equals("_END_"))){
            graph.getModel().removeCell(task, this.criticalpathgraph);
        }

        this.graph.endUpdate();

    }

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