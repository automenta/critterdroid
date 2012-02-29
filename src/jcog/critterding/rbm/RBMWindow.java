/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jcog.critterding.rbm;

import com.syvys.jaRBM.Layers.Layer;
import com.syvys.jaRBM.Layers.LinearLayer;
import com.syvys.jaRBM.Layers.StochasticBinaryLayer;
import com.syvys.jaRBM.Math.Matrix;
import com.syvys.jaRBM.RBMLearn.CDRecurrentTemporalRBMLearner;
import com.syvys.jaRBM.RecurrentTemporalRBM;
import java.awt.Color;
import java.awt.Graphics;
import java.util.Collection;
import javax.swing.JPanel;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.SenseNeuron;

/**
 *
 * @author Sue
 */
public class RBMWindow extends JPanel {

    double[][] batchdata = null;
    int learningIterations = 4;
    int historyLength = 4;
    
    double learningRate = 0.05;
    double momentum = 0.01;
    
    public static RecurrentTemporalRBM rbm;
    private int numSenses;
    private double[][] targetGenerated;
    private double[][] targetReconstruction;
    private double[] xhiddenActivities;
    private double[][] hiddenActivities;

    public RBMWindow() {
        super();
    }

    public void update(Collection<CritterdingBrain> b) {

        if (batchdata == null) {
            for (CritterdingBrain x : b) {
                numSenses+= x.getNumInputs();
            }

            Layer visibleLayer = new LinearLayer(numSenses);
            Layer hiddenLayer = new StochasticBinaryLayer(numSenses/4);
            rbm = new RecurrentTemporalRBM(visibleLayer, hiddenLayer);

            batchdata = new double[historyLength][];
            for (int i = 0; i < historyLength; i++) {
                batchdata[i] = new double[numSenses];
            }
        }

        //shift everything down
        for (int i = historyLength - 1; i >= 1; i--) {
            for (int j = 0; j < numSenses; j++) {
                batchdata[i][j] = batchdata[i - 1][j];
            }
        }

        //add latest vector to row 0
        int j = 0;
        for (CritterdingBrain x : b) {
            for (SenseNeuron sn : x.getSense()) 
                batchdata[0][j++] = sn.getOutput();
        }

        learn();

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (batchdata == null) {
            return;
        }

        drawMatrix(g, batchdata, 0, 0);
        drawMatrix(g, targetReconstruction, 0, 200);
        //drawMatrix(g, targetGenerated, 0, 400);
        drawMatrix(g, hiddenActivities, 0, 400);
        
        drawVector(g, batchdata[0], 0, 500);
        drawVector(g, xhiddenActivities, 0, 510);
    }

    public void drawMatrix(Graphics g, double[][] d, int x, int y) {
        if (d == null) {
            return;
        }

        int w = 6;
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                float v = (float) d[i][j];
                if (v < 0) {
                    v = Math.min(-v, 1.0f);
                    g.setColor(new Color(v, 0, 0));
                } else {
                    v = Math.min(v, 1.0f);
                    g.setColor(new Color(0, v, 0));
                }
                g.fillRect(x + j * w, y + i * w, w, w);
            }
        }

    }

    public void drawVector(Graphics g, double[] d, int x, int y) {
        if (d == null) {
            return;
        }

        int w = 6;
        for (int i = 0; i < d.length; i++) {
            float v = (float) d[i];
            if (v < 0) {
                v = Math.min(-v, 1.0f);
                g.setColor(new Color(v, 0, 0));
            } else {
                v = Math.min(v, 1.0f);
                g.setColor(new Color(0, v, 0));
            }

            g.fillRect(x + i * w, y, w, w);
        }

    }

    public void learn() {

        rbm.setLearningRate(learningRate);
        rbm.setMomentum(momentum);

        double mse = 1.0;
        int epoch = 1;
        /*while (mse > 0.005)*/
        for (int i = 0; i < learningIterations; i++) {
            mse = (mse + CDRecurrentTemporalRBMLearner.Learn(rbm, batchdata, 1)) / 2;
            //System.out.println("Epoch: " + epoch + ", error: " + mse);
            epoch++;
        }
        //assertTrue(mse<0.01);

        hiddenActivities = rbm.getHiddenActivitiesFromVisibleData(batchdata);
        hiddenActivities = rbm.GenerateHiddenUnits(hiddenActivities);
        targetReconstruction = rbm.getVisibleActivitiesFromHiddenData(hiddenActivities);

        //System.out.println("Original -------------");
        //Matrix.printMatrix(batchdata);
        //System.out.println("Reconstructed -------------");
        //Matrix.printMatrix(targetreconstruction);

        //mse = Matrix.getMeanSquaredError(batchdata, targetreconstruction);
        //System.out.println("Error " + mse);
        //assertTrue(mse < 0.01);

        targetGenerated = Matrix.clone(batchdata);
        double[] reconstruction = batchdata[0];
        for (int i = 0; i < batchdata.length; i++) {
            xhiddenActivities = rbm.getHiddenActivitiesFromVisibleData(reconstruction);
            xhiddenActivities = rbm.GenerateHiddenUnits(xhiddenActivities);
            reconstruction = rbm.getVisibleActivitiesFromHiddenData(xhiddenActivities);
            targetGenerated[i] = reconstruction;
        }


        //System.out.println("Original -------------");
        //Matrix.printMatrix(batchdata);
        //System.out.println("Generated -------------");
        //Matrix.printMatrix(targetGenerated);
        //mse = Matrix.getMeanSquaredError(batchdata, targetGenerated);
        //System.out.println("Error " + mse);
        //assertTrue(mse < 0.01);
    }
}
