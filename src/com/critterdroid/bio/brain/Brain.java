/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import java.util.ArrayList;
import java.util.List;
import jcog.critterding.OutputNeuron;
import jcog.critterding.InputNeuron;

/**
 *
 * @author me
 */
public abstract class Brain {
    final protected List<InputNeuron> sense = new ArrayList();
    final protected List<OutputNeuron> motor = new ArrayList();
    double percentChanceMotorNeuron; //    // percent chance that when adding a new random neuron, it has a motor function

    abstract public void forward();

    public void forwardOutputs() {
        // clear Motor Outputs
        for (OutputNeuron mn : motor) {
            mn.tick();
        }
    }

    public InputNeuron getInput(final int i) {
        return sense.get(i);
    }

    public List<InputNeuron> getInputs() {
        return sense;
    }

    //    MotorNeuron motor(int i) {
    //        return motor.get(i);
    //    }
    //
    //    SenseNeuron sense(int i) {
    //        return sense.get(i);
    //    }
    public int getNumInputs() {
        return sense.size();
    }

    public void addInput(InputNeuron s) {
        sense.add(s);
    }

    public void addOutput(OutputNeuron m) {
        motor.add(m);
    }

    public int getNumOutputs() {
        return motor.size();
    }

    public OutputNeuron getOutput(final int i) {
        return motor.get(i);
    }

    public List<OutputNeuron> getOutputs() {
        return motor;
    }

    public InputNeuron newInput() {
        InputNeuron s = new InputNeuron();
        sense.add(s);
        return s;
    }

    public OutputNeuron newOutput() {
        OutputNeuron m = new OutputNeuron();
        motor.add(m);
        return m;
    }
    
}
