/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.feel;

import com.badlogic.gdx.physics.box2d.Body;
import jcog.critterding.SenseNeuron;

/**
 *
 * @author seh
 */
public class VelocityAxis extends SenseNeuron {
    private final Body body;
    private final boolean xOrY;

    public VelocityAxis(Body b, boolean xOrY) {
        super();
        this.body = b;
        this.xOrY = xOrY;
                
    }

    @Override
    public double getOutput() {
        double v = 0;        
        
        float tv = body.getLinearVelocity().len();
        if (tv!=0) {        
            if (xOrY) {            
                v = body.getLinearVelocity().x/tv;            
            }
            else {
                v = body.getLinearVelocity().y/tv;                        
            }
            
            v -= 0.5f;
            v *= 2f;
        }
                
        //System.out.println("Velocity Axis: " + body.getLinearVelocity() + " " + v);
        
        return v;
        
    }
    
    
    
}
