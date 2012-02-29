/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.critterdroid.bio.brain.MeshWiring;
import com.critterdroid.entities.Critter;
import java.util.ArrayList;
import java.util.List;
import jcog.critterding.CritterdingNeuron;
import jcog.critterding.InterNeuron;


/**
 *
 * @author seh
 */
public class MeshPanel extends CanvasPanel implements Critter.CritterUpdateListener {
    private final Critter critter;
    boolean listening;
    int maxDrawn = 4000;
    int neuronWidth = 3;
    float scrollRate = 0.1f;
    private final int scale;
    private final MeshWiring mesh;
    
    public MeshPanel(Critter c, MeshWiring m, int scale) {
        super(m.getWidth() * scale, m.getHeight() * scale);

        this.critter = c;
        this.scale = scale;
        this.mesh = m;
        
        critter.addUpdateListener(this);
        listening = true;
        
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
          


    @Override
    public void paint(Pixmap p) {
        super.paint(p);
        
        p.setColor(0,0,0,1f);
        p.fill();
        
    
                                
        final float startX = 0;
        float x = startX;
        float y = 0;

        int drawn = 0;

        //g.setLineWidth(0f);

        int k = 0;
        
        for (int yy = 0; yy < mesh.getHeight(); yy++) {
            for (int xx = 0; xx < mesh.getWidth(); xx++) {
                
                //TODO use an iterator, in case neurons is a LinkedList
                CritterdingNeuron cn = mesh.getMesh()[yy][xx];


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
                    p.fillRectangle((int)x, (int)y, scale, scale);
                }

                x += scale;

                k++;


            }
            
            y += scale;
            x = startX;
            k = 0;

        }

    }

    public void setNeuronWidth(int i) {
        this.neuronWidth = i;
    }
}
