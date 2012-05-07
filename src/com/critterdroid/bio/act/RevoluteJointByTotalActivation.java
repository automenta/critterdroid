/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.critterdroid.bio.brain.Brain;

/**
 *
 * @author seh
 */
public class RevoluteJointByTotalActivation extends RevoluteJointByIndexVote {
    
     public RevoluteJointByTotalActivation(Brain b, RevoluteJoint rj, float angleFrom, float angleTo, int steps) {
         super(b, rj, angleFrom, angleTo, steps % 2 == 1 ? steps + 1 : steps);
             
     }

    @Override public void tick() {
        float left = 0;
        float right = 0;
        float s = 1.0f;
        for (int k = 0; k < steps; k++) {
            int i = k / 2;
            if (k%2 == 0)
                left += stepActivation[k] * s;
            else
                right += stepActivation[k] * s;
            s/=2f;
        }
        float m = Math.max(left, right);
        float target = 0;
        if (m > 0) {
            left /= m;
            right /= m;
         
            if (right > left)
                target = (right - left) * steps;
            else
                target = steps - (left - right) * steps;
            
        }
        else {
            target = steps/2;
        }

        // Fade
        for (int i = 0; i < steps; i++) {
            stepActivation[i] *= stepMomentum;
        }

        rotate(target * dt + angleFrom);        
    }
   
}
