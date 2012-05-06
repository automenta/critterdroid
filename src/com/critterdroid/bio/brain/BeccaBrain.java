/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import java.io.*;

/**
 * High-performance interface to BECCA cognition engine ( http://openbecca.org )
 * which runs on python 2.7 + numpy 1.6 
 * @author me
 */
abstract public class BeccaBrain extends Brain {
    private Process proc;
    private PrintStream oo;
    private BufferedReader oi;

    public BeccaBrain() {
        this(0, 0);        
    }
    
    public BeccaBrain(final int numInputs, final int numOutputs) {
        super();
        for (int i = 0; i < numInputs; i++)
            newInput();
        for (int i = 0; i < numOutputs; i++)
            newOutput();
        
    }
    
    public void init(float featureMultiplier) {
        
        int numFeatures = (int)Math.ceil(featureMultiplier * ( getNumInputs() + getNumOutputs() ));
        
        System.out.println("Initializing BECCA: " + getNumInputs() + " inputs, " + getNumOutputs() + " outputs, " + numFeatures + " features");
        getBeccaResult("localhost", 9999, "init(" + getNumInputs() + ", " + getNumOutputs() + "," + numFeatures + ")");
        
    }
    
    public static String getBeccaResult(String host, int port, String cmd) {
        return getResult("nc " + host + " " + port, cmd);
    }
    
    public static String getResult(String ncCmd, String cmd) {
        try {
            Process child = Runtime.getRuntime().exec(ncCmd);
            
            PrintStream ps = new PrintStream(child.getOutputStream());
            ps.println(cmd);
            ps.flush();
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(child.getInputStream()));

            StringBuffer sb = new StringBuffer();
            String s;
            while ((s = stdInput.readLine()) != null) {
                sb.append(s);
            }                
            return sb.toString();
            
        } catch (IOException e) {
            return "";
        }
    }

    
    @Override
    public void forward() {
        
        //String act = "act( [0.3, 0.6], 0.2 )";
        StringBuffer sensorList = new StringBuffer();
        for (int i = 0; i < getNumInputs(); i++)
            sensorList.append((float)getInput(i).getInput() + ((i == getNumInputs() - 1) ? "" : ", "));
        
        String act = "act( [" + sensorList + "], " + (float)getReward() + ")";        
        
        String result = getBeccaResult("localhost", 9999, act);
        result = result.replace("[", "").replace("]", "").replace(",", "");

        //System.out.println(result);
        
        //TODO for performance, use parser to avoid creating new String's
        
        String[] results = result.split(" ");
        
        
        for (int i = 0; i < getNumOutputs(); i++)
            getOutput(i).setFiring( Float.parseFloat(results[i]) > 0  );
                
        
    }

    public abstract float getReward();

    
    public static void main(String[] args) throws Exception {
        BeccaBrain b = new BeccaBrain(7, 7) {

            @Override
            public float getReward() {
                return (float)Math.random();
            }
            
        };
        
        b.init(2);
        
        for (int i = 0; i < 9; i++) {
            b.forward();
            b.forwardOutputs();
        }
        
        //b.stop();
    }
    
}
