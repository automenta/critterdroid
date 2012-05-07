/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding;

import com.critterdroid.bio.brain.Brain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jcog.math.RandomNumber;

/**
 * java port of critterding's BRAINZ system
 */
public class CritterdingBrain extends Brain  {
    public final Map<CritterdingNeuron, CritterdingSynapse[]> neuronSynapses = new HashMap();
    public final List<CritterdingSynapse> synapses = new LinkedList();
    final List<InterNeuron> neuron = new ArrayList();
    
    private transient InterNeuron[] neuronArray = null;
    private transient CritterdingSynapse[] synapseArray = null;
    
    double percentChanceInhibitoryNeuron;      // percent chance that when adding a new random neuron, it's inhibitory
    double percentChanceConsistentSynapses;    // synaptic consistancy, meaning all synapses of a neuron will be OR I OR E:  if set to 0, neurons will have mixed I and E synapses
    double percentChanceInhibitorySynapses;    //    // percent chance that when adding a new random neuron, it has inhibitory synapses
    double percentChancePlasticNeuron; //    // percent chance that when adding a new random neuron, it is has synaptic plasticity
    double minPlasticityStrengthen;    //    // min/max synaptic plasticity strengthening factor
    double maxPlasticityStrengthen;    //    // min/max synaptic plasticity strengthening factor
    double minPlasticityWeaken;    //    // min/max synaptic plasticity weakening factor
    double maxPlasticityWeaken;    //    // min/max synaptic plasticity weakening factor
    double minFiringThreshold; //    // min/max firing threshold
    double maxFiringThreshold; //    // min/max firing threshold
    //double percentChanceSensorySynapse;    //    // percent chance that a new synapse is connected to a sensor neuron
    double percentMutation;    //    // brain architecture mutation factor @ mutation time (%)
//    // INFO
//    // total neuron & connection keepers
//    // after every time instance, this will contain how many neurons where fired in that instant (energy usage help)
    int neuronsFired;
    int motorNeuronsFired;

    public CritterdingBrain(int numInputs, int numOutputs) {
        this();

        for (int i = 0; i < numInputs; i++) {
            newInput();
        }

        for (int i = 0; i < numOutputs; i++) {
            newOutput();
        }


    }

    public CritterdingBrain() {
        super();
//        percentChanceMotorNeuron = 0.25;
//        percentChanceSensorySynapse = 0.25;

        percentChanceInhibitoryNeuron = 0.5;
        percentChanceInhibitorySynapses = 0.50;

        percentChanceConsistentSynapses = 0.10;



        percentChancePlasticNeuron = 0.95;

        minPlasticityStrengthen = 2;
        maxPlasticityStrengthen = 100;
        minPlasticityWeaken = 2;
        maxPlasticityWeaken = 100;


        minFiringThreshold = 0.5;

        maxFiringThreshold = 0.95;

        percentMutation = 0.01;

    }

    // RUN TIME
//    public void clearInputs() {
//        for (SenseNeuron sn : sense) {
//            sn.senseInput = 0;
//        }
//    }



    public CritterdingSynapse[] getIncomingSynapses(final InterNeuron n) {
        return neuronSynapses.get(n);
    }

    public void forward() {
        // reset fired neurons counter
        neuronsFired = 0;
        motorNeuronsFired = 0;

        if (synapseArray == null) {            
            synapseArray = synapses.toArray(new CritterdingSynapse[synapses.size()]);
        }
        if (neuronArray ==null) {
            neuronArray = neuron.toArray(new InterNeuron[neuron.size()]);
        }
        
        for (final CritterdingSynapse s : synapseArray) {
            s.invalid = true;
        }

        for (final InterNeuron n : neuronArray) {
            n.forward(getIncomingSynapses(n));

            boolean fired = n.nextOutput!=0;
            OutputNeuron mn = n.motor;
            
            if (fired) {
                neuronsFired++;

                if (mn != null) {
                    motorNeuronsFired++;
                    mn.setFiring(true);
                }
            }
            else {
                if (mn != null) {
                    mn.setFiring(false);
                }                
            }
        }

        // commit outputs at the end
        for (final InterNeuron in : neuronArray) {
            in.output = in.nextOutput;
        }

    }

    public List<CritterdingSynapse> getSynapses() {
        return synapses;
    }

    public OutputNeuron getRandomMotorNeuron() {
        return motor.get((int) RandomNumber.getInt(0, motor.size() - 1));
    }

    public InterNeuron getRandomInterNeuron() {
        return neuron.get((int) RandomNumber.getInt(0, neuron.size() - 1));
    }

    //final Set<Integer> mappedSenseNeurons = new HashSet();
    public InputNeuron getRandomSenseNeuron() {
        final int i = (int) RandomNumber.getInt(0, sense.size() - 1);
        //mappedSenseNeurons.add(i);
        //System.out.println("sense neuron: " + ((((float)mappedSenseNeurons.size()) / ((float)this.getNumInputs()))));
        return sense.get(i);
    }

    // build time functions
    public NeuronBuilder newRandomNeuronBuilder(final List<NeuronBuilder> neuronBuilders, double percentChanceMotorNeuron) {
        // new architectural neuron
        NeuronBuilder an = new NeuronBuilder();

        if (Math.random() <= percentChanceMotorNeuron) {
            OutputNeuron mn = getRandomMotorNeuron();

            // check if motor already used
            boolean proceed = true;
            for (NeuronBuilder nb : neuronBuilders) {
                if (nb.motor == mn) {
                    proceed = false;
                    break;
                }
            }

            if (proceed) {
                an.motor = mn;
            }
        } else if (Math.random() <= percentChanceInhibitoryNeuron) {
            an.isInhibitory = true;
        }

        // does it have synaptic plasticity ?
        if (Math.random() <= percentChancePlasticNeuron) {
            an.isPlastic = true;
            an.plasticityStrengthen = RandomNumber.getDouble(minPlasticityStrengthen, maxPlasticityStrengthen);
            an.plasticityWeaken = RandomNumber.getDouble(minPlasticityWeaken, maxPlasticityWeaken);
        }

//        // does it have consistent synapses ?
//        if (Math.random() <= percentChanceConsistentSynapses) {
//            an.hasConsistentSynapses = true;
//
//            // if so, does it have inhibitory synapses ?
//            if (Math.random() <= percentChanceInhibitorySynapses) {
//                an.hasInhibitorySynapses = true;
//            }
//        }

        // determine firing threshold
        if (an.motor != null) {
            an.firingThreshold = maxFiringThreshold;
        } else {
            an.firingThreshold = RandomNumber.getDouble(minFiringThreshold, maxFiringThreshold);
        }


        // push it on the vector
        neuronBuilders.add(an);

        return an;
    }

    public SynapseBuilder newRandomSynapseBuilder(List<NeuronBuilder> neuronBuilders, NeuronBuilder bn, double percentChanceSensorySynapse) {
        final float weight;

        // synaptic weight
//        if (bn.hasConsistentSynapses) {
//            weight = (bn.hasInhibitorySynapses) ? -1.0f : 1.0f;
//        } else {
            weight = (Math.random() <= percentChanceInhibitorySynapses) ? -1.0f : 1.0f;
//        }

        // new architectural synapse
        //  is it connected to a sensor neuron ?
        //  < 2 because if only 1 archneuron, it can't connect to other one
        SynapseBuilder as = new SynapseBuilder(weight, (Math.random() <= percentChanceSensorySynapse || neuronBuilders.size() < 2));

        bn.synapseBuilders.add(as);

        return as;
    }

    public List<InterNeuron> getInter() {
        return neuron;
    }

    public int getTotalNeurons() {
        return neuron.size() + sense.size() + motor.size();
    }

    public int getNumInterNeurons() {
        return neuron.size();
    }

    public int getNumSynapses() {
        return synapses.size();
    }

    public void removeSynapse(CritterdingSynapse s) {
        System.err.println("remove synapse not implemented yet");
//        if (synapses.remove(s)) {
//            List<CritterdingSynapse> incoming = neuronSynapses.get(s.target);
//            if (!incoming.remove(s)) {
//                System.err.println("Error removing " + s);
//            }
//        }
        synapseArray = null;
    }
    
    public CritterdingSynapse newSynapse(CritterdingNeuron from, CritterdingNeuron to, double weight) {
        final CritterdingSynapse s = new CritterdingSynapse(from, to, weight);
        synapses.add(s);

        CritterdingSynapse[] incoming = neuronSynapses.get(to);
        if (incoming == null) {
            incoming = new CritterdingSynapse[] { s };
            neuronSynapses.put(to, incoming);
        }
        else {
            incoming = Arrays.copyOf(incoming, incoming.length+1);
            incoming[incoming.length-1] = s;
            neuronSynapses.put(to, incoming);
        }

        synapseArray = null;
        
        return s;
    }

//		// load save architecture (serialize)
//			void			setArch(string* content);
//			string*			getArch();
//
//    // build commands
//    // functions
//    void copyFrom(const   Brainz& otherBrain);
//			void			mergeFrom(const Brainz& otherBrain1, const Brainz& otherBrain2);
//    private void newMotorSynapse(InterNeuron from, MotorNeuron to) {
//        add(new MotorSynapse(from, to));
//    }
//    public void forwardUntilAnswer() {
//        //		neuronsFired = 0;
//
//		// clear Motor Outputs
//		for ( unsigned int i=0; i < numberOfOutputs; i++ )
//			Outputs[i].output = false;
//
//		// clear Neurons
//		for ( unsigned int i=0; i < totalNeurons; i++ )
//		{
//			Neurons[i].output = 0;
//			Neurons[i].potential = 0.0f;
//		}
//
//		unsigned int counter = 0;
//		bool motorFired = false;
//
//		while ( counter < 1000 && !motorFired )
//		{
//			for ( unsigned int i=0; i < totalNeurons; i++ )
//			{
//				NeuronInterz* n = &Neurons[i];
//
//				n->process();
//
//				// if neuron fires
//				if ( n->waitoutput != 0 )
//				{
//					neuronsFired++;
//
//					// motor neuron check & exec
//					if ( n->isMotor )
//					{
//						motorFired = true;
//						*Outputs[n->motorFunc].output = true;
//						//cerr << "neuron " << i << " fired, motor is " << Neurons[i]->MotorFunc << " total now " << Outputs[Neurons[i]->MotorFunc]->output << endl;
//					}
//				}
//			}
//			// commit outputs at the end
//			for ( unsigned int i=0; i < totalNeurons; i++ ) Neurons[i].output = Neurons[i].waitoutput;
//
//			counter++;
//		}
//    }
//    public void removeObsoleteMotorsAndSensors() {
//		for ( int i = 0; i < (int)ArchNeurons.size(); i++ )
//		{
//			ArchNeuronz* an = &ArchNeurons[i];
//			// disable motor neurons
//			if ( an->isMotor )
//			{
//				if ( findMotorNeuron( an->motorID ) == -1 )
//				{
//					an->isMotor = false;
//				}
//			}
//
//			// disable sensor inputs
//			for ( int j = 0; j < (int)an->ArchSynapses.size(); j++ )
//			{
//				ArchSynapse* as = &an->ArchSynapses[j];
//				if ( as->isSensorNeuron )
//				{
//					if ( findSensorNeuron( as->neuronID ) == -1 )
//					{
//						an->ArchSynapses.erase( an->ArchSynapses.begin()+j );
//						j--;
//					}
//				}
//			}
//		}
//    }

    public void addNeuron(InterNeuron n) {
        neuron.add(n);
        neuronArray = null;
    }
    public void removeNeuron(InterNeuron n) {
        neuron.remove(n);
        this.neuronSynapses.remove(n);
        neuronArray = null;
    }

    public int getMotorNeuronsFired(boolean reset) {        
        int i = motorNeuronsFired;
        if (reset)
            motorNeuronsFired = 0;
        return i;
    }

    public CritterdingSynapse getWeakestSynapse() {
        double minWeight = 0;
        CritterdingSynapse m = null;
        for (CritterdingSynapse s : synapses) {
            if ((Math.abs(s.weight) < minWeight) || (m == null)) {
                minWeight = s.weight;
                m = s;
            }
        }
        return m;
    }

    public void removeDisconnectedNeurons() {
        final List<InterNeuron> rem = new LinkedList();
        for (final InterNeuron i : neuron) {
            final CritterdingSynapse[] ll = getIncomingSynapses(i);
            if (ll != null)
                if (ll.length > 0)
                    continue;
            rem.add(i);
        }
        for (final InterNeuron in : rem) {
            removeNeuron(in);
        }
    }

    public void removeWeakestSynapses(int deadSynapsesRemoved) {
        int toRemove = Math.min(getNumSynapses(), deadSynapsesRemoved);
        for (int i = 0; i < toRemove; i++) {
            CritterdingSynapse s = getWeakestSynapse();
            removeSynapse(s);
        }
    }
    
    
    
}
