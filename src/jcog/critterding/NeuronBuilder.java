package jcog.critterding;

import java.util.LinkedList;
import java.util.List;

/**
 * see archneuronz.h
 */
public class NeuronBuilder {

    // inhibitory neuron by flag
    boolean isInhibitory;
    // Consistent Synapses flag
    //boolean hasConsistentSynapses;
    // inhibitory synapses flag
    boolean hasInhibitorySynapses;
    // neuron firing potential
    double firingThreshold;
    
    // motor neuron ability (excititatory only) flag
    //boolean isMotor; //isMotor if motor!=null
    // function
    OutputNeuron motor;
    // synaptic plasticity by flag
    boolean isPlastic;
    // factors
    double plasticityStrengthen;
    double plasticityWeaken;
    public final List<SynapseBuilder> synapseBuilders = new LinkedList();

    public InterNeuron newNeuron(double potentialDecay) {
        InterNeuron ni = new InterNeuron(potentialDecay, 1.0f + (1.0f / plasticityStrengthen), 1.0f - (1.0f / plasticityWeaken));

        ni.isInhibitory = isInhibitory;
        ni.firingThreshold = firingThreshold;

        ni.motor = motor;

        ni.isPlastic = isPlastic;


        return ni;
    }
}
