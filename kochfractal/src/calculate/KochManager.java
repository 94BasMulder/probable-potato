/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import sample.Main;
import threads.*;
import timeutil.TimeStamp;

/**
 *
 * @author Bjork
 */
public class KochManager {

    private Main application;

    private ArrayList<Edge> edges = new ArrayList<Edge>();

    private ExecutorService pool;

    private TimeStamp ts;

    private CalcLeft calcleft;
    private CalcBottom calcbottom;
    private CalcRight calcright;

    private int currentLevel;

    private String calcTime;
    
    private int calcDone = 0;

    public KochManager(Main appl) {
        this.application = appl;
        
        //Threads
        pool = Executors.newFixedThreadPool(3);
        
        calcleft = new CalcLeft(this);
        calcbottom = new CalcBottom(this);
        calcright = new CalcRight(this);
    }

    public void changeLevel(int currentLevel) {
        calcleft.cancelTask();
        calcright.cancelTask();
        calcbottom.cancelTask();



        this.currentLevel = currentLevel;
        
        //Calc edges
        ts = new TimeStamp();
        ts.setBegin("Begin calculatie");
        edges.clear();
        calcDone = 0;
        
        calcleft = new CalcLeft(this);
        calcbottom = new CalcBottom(this);
        calcright = new CalcRight(this);        
        
        application.progressBottom.progressProperty().bind(calcbottom.progressProperty());
        application.progressLeft.progressProperty().bind(calcleft.progressProperty());
        application.progressRight.progressProperty().bind(calcright.progressProperty());
        
        application.progressBottomEdgesNr.textProperty().bind(calcbottom.messageProperty());
        application.progressLeftEdgesNr.textProperty().bind(calcleft.messageProperty());
        application.progressRightEdgesNr.textProperty().bind(calcright.messageProperty());

        pool.submit(calcbottom);
        pool.submit(calcleft);
        pool.submit(calcright);
        
    }

    public void drawEdges() {
        application.setTextNrEdges("" + calcright.koch.getNrOfEdges());
        application.setTextCalc(calcTime);

        ts = new TimeStamp();

        ts.setBegin("Begin tekenen");

        application.clearKochPanel();

        for (Edge e : edges) {
            application.drawEdge(e);
        }

        ts.setEnd("Einde tekenen");

        application.setTextDraw(ts.toString());
    }
    
     public synchronized void drawEdge(Edge e) {
       // application.drawEdge(e);
         edges.add(e);
         final Edge tempWhiteEdge = new Edge(e.X1, e.Y1, e.X2, e.Y2, Color.WHITE);
         
         Platform.runLater(new Runnable(){
            @Override
            public void run() {
                application.drawEdge(tempWhiteEdge);
            }
        });
         
        
    }
     
    public synchronized void addCount() {
        calcDone++;
        addToList();;
    }

    public void addToList() {
        if (calcDone == 3) {
            ts.setEnd("Einde calculatie");
            calcTime = ts.toString();
            application.requestDrawEdges();
            System.out.println("Level: " + calcright.koch.getLevel() + "  Tijd: " + ts.toString() + "  Edges:" + calcright.koch.getNrOfEdges());
            
            calcDone = 0;
        }
    }
    
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void stopPool() {
        pool.shutdown();
    }

}
