package jcog.critterding;

import com.syncleus.dann.neural.InputNeuron;

public class SenseNeuron implements CritterdingNeuron, InputNeuron {
    
    private double senseInput;

    final public void setInput(double senseInput) {
        this.senseInput = senseInput;
    }
    
   
    @Override
    public double getOutput() {
        return senseInput;
    }

    @Override
    public void tick() {
    }

    @Override
    public final double getInput() {
        return getOutput();
    }
      
}
