/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jcog.critterding;

import jcog.critterding.rbm.RBMPerception;
import com.critterdroid.bio.InputRandomizer;
import jcog.math.RandomNumber;
import junit.framework.TestCase;

/**
 *
 * @author me
 */
public class TestBrain1 extends TestCase {
    
//    public void testBrain() {
//        int inputs = 16;
//        int outputs = 16;
//        
//        int neurons = 256;
//        int minSynapses = 8;
//        int maxSynapses = 12;
//        
//        CritterdingBrain b = new CritterdingBrain(inputs, outputs).wireRandomly(neurons, minSynapses, maxSynapses, 0.25, 0.25, 0.75);
//
//        //RBMPerception p = new RBMPerception(b);
//                
//        assertEquals(b.getTotalNeurons(), inputs + outputs + neurons);
//        //System.out.println(b);
//        //System.out.println(b.getEdges().size());
//        
//        InputRandomizer ir = new InputRandomizer(b);
//        for (int i = 0; i < 100; i++) {            
//            ir.random(-1.0, 1.0);
//            b.forward();
//            //p.update();
//            
//            for (int j = 0; j < outputs; j++) {
//                System.out.print(b.getMotorNeuron(j).getOutput() + " ");
//            }
//            System.out.println();
//            
//        }
//    }
    
}
