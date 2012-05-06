/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import java.awt.geom.RectangularShape;
import jcog.critterding.OutputNeuron;

/**
 *
 * @author seh
 */
public class StretchRectangle extends OutputNeuron {
    private final float growth;
    private final Fixture fixture;

    float targetRadius;
    private final float minLength;
    private final float maxLength;
    private final float width;
    
    public StretchRectangle(Fixture fixture, float growth, float width, float minLength, float maxLength) {
        super();
        this.fixture = fixture;
        this.growth = growth;
        this.width = width;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

//    public PolygonShape getShape() {
//        return (PolygonShape)fixture.getShape();
//    }
//    
//    
//    @Override
//    public void tick() {
//        super.tick();
//        float momentum = 0.5f;
//        getShape().setRadius( (float) (momentum * getCurrentRadius() + (1.0 - momentum) * targetRadius ) );
//    }
//        
//    @Override
//    public void onFired() {
//        float r = getCurrentRadius() * growth;
//        r = Math.min(r, maxRadius);
//        r = Math.max(r, minRadius);
//        targetRadius = r;
//    }
//        
    
}
