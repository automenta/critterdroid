/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.critterdroid.entities.Critter;
import java.util.ArrayList;
import java.util.List;
import jcog.critterding.CritterdingNeuron;
import jcog.critterding.InterNeuron;


/**
 *
 * @author seh
 */
public class NeuronGridPanel extends CanvasPanel implements Critter.CritterUpdateListener {
    private List<?  extends CritterdingNeuron> neurons;
    private final Critter critter;
    boolean listening;
    int maxDrawn = 4000;
    int neuronWidth = 3;
    float scrollRate = 0.1f;
    
    public NeuronGridPanel(Critter c, ArrayList<? extends CritterdingNeuron> neurons, int width, int height) {
        super(width, height);

        this.critter = c;
        
        critter.addUpdateListener(this);
        listening = true;
        
        setNeurons(neurons);        
    }

    

    @Override
    public void onUpdate(Critter c, double dt) {    
        if (parent==null) {
            critter.removeUpdateListener(this);
            listening = false;
        }
        else {
            repaint();
        }
    }
          
    float drawOffset = 0;

    public void setNeurons(ArrayList<? extends CritterdingNeuron> neurons) {
        this.neurons = neurons;
    }

    float yTouched;
    @Override
    public boolean touchDown(float x, float y, int pointer) {
        yTouched = y;
        this.stage.setTouchFocus(this, pointer);
        return super.touchDown(x, y, pointer);
    }

    @Override
    public void touchUp(float x, float y, int pointer) {
        this.stage.unfocus(this);
        super.touchUp(x, y, pointer);
    }
    

    @Override
    public void touchDragged(float x, float y, int pointer) {
        scroll((int)(scrollRate * (y - yTouched)));
        super.touchDragged(x, y, pointer);
    }      
    
    public boolean scroll(int amount) {
        super.scrolled(amount);
        drawOffset+=amount * getNeuronsAcross();
        drawOffset = Math.max(drawOffset, 0);
        drawOffset = Math.min(drawOffset, neurons.size());
        return true;
    }
       
    
    public int getNeuronsAcross() {
        return (int)(width / neuronWidth);
    }

    @Override
    public void paint(Pixmap p) {
        super.paint(p);
        
        p.setColor(0,0,0,1f);
        p.fill();
        
        if (neurons == null)
            return;
        if (neurons.size() == 0)
            return;
    
        final int n = neurons.size();
        
        final int wide = getNeuronsAcross();
                        
        final float startX = 0;
        float x = startX;
        float y = 0;

        int drawn = 0;

        //g.setLineWidth(0f);

        int k = 0;
        final int dox = (int)Math.floor(drawOffset);
        
        int toDraw = Math.min(n, maxDrawn);
        
        for (int j = 0; j < toDraw; j++) {
            int ii = (j + dox);
            if (ii >= n)
                break;
            
            //TODO use an iterator, in case neurons is a LinkedList
            CritterdingNeuron cn = neurons.get(ii);

            if (drawn++ > maxDrawn) {
                break;
            }

            float v;
            boolean inhibitory = false;
            if (cn instanceof InterNeuron) {
                v = (float) Math.min(Math.abs(((InterNeuron) cn).getPotential()), 1.0f);
                inhibitory = ((InterNeuron) cn).isInhibitory();
            } else {
                v = (float) cn.getOutput();
            }

            float a = 1.0f;
            float r = 0, g = 0, b = 0;

            boolean draw = true;
            if (inhibitory) {
                r = v;
                g = 0;
                b = 0;
                a = v/2.0f;
            } else {
                r = 0;
                g = v;
                b = 0;
                a = v/2.0f;
            }

            if (draw) {
                //p.setColor(r, g, b, a);
                p.setColor(r, g, b, 1.0f);
                p.fillRectangle((int)x, (int)y, neuronWidth, neuronWidth);
            }

            x += neuronWidth;

            k++;

            if (k == wide) {
                y += neuronWidth;
                x = startX;
                k = 0;
            }
            
            if (y > height)
                break;

        }

    }

    public void setNeuronWidth(int i) {
        this.neuronWidth = i;
    }
}
