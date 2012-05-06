/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.feel;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import jcog.critterding.InputNeuron;

/**
 *
 * @author seh
 */
public class RevoluteJointAngle extends InputNeuron {
    private final RevoluteJoint joint;

    public RevoluteJointAngle(RevoluteJoint j) {
        super();
        this.joint = j;
                
    }

    @Override
    public double getOutput() {
        
        return joint.getJointAngle();
        
    }
    
    
    
}
