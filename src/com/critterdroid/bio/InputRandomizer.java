/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio;

import jcog.critterding.CritterdingBrain;
import jcog.critterding.SenseNeuron;
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
        for (SenseNeuron sn : brain.getSense()) {
            sn.setInput(RandomNumber.getDouble(min, max));
        }
    }
    
}
