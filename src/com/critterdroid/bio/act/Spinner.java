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
public class Spinner extends OutputNeuron {
    private final Body body;
    private final float torque;
    private final float force = 10f;
 
    public Spinner(Body b, float torque) {
        super();
        this.body = b;
        this.torque = torque;
                
    }
    
    @Override
    public void onFired() {
        super.onFired();
                
        body.applyTorque(torque);
    }
    
    
}
