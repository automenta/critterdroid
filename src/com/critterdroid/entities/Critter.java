/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.entities;

import com.badlogic.gdx.Graphics;
import com.critterdroid.simulation.App;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author me
 */
public abstract class Critter {

    public interface CritterUpdateListener {
        public void onUpdate(Critter c, double dt);
    }
    List<CritterUpdateListener> updateListeners = new LinkedList();
    
    abstract public void init(App s);
 
    abstract protected void update(double dt);
    
    public final void _update(double dt) {
        update(dt);
                
        for (CritterUpdateListener l : updateListeners) {
            l.onUpdate(this, dt);
        }
    }

    abstract public void renderUnderlay(Graphics g);
    abstract public void renderOverlay(Graphics g);
    
    public void addUpdateListener(CritterUpdateListener l) {
        updateListeners.add(l);
    }
    public void removeUpdateListener(CritterUpdateListener l) {
        updateListeners.remove(l);
    }
}
