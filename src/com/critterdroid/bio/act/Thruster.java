/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import com.critterdroid.simulation.App;
import jcog.critterding.OutputNeuron;

/**
 *
 * @author me
 */
public class Thruster extends OutputNeuron {
    private final Body body;
    private final float angle;
    private final float force = 10f;
    private final Shape shape;
    private boolean fired = false;
    private int remainingFrames = 0;
    private int maxRemainingFrames = 40;
    private int addFrames = 7;
    Color c = new Color();
 
    public Thruster(Body b, float angle) {
        super();
        this.body = b;
        this.angle = angle;
        this.shape = body.getFixtureList().get(0).getShape();
                
    }

    Vector2 v = new Vector2();
    
    @Override
    public void onFired() {
        super.onFired();
        
        v.set((float)Math.cos(angle), (float)Math.sin(angle));
        v.mul(force);
        
        body.applyForceToCenter(v);
        
        fired = true;
    }

    public void draw(App app) {
        if (fired) {
            remainingFrames += addFrames;
            remainingFrames = Math.min(remainingFrames, maxRemainingFrames);
            fired = false;
        }
        
        if (remainingFrames > 0) {
            float rangle = body.getAngle() + angle + (float)Math.PI;
            float radius = app.getRadius(shape);

            float cf = ((float)remainingFrames)/((float)maxRemainingFrames);
            c.set(1f, 1f, 1f, 0.1f + 0.9f * cf );
            
            App.setColor(c);
            App.setLineWidth(2.0f);

            float x = body.getWorldCenter().x;
            float y = body.getWorldCenter().y;
            
            float arc = -0.2f;
            int numFlames = 6;
            float d = angle - arc/2.0f;
            
            for (int i = 0; i < numFlames; i++) {
                final float ca = (float)Math.cos(rangle+d);
                final float sa = (float)Math.sin(rangle+d);
                
                float ss = (float)(Math.random()/8f);
                float sx = x + ca * radius * (1.1f + ss);
                float sy = y + sa * radius * (1.1f + ss);

                float rs = (float)(Math.random()/8f);
                float tx = x + ca * radius * (2.0f - rs);
                float ty = y + sa * radius * (2.0f - rs);
                
                d += arc/((float)numFlames);
                App.drawLine(sx, sy, tx, ty);
            }


            remainingFrames--;
        }

    }
    
    
}
