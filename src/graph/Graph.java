package graph;

import javafx.scene.layout.Pane;

/**
 * A collection of all the Cells and Edges that are to be stored in a Pane to create a Graph looking element.
 * @author Cameron Sabuda
 */
public class Graph {

    private Model model;

    private MouseGestures mouseGestures;

    private Pane cellLayer;

    public Graph() {
        model = new Model();
        cellLayer = new Pane();
        mouseGestures = new MouseGestures();
    }

    /**
     * Gets the CellLayer property.
     * @return The CellLayer of the graph.
     */
    public Pane getCellLayer() { return this.cellLayer; }

    /**
     * Gets the model the graph is based on.
     * @return The model the graph is based on.
     */
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