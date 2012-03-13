/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding;

/**
 *
 * @author seh
 */
public class CritterdingSynapse {
    public final CritterdingNeuron source;
    public final CritterdingNeuron target;
    public double weight;
    public double curInput;
    public boolean invalid = true;
    
    public CritterdingSynapse(CritterdingNeuron source, CritterdingNeuron target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

//    public void invalidate() {
//        this.dirty = true;
//    }
    
    public final double getInput() {
        if (invalid) {
            curInput = source.getOutput();
            invalid = false;
        }
        return curInput;
    }
    
}
