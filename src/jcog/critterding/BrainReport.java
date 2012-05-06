/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Sue
 */
public class BrainReport {

    public BrainReport(CritterdingBrain b) {
        
        System.out.print(b.getNumInputs() + " inputs | " + b.getNumOutputs() + " outputs | " + b.getNumInterNeurons() + " inter neurons | " + b.getNumSynapses() + " total synapses");

        Set<InputNeuron> senses = new HashSet(b.getInputs());
        Set<OutputNeuron> motors = new HashSet(b.getOutputs());
        
        for (InterNeuron i : b.getInter()) {
            OutputNeuron mn = i.motor;
            if (mn!=null) 
                motors.remove(mn);            
        }
        
        for (CritterdingSynapse s : b.getSynapses()) {
            CritterdingNeuron n = s.source;
            if (n instanceof InputNeuron) {
                senses.remove(n);
            }                    
        }
        
        double deadSenses = ((double)senses.size()) / ((double)b.getNumInputs());
        double deadMotors = ((double)motors.size()) / ((double)b.getNumOutputs());
        System.out.println(" ||  dead senses: " + deadSenses*100.0 + "%, dead motors: " + deadMotors*100.0 + "%");
        
    }
}
