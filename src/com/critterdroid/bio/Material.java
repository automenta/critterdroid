/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio;

import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author seh
 */
public class Material {
 
    public Color fillColor;
    public Color strokeColor;
    public int strokeWidth;

    public Material(Color fillColor) {
        this(fillColor, null, 0);
    }
    
    public Material(Color fillColor, Color strokeColor, int strokeWidth) {
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }
    
}
