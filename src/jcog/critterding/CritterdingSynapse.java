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

    public CritterdingSynapse(CritterdingNeuron source, CritterdingNeuron target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public final double getInput() {
        return source.getOutput();
    }
    
}
