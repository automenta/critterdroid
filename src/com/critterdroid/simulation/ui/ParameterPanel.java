/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.ValueChangedListener;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.critterdroid.simulation.SeHSpiderSimulation;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author seh
 */
public class ParameterPanel extends Table {
  
    private final float prefWidth, prefHeight;
    private final Skin skin;

    public ParameterPanel(Skin skin, float prefWidth, float prefHeight) {
        super(skin);
        
        this.prefHeight = prefHeight;
        this.prefWidth = prefWidth;
        this.skin = skin;
    }


    public void addSlider(String label, float min, float max, float def, final SliderListener sliderListener, final String unit) {
        final float scale = 1000.0f;
        
        final Slider slider = new Slider(scale * min, scale * max, 1, skin);
        slider.setValue(def*scale);
        
        final Label unitLabel = new Label(unit, skin);
        add(new Label(label, skin)).expandX();
        row();
        add(unitLabel).expandX();
        row();
        add(slider).expandX();
        row();

        ValueChangedListener cl = new ValueChangedListener() {
            @Override
            public void changed(Slider slider, float value) {
                value = (value/scale);
                
                sliderListener.onChanged(value);
                unitLabel.setText(value + " " + unit);
            }            
        };
        
        slider.setValueChangedListener(cl);

        cl.changed(null, def*scale);
    }
    
}
