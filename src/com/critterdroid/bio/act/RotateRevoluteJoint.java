/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import jcog.critterding.MotorNeuron;

/**
 *
 * @author seh
 */
public class RotateRevoluteJoint extends MotorNeuron {
    private final RevoluteJoint joint;
    private float factor;

    private float wiggle;
    private final float maxLimit;
    private final boolean reverse;
    private float restitution;
    
    final float maxTorque = 10000.0f;
    float dr = 0;
    
    public RotateRevoluteJoint(RevoluteJoint rj, float jointRange, float factor, boolean reverse) {
        super();
        
        this.maxLimit = jointRange;
        this.joint = rj;
        this.reverse = reverse;
        this.restitution = 1.0f;
        this.wiggle = 0.1f;

        joint.enableLimit(true);
        joint.enableMotor(true);
        
        joint.setMaxMotorTorque(maxTorque);

        setFactor(factor);
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }   
    
    public void rotate(float a) {

        if (a > maxLimit) a = maxLimit;
        if (a < -maxLimit) a = -maxLimit;
        
        float lower = a - wiggle/2.0f;
        float higher = a + wiggle/2.0f;
        
        if (higher < lower) {
            higher = lower;
        }
        else if (lower > higher) {
            lower = higher;
        }
        //System.out.println(maxLimit + " " + lower + " " + higher);
                
        joint.setLimits(lower, higher);
        //joint.setMotorSpeed(joint.getMotorSpeed() + factor);
        
        //System.out.println(factor + " FIRED: " + " "+ a );
        //System.out.println("  "+ joint.getAnchorA() + " " + joint.getAnchorB());        
    }
    
    public float getRotation() {
        return 0.5f * (joint.getLowerLimit() + joint.getUpperLimit());
    }
    
    @Override
    public void onFired() {
        super.onFired();
               
        dr += factor * getOutput() * (reverse ? -1f : 1.0f);
    }

    public void setRestitution(float f) {
        this.restitution = f;
    }

    public void setWiggle(float wiggle) {
        this.wiggle = wiggle;
    }

    @Override
    public void tick() {
        super.tick();
        
        joint.setMotorSpeed(factor);
        
        rotate(dr + getRotation() * restitution);
        dr = 0;
    }
    
    
        

}
