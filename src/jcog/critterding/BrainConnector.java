/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding;

/**
 *
 * @author seh
 */
public class BrainConnector {
 
    public void addConnection(CritterdingBrain a, CritterdingBrain b, int width) {

        for (int w = 0; w < width; w++) {
            final OutputNeuron bOut = b.newOutput();
            a.addInput(new InputNeuron() {
                @Override
                public double getOutput() {
                    return bOut.getOutput();
                }                               
            });
            final OutputNeuron aOut = a.newOutput();
            b.addInput(new InputNeuron() {
                @Override
                public double getOutput() {
                    return aOut.getOutput();
                }                               
            });
        }
    }

}


