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
import com.critterdroid.bio.act.ColorBodyTowards;
import com.critterdroid.bio.act.RevoluteJointByTotalActivation;
import com.critterdroid.bio.act.Spinner;
import com.critterdroid.bio.act.Thruster;
import com.critterdroid.bio.brain.Brain;
import com.critterdroid.bio.feel.Retina;
import com.critterdroid.entities.Critter;
import com.critterdroid.simulation.App;
import com.critterdroid.simulation.ui.ParameterPanel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
abstract public class RLSpiderSim implements Simulation {
    private App sim;

    List<Body> blocks = new LinkedList();
    List<Body> food = new LinkedList();
    final float worldSize = 5.0f;
    
    public class Spider extends Critter {
        float armLength = 0.4f;
        float armWidth = 0.25f;

        int arms;
        int armSegments;

        float torsoRadius = 0.25f;
        
        float servoRange = ((float)Math.PI / 2.0f) * 0.7f;
        int servoSteps = 2;
        
        float thrusterForce = 0.15f;
        float spinnerTorque = 0.01f;
        
        int numThrustersPerTorso = 1;
        int retinaLevels = 2;
        
        final int velocityLevels = 16;
        final int positionLevels = 16;

        int orientationLevels = 16;
        
        double initialVisionDistance = worldSize*(1.25f);

            
        
        float armSegmentExponent;
                
        public final Brain brain;
        List<Retina> retinas = new LinkedList();
        List<Thruster> thrusters = new LinkedList();
        private final float ix;
        private final float iy;
        private final Color color;
        private final Material m;
        private App sim;
        public Body torso;
        private List<Body> bodies = new LinkedList();

        public Spider(Brain b, int arms, int armSegments, float armSegmentExponent, float ix, float iy, Color c) {
            super();
            this.arms = arms;
            this.armSegments = armSegments;
            this.armSegmentExponent = armSegmentExponent;
            this.ix = ix;
            this.iy = iy;
            this.color = c;
            m = new Material(color, Color.DARK_GRAY, 2);
            
            brain = b;
        }

        Vector2 worldCenter = new Vector2();
        
        public Vector2 getWorldCenter() {
            worldCenter.set(0,0);
            if (!bodies.isEmpty()) {
                for (Body b : bodies) {
                    worldCenter.add(b.getWorldCenter());
                }
                float m = 1.0f / ((float)bodies.size());
                worldCenter.set(worldCenter.x * m, worldCenter.y * m);
            }
            return worldCenter;
        }
        
        public void addArm(Body base, float x, float y, float angle, int armSegments, float armLength, float armWidth, int level, int maxLevel) {
            Body[] arm = new Body[armSegments];
                        
            Body prev = base;
            
            double dr = getArmLength(armLength, 0)/2.0;
            float ax=0, ay=0, al=0, aw=0;
            
            for (int i = 0; i < armSegments; i++) {
                                
                al = getArmLength(armLength, i);
                aw = getArmWidth(armWidth, i);
                                
                ax = (float)(x + Math.cos(angle) * dr);
                ay = (float)(y + Math.sin(angle) * dr);
                final Body b = arm[i] = sim.newRectangle(al, aw, ax, ay, angle, 1.0f, m);
                bodies.add(b);
                
                float rx = (float)(x + Math.cos(angle) * (dr-al/2.0f));
                float ry = (float)(y + Math.sin(angle) * (dr-al/2.0f));
                final RevoluteJoint j = sim.joinRevolute(arm[i], prev, rx, ry);

                new RevoluteJointByTotalActivation(brain, j, -servoRange, servoRange, servoSteps);

//                new QuantizedScalarInput(brain, 1) {
//                    @Override public float getValue() {
//                        return App.normalizeAngle(j.getJointAngle())/(float)(Math.PI*2.0f);
//                    }  
//                };
                addVelocities(b, false, true, false, false);
                        
                
                //DEPRECATED
                //brain.addInput(new RevoluteJointAngle(j));                
                //brain.addInput(new VelocityAxis(b, true));                
                //brain.addInput(new VelocityAxis(b, false));                
                //Orientation.newVector(brain, b, orientationSteps);                
                //brain.addInput(new VelocityAngular(b));

                brain.addOutput(new ColorBodyTowards(b, color, 0.95f));
                brain.addOutput(new ColorBodyTowards(b, new Color(color.r * 0.5f, color.g * 0.5f, color.b * 0.5f, color.a * 0.25f), 0.95f));

                int n = getNumRetinas(i, level, maxLevel );
                for (float z = 0; z < n; z++) {

                    float a = 
                            (i == armSegments-1) ? 
                            z * (float)(Math.PI*1.0 / ((float)n)) + (float)(Math.PI*1.5) + angle
                            :
                            z * (float)(Math.PI*2.0 / ((float)n)) + (float)(Math.PI*0.25) + angle;
                    
                    retinas.add(new Retina(brain, b, new Vector2(0, 0), a, (float)initialVisionDistance, retinaLevels));
                }
                //TODO Retina.newVector(...)
                
                //y -= al*0.9f;
                dr += al * 0.9f;
                
                prev = arm[i];
                
            }
            if (level!=maxLevel) {
                int nextArmSegments = armSegments / 2;
                float nextArmWidth = aw * 0.618f;
                float nextArmLength = al * 0.618f;
                if (nextArmSegments == 0) {
                    nextArmSegments = 1;
                }
                
                float aa = angle - 0.3f;
                for (int i = 0; i < 2; i++) {                
                    
                    float tax = ax + (float)Math.cos(aa) * al/1.2f;
                    float tay = ay + (float)Math.sin(aa) * al/1.2f;
                    addArm(prev, tax, tay, aa, nextArmSegments, nextArmLength, nextArmWidth, level+1, maxLevel);
                    
                    aa += 0.6f;
                }
                
            }
            
            
        }
        
        
        @Override
        public void init(App s) {

            this.sim = s;
            
            torso = sim.newCircle(torsoRadius, ix, iy, 3f, m);            
            torso.setAngularDamping(0.9f);
            torso.setLinearDamping(0.9f);
            bodies.add(torso);

            float da = (float)((Math.PI*2.0)/arms);
            float a = 0;
            for (int i = 0; i < arms; i++) {
                float ax = (float)(ix + (Math.cos(a) * torsoRadius));
                float ay = (float)(iy + (Math.sin(a) * torsoRadius));
                addArm(torso, ax, ay, a, armSegments, armLength, armWidth, 0, 0);
                a += da;
            }

            //base's eyes
            int n = getNumRetinaTorso();
            for (float z = 0; z < n; z++) {

                float ba = z * (float)(Math.PI*2.0 / ((float)n));
                retinas.add(new Retina(brain, torso, new Vector2(0, 0), ba, (float)initialVisionDistance, retinaLevels, false));
                
            }

            addVelocities(torso, true, true, true, true);
            
            n = numThrustersPerTorso;
            for (float z = 0; z < n; z++) {
                float ba = z * (float)(Math.PI*2.0 / ((float)n));
                Thruster t;
                brain.addOutput(t = new Thruster(torso, ba, false, thrusterForce));
                thrusters.add(t);
            }
            
            
            brain.addOutput(new Spinner(torso, -spinnerTorque));
            brain.addOutput(new Spinner(torso, spinnerTorque));

            

            
        }
        
        int c = 0;
        
        @Override
        protected void update(double dt) {
            for (final Retina r : retinas) {
                r.update();
            }


            brain.forward();

            brain.forwardOutputs();
            
            c++;

//            if (c%100==0)
//                brain.plots();
            

        }
        

        @Override
        public void renderUnderlay(Graphics g) {
            //System.out.println("reward: " + brain.getLastReward());
//            float rs = brain.getLastReward();
//            CharSequence t = ((Label) sim.getStage().findActor("label")).getText();
//            ((Label) sim.getStage().findActor("label")).setText(t + " reward=" + rs );

            for (Retina r : retinas) {
                r.draw();
            }
        }

        @Override
        public void renderOverlay(Graphics g) {
            for (Thruster t : thrusters) {
                t.draw(sim);
            }
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
            ParameterPanel sp = new ParameterPanel(sim.getSkin(), 300, 300);
//            sp.addSlider("Memory Factor", 0, 10.0f, 0.999f, new SliderListener() {
//
//                @Override
//                public void onChanged(float v) {
//                    for (InterNeuron in : brain.getInter()) {
//                        in.setPotentialDecay(v);
//                    }
//                }
//
//            }, "potential multiplier / cycle");
//            sp.addSlider("InterNeuron Firing Threshold", 0.1f, 2.0f, 1.0f, new SliderListener() {
//
//                @Override
//                public void onChanged(float v) {
//                    for (InterNeuron in : brain.getInter()) {
//                        in.setFiringThreshold(v);
//                    }
//                }
//
//            }, "potential");
//            sp.addSlider("Max Absolute Synapse Weight", 0.1f, 2.0f, 1.0f, new SliderListener() {
//
//                @Override
//                public void onChanged(float v) {
//                    for (InterNeuron in : brain.getInter()) {
//                        in.setMaxAbsoluteSynapseWeight(v);
//                    }
//                }
//
//            }, "weight");
            
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

        private int getNumRetinaTorso() {
            return numThrustersPerTorso*8;
        }
        private int getNumRetinas(int segment, int level, int maxLevels) {
            if ((segment == armSegments - 1) && (level == maxLevels)) {
                return 12;
            }
            return 4;
        }

        private void addVelocities(final Body b, boolean position, boolean orientation, boolean linearVelocity, boolean angularVelocity ) {
            for (float ff = -1; ff<=1; ff+=2.0f ) {
                final float f = ff;
                if (linearVelocity) {
                    new QuantizedScalarInput(brain, velocityLevels) {
                        @Override public float getValue() {
                            final float zl = b.getLinearVelocity().len();
                            if (zl == 0) return 0;
                            float xx = b.getLinearVelocity().x * f;
                            return Math.min(1,Math.max(0, xx/zl));
                        }  
                    };
                    new QuantizedScalarInput(brain, velocityLevels) {
                        @Override public float getValue() {
                            final float zl = b.getLinearVelocity().len();
                            if (zl == 0) return 0;
                            float yy = b.getLinearVelocity().y * f;
                            return Math.min(1, Math.max(0, yy/zl));
                        }  
                    };
                }
                if (angularVelocity) {
                    new QuantizedScalarInput(brain, velocityLevels) {
                        @Override public float getValue() {
                            final float maxRadsPerSecond = 2.0f; //n cycles per second is the max velocity before maxing out the signal input
                            return Math.min(1.0f, Math.max(0, f * b.getAngularVelocity() / (maxRadsPerSecond * (float)(2.0 * Math.PI))));
                        }                    
                    };
                }
                if (position) {
                    new QuantizedScalarInput(brain, positionLevels) {
                        @Override public float getValue() {
                            return Math.max(0, Math.min(1, torso.getWorldCenter().x / worldSize * f));
                        }                    
                    };
                    new QuantizedScalarInput(brain, positionLevels) {
                        @Override public float getValue() {
                            return Math.max(0, Math.min(1, torso.getWorldCenter().y / worldSize * f));
                        }                    
                    };
                }
            
            }
            if (orientation) {
                new QuantizedScalarInput(brain, orientationLevels) {
                    @Override public float getValue() {
                        return App.normalizeAngle(b.getAngle()) / (float)(2.0 * Math.PI);
                    }                    
                };
            }
 
        }

        public float getThrusterTemperature() {
            float te = 0;
            for (Thruster t : thrusters) {
                te += t.getTemperature();
            }
            return te;
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
    
    abstract public Brain getBrain();
    abstract public void initBrain(Spider s);
    
    @Override
    public void init(App app) {

        this.sim = app;
        
        World world = app.world;
        
        world.setGravity(new Vector2(0, 0f));

        Spacegraph.addWorldBox(app, world, worldSize, worldSize, 0.15f);

        Brain brain = getBrain();

        final Spider r;
        app.addCritter(r = new Spider(brain,2, 1, 0.618f, 0, 0, new Color(0.9f, 0.4f, 0.8f, 0.9f)));
        addControls("Spider", app, r);

        initBrain(r);
        
//        Spider snake = new Spider(1, 12, 0.9f, -4, -1, new Color(0.1f, 0.6f, 0.7f, 0.8f), new RandomWiring(2048, 1, 4, 0.5f, 0.2f));
//        snake.armLength /= 2f;
//        snake.armWidth /= 2f;
//        snake.torsoRadius /= 2f;
//        snake.retinaLevels = 2;
//        snake.numRetinasPerSegment = 7;
//        snake.orientationSteps = 6;
//        app.addCritter(snake);
        
//        for (int i = 0; i < 8; i++) {
//            Material m = new Material(Color.ORANGE, new Color(1.0f, 0.9f, 0, 0.5f), 3);
//            addBlock(app, 2f, -2f+i*0.3f, 0.1f + ((float)Math.random()) * 0.95f, m);
//        }

        //for (int i = 0; i < 1; i++) {
            Material m = new Material(new Color(0.15f, 0.75f, 0.45f, 0.75f), new Color(0.1f, 0.5f, 0.1f, 0.15f), 1);
            addFood(app, worldSize/2.5f, worldSize/2.5f, 0.1f + ((float)Math.random()) * 0.1f, m);
        //}
        
    }
    
    public void addBlock(App sim, float x, float y, float r, Material m) {
        //blocks.add( sim.newCircle(r, x, y, 1.0f, m) );
        Body f;
        blocks.add( f = sim.newRectangle(r, r*0.618f, x, y, 0, 10.0f, m) );
        f.setLinearDamping(0.3f);
        f.setAngularDamping(0.5f);
    }
    public void addFood(App sim, float x, float y, float r, Material m) {
        //blocks.add( sim.newCircle(r, x, y, 1.0f, m) );
        Body f = sim.newCircle(r, x, y, 1.0f, 0f, 0.95f, m);
        f.setLinearDamping(0.3f);
        food.add( f );
    }
    
//        public void addRectRock(App sim, float x, float y, float r, Color c) {
//            sim.newRectangle(r, r*1.6f, x, y, 0, c, 4.0f);
//        }
    
    
}
