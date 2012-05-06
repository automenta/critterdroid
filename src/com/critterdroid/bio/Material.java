/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class Material {
 
    public Color fillColor;
    public Color strokeColor;
    public int strokeWidth;
    public List<Text> texts;

    public void onTouchDown(int i, Vector2 p) {
        
    }
    
    public static class Text {
        public final String text;
        public final Vector2 position;
        public final Vector2 scale;

        //TODO color
        
        public Text(String text, Vector2 position, Vector2 scale) {
            this.text = text;
            this.position = position;
            this.scale = scale;
        }
        
        public Text(String text, float x, float y, float w, float h) {
            this(text, new Vector2(x, y), new Vector2(w, h));            
        }
        
        
        
    }

    public Material(Color fillColor) {
        this(fillColor, null, 0);
    }
    
    public Material(Color fillColor, Color strokeColor, int strokeWidth) {
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }
    
    public void addText(Text t) {
        if (texts == null)
             texts = new LinkedList();
        
        texts.add(t);
    }
    
}
