package editeur.model.draw;

import java.util.HashMap;


import editeur.model.geometry.IShape;

import editeur.model.geometry.base.Rectangle;
import editeur.model.geometry.base.SimplePolygon;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

/**
 * The type JavaFxDrawBridge is the concrete implementation of DrawBridge, which can draw on JavaFx objects.
 * Only utilisable with JavaFx.
 */
public class JavaFxDrawBridge implements DrawBridge {

    /**
     * The Map to store the shapes until clear.
     */
    private final HashMap<IShape,javafx.scene.shape.Shape> map =
            new HashMap<IShape,javafx.scene.shape.Shape>();


    /**
     * Draw the current selection on drawSurface javaFX.
     *
     * @param drawSurface the draw surface
     * @param selection   the selection
     */
    @Override
    public void drawSelection(Object drawSurface, Rectangle selection) {
        javafx.scene.shape.Rectangle select = new javafx.scene.shape.Rectangle(selection.getWidth(), selection.getHeight());
        select.setX(selection.getPosition().getX());
        select.setY(selection.getPosition().getY());

        select.setTranslateX(selection.getPosition().getX());
        select.setTranslateY(selection.getPosition().getY());

        select.setStroke(Color.DARKMAGENTA);
        select.setFill(Color.TRANSPARENT);
        select.getStrokeDashArray().add(3.0);

        this.update(drawSurface, select);
    }

    /**
     * Simply Draw a rectangle on the drawSurface JavaFX if possible.
     *
     * @param drawSurface the draw surface.
     * @param r      the r
     *
     */
    @Override
    public void drawRectangle(Object drawSurface, Rectangle r ) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) this.map.get(r);
        if(rect == null) 
            rect = new javafx.scene.shape.Rectangle(r.getWidth(),r.getHeight());

        rect.setX(r.getPosition().getX());
        rect.setY(r.getPosition().getY());

        rect.setWidth(r.getWidth());
        rect.setHeight(r.getHeight());
            
        rect.setArcHeight(r.getRoundHeight());
        rect.setArcWidth(r.getRoundWidth());
        
        rect.setTranslateX(r.getPosition().getX());
        rect.setTranslateY(r.getPosition().getY());
        
        Rotate rotation = new Rotate(r.getRotation(), 
                          r.getRotationCenter().getX(),
                          r.getRotationCenter().getY());
        
        rect.getTransforms().add(rotation);

        this.map.put(r, rect);
        this.fillRectangle(drawSurface, r);
        
    }

    /**
     * Fill the rectangle on drawSurface JavaFx.
     *
     * @param drawSurface the draw surface
     * @param r           the r
     */
    @Override
    public void fillRectangle(Object drawSurface, Rectangle r) {
        javafx.scene.shape.Rectangle rect = (javafx.scene.shape.Rectangle) this.map.get(r);
        rect.setFill(Color.rgb(r.getColorR(),r.getColorG(), r.getColorB(), r.getAlpha()));
        this.update(drawSurface,rect);
    }

    /**
     * Simpy Draw a polygon on the draw surface JavaFx if possible.
     *
     * @param drawSurface the draw surface
     * @param p      the p
     */
    @Override
    public void drawPolygon(Object drawSurface, SimplePolygon p) {
        javafx.scene.shape.Polygon polygon = (javafx.scene.shape.Polygon) this.map.get(p);
        if(polygon == null) {
            double[] pts = p.getPolygonPoints();
            polygon = new javafx.scene.shape.Polygon(pts);
        }

        polygon.setTranslateX(p.getPosition().getX());
        polygon.setTranslateY(p.getPosition().getY());

        polygon.setRotate(p.getRotation());

        this.map.put(p, polygon);
        this.fillPolygon(drawSurface, p);
    }

    /**
     * Fill polygon on drawSurface JavaFx
     *
     * @param drawSurface the draw surface
     * @param p           the p
     */
    @Override
    public void fillPolygon(Object drawSurface, SimplePolygon p) {
        javafx.scene.shape.Polygon poly = (javafx.scene.shape.Polygon) this.map.get(p);
        poly.setFill(Color.rgb(p.getColorR(),p.getColorG(), p.getColorB(), p.getAlpha()));
        this.update(drawSurface,poly);
    }

    /**
     * Update the javaFx Shape contains in drawSurface.
     *
     * @param drawSurface the draw surface
     * @param shape       the shape
     */
    private void update(Object drawSurface, javafx.scene.shape.Shape shape) {
        ((Pane)drawSurface).getChildren().remove(shape);
        ((Pane)drawSurface).getChildren().add(shape);
    }

    /**
     * Clear view of the IShape object.
     *
     * @param whiteboard the whiteboard
     * @param toolbar    the toolbar
     * @param s          the s
     */
    public void clearView(Object whiteboard, Object toolbar, IShape s){
        javafx.scene.shape.Shape shape = map.get(s);

        ((Pane)whiteboard).getChildren().remove(shape);
        ((Pane)toolbar).getChildren().remove(shape);

        map.remove(s);
    }

    /**
     * Full clear view.
     *
     * @param whiteboard the whiteboard
     * @param toolbar    the toolbar
     */
    public void FullClearView(Object whiteboard, Object toolbar){
        ((Pane)whiteboard).getChildren().clear();
        ((Pane)toolbar).getChildren().clear();
        map.clear();
    }

}
