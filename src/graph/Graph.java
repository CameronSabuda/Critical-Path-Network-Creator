package graph;

import javafx.scene.layout.Pane;

public class Graph {

    private Model model;

    private MouseGestures mouseGestures;

    private Pane cellLayer;

    public Graph() {
        model = new Model();
        cellLayer = new Pane();
        mouseGestures = new MouseGestures();
    }

    public Pane getCellLayer() { return this.cellLayer; }

    public Model getModel() { return model; }

    public void beginUpdate() {
    }

    public void endUpdate() {
        // add components to graph pane
        getCellLayer().getChildren().addAll(model.getAddedEdges());
        try{
            Cell newCell = getModel().getAddedCells().get(0);
            newCell.setTranslateX(640);
            newCell.setTranslateY(360);
        } catch(Exception e){

        }

        for (Edge edge : model.getAddedEdges()){
            edge.toBack();
        }

        getCellLayer().getChildren().addAll(model.getAddedCells());

        // remove components from graph pane
        getCellLayer().getChildren().removeAll(model.getRemovedCells());
        getCellLayer().getChildren().removeAll(model.getRemovedEdges());

        // enable dragging of cells
        for (Cell cell : model.getAddedCells()) {
            mouseGestures.makeDraggable(cell);
        }

        // merge added & removed cells with all cells
        getModel().merge();
    }
}