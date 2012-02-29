/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.critterdroid.entities.Critter;
import java.util.LinkedList;

/**
 *
 * @author seh
 */
abstract public class LineChartPanel extends CanvasPanel implements Critter.CritterUpdateListener {

    LinkedList<Float> history = new LinkedList();
    private final float yminValue;
    private final float ymaxValue;
    
    public LineChartPanel(Critter c, int width, int height) {
        this(c, width, height, -1, -1);
    }
    
    public LineChartPanel(Critter c, int width, int height, float minValue, float maxValue) {
        super(width, height); 

        this.yminValue = minValue;
        this.ymaxValue = maxValue;
        
        c.addUpdateListener(this);
        
    }

    public void push(float v) {
        if (history.size() >= width-1) {
            history.removeLast();
        }
        history.push(v);
        repaint();        
    }

    @Override
    public void paint(Pixmap p) {
        super.paint(p);
        
        if (history.size() < 2)
            return;
        
        p.setColor(0,0,0,1f);
        p.fill();
        
        float maxValue, minValue;

        if (yminValue == ymaxValue) {
            minValue = maxValue = history.get(0);
            for (int i = 0; i < history.size(); i++) {
                final float v = history.get(i);
                if (v > maxValue)
                    maxValue = v;
                if (v < minValue)
                    minValue = v;            
            }
        }
        else {
            minValue = yminValue;
            maxValue = ymaxValue;            
        }
        
        p.setColor(1f, 1f, 1f, 1f);
        
        int px = 0, py = 0;
        for (int i = 0; i < history.size(); i++) {
            final float v = history.get(i);
            
            float den = (maxValue - minValue);
            if (den == 0) den = 1.0f;
            
            final float prop = (v - minValue) / den;
            
            
            int y = (int)(height * prop);

            int nx = ((int)width) - i;
            int ny = ((int)height) - y;
            
            if (i > 0)
                p.drawLine(px, py, nx, ny);
            
            px = nx;
            py = ny;
        }
        
    }
    
    
    
}
