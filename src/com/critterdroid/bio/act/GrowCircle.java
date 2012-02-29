/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Shape;
import jcog.critterding.MotorNeuron;

/**
 *
 * @author seh
 */
public class GrowCircle extends MotorNeuron {
    private final float growth;
    private final Fixture fixture;
    private final float minRadius, maxRadius;

    float targetRadius;
    
    public GrowCircle(Fixture fixture, float growth, float minRadius, float maxRadius) {
        super();
        this.fixture = fixture;
        this.growth = growth;
        this.minRadius = minRadius;
        this.maxRadius = maxRadius;
        targetRadius = getCurrentRadius();        
    }

    public CircleShape getShape() {
        return (CircleShape)fixture.getShape();
    }
    
    public float getCurrentRadius() {
        return getShape().getRadius();
    }
    
    @Override
    public void tick() {
        super.tick();
        float momentum = 0.5f;
        getShape().setRadius( (float) (momentum * getCurrentRadius() + (1.0 - momentum) * targetRadius ) );
    }
        
    @Override
    public void onFired() {
        float r = getCurrentRadius() * growth;
        r = Math.min(r, maxRadius);
        r = Math.max(r, minRadius);
        targetRadius = r;
    }
        
    
}
