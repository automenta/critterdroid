/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding.rbm;

import com.syvys.jaRBM.IO.BatchDatasourceReaderImpl;
import com.syvys.jaRBM.Layers.LinearLayer;
import com.syvys.jaRBM.Layers.StochasticBinaryLayer;
import com.syvys.jaRBM.RBMImpl;
import com.syvys.jaRBM.RBMNet;
import com.syvys.jaRBM.RBMNetLearn.AutoencoderLearner;
import com.syvys.jaRBM.RBMNetLearn.GreedyLearner;
import com.syvys.jaRBM.RBMNetLearn.RBMNetLearner;
import java.util.Arrays;
import java.util.LinkedList;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.SenseNeuron;

/**
 * adds a RBM Perception Coprocessor to a CritterdingBrain.  
 * attaches inputs to all the brain's current inputs, 
 * then attaches the RBM's outputs as new inputs to CB
 * @author seh
 */
public class RBMPerception {

    private final CritterdingBrain brain;
    private final StochasticBinaryLayer hiddenLayer;
    private final LinearLayer visibleLayer;
    private RBMNet myrbmnet;
    private final LinkedList<SenseNeuron> existingInputs;
    private double[] outputs;
    
    
    public RBMPerception(CritterdingBrain b) {
        super();

        this.brain = b;

        visibleLayer = new LinearLayer(brain.getNumInputs());
      
        hiddenLayer = new StochasticBinaryLayer(brain.getNumInputs());
        
        RBMImpl rbm1 = new RBMImpl(visibleLayer, hiddenLayer);
        myrbmnet = new RBMNet(rbm1);
        
        existingInputs = new LinkedList(brain.getSense());
        
        for (int i = 0; i < existingInputs.size(); i++) {
            final int ii = i;
            brain.addInput(new SenseNeuron() {
                @Override public double getOutput() {                    
                    double o = outputs[ii];
                    if (Double.isNaN(o))
                        return 0;
                    return o;
                }                                
            });
        }
    }

    public void update() {
        
        
//        try {
//            RBMImpl rbm2 = new RBMImpl(new StochasticBinaryLayer(myrbmnet.getNumOfTopHiddenUnits()), new StochasticBinaryLayer(9));
//            myrbmnet.AddRBM(rbm2);
//            RBMImpl rbm3 = new RBMImpl(new StochasticBinaryLayer(myrbmnet.getNumOfTopHiddenUnits()), new StochasticBinaryLayer(2));
//            myrbmnet.AddRBM(rbm3);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        
        double batchdata[][] = new double[1][existingInputs.size()];
        int i = 0;
        for (SenseNeuron sn : existingInputs) {
            batchdata[0][i] = sn.getOutput();
            //batchdata[0][i] = 0;
            i++;
        }
        System.out.println(Arrays.asList(batchdata[0]));
        
        BatchDatasourceReaderImpl batchReader = new BatchDatasourceReaderImpl(batchdata);
        
        GreedyLearner glearner = new GreedyLearner(myrbmnet, batchReader, 1);
//        System.out.println("greedy training error = " + glearner.Learn(200));
        
        myrbmnet = glearner.getLearnedRBMNet();

//        System.out.println("recalling before backprop: ");
//        System.out.println("visible activities: ");
//        Matrix.printMatrix(myrbmnet.getVisibleActivitiesFromHiddenData(myrbmnet.getHiddenActivitiesFromVisibleData(batchdata)));
//        System.out.println("visible data: ");
//        Matrix.printMatrix(myrbmnet.GenerateVisibleUnits(myrbmnet.getVisibleActivitiesFromHiddenData(myrbmnet.getHiddenActivitiesFromVisibleData(batchdata))));
        
        
        RBMNetLearner mylearner = new AutoencoderLearner(myrbmnet, batchReader, 1);
        
        double minimumerror = Double.MAX_VALUE;
        int counter = 0;
        //RBMNet minimumnet = mylearner.getLearnedRBMNet();
        
        //System.out.println("\n2D coordinates before backprop: ");        
        //Matrix.printMatrix(myrbmnet.getHiddenActivitiesFromVisibleData(batchdata));
        
        for (int e = 0; e < 1; e++) {
            double error = mylearner.Learn();
            //System.out.println("backprop training error epoch: "+e+" = " + error);
        }
        
        myrbmnet = mylearner.getLearnedRBMNet().clone();
        
        outputs = myrbmnet.getHiddenActivitiesFromVisibleData(batchdata)[0];
        
        //System.out.println("\n2D coordinates after backprop: ");
        //Matrix.printMatrix(myrbmnet.getHiddenActivitiesFromVisibleData(batchdata));
        
        //System.out.println(brain.getNumInputs() + " " + myrbmnet.getHiddenActivitiesFromVisibleData(batchdata)[0].length);
        
        
    }
}
