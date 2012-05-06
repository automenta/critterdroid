/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.critterdroid.bio.Material;
import jcog.critterding.OutputNeuron;

/**
 *
 * @author seh
 */
public class ColorBodyTowards extends OutputNeuron {
    private Material material;
    private final Color color;
    private final float momentum;

    public ColorBodyTowards(Body b, Color c, float momentum) {
        this(b.getFixtureList().get(0), c, momentum);
    }
    
    public ColorBodyTowards(Fixture b, Color c, float momentum) {
        super();
        
        this.momentum = momentum;
        this.color = c;
        
        Object m = b.getUserData();
        if (m instanceof Material) {
            material = (Material)m;
        }
        else {
            System.err.println(this + " material is null");
        }
        
    }

    
    @Override
    public void onFired() {
        if (material!=null) {
            material.fillColor.r = material.fillColor.r * momentum + color.r * (1.0f - momentum);
            material.fillColor.g = material.fillColor.g * momentum + color.g * (1.0f - momentum);
            material.fillColor.b = material.fillColor.b * momentum + color.b * (1.0f - momentum);
        }
                    
    }
    
    
    
}
