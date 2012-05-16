/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import jcog.critterding.OutputNeuron;
import pl.gdan.elsy.qconf.Action;
import pl.gdan.elsy.qconf.Perception;

/**
 * following tutorial at: http://elsy.gdan.pl/index.php?option=com_content&task=view&id=16&Itemid=32
 * @author me
 */
public class QLearningBrain extends Brain {
    private MyPerception perception;
    private pl.gdan.elsy.qconf.Brain qbrain;
    private RewardFunction rewardFunction;

    public class MyPerception extends Perception {

        @Override
        public boolean isUnipolar() {
            return true;
        }

        @Override
        public double getReward() {
            return QLearningBrain.this.getReward();
        }

        @Override
        protected void updateInputValues() {
            for (int i = 0; i < getNumInputs(); i++) {
                setNextValue(getInput(i).getInput());
            }
        }
        
    }
    
    public QLearningBrain() {
        super();
        
        //Do-Nothing Action
        addOutput(new OutputNeuron() {

//            @Override
//            public void onFired() {
//                super.onFired();
//                System.out.println("DO NOTHING");
//            }
            
        }); 
    }
    
    OutputNeuron lastExecuted = null;

    public void init(RewardFunction reward) {
        System.out.println("Initializing QLearning: " + getNumInputs() + " total inputs, " + getNumOutputs() + " outputs");
        this.rewardFunction = reward;
        
        Action actionArray[] = new Action[getNumOutputs()];
        for (int o = 0; o < getNumOutputs(); o++) {
            final OutputNeuron on = getOutput(o);
            final int oo = o;
            actionArray[o] = new Action() {
                
                @Override
                public int execute() {
                    if ((lastExecuted!=null) && (lastExecuted!=on))
                        lastExecuted.setFiring(false);
                    on.setFiring(true);
                    lastExecuted = on;
                    return 1;
                }
                
            };
        }
        perception = new MyPerception();
        
        int hidden[] = new int[] { getNumInputs() };
        //int hidden[] = new int[] { };
        
        qbrain = new pl.gdan.elsy.qconf.Brain(perception, actionArray, hidden);
        qbrain.setAlpha(0.1);
        qbrain.setGamma(0.9);
        qbrain.setLambda(0.8);
        qbrain.setUseBoltzmann(true);
        qbrain.setTemperature(0.02);
        qbrain.setRandActions(5);
        
        
    }
    
    
    @Override
    public void forward() {
        perception.perceive();
        qbrain.count();
        
        qbrain.executeAction();
    }
    
    public float getReward() {
        final float r = rewardFunction.getReward();
        System.out.println("Reward: " + r);
        return r;
    }
}
