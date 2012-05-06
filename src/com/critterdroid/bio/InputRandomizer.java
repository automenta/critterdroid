/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio;

import jcog.critterding.CritterdingBrain;
import jcog.critterding.InputNeuron;
import jcog.math.RandomNumber;

/**
 *
 * @author seh
 */
public class InputRandomizer {
    private final CritterdingBrain brain;

    public InputRandomizer(CritterdingBrain b) {
        this.brain = b;
    }

    public void random(double min, double max) {
        for (InputNeuron sn : brain.getInputs()) {
            sn.setInput(RandomNumber.getDouble(min, max));
        }
    }
    
}
