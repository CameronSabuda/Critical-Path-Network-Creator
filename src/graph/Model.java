package graph;

import criticalpath.CriticalPathGraph;
import criticalpath.Task;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains and handles all the data for the graph, including all the edges and cells.
 * @author Cameron Sabuda
 */
public class Model {

    private List<Cell> allCells;
    private List<Cell> addedCells;
    private List<Cell> removedCells;

    private List<Edge> allEdges;
    private List<Edge> addedEdges;
    private List<Edge> removedEdges;

    private Map<String,Cell> cellMap; // <id,cell>

    /**
     * Creates an empty model.
     */
    public Model() {

        // clear model, create lists
        clear();
    }

    /**
     * Initialises all the properties of the class so they're all empty.
     */
    public void clear() {

        allCells = new ArrayList<>();
        addedCells = new ArrayList<>();
        removedCells = new ArrayList<>();

        allEdges = new ArrayList<>();
        addedEdges = new ArrayList<>();
        removedEdges = new ArrayList<>();

        cellMap = new HashMap<>(); // <id,cell>

    }

    /**
     * Gets the list of cells that have been added to the model.
     * @return The list of cells added to the model.
     */
    public List<Cell> getAddedCells() {
        return addedCells;
    }

    /**
     * Gets the list of cells that have been removed from the model.
     * @return The list of cells removed from the model.
     */
    public List<Cell> getRemovedCells() {
        return removedCells;
    }

    /**
     * Gets a list of all the cells in the model.
     * @return A list of all the cells in the model.
     */
    public List<Cell> getAllCells() {
        return allCells;
    }

    /**
     * Gets a list of all the edges that have been added to the model.
     * @return A list of all the edges that have been added to the model.
     */
    public List<Edge> getAddedEdges() {
        return addedEdges;
    }

    /**
     * Gets a list of all the edges that have been removed from the model.
     * @return A list of all the edges that have been removed from the model.
     */
    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }

    /**
     * Gets a list of all the edges in the model.
     * @return A list of all the edges in the model.
     */
    public List<Edge> getAllEdges() {
        return allEdges;
    }

    /**
     * Adds a cell to the model based on the given model.
     * @param task The task of which the new cell for the model should be based on.
     */
    public void addCell(Task task) {

        Cell cell = new Cell(task);
        addCell(cell);

    }

    /**
     * Adds a given cell to the model.
     * @param cell Tbe cell to be added to the model.
     */
    private void addCell( Cell cell) {

        addedCells.add(cell);
        cellMap.put( cell.getCellId(), cell);
    }

    /**
     * Removes a cell from the graph and updates the critical path graph object it is based upon.
     * @param task The task to be removed from the graph.
     * @param criticalPathGraph The graph that the task is in.
     */
    public void removeCell(Task task, CriticalPathGraph criticalPathGraph) {
        // Find and remove the cell from the model.
        for (Cell cell : this.allCells){
            if (cell.getCellId().equals(task.getId())){
                System.out.println("Removing cell " + cell.getCellId());
                removeCell(cell);
            }
        }

        // Remove any edges connected to the cell and make the successors/predecessors of the cell attached.
        for (Edge edge : this.allEdges){
            for (Task tsk : task.getPredecessors()){
                if (edge.getSource().getCellId().equals(tsk.getId()) && edge.getTarget().getCellId().equals(task.getId())){
                    System.out.println("Removing edge from " + tsk.getId() + " to " + task.getId());
                    this.removedEdges.add(edge);
                }
            }
            for (Task tsk : criticalPathGraph.getSuccessorTasks(task)){
                if (edge.getSource().getCellId().equals(task.getId()) && edge.getTarget().getCellId().equals(tsk.getId())){
                    System.out.println("Removing edge from " + task.getId() + " to " + tsk.getId());
                    this.removedEdges.add(edge);
                }
            }
        }
    }

    /**
     * Removes a cell from the model.
     * @param cell The cell to removed from the model.
     */
    private void removeCell(Cell cell) {

        removedCells.add(cell);

        cellMap.remove(cell.getCellId());
    }

    /**
     * Adds an edge from a cell with the sourceId to the cell with the targetId
     * @param sourceId The id of the cell that the edge is to go from.
     * @param targetId The id of the cell that the edge is to go to.
     */
    public void addEdge(String sourceId, String targetId) {

        Cell sourceCell = cellMap.get( sourceId);
        Cell targetCell = cellMap.get( targetId);

        Edge edge = new Edge(sourceCell, targetCell);

        addedEdges.add( edge);

    }

    /**
     * Adds all the cells in the addedCells list to the cells in the model and removes all the cells in the removedCells
     * list from the model.
     */
    public void merge() {

        // cells
        allCells.addAll( addedCells);
        allCells.removeAll( removedCells);

        addedCells.clear();
        removedCells.clear();

        // edges
        allEdges.addAll( addedEdges);
        allEdges.removeAll( removedEdges);

        addedEdges.clear();
        removedEdges.clear();

    }
}