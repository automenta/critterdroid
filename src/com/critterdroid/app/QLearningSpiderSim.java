/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.app;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.critterdroid.bio.brain.BeccaBrain;
import com.critterdroid.bio.brain.Brain;
import com.critterdroid.bio.brain.QLearningBrain;
import com.critterdroid.bio.brain.RewardFunction;
import com.critterdroid.simulation.App;

/**
 *
 * @author me
 */
public class QLearningSpiderSim extends RLSpiderSim {

    private QLearningBrain b;

    @Override
    public Brain getBrain() {
        b = new QLearningBrain();
        return b;
    }

    @Override
    public void initBrain(final Spider r) {

        b.init(new RewardFunction() {

            final float ws = (float) Math.sqrt((worldSize*2f) * (worldSize*2f));

            @Override
            public float getReward() {

                final Vector2 center = r.getWorldCenter();

                float avgDistance = 0;
                for (Body b : food) {
                    float d = center.dst(b.getWorldCenter()) / ws;
                    avgDistance += d;
                }
                avgDistance /= food.size();
                
                if (avgDistance < 0.1f)
                    return 1f - avgDistance;
                else {
                    float speed = r.torso.getLinearVelocity().len() / 10f;

                    float thrusterEnergy = r.getThrusterTemperature()/50f;
                    
                    float r = -avgDistance*1.5f - thrusterEnergy + speed; //+ speed; // + remainingEnergy; // - movementCost;
                    r = Math.min(r, 1.0f);
                    r = Math.max(r, -1.0f);
                    return r;
                }


            }
        });

    }

    public static void main(String[] args) {
        App.run(new QLearningSpiderSim(), "QLearning in Critterdroid", 1280, 720);
    }

}
