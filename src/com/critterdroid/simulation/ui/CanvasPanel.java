/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation.ui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 *
 * @author seh
 */
public class CanvasPanel extends Image {

    
    public static int nextP2(int x) {
        if (x >= 512) return 1024;
        if (x >= 256) return  512;
        if (x >= 128) return  256; 
        if (x >= 64) return  128; 
        if (x >= 32) return  64; 
        if (x >= 16) return  32; 
        if (x >= 8) return  16; 
        if (x >= 4) return  8; 
        if (x >= 2) return  4; 
        if (x >= 1) return  2; 
        return 0;
    }
    private final Pixmap p;
    private final Texture tex;
    
    public CanvasPanel(int width, int height) {
        super();
                
        // Create an empty dynamic pixmap
        p = new Pixmap(width, height, Pixmap.Format.RGBA8888); // Pixmap.Format.RGBA8888);

        // Create a texture to contain the pixmap
        tex = new Texture(nextP2(width), nextP2(height), Pixmap.Format.RGBA8888); // Pixmap.Format.RGBA8888);
        tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Linear);
        tex.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
                
        setRegion(new TextureRegion(tex, width, height));
        
    }

    @Override
    public void layout() {
        paint(p);
        
        tex.draw(p, 0, 0);
        
        super.layout();
    }
    
    public void paint(Pixmap p) {
    }

    public void repaint() {
        invalidate();
    }
            
    
}
