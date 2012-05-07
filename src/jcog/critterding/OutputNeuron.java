package jcog.critterding;


public class OutputNeuron implements CritterdingNeuron, com.syncleus.dann.neural.OutputNeuron {
    private boolean firing;

    /**
     * 
     * @param f
     * @return  whether changed
     */
    public boolean setFiring(boolean f) {
        if (firing!=f) {
            firing = f;

            if (f == false)
                onCleared();
            else
                onFired();
            
            return true;
        }
        return false;
    }
    
    @Override
    public double getOutput() {
        return firing ? 1.0 : 0.0;
    }
    

    @Override
    public void tick() {
    }

    public void onFired() { }
    public void onCleared() { }

}

