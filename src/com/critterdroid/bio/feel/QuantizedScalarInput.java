/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.feel;

import jcog.critterding.CritterdingBrain;
import jcog.critterding.SenseNeuron;

/**
 *
 * @author Sue
 */
public abstract class QuantizedScalarInput {

    public QuantizedScalarInput(CritterdingBrain brain, int levels) {
        float dl = 1.0F / ((float) levels);
        float l = 0;
        for (int i = 0; i < levels; i++) {
            brain.addInput(new QSensor(l, l + dl));
            l += dl;
        }
    }

    public class QSensor extends SenseNeuron {

        private final float iStart;
        private final float iStop;

        public QSensor(float iStart, float iStop) {
            this.iStart = iStart;
            this.iStop = iStop;
        }

        @Override
        public double getOutput() {
            final float v = getValue();
            if (iStart > v) {
                return 0.0;
            } else if (iStop <= v) {
                return 1.0;
            } else {
                return (v - iStart) / (iStop - iStart);
            }
        }
    }

    public abstract float getValue();
    
}
