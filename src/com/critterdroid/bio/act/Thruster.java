/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import jcog.critterding.OutputNeuron;

/**
 *
 * @author me
 */
public class Thruster extends OutputNeuron {
    private final Body body;
    private final float angle;
    private final float force = 10f;
 
    public Thruster(Body b, float angle) {
        super();
        this.body = b;
        this.angle = angle;
                
    }

    Vector2 v = new Vector2();
    
    @Override
    public void onFired() {
        super.onFired();
        
        v.set((float)Math.cos(angle), (float)Math.sin(angle));
        v.mul(force);
        
        body.applyForceToCenter(v);
    }
    
    
}
