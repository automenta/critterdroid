package jcog.critterding;

public class InputNeuron implements CritterdingNeuron, com.syncleus.dann.neural.InputNeuron {
    
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
