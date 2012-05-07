/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.app;

import com.critterdroid.bio.feel.QuantizedScalarInput;
import com.critterdroid.simulation.ui.SliderListener;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.critterdroid.bio.Material;
import com.critterdroid.bio.Simulation;
import com.critterdroid.bio.act.RevoluteJointByIndexVote;
import com.critterdroid.bio.act.ColorBodyTowards;
import com.critterdroid.bio.act.Thruster;
import com.critterdroid.bio.brain.BrainWiring;
import com.critterdroid.bio.brain.RandomWiring;
import com.critterdroid.bio.feel.Retina;
import com.critterdroid.entities.Critter;
import com.critterdroid.simulation.App;
import com.critterdroid.simulation.ui.ParameterPanel;
import java.util.LinkedList;
import java.util.List;
import jcog.critterding.BrainReport;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.InterNeuron;

/**
 *
 * @author seh
 */
public class SpiderSim implements Simulation {
    private App sim;

    public static class Spider extends Critter {

        int arms;
        float armLength = 0.65f;
        float armWidth = 0.30f;
        int armSegments;

        float torsoRadius = 0.4f;
        
        float servoRange = ((float)Math.PI / 2.0f) * 0.9f;
        int servoSteps = 7;
        
        int numRetinasPerSegment = 32;
        int retinaLevels = 4;
        
        final int velocityLevels = 16;

        int orientationSteps = 9;
        
        double initialVisionDistance = 10.0;

        float armSegmentExponent;
                
        public final CritterdingBrain brain = new CritterdingBrain();
        List<Retina> retinas = new LinkedList();
        private final float ix;
        private final float iy;
        private final Color color;
        private final Material m;
        private App sim;
        private final BrainWiring brainWiring;

        public Spider(int arms, int armSegments, float armSegmentExponent, float ix, float iy, Color c, BrainWiring bw) {
            super();
            this.arms = arms;
            this.armSegments = armSegments;
            this.armSegmentExponent = armSegmentExponent;
            this.ix = ix;
            this.iy = iy;
            this.color = c;
            this.brainWiring = bw;
            m = new Material(color, Color.DARK_GRAY, 2);
        }
 
        public void addArm(Body base, float x, float y, float angle, int armSegments, float armLength, float armWidth) {
            Body[] arm = new Body[armSegments];
                        
            Body prev = base;
            
            double dr = getArmLength(armLength, 0)/2.0;
            
            for (int i = 0; i < armSegments; i++) {
                                
                float al = getArmLength(armLength, i);
                float aw = getArmWidth(armWidth, i);
                                
                float ax = (float)(x + Math.cos(angle) * dr);
                float ay = (float)(y + Math.sin(angle) * dr);
                final Body b = arm[i] = sim.newRectangle(al, aw, ax, ay, angle, 1.0f, m);
                
                float rx = (float)(x + Math.cos(angle) * (dr-al/2.0f));
                float ry = (float)(y + Math.sin(angle) * (dr-al/2.0f));
                final RevoluteJoint j = sim.joinRevolute(arm[i], prev, rx, ry);

                new RevoluteJointByIndexVote(brain, j, -servoRange, servoRange, servoSteps);

                new QuantizedScalarInput(brain, 4) {
                    @Override public float getValue() {
                        return j.getJointAngle()/(float)(Math.PI*2.0f);
                    }  
                };
                new QuantizedScalarInput(brain, velocityLevels) {
                    @Override public float getValue() {
                        final float zl = b.getLinearVelocity().len2();
                        if (zl == 0) return 0;
                        float xx = b.getLinearVelocity().x;
                        xx*=xx;
                        return xx/zl;
                    }  
                };
                new QuantizedScalarInput(brain, velocityLevels) {
                    @Override public float getValue() {
                        final float zl = b.getLinearVelocity().len2();
                        if (zl == 0) return 0;
                        float yy = b.getLinearVelocity().y;
                        yy*=yy;
                        return yy/zl;
                    }  
                };
                        
                new QuantizedScalarInput(brain, orientationSteps) {
                    @Override public float getValue() {
                        return b.getAngle() / (float)(2.0 * Math.PI);
                    }                    
                };
                new QuantizedScalarInput(brain, velocityLevels) {
                    @Override public float getValue() {
                        return b.getAngularVelocity() / (float)(2.0 * Math.PI);
                    }                    
                };
                
                //DEPRECATED
                //brain.addInput(new RevoluteJointAngle(j));                
                //brain.addInput(new VelocityAxis(b, true));                
                //brain.addInput(new VelocityAxis(b, false));                
                //Orientation.newVector(brain, b, orientationSteps);                
                //brain.addInput(new VelocityAngular(b));

                brain.addOutput(new ColorBodyTowards(b, color, 0.95f));
                brain.addOutput(new ColorBodyTowards(b, new Color(color.r * 0.5f, color.g * 0.5f, color.b * 0.5f, color.a * 0.25f), 0.95f));

                int n = numRetinasPerSegment;
                for (float z = 0; z < n; z++) {

                    float a = z * (float)(Math.PI*2.0 / ((float)n));
                    retinas.add(new Retina(brain, b, new Vector2(0, 0), a, (float)initialVisionDistance, retinaLevels));
                }
                //TODO Retina.newVector(...)
                
                //y -= al*0.9f;
                dr += al * 0.9f;
                
                prev = arm[i];
            }
            
            
        }
        
        @Override
        public void init(App s) {

            this.sim = s;
            
            Body base = sim.newCircle(torsoRadius, ix, iy, 1.0f, m);            

            float da = (float)((Math.PI*2.0)/arms);
            float a = 0;
            for (int i = 0; i < arms; i++) {
                float ax = (float)(ix + (Math.cos(a) * torsoRadius));
                float ay = (float)(iy + (Math.sin(a) * torsoRadius));
                addArm(base, ax, ay, a, armSegments, armLength, armWidth);
                a += da;
            }

            //base's eyes
            int n = numRetinasPerSegment;
            for (float z = 0; z < n; z++) {

                float ba = z * (float)(Math.PI*2.0 / ((float)n));
                retinas.add(new Retina(brain, base, new Vector2(0, 0), ba, (float)initialVisionDistance, retinaLevels));
                
                brain.addOutput(new Thruster(base, ba));
            }

            
            brainWiring.wireBrain(brain);
            
            new BrainReport(brain);
            
        }
        
        @Override
        protected void update(double dt) {
            for (final Retina r : retinas) {
                r.update();
            }


            brain.forward();

            brain.forwardOutputs();

        }
        

        @Override
        public void renderUnderlay(Graphics g) {

            for (Retina r : retinas) {
                r.draw();
            }
        }

        @Override
        public void renderOverlay(Graphics g) {
        }

        public void setVisionDistance(float v) {
            for (Retina r : retinas) {
                r.setVisionDistance(v);
            }
        }
        
        private float getArmLength(float armLength, int i) {
            //return armLength * (1.0f - ( (float)i ) / ((float) armSegments ) * 0.5f);
            
            return armLength * (float)Math.pow(armSegmentExponent, i);   //0.618 = golden ratio
        }

        private float getArmWidth(float armWidth, int i) {
            //return armWidth;
            return armWidth * (float)Math.pow(armSegmentExponent, i);
        }

        public ParameterPanel newParameterPanel() {
            ParameterPanel sp = new ParameterPanel(sim.getSkin(), 300, 400);
            sp.addSlider("Memory Factor", 0, 10.0f, 0.999f, new SliderListener() {

                @Override
                public void onChanged(float v) {
                    for (InterNeuron in : brain.getInter()) {
                        in.setPotentialDecay(v);
                    }
                }

            }, "potential multiplier / cycle");
            sp.addSlider("InterNeuron Firing Threshold", 0.1f, 2.0f, 1.0f, new SliderListener() {

                @Override
                public void onChanged(float v) {
                    for (InterNeuron in : brain.getInter()) {
                        in.setFiringThreshold(v);
                    }
                }

            }, "potential");
            sp.addSlider("Max Absolute Synapse Weight", 0.1f, 2.0f, 1.0f, new SliderListener() {

                @Override
                public void onChanged(float v) {
                    for (InterNeuron in : brain.getInter()) {
                        in.setMaxAbsoluteSynapseWeight(v);
                    }
                }

            }, "weight");
    //        sp.addSlider("Wobbly", 0.0f, 1.0f, 0.05f, new SliderListener() {
    //
    //            @Override
    //            public void onChanged(final double v) {
    //                for (CritterdingBrain b : s.brains) {
    //                    for (MotorNeuron mn : b.getMotor()) {
    //                        if (mn instanceof RotateRevoluteJoint) {
    //                            RotateRevoluteJoint rrv = (RotateRevoluteJoint)mn;
    //                            rrv.setWiggle((float)v);                                
    //                        }
    //                    }
    //                }
    //            }
    //            
    //        }, "rot-motor wiggle radians");
            sp.addSlider("Vision Distance", 0f, 20.0f, (float)initialVisionDistance, new SliderListener() {

                @Override
                public void onChanged(final float v) {
                    setVisionDistance(v);
                }

            }, "meters");


            sp.pack();
            return sp;
        }
        
    }
    

    protected static void addControls(String title, App sim, final Spider s) {

        ParameterPanel sp = s.newParameterPanel();
        
        Window w = new Window(title, sim.getSkin());
        w.defaults().spaceBottom(10);
        w.row().fill().expandX();
        w.add(sp);
        w.pack();
        
        sim.addWindow(w);
         
    }
    
    @Override
    public void init(App app) {

        this.sim = app;
        
        World world = app.world;
        
        world.setGravity(new Vector2(0, 9.8f));

        Spacegraph.addWorldBox(app, world, 16f, 7f, 0.1f);
        
        Spider r;
        //app.addCritter(r = new Spider(3, 9, 0.8f, 0, 0, new Color(0.5f, 1f, 0.1f, 0.8f), new RandomWiring(10000, 2, 12, 0.5f, 0.1f)));
        app.addCritter(r = new Spider(3, 4, 0.618f, 0, 0, new Color(0.4f, 0.9f, 1.0f, 0.8f), new RandomWiring(10000, 2, 200, 0.25f, 0.1f)));
        addControls("Spider", app, r);

//        Spider snake = new Spider(1, 12, 0.9f, -4, -1, new Color(0.1f, 0.6f, 0.7f, 0.8f), new RandomWiring(2048, 1, 4, 0.5f, 0.2f));
//        snake.armLength /= 2f;
//        snake.armWidth /= 2f;
//        snake.torsoRadius /= 2f;
//        snake.retinaLevels = 2;
//        snake.numRetinasPerSegment = 7;
//        snake.orientationSteps = 6;
//        app.addCritter(snake);
        
        for (int i = 0; i < 8; i++) {
            Material m = new Material(Color.ORANGE, new Color(1.0f, 0.9f, 0, 0.5f), 3);
            addCircleRock(app, 2f, -2f+i*0.3f, 0.1f + ((float)Math.random()) * 0.15f, m);
        }

        
    }
    
    public void addCircleRock(App sim, float x, float y, float r, Material m) {
        sim.newCircle(r, x, y, 1.0f, m);
    }
    
//        public void addRectRock(App sim, float x, float y, float r, Color c) {
//            sim.newRectangle(r, r*1.6f, x, y, 0, c, 4.0f);
//        }
    
    
    public static void main(String[] args) {
        App.run(new SpiderSim(), "Arm", 1280, 720);
    }
}
