package editeur.controller;

import java.util.List;
import java.util.Observable;
import java.util.Vector;

import editeur.model.commands.CareTaker;
import editeur.model.commands.Command;
import editeur.model.commands.CommandAdd;
import editeur.model.commands.CommandDelete;
import editeur.model.commands.CommandMove;
import editeur.model.commands.CommandRescale;
import editeur.model.commands.CommandRotate;
import editeur.model.geometry.IShape;
import editeur.model.geometry.base.Point;
import editeur.model.geometry.base.Rectangle;
import editeur.view.AbstractApplication;

import editeur.view.GraphicalObjectObserver;


public class Mediator implements IMediator {

    
	//On peut en faire un singleton si on veut
    private static Mediator instance = new Mediator(null);
    private AbstractApplication app;
    private CareTaker           careTaker;
    private Vector<Integer>     selectedShapes;
    private Vector<GraphicalObjectObserver> observers;


    public  Mediator(AbstractApplication app) {
        this.app            = app;
        this.careTaker      = new CareTaker();
        this.selectedShapes = new Vector<Integer>();
        this.observers      = new Vector<GraphicalObjectObserver>();
    }

    //Override SubjectObservee
    @Override
    public void Attach(GraphicalObjectObserver observer){
        this.observers.add(observer);
    }

    @Override
    public void Detach(GraphicalObjectObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void Notify() {
        Vector<GraphicalObjectObserver> clone = (Vector<GraphicalObjectObserver>) observers.clone();
        for (GraphicalObjectObserver observer : clone)
            observer.update();
    }



    //Override IMediator
    @Override
    public void start() {
        Rectangle r = new Rectangle(20,50,100,100);
        r.changeColor(255, 165, 0);
        app.getToolBar().addShape(r);
        this.Notify();
    }
    
    @Override
    public void move(IShape shape, int dx, int dy) {
        Command cmd = new CommandMove(shape, dx, dy);
        cmd.execute();
        careTaker.add(cmd);
        this.Notify();
    }
    
	@Override
	public void group() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unGroup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reColor() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ReScale(IShape shape, double factor) {
		Command cmd = new CommandRescale(shape, factor);
		cmd.execute();
		careTaker.add(cmd);
        this.Notify();
	}

	@Override
	public void rotate(IShape shape, double factor) {
	      Command cmd = new CommandRotate(shape, factor);
	      cmd.execute();
	      careTaker.add(cmd);
	      this.Notify();
	}
	
	public static Mediator getInstance() {
	    return instance;
	}

    @Override
    public void undo() {
        careTaker.undo();
        this.Notify();
        
    }

    @Override
    public void redo() {
        careTaker.redo();
        this.Notify();
        
    }

    @Override
    public void add(IShape shapes, IShape toAdd) {
        Command cmd = new CommandAdd(shapes, toAdd);
        cmd.execute();
        careTaker.add(cmd);
        this.Notify();
        
    }
    @Override
    public void delete(IShape shapes, IShape toDelete) {
        Command cmd = new CommandDelete(shapes, toDelete);
        cmd.execute();
        careTaker.add(cmd);
        this.Notify();
    }

    private void AddToSelection(Point p1 , Point p2){
        //TODO:
    }
    
    private Point computeNewPos(IShape s, Point p1, Point p2) {
        Point oldPos = s.getPosition();
        int stepX = p1.getX() - oldPos.getX();
        int stepY = p1.getY() - oldPos.getY();
        return new Point(p2.getX()-stepX, p2.getY()-stepY);
    }
    
    @Override
    public void MouseClickEvent(boolean fromToolbar ,int clickSide,Point old, Point to) {
        //test x et y sont la pos des clicks
        if (clickSide == LEFT && old != null){
            IShape s;
            if(fromToolbar) {
                s = this.app.getToolBar().getShape(old);
                if (s != null) {
                    Point p = computeNewPos(s, old, to);
                    this.move(s, p.getX(), p.getY());
                }
            }

            else {
                s = this.app.getWhiteBoard().getShape(old);
                if (s != null){
                    Point p = computeNewPos(s, old, to);
                    this.move(s, p.getX(), p.getY());
                }
                else
                    this.AddToSelection(old, to);
            }

        }
        
    }

    @Override
    public void MouseClickEventAddTool(boolean fromToolbar ,int clickSide,Point old, Point to) {
        if (clickSide == LEFT && old != null){
            IShape s;
            if(!fromToolbar) {
                s = this.app.getWhiteBoard().getShape(old);
                if (s != null){
                    Point  p     = computeNewPos(s, old, to);
                    IShape tool  = s.clone();
                    //Todo: Scale si trop grand pour l'appli
                    tool.setPosition(p.getX(), p.getY());
                    if(app.getToolBar().inToolBar(to))
                        this.add(app.getToolBar().getShapeVector(), tool);
                }
            }

        }
    }

    @Override
    public void MouseDraggedEvent(boolean fromToolbar ,int clickSide,Point old, Point to) {
        if (clickSide == LEFT && old != null){
            IShape s;
            if(fromToolbar) {
                s = this.app.getToolBar().getShape(old);
                if (s != null){
                    Point  p     = computeNewPos(s, old, to);
                    IShape tool  = s.clone();
                    tool.setPosition(p.getX(), p.getY());
                    System.out.println("coucou");
                    if(app.getWhiteBoard().inWhiteBoard(p))
                        this.add(app.getWhiteBoard().getShapeVector(), tool);
                }
            }

        }
    }


    //Mouse events ? c'est un peu différent de java awt (la first gen javafx)
	//donc je sais pas si on peut hériter en javafx d'une classe mouse listener

}
