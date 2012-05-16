/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.app;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.critterdroid.bio.brain.BeccaBrain;
import com.critterdroid.bio.brain.Brain;
import com.critterdroid.bio.brain.RewardFunction;
import com.critterdroid.simulation.App;

/**
 *
 * @author me
 */
public class BeccaSpiderSim extends RLSpiderSim {

    private BeccaBrain b;

    @Override
    public Brain getBrain() {
        b = new BeccaBrain();
        return b;
    }

    @Override
    public void initBrain(final Spider r) {

        b.init(2f, 1, new RewardFunction() {

            final float ws = (float) Math.sqrt(worldSize * worldSize);

            @Override
            public float getReward() {
                //1. weighted "closeness" to food particles
                //2. remaining energy

                float remainingEnergy = 0;

                final Vector2 center = r.getWorldCenter();

                float weightedCloseness = 0;
                for (Body b : food) {
                    float d = center.dst(b.getWorldCenter()) / ws;
                    weightedCloseness += 1.0f / (1 + d);
                }
                weightedCloseness /= food.size();

                //float movementCost = brain.getOutputChangeMagnitude();

                float speed = r.torso.getLinearVelocity().len() / 4f;

                return weightedCloseness * 2f - 1f; //+ speed; // + remainingEnergy; // - movementCost;
            }
        });

    }

    public static void main(String[] args) {
        App.run(new BeccaSpiderSim(), "OpenBECCA in Critterdroid", 1280, 720);
    }

}
