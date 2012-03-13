package jcog.critterding;

public class InterNeuron extends MotorNeuron {

    double output, nextOutput;
    int maxSynapses;
    boolean isInhibitory;
    double firingThreshold;
    MotorNeuron motor;
    double potential;
    double potentialDecay;
    boolean isPlastic;
    double plasticityStrengthen;
    double plasticityWeaken;
    
    double maxSynapseWeight = 1.0f;
    
    //List<SynapseBuilder> synapseBuilders;

    public InterNeuron(double potentialDecay, double plasticityStrengthen, double plasticityWeaken ) {
        super();
        isInhibitory = false;
        potential = 0.0F;
        this.potentialDecay = potentialDecay;
        // output
        output = 0;
        nextOutput = 0;
        
        this.firingThreshold = 0.9f;
        this.maxSynapseWeight = 1.0f;
        
        isPlastic = true;
        this.plasticityStrengthen = plasticityStrengthen;
        this.plasticityWeaken = plasticityWeaken;
        
        motor = null;
    }

    public void forward(final CritterdingSynapse[] synapses) {        

        // potential decay
        potential *= potentialDecay;

        if (Double.isNaN(potential)) {
            System.out.println("NAN");
            potential = 0;
        }

        // make every connection do it's influence on the neuron's total potential        
        for (final CritterdingSynapse s : synapses) {
            
            // lower synaptic weights
            if (isPlastic) {
                s.weight = s.weight * plasticityWeaken;
            }
            
            final double i = s.getInput();
            potential += s.weight * i;
        }
        
        
//        if ((potential!=0) || (output!=0))
//            System.out.println(this + " pot=" + potential + ", out=" + output + " " + isInhibitory + " " + firingThreshold);

        if (isInhibitory) {
            forwardInhibitory(synapses);
        } else {
            forwardExhibitory(synapses);
        }
        
    }

    protected void forwardInhibitory(final CritterdingSynapse[] synapses) {
        // do we spike/fire
        if (potential <= -1.0f * firingThreshold) {
            // reset neural potential
            potential = 0.0f;

            // fire the neuron
            nextOutput = -1;

            // PLASTICITY: if neuron & synapse fire together, the synapse strenghtens
            if (isPlastic) {
                for (final CritterdingSynapse s : synapses) {
                    final double o = s.curInput; //s.getInput(); //input will be cached by here
                    // if synapse fired, strenghten the weight
                    final double w = s.weight;
                    if ((o < 0.0f && w > 0.0f) || (o > 0.0f && w < 0.0f)) {
                        // 						cerr << endl << "Inhibitory firing" << endl << "synref: " << *Synapses[i].ref << endl << "pre weight:  " << Synapses[i].weight << endl;
                        s.weight = w * plasticityStrengthen;
                        // 						cerr << "post weight: " << Synapses[i].weight << endl;
                    }

                    // clamp weight
                    clampWeight(s);
                }
            }
        } // don't fire the neuron
        else {
            nextOutput = 0;
            // reset potential if < 0
            if (potential > 0.0f) {
                potential = 0.0f;
            }
        }
    }

    protected void forwardExhibitory(final CritterdingSynapse[] synapses) {
        // do we spike/fire
        if (potential >= firingThreshold) {
            // reset neural potential
            potential = 0.0f;

            // fire the neuron
            nextOutput = 1;

            // PLASTICITY: if neuron & synapse fire together, the synapse strenghtens
            if (isPlastic) {
                for (final CritterdingSynapse s : synapses) {
                    final double o = s.curInput; //s.getInput(); //input will be cached by here

                    final double w = s.weight;

                    // if synapse fired, strenghten the weight
                    if ((o > 0.0f && w > 0.0f) || (o < 0.0f && w < 0.0f)) {
                        // 						cerr << endl << "Excititory firing" << endl << "synref: " << *Synapses[i].ref << endl << "pre weight:  " << Synapses[i].weight << endl;
                        s.weight = w * plasticityStrengthen;
                        // 						cerr << "post weight: " << Synapses[i].weight << endl;
                        }

                    // if weight > max back to max
                    clampWeight(s);
                }
            }
        } // don't fire the neuron
        else {
            nextOutput = 0;
            // reset potential if < 0
            if (potential < 0.0f) {
                potential = 0.0f;
            }
        }
    }

    public void clampWeight(final CritterdingSynapse s) {
        double w = s.weight;
        if (w > maxSynapseWeight) w = maxSynapseWeight;
        if (w < -maxSynapseWeight) w = -maxSynapseWeight;
        s.weight = w;
    }


    public double getPotential() {
        return potential;
    }

    @Override
    public double getOutput() {
        return output;
    }

    public boolean isInhibitory() {
        return isInhibitory;
    }

    public void setPotentialDecay(double v) {
        this.potentialDecay = v;
    }

    public void setMaxAbsoluteSynapseWeight(double w) {
        this.maxSynapseWeight = w;
    }

    public void setFiringThreshold(double v) {
        this.firingThreshold = v;
    }

//    public List<SynapseBuilder> getSynapseBuilders() {
//        return synapseBuilders;
//    }

    public MotorNeuron getMotor() {
        return motor;
    }

    public void setMotor(MotorNeuron motor) {
        this.motor = motor;
    }

    public void setIsPlastic(boolean isPlastic) {
        this.isPlastic = isPlastic;
    }

    
    
    
    
}
