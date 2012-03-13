/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.brain;

import jcog.critterding.CritterdingBrain;
import jcog.critterding.InterNeuron;

/**
 *
 * @author seh
 */
public class MeshWiring implements BrainWiring {
    private final int height;
    private final int width;
    private final CritterdingBrain brain;
    private final InterNeuron[][] mesh;

    public MeshWiring(CritterdingBrain b, int mHeight, int mWidth) {
        this.height = Math.max(mHeight, Math.max(b.getNumInputs(), b.getNumOutputs()));
        this.width = mWidth;
        this.brain = b;
        
        this.mesh = new InterNeuron[height][width];
       
    }

    @Override
    public void wireBrain(CritterdingBrain b) {
        if (b != this.brain) {
            System.err.println(this + " wiring unknown brain");
            return;
        }
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
//                mesh[h][w] = new InterNeuron(0.99) {


//                    @Override
//                    public void forward(Collection<CritterdingSynapse> synapses) {
//                        System.out.println("f> " + getPotential());
//                        super.forward(synapses);
//                        
//                        float totalInput = 0;
//                        for (CritterdingSynapse cs : synapses)
//                            totalInput += cs.getInput();
//                        
//                        System.out.println("<f " + getPotential() + " " + totalInput + " " + getOutput());
//                     }

                    
//                };
//                b.addNeuron(mesh[h][w]);
            }
        }
        for (int j = 0; j < width/2; j++)
            for (int i = 0; i < b.getNumInputs(); i++) {
                b.newSynapse(b.getSenseNeuron(i), mesh[i][j], 1.0f);
                mesh[i][j].setIsPlastic(false);
            }
        for (int o = 0; o < b.getNumOutputs(); o++) {
            mesh[o][width-1].setMotor( b.getMotorNeuron(o) );
        }
        
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                final InterNeuron c = mesh[h][w];
                
                final InterNeuron left = mesh[h][ww(w-1)];
                final InterNeuron right = mesh[h][ww(w+1)];
                final InterNeuron up = mesh[hh(h+1)][w];
                final InterNeuron down = mesh[hh(h-1)][w];
                
                b.newSynapse(c, right, 1.0);
                //b.newSynapse(c, left, 1.0);
                
                //b.newSynapse(c, right, 5.0);
                //b.newSynapse(right, c, 1.0);
                
                b.newSynapse(c, down, 1.0);
                //b.newSynapse(down, c, 1.0);

                b.newSynapse(c, up, 1.0);
                //b.newSynapse(c, up, 1.0);
                
            }
        }
        
    }
    
    public int hh(int h) {
        if (h == -1) return height-1;
        if (h == height) return 0;
        return h;
    }
    
    public int ww(int w) {
        if (w == -1) return width-1;
        if (w == width) return 0;
        return w;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public CritterdingBrain getBrain() {
        return brain;
    }

    public InterNeuron[][] getMesh() {
        return mesh;
    }
    
    
}
