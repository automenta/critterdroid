/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation.ui;

import com.critterdroid.simulation.SeHSpiderSimulation;
import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author seh
 */
@Deprecated public class ParameterPanelSwing extends JPanel {

    public ParameterPanelSwing() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    public void addSlider(String label, double min, double max, double def, final SliderListener sliderListener, final String unit) {
        final double scale = 1000;
        BoundedRangeModel brm = new DefaultBoundedRangeModel((int) (def * scale), 1, (int) (min * scale), (int) (max * scale));
        final JSlider slider = new JSlider(brm);
        final JLabel unitLabel = new JLabel(unit);
        add(new JLabel(label));
        add(unitLabel);
        add(slider);
        double v = ((double) slider.getValue()) / scale;

        ChangeListener cl = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                double v = ((double) slider.getValue()) / scale;
                sliderListener.onChanged(v);
                unitLabel.setText(v + " " + unit);
            }
        };
        slider.addChangeListener(cl);

        cl.stateChanged(null);
    }
    
}
