/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding;

import com.critterdroid.bio.InputRandomizer;
import junit.framework.TestCase;

/**
 *
 * @author seh
 */
public class TestConnectedBrains extends TestCase {
//    public void testBrain() {
//        int inputs = 64;
//        int outputs = 64;
//        
//        int neurons = 256;
//        int minSynapses = 8;
//        int maxSynapses = 12;
//        
//        CritterdingBrain a = new CritterdingBrain(inputs, outputs);
//
//        CritterdingBrain b = new CritterdingBrain(inputs, outputs);
//
//        
//        int bridgeWidth = 16;
//        BrainConnector bc = new BrainConnector();
//        bc.addConnection(a, b, bridgeWidth);
//        
//        assertTrue(a.getNumInputs() == inputs + bridgeWidth);
//        assertTrue(a.getNumOutputs() == outputs + bridgeWidth);
//        
//        //wire AFTER the brain connector has added inputs/outputs to each brain
//        a.wireRandomly(neurons, minSynapses, maxSynapses, 0.25, 0.25, 0.75);
//        b.wireRandomly(neurons, minSynapses, maxSynapses, 0.25, 0.25, 0.75);
//
//        InputRandomizer ia = new InputRandomizer(a);    //currently this will randomize all inputs. but bridge inputs are hardwired into the other brain's outputs so this wont affect them
//        InputRandomizer ib = new InputRandomizer(b);
//        for (int i = 0; i < 100; i++) {            
//            ia.random(-1.0, 1.0);
//            ib.random(-1.0, 1.0);
//            
//            a.forward();
//            b.forward();
//            
//            for (int j = 0; j < outputs; j++) {
//                System.out.print(a.getMotorNeuron(j).getOutput() + " ");
//                System.out.print(b.getMotorNeuron(j).getOutput() + " ");
//            }
//            System.out.println();
//            
//        }
//    }
    
}
