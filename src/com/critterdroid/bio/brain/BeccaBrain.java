/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcog.critterding.OutputNeuron;

/**
 * Interface to BECCA cognition engine ( http://openbecca.org )
 * which runs on python 2.7 + numpy 1.6 
 * @author me
 */
public class BeccaBrain extends Brain {
    private Process proc;
    private PrintStream oo;
    private BufferedReader oi;
    private RewardFunction rewardFunction = null;
    private float lastReward = 0;
    private int outputChangeMagnitude = 0;
    private PrintWriter beccaOut = null;
    private BufferedReader beccaIn = null;
    private int historySize;
    private float[][] history;
    
    public BeccaBrain() {
        this(0, 0);        
    }

    public float getLastReward() {
        return lastReward;
    }

    /** number of outputs that changed from last cycle */
    public float getOutputChangeMagnitude() {
        if (getNumOutputs() > 0)
            return ((float)outputChangeMagnitude) / ((float)getNumOutputs());
        return 0;
    }
    
    public BeccaBrain(final int numInputs, final int numOutputs) {
        super();
        for (int i = 0; i < numInputs; i++)
            newInput();
        for (int i = 0; i < numOutputs; i++)
            newOutput();
        
    }

    public void setRewardFunction(RewardFunction rewardFunction) {
        this.rewardFunction = rewardFunction;
    }

    public RewardFunction getRewardFunction() {
        return rewardFunction;
    }
    
    public void init(float featureMultiplier, int historySize, RewardFunction initialRewardFunction) {
        setRewardFunction(initialRewardFunction);
        
        this.historySize = historySize;
        if (historySize > 0)
            this.history = new float[historySize][getNumInputs()];
        
        int numFeatures = (int)Math.ceil(featureMultiplier * ( getTotalInputs() + getNumOutputs() ));
        int numPrimitives = getNumOutputs();
        
        System.out.println("Initializing BECCA: " + getNumInputs() + " actual inputs, " + getTotalInputs() + " total inputs, " + getNumOutputs() + " outputs, " + numFeatures + " features");
        
        
        
        Socket kkSocket = null;
 
        try {
            kkSocket = new Socket("localhost", 9999);
            beccaOut = new PrintWriter(kkSocket.getOutputStream(), true);
            beccaIn = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
 
//        while ((fromServer = in.readLine()) != null) {
//            System.out.println("Server: " + fromServer);
//            if (fromServer.equals("Bye."))
//                break;
//             
//            fromUser = stdIn.readLine();
//        if (fromUser != null) {
//                System.out.println("Client: " + fromUser);
//                out.println(fromUser);
//        }
        
        getBeccaResult("init(" + getTotalInputs() + ", " + numPrimitives + "," + getNumOutputs() + "," + numFeatures + ")");
        
    }
    
    public int getTotalInputs() {
        return getNumInputs() * (historySize+1);
    }
    
    public String getBeccaResult(String cmd) {
        beccaOut.println(cmd);
        try {
            return beccaIn.readLine().trim();
        } catch (IOException ex) {
            Logger.getLogger(BeccaBrain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    

    public float getReward() {
        if (rewardFunction!=null)
            return lastReward = rewardFunction.getReward();
        
        return lastReward = 0f;
    }
    
    private Thread beccaComm;
    private String result;
    int cycle = 0;
    
    @Override
    public void forward() {
        if (beccaComm!=null) {
            try {
                beccaComm.join();
                
                result = result.replace("[", "").replace("]", "").replace(",", "");

                //System.out.println(result);

                //TODO for performance, use parser to avoid creating new String's

                String[] results = result.split(" ");

                outputChangeMagnitude = 0;
                for (int i = 0; i < getNumOutputs(); i++) {
                    boolean nextFire = Float.parseFloat(results[i]) > 0;

                    final OutputNeuron on = getOutput(i);
                    if (on.setFiring( nextFire   ))
                        outputChangeMagnitude++;
                }
                
            } catch (InterruptedException ex) {
                Logger.getLogger(BeccaBrain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
                
        beccaComm = new Thread(new Runnable() {
            @Override
            public void run() {
                //String act = "act( [0.3, 0.6], 0.2 )";
                StringBuffer sensorList = new StringBuffer();
                for (int i = 0; i < getNumInputs(); i++) {
                    final float x = (float)getInput(i).getInput();
//                    if (x < 0)
//                        System.err.println("NEGATIVE VALUE: " + i + "=" + x);
                    sensorList.append(x + ",");
                }
                for (int j = 0; j < historySize; j++) {
                    for (int i = 0; i < getNumInputs(); i++) {
                        sensorList.append(history[j][i] + ",");
                    }
                }

                
                String act = "act( [" + sensorList.substring(0, sensorList.length()-1) + "], " + (float)getReward() + ")";        

                result = getBeccaResult(act);
 
                //shift history
                if (historySize > 0) {
                    for (int j = historySize-2; j >= 0; j--) {
                        for (int i = 0; i < getNumInputs(); i++) {
                            history[j+1][i] = history[j][i];
                        }

                    }
                    for (int i = 0; i < getNumInputs(); i++) {
                        history[0][i] = (float)getInput(i).getInput();
                    }
                }
                

            }                
        });
        
        beccaComm.start();  //runs becca communication in new thread while simulation proceeds, so that by the time this is called again, the result will be available
    
        cycle++;
    }

    public void plots() {
        getBeccaResult("plots()");
    }
    
    public static void main(String[] args) throws Exception {
        BeccaBrain b = new BeccaBrain(7, 7);
        
        b.init(6, 0, new RewardFunction() {
            @Override
            public float getReward() {
                return (float)Math.random();
            }
        });
        
        
        for (int i = 0; i < 15; i++) {
            b.forward();
            b.forwardOutputs();
        }
        
        b.plots();
        
        //b.stop();
    }
    
}
