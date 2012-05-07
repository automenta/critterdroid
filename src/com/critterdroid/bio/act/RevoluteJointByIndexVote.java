/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.critterdroid.bio.brain.Brain;
import jcog.critterding.OutputNeuron;

/**
 *
 * @author seh
 */
public class RevoluteJointByIndexVote  {
    private final RevoluteJoint joint;
    
    private float wiggle = 0.01f;
    
    final float maxTorque = 1.0f;
    
    float dr = 0;
    private final float range;
    protected final float dt;
    protected float stepActivation[];
    protected final int steps;

    float stepMomentum = 0.95f;
    float rotationMomentum = 0.05f;
    float motorSpeed = 15f;
    
    protected final float angleFrom;
    private float lastAngle;
    
    int best=0;
    
    public RevoluteJointByIndexVote(Brain b, RevoluteJoint rj, float angleFrom, float angleTo, int steps) {
        super();
        
        this.joint = rj;

        joint.enableLimit(true);
        joint.enableMotor(true);

        joint.setMotorSpeed(motorSpeed);
        joint.setMaxMotorTorque(maxTorque);        


        this.angleFrom = angleFrom;
        range = angleTo - angleFrom;
        dt = range / ((float)steps);
        this.steps = steps;
        
        stepActivation = new float[steps];
        
        float t = angleFrom;
        for (int i = 0; i < steps; i++) {
            final int ii = i;
            b.addOutput(new OutputNeuron() {

                @Override
                public void onFired() {
                    super.onFired();
                    fire(ii);
                }

                @Override
                public void tick() {
                    super.tick();
                    if (ii == RevoluteJointByIndexVote.this.steps - 1) {
                       RevoluteJointByIndexVote.this.tick();
                    }
                }
                                
                
            });
            t += dt;
        }
    }

    public void fire(int step) {
        stepActivation[step] = Math.min(stepActivation[step]+1.0f, 1.0f);        
    }
    
    public void tick() {
        //A: winner take all        
        float maxValue = 0;
        for (int i = 0; i < steps; i++) {
            if (stepActivation[i] > maxValue) {
                best = i;
                maxValue = stepActivation[i];
            }
        }

        // Fade
        for (int i = 0; i < steps; i++) {
            stepActivation[i] *= stepMomentum;
        }


        //B: TODO vector interpolation       ?
        
        rotate(best * dt + angleFrom);
        
    }
    
    public void rotate(float newA) {
        //System.out.println("rotating: " + a);
        
        float a = lastAngle * rotationMomentum + newA * (1.0f - rotationMomentum);
        
        float lower = a - wiggle/2.0f;
        float higher = a + wiggle/2.0f;
        
        if (higher < lower) {
            higher = lower;
        }
        else if (lower > higher) {
            lower = higher;
        }
        //System.out.println(" " + lower + " " + higher);
                
        
        joint.setLimits(lower, higher);
        //joint.setMotorSpeed(joint.getMotorSpeed() + factor);
        
        //System.out.println(factor + " FIRED: " + " "+ a );
        //System.out.println("  "+ joint.getAnchorA() + " " + joint.getAnchorB());        
        
        lastAngle = a;
    }
    
    public float getRotation() {
        return 0.5f * (joint.getLowerLimit() + joint.getUpperLimit());
    }

    public void setWiggle(float wiggle) {
        this.wiggle = wiggle;
    }
    
}
