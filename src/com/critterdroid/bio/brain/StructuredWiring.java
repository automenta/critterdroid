/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import com.critterdroid.entities.DistributedSpider.BrainWiring;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.InterNeuron;
import jcog.critterding.NeuronBuilder;
import jcog.critterding.SenseNeuron;

/**
 *
 * @author Sue
 */
public class StructuredWiring implements BrainWiring {

    public StructuredWiring(int numSenseNeuronsPerInput, int numMotorNeuronsPerOutput, int numInterNeuronsPerSense, int numInterNeuronsPerMotor) {
    }

    
//    @Override
//    public void wireBrain(CritterdingBrain b) {
//        for (SenseNeuron sn : b.getInputNeurons()) {
//            NeuronBuilder nb = new NeuronBuilder();
//        // create all runtime neurons
//        for (NeuronBuilder nb : neuronBuilders) {
//            final InterNeuron n = nb.newNeuron(defaultPotentialDecay, maxSynapses);
//            b.addNeuron(n);
//        }            
//        }
//        
//    }

    @Override
    public void wireBrain(CritterdingBrain b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
