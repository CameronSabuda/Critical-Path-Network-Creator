package graph;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class MouseGestures {

    private final DragContext dragContext = new DragContext();

    public MouseGestures() {
    }

    public void makeDraggable( final Node node) {

        node.setOnMousePressed(onMousePressedEventHandler);
        node.setOnMouseDragged(onMouseDraggedEventHandler);

    }

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            Node node = (Node) event.getSource();

            dragContext.x = node.getBoundsInParent().getMinX() - event.getScreenX() - 640;
            dragContext.y = node.getBoundsInParent().getMinY() - event.getScreenY() - 360;
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            Node node = (Node) event.getSource();

            double offsetX = event.getScreenX() + dragContext.x;
            double offsetY = event.getScreenY() + dragContext.y;

            if(offsetX > 520){//640-120 (width of box)
                offsetX = 520;
            }
            if(offsetX < -640){
                offsetX = -640;
            }

            if(offsetY > 240){//360-80(height of box) - 40(toolbar)
                offsetY = 240;
            }
            if(offsetY < -360){
                offsetY = -360;
            }

            node.relocate(offsetX, offsetY);
        }
    };

    class DragContext {

        double x;
        double y;

    }
}