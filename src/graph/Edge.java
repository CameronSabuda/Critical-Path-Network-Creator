package graph;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class Edge extends Group {

    private Cell source;
    private Cell target;

    public Edge(Cell source, Cell target) {

        this.source = source;
        this.target = target;

        Line line = new Line();

        line.startXProperty().bind( source.layoutXProperty().add((source.getBoundsInParent().getWidth() / 2.0)+640));
        line.startYProperty().bind( source.layoutYProperty().add((source.getBoundsInParent().getHeight() / 2.0)+360));

        line.endXProperty().bind( target.layoutXProperty().add((target.getBoundsInParent().getWidth() / 2.0)+640));
        line.endYProperty().bind( target.layoutYProperty().add((target.getBoundsInParent().getHeight() / 2.0)+360));

        Line arrow1 = new Line();
        Line arrow2 = new Line();

        InvalidationListener updater = (Observable o) -> {
            double ex = line.getEndX();
            double ey = line.getEndY();
            double sx = line.getStartX();
            double sy = line.getStartY();

            double x = sx + (ex - sx)/2;
            double y = sy + (ey - sy)/2;

            arrow1.setEndX(x);
            arrow1.setEndY(y);
            arrow2.setEndX(x);
            arrow2.setEndY(y);

            if (ex == sx && ey == sy) {
                // arrow parts of length 0
                arrow1.setStartX(ex);
                arrow1.setStartY(ey);
                arrow2.setStartX(ex);
                arrow2.setStartY(ey);
            } else {
                double factor = 20 / Math.hypot(sx-ex, sy-ey);
                double factorO = 7 / Math.hypot(sx-ex, sy-ey);

                // part in direction of main line
                double dx = (sx - ex) * factor;
                double dy = (sy - ey) * factor;

                // part orthogonal to main line
                double ox = (sx - ex) * factorO;
                double oy = (sy - ey) * factorO;

                arrow1.setStartX(x + dx - oy);
                arrow1.setStartY(y + dy + ox);
                arrow2.setStartX(x + dx + oy);
                arrow2.setStartY(y + dy - ox);
            }
        };

        // add updater to properties
        line.startXProperty().addListener(updater);
        line.startYProperty().addListener(updater);
        line.endXProperty().addListener(updater);
        line.endYProperty().addListener(updater);
        updater.invalidated(null);

        /*Line arrowLeft = new Line();

        arrowLeft.startXProperty().bind(line.endXProperty().subtract(line.startXProperty()));
        arrowLeft.endXProperty().bind(line.);
        */

        getChildren().addAll(line, arrow1, arrow2);

    }

    public Cell getSource() {
        return source;
    }

    public Cell getTarget() {
        return target;
    }

}