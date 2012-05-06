/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.feel;

import com.badlogic.gdx.physics.box2d.Body;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.InputNeuron;

/**
 *
 * @author seh
 */
public class Orientation extends InputNeuron {

    public static void newVector(CritterdingBrain brain, Body body, int orientationSteps) {
        double dr = (Math.PI*2.0)/((float)orientationSteps);
        double a =0;
        for (int i = 0; i < orientationSteps; i++) {
            brain.addInput(new Orientation(body, a, a+dr));
            a+=dr;
        }
    }
    
    private final Body body;
    private final double radStart;
    private final double radEnd;
    

    public Orientation(Body b, double radStart, double radEnd) {
        super();
        
        this.body = b;
        this.radStart = radStart;
        this.radEnd = radEnd;
                
    }

    @Override
    public double getOutput() {
        final double v = body.getAngle();
        if (radStart > v)
            return 0.0;
        else if (radEnd <= v) {
            return 1.0;
        }
        else  {
            return (v - radStart) / (radEnd - radStart);
        }
    }
    
//    @Override
//    public double getOutput() {
//        final double a = body.getAngle();
//        if ((a >= radStart) && (a < radEnd)) {
//            return 1.0;
//        }
//        return 0;
//    }
    
    
    
}
