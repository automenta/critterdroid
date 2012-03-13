/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.CritterdingNeuron;
import jcog.critterding.InterNeuron;
import jcog.critterding.NeuronBuilder;
import jcog.critterding.SynapseBuilder;
import jcog.math.RandomNumber;

/**
 *
 * @author Sue
 */
public class RandomWiring implements BrainWiring {
    private final int numNeurons;
    private final int minSynapses;
    private final int maxSynapses;
    private final double percentChanceMotorNeuron;
    private final double percentChanceSensorySynapse;
    double defaultPotentialDecay = 0;
    
    public RandomWiring(int numNeurons, int minSynapses, int maxSynapses, double percentChanceSensorySynapse, double percentChanceMotorNeuron) {
        this.numNeurons = numNeurons;
        this.minSynapses = minSynapses;
        this.maxSynapses = maxSynapses;
        this.percentChanceMotorNeuron = percentChanceMotorNeuron;
        this.percentChanceSensorySynapse = percentChanceSensorySynapse;
                
    }

    
    @Override
    public void wireBrain(CritterdingBrain b) {
        //b.wireRandomly(numNeurons, minSynapses, maxSynapses, percentChanceSensorySynapse, percentChanceMotorNeuron, potentialDecay)
    //}
    //public CritterdingBrain wireRandomly(int numNeurons, int minSynapses, int maxSynapses, double percentChanceSensorySynapse, double percentChanceMotorNeuron, double potentialDecay) {
        final List<NeuronBuilder> neuronBuilders = new ArrayList();

        // determine number of neurons this brain will start with
        //int numNeurons = (int) Math.round(Maths.random(minNeuronsAtBuildtime, maxNeuronsAtBuildtime));

        // create the architectural neurons
        for (int i = 0; i < numNeurons; i++) {
            b.newRandomNeuronBuilder(neuronBuilders, percentChanceMotorNeuron);
        }

        // create architectural synapses
        for (NeuronBuilder n : neuronBuilders) {
            // determine amount of synapses this neuron will start with
            int SynapseAmount = RandomNumber.getInt(minSynapses, maxSynapses);

            // create the architectural neurons
            for (int j = 0; j < SynapseAmount; j++) {
                b.newRandomSynapseBuilder(neuronBuilders, n, percentChanceSensorySynapse);
            }
        }

        Map<InterNeuron, List<SynapseBuilder>> built = new HashMap();
        
        // create all runtime neurons
        for (NeuronBuilder nb : neuronBuilders) {
            final InterNeuron n = nb.newNeuron(defaultPotentialDecay);
            b.addNeuron(n);
            built.put(n, nb.synapseBuilders);
        }

        // create their synapses & link them to their inputneurons
        for (InterNeuron n : built.keySet()) {

            for (SynapseBuilder sb : built.get(n)) {
                CritterdingNeuron i;

                if (sb.isSensorNeuron) {
                    // sensor neuron id synapse is connected to
                    i = b.getRandomSenseNeuron();
                } // if not determine inter neuron id
                else {
                    // as in real life, neurons can connect to themselves
                    i = b.getRandomInterNeuron();
                }


                b.newSynapse(i, n, sb.weight);

            }

//            if (n.getMotor() != null) {
//                b.newSynapse(n, n.getMotor(), 1.0);
//            }
        }

//	//cerr << "total neurons: " << totalNeurons << "total synapses: " << totalSynapses << endl;

    }

    
}
