/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import calculate.Edge;
import calculate.KochFractal;
import calculate.KochManager;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Bjork
 */
public class CalcRight extends Task implements Observer  {

    public KochFractal koch;
    KochManager kochM;
    
    private boolean cancelTask = false;
    private int size = 0;

    public CalcRight(KochManager kochm) {
        this.koch = new KochFractal();
        koch.setLevel(kochm.getCurrentLevel());
        kochM = kochm;
        koch.addObserver(this);
    }
    
    @Override
    public Object call() {
        koch.generateRightEdge();
        if (!cancelTask) {
            kochM.addCount();
        }
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        //Add to kochmanager
        if (!cancelTask) {
            size++;
            updateMessage("" + size);
            updateProgress(size, Math.pow(4, koch.getLevel() - 1));
            kochM.drawEdge((Edge) arg);
            try {
                Thread.sleep(8);
            } catch (InterruptedException interrupted) {
                //Wait for sleep
            }
        }

    }
    
    public void cancelTask() {
        cancelTask = true;
    }
}