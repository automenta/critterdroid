/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.act;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.critterdroid.bio.Material;
import jcog.critterding.MotorNeuron;

/**
 *
 * @author seh
 */
public class ColorBody extends MotorNeuron {
    private Material material;
    private final int index;
    private final float delta;
    private final boolean posOrNeg;
    

    public ColorBody(Fixture b, int index, boolean posOrNeg, float delta /*float restoreMomentum*/) {
        super();
        
        this.index = index;
        this.posOrNeg = posOrNeg;
        this.delta = delta;
        
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
        if (material == null)
            return;
        
        float v;
        
        if (index == 0) v = material.color.r;
        else if (index == 1) v = material.color.g;
        else /*if (index == 2)*/ v = material.color.b;
        
        if (posOrNeg) v+= delta; else v-= delta;
        
        if (v < 0) v = 0;
        if (v > 1.0f) v = 1.0f;
        
        if (index == 0) material.color.r = v;
        else if (index == 1) material.color.g = v;
        else if (index == 2) material.color.b =v;
    }
    
    
    
}
