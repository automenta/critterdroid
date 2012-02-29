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
public class Orientation extends SenseNeuron {
    private final Body body;
    

    public Orientation(Body b) {
        super();
        this.body = b;
                
    }

    @Override
    public double getOutput() {
        return body.getAngle();
    }
    
    
    
}
