/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.entities;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.critterdroid.bio.act.ColorBody;
import com.critterdroid.bio.act.ServoRevoluteJoint;
import com.critterdroid.bio.feel.Orientation;
import com.critterdroid.bio.feel.Retina;
import com.critterdroid.bio.feel.RevoluteJointAngle;
import com.critterdroid.bio.brain.RandomWiring;
import com.critterdroid.bio.feel.VelocityAngular;
import com.critterdroid.bio.feel.VelocityAxis;
import com.critterdroid.simulation.App;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javolution.context.ConcurrentContext;
import jcog.critterding.BrainConnector;
import jcog.critterding.BrainReport;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.CritterdingNeuron;
import jcog.critterding.InterNeuron;
import jcog.critterding.MotorNeuron;
import jcog.critterding.SenseNeuron;
import jcog.critterding.rbm.RBMWindow;

/**
 *
 * @author seh
 */
public class DistributedSpider extends Critter {
    private final BrainWiring cortexWiring;
    private final BrainWiring limbWiring;
    protected CritterdingBrain cortex;
    private int deadSynapsesRemoved = 0;
    private int addNewNeurons = 0;


    public static interface BrainWiring {
        public void wireBrain(CritterdingBrain b);
    }
    
    private final int arms;
    protected final int segments;
    public final List<CritterdingBrain> brains = new LinkedList();
    private Map<Body, CritterdingBrain> bodyBrains = new HashMap();
    protected float jointSpeed = 0.8f;
    //protected List<Retina> retinas = new LinkedList();
    protected final Vector2 initialPos;
    final BrainConnector connector = new BrainConnector();
    protected List<Retina> retinas = new LinkedList();
    final int spineWidth, centralSpineWidth;
    final int numRetinasPerSegment;
    
    float visionDistance = 250;
    
    final boolean copyPeripheralSensesForCortex = true;
    final boolean copyPeripheralMotorsForCortex = true;
    
    
    final boolean includeRectangleSegments = false;
    final float rectangleLimbWidthRatio = 0.9f;
    final float rectangleLimbLengthRatio = 1.8f;
    final float rectangleLimbDensity = 0.2f;
    int brainCyclesPerFrame = 0;
    final boolean parallel = false;
    
    public DistributedSpider(Vector2 pos, int arms, int segments, BrainWiring cortexWiring, BrainWiring limbWiring, int spineWidth, int centralSpineWidth, int numRetinasPerSegment) {
        super();
        this.initialPos = pos;
        this.segments = segments;
        this.arms = arms;
        this.spineWidth = spineWidth;
        this.centralSpineWidth = centralSpineWidth;
        
        this.cortexWiring = cortexWiring;
        this.limbWiring = limbWiring;
        this.numRetinasPerSegment = numRetinasPerSegment;
    }

    @Override
    public void init(App w)  {

        float theta = 0;

        float centerRadius = 25.0f;
        float armRadius = 15.0f;

        Color c1 = new Color(0.9f, 0.2f, 0.3f, 1.0f);
        Color c2 = new Color(0.3f, 0.2f, 0.9f, 1.0f);
        Color c3 = new Color(0.6f, 0.2f, 0.6f, 1.0f);

        float visionDistance = 250;
    
        Body center = w.newCircle(centerRadius, initialPos.x, initialPos.y, c2, 1.0f);

        cortex = new CritterdingBrain();

        for (int i = 0; i < arms; i++) {

            float r = armRadius;

            Body prev = null;
            CritterdingBrain prevBrain = null;

            float x = centerRadius + armRadius;
            float jointRange = 0.7f * (float)Math.PI;

            float range = jointRange;


            for (int s = 0; s < segments; s++) {


                float dx = initialPos.x + (float) Math.cos(theta) * x;
                float dy = initialPos.y + (float) Math.sin(theta) * x;

                Body b = w.newCircle(r, dx, dy, s % 2 == 0 ? new Color(c1) : new Color(c2), 1.0f);

                x += r * 2f;

                final CritterdingBrain brain = new CritterdingBrain();

                bodyBrains.put(b, brain);
                brains.add(brain);

                Fixture f = b.getFixtureList().get(0);
                
                //brain.addOutput(new GrowCircle(f, 1.1f, r * 0.8f, r * 1.2f));
                //brain.addOutput(new GrowCircle(f, 0.9f, r * 0.8f, r * 1.2f));

                for (int xi = 0; xi < 3; xi++) {
                    float df = 0.1f;
                    brain.addOutput(new ColorBody(f, xi, true, df));
                    brain.addOutput(new ColorBody(f, xi, false, df));
                }

                RevoluteJoint rj = w.joinRevolute(center, b, dx, dy);

                //brain.addOutput(new RotateRevoluteJoint(rj, range, jointSpeed, false));
                //brain.addOutput(new RotateRevoluteJoint(rj, range, jointSpeed, true));
                new ServoRevoluteJoint(brain, rj, -range, range, 16);
                
                brain.addInput(new RevoluteJointAngle(rj));


                brain.addInput(new VelocityAxis(b, true));
                brain.addInput(new VelocityAxis(b, false));
                brain.addInput(new Orientation(b));
                brain.addInput(new VelocityAngular(b));


                int n = numRetinasPerSegment;
                for (float z = 0; z < n; z++) {

                    float a = z * (float)(Math.PI*2.0 / ((float)n));
                    retinas.add(new Retina(brain, b, new Vector2(0, 0), a, visionDistance));
                }

//                if (s == segments - 1) {
//                    for (float z = -4; z <= 4; z++) {
//                        retinas.add(new Retina(brain, b, new Vector2(0, 0), (float) Math.PI * -1.0f + ((float) z) * 0.4f, 650));
//                    }
//                }

                if (copyPeripheralSensesForCortex) {
                    for (SenseNeuron sn : brain.getSense())
                        cortex.addInput(sn);
                }
                if (copyPeripheralMotorsForCortex) {
                    for (MotorNeuron sn : brain.getMotor())
                        cortex.addOutput(sn);
                }
                
                if (prevBrain != null) {
                    connector.addConnection(prevBrain, brain, spineWidth);
                }



                if (includeRectangleSegments) {
                    
                    //x += r/2.0f;
                    
                    float rx = initialPos.x + (float) Math.cos(theta) * x;
                    float ry = initialPos.y + (float) Math.sin(theta) * x;
                    
                    float length = r * rectangleLimbLengthRatio;
                    float width = r * rectangleLimbWidthRatio;
                    Body rl = w.newRectangle(length, width, rx, ry, theta, c3, rectangleLimbDensity);
                    
                    for (float z = 0; z < numRetinasPerSegment; z++) {

                        float a = z * (float)(Math.PI*2.0 / ((float)numRetinasPerSegment));
                        retinas.add(new Retina(brain, rl, new Vector2(0, 0), a, visionDistance));
                    }

                    //w.joinWeld(b, rl, new Vector2(rx + length/2.0f, ry));
                    //w.joinWeld(b, rl, new Vector2(rx, ry));
                    w.joinWeld(b, rl, new Vector2(dx, dy));
                    
                    x += length;
                    
                    prev = rl;
                }
                else {
                    prev = b;
                }
                
                r *= 0.8;
                
                prevBrain = brain;
            }

            theta += Math.PI * 2.0 / ((float) arms);
        }

        for (CritterdingBrain cb : brains) {
            connector.addConnection(cortex, cb, centralSpineWidth);
            limbWiring.wireBrain(cb);
        }

        cortex.addInput(new Orientation(center));
        cortexWiring.wireBrain(cortex);

        brains.add(cortex);

        int totalNeurons = 0;
        int totalSynapses = 0;
        for (CritterdingBrain b : brains) {
            new BrainReport(b);
            totalNeurons += b.getNumInterNeurons();
            totalSynapses += b.getNumSynapses();
        }
        System.out.println("Total Neurons=" + totalNeurons + " , Total Synapses=" + totalSynapses);

    }

    @Override
    protected void update(final double dt) {
        for (final Retina r : retinas) {
            r.update();
        }
        
        //updateRBM();

        if (parallel) {
            for (int i = 0; i < brainCyclesPerFrame; i++) {
                ConcurrentContext.enter();
                try {
                    for (final CritterdingBrain brain : brains) {
                        ConcurrentContext.execute(new Runnable() {
                            public void run() {
                                brain.forward();
                            }
                        });
                    }
                } finally {
                    ConcurrentContext.exit();
                }
            }
        }
        else {            
            for (int i = 0; i < brainCyclesPerFrame; i++) {
                    for (final CritterdingBrain brain : brains) {
                          brain.forward();
                    }
            }
        }
        
        for (final CritterdingBrain brain : brains) {
            brain.forwardOutputs();
        }

        if ((deadSynapsesRemoved > 0) || (addNewNeurons > 0)) {
            evolveBrain();
        }

    }
    
    RBMWindow rw;
    
    public void updateRBM() {
        if (rw == null) {
            rw = new RBMWindow();
            JFrame jf = new JFrame("RBM View");
            jf.getContentPane().add(rw);
            jf.setSize(500, 500);
            jf.setVisible(true);            
        }
        rw.update(brains);
    }
    
    float drawOffsetRate = 0.1f;
    float drawOffset = 0;

    final Color z = new Color(0f, 0f, 0f, 1f);
    
    protected void drawNeuronMap(Graphics g, CritterdingBrain brain, Vector2 pos, int w, int wide, int maxDrawn) {
        float startX = pos.x - (wide * w) / 2;
        float x = startX;
        float y = pos.y - (wide * w) / 2;

        int drawn = 0;

        int p = 0;
        //g.setLineWidth(0f);

        final int dox = (int)Math.floor(drawOffset);
        
        for (int j = 0; j < maxDrawn; j++) {
            CritterdingNeuron cn = brain.getInter().get((j + dox) % brain.getInter().size());

            if (drawn++ > maxDrawn) {
                break;
            }

            float v;
            boolean inhibitory = false;
            if (cn instanceof InterNeuron) {
                v = (float) Math.min(Math.abs(((InterNeuron) cn).getPotential()), 1.0f);
                inhibitory = ((InterNeuron) cn).isInhibitory();
            } else {
                v = (float) cn.getOutput();
            }

            float a = 1.0f;


            boolean draw = true;
            if (v == 0) {
                draw = false;
            } else if (inhibitory) {
                z.r = v;
                z.g = 0;
                z.b = 0;
                z.a = v/2.0f;
            } else {
                z.r = 0;
                z.g = v;
                z.b = 0;
                z.a = v/2.0f;
            }

            if (draw) {
                App.setColor(z);
                //g.fillRect(x - (w * a) / 2.0f, y - (w * a) / 2.0f, w * a, w * a);
                float px = x - (w * a) / 2.0f;
                float py = y - (w * a) / 2.0f;                
                App.drawLine(px, py-(w/2), px, py+(w/2));
            }

            x += w + 1;

            p++;

            if (p == wide) {
                y += w + 1;
                x = startX;
                p = 0;
            }

        }

    }

    public void renderUnderlay(Graphics g) {
        //drawNeuronMap(g);


        for (Retina r : retinas) {
            App.setColor(r.getColor());
            App.setLineWidth(2.0f);
            App.drawLine(r.p1.x, r.p1.y, r.pint.x, r.pint.y);
        }

    }

    public void renderOverlay(Graphics g) {
        for (Body b : bodyBrains.keySet()) {
            CritterdingBrain brain = bodyBrains.get(b);
            drawNeuronMap(g, brain, b.getWorldCenter(), 6, 10, 64);
        }
        
        drawOffset+=(drawOffsetRate);

    }

    public void setVisionDistance(float v) {
        for (Retina r : retinas) {
            r.setVisionDistance(v);
            visionDistance = v;
        }
    }
    
    public void setDeadSynapsesRemoved(int n) {
        this.deadSynapsesRemoved = n;
    }
    public void setAddNewNeurons(int n) {
        this.addNewNeurons = n;
    }
   
    protected void evolveBrain() {
        cortex.removeWeakestSynapses(deadSynapsesRemoved);
        
        cortex.removeDisconnectedNeurons();
        
        int toAdd = addNewNeurons;
        if (addNewNeurons > 0)
            new RandomWiring(toAdd, 1, 4, 0.1, 0.1, 0.99).wireBrain(cortex);
                
    }

    public CritterdingBrain getCortex() {
        return cortex;
    }
    
    public void setBrainCyclesPerFrame(int i) {
        this.brainCyclesPerFrame = i;
    }

    
}
