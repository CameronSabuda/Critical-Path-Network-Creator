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

public class Model {

    private List<Cell> allCells;
    private List<Cell> addedCells;
    private List<Cell> removedCells;

    private List<Edge> allEdges;
    private List<Edge> addedEdges;
    private List<Edge> removedEdges;

    private Map<String,Cell> cellMap; // <id,cell>

    public Model() {

        // clear model, create lists
        clear();
    }

    public void clear() {

        allCells = new ArrayList<>();
        addedCells = new ArrayList<>();
        removedCells = new ArrayList<>();

        allEdges = new ArrayList<>();
        addedEdges = new ArrayList<>();
        removedEdges = new ArrayList<>();

        cellMap = new HashMap<>(); // <id,cell>

    }

    public List<Cell> getAddedCells() {
        return addedCells;
    }

    public List<Cell> getRemovedCells() {
        return removedCells;
    }

    public List<Cell> getAllCells() {
        return allCells;
    }

    public List<Edge> getAddedEdges() {
        return addedEdges;
    }

    public List<Edge> getRemovedEdges() {
        return removedEdges;
    }

    public List<Edge> getAllEdges() {
        return allEdges;
    }

    public void addCell(Task task) {

        Cell cell = new Cell(task);
        addCell(cell);

    }

    private void addCell( Cell cell) {

        addedCells.add(cell);
        cellMap.put( cell.getCellId(), cell);
    }

    public void removeCell(Task task, CriticalPathGraph criticalPathGraph) {
        for (Cell cell : this.allCells){
            if (cell.getCellId().equals(task.getId())){
                System.out.println("Removing cell " + cell.getCellId());
                removeCell(cell);
            }
        }

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

    private void removeCell(Cell cell) {

        removedCells.add(cell);

        cellMap.remove(cell.getCellId());
    }

    public void addEdge( String sourceId, String targetId) {

        Cell sourceCell = cellMap.get( sourceId);
        Cell targetCell = cellMap.get( targetId);

        Edge edge = new Edge( sourceCell, targetCell);

        addedEdges.add( edge);

    }

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