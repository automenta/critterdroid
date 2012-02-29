/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation;

import com.critterdroid.entities.Critter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.critterdroid.bio.Simulation;
import com.critterdroid.bio.brain.RandomWiring;
import com.critterdroid.entities.DistributedSpider;
import com.critterdroid.entities.DistributedSpider.BrainWiring;
import com.critterdroid.simulation.ui.LineChartPanel;
import com.critterdroid.simulation.ui.NeuronGridPanel;
import com.critterdroid.simulation.ui.ParameterPanel;
import java.util.ArrayList;
import jcog.critterding.CritterdingBrain;
import jcog.critterding.CritterdingNeuron;
import jcog.critterding.InterNeuron;
import jcog.math.RandomNumber;

/**
 *
 * @author seh
 */
public class SeHSpiderSimulation implements Simulation {
    
    public interface SliderListener {
        public void onChanged(double v);
    }
    
    protected void addCreatures(App sim) {
        
        
        sim.physicsWorld.setGravity(new Vector2(0, 80.0f));
        //sim.physicsWorld.setGravity(new Vector2(0, 0.0f));
        
        //BrainWiring wiring = new StructuredWiring(3);
        //BrainWiring wiring = new RandomWiring(numNeurons, minSynapses, maxSynapses, percentChanceSensorySynapse, percentChanceMotorNeuron, potentialDecay)
        BrainWiring cWiring = new RandomWiring(2048, 4, 10, 0.1, 0.1, 0.9);
        BrainWiring lWiring = new RandomWiring(128, 2, 4, 0.35, 0.35, 0.9);
                
        final DistributedSpider s = new DistributedSpider(new Vector2(300, 200), 3, 2, cWiring, lWiring, 6, 6, 32);        
        sim.addCritter(s);        
        
        {
            ArrayList<CritterdingNeuron> allInputs = new ArrayList();
            for (CritterdingBrain b : s.brains)
                allInputs.addAll(b.getSense());
            
            ArrayList<CritterdingNeuron> allOutputs = new ArrayList();
            for (CritterdingBrain b : s.brains)
                allOutputs.addAll(b.getMotor());
            
            final NeuronGridPanel inp = new NeuronGridPanel(s, allInputs, 250, 100);
            sim.window.add(inp);
            
            final NeuronGridPanel outp = new NeuronGridPanel(s, allOutputs, 250, 100);
            outp.setNeuronWidth(7);
            sim.window.add(outp);
            
            final LineChartPanel lcp = new LineChartPanel(s, 250, 100, 0.0f, 1.0f) {
                @Override
                public void onUpdate(Critter c, double dt) {
                    float totalFired = 0;
                    int totalMotors = 0;
                                        
                    for (CritterdingBrain b : s.brains) {
                        totalMotors += b.getNumOutputs();
                    }
                    
                    for (CritterdingBrain b : s.brains) {
                        totalFired += b.getMotorNeuronsFired(true);
                    }
                    
                    push(totalFired / totalMotors);
                }                
            };
            sim.window.add(lcp);
            sim.window.row();
            
            final Label nl = new Label("Neurons / Synapses", sim.getSkin());
            s.addUpdateListener(new Critter.CritterUpdateListener() {
                @Override public void onUpdate(Critter c, double dt) {
                    nl.setText(s.getCortex().getNumInterNeurons() + " interneurons, " + s.getCortex().getNumSynapses() + " synapses");
                }
            });

            sim.window.add(nl).expandX();
            
            sim.window.pack();            
        }

        
        
        ParameterPanel sp = new ParameterPanel(sim.getSkin(), 300, 400);
        sp.addSlider("Amphetamine", 0, 3.5f, 0.95f, new SliderListener() {

            @Override
            public void onChanged(double v) {
                for (CritterdingBrain b : s.brains) {
                    for (InterNeuron in : b.getInter()) {
                        in.setPotentialDecay(v);
                    }
                }
            }
            
        }, "potential multiplier / cycle");
        sp.addSlider("InterNeuron Firing Threshold", 0.1f, 2.0f, 1.0f, new SliderListener() {

            @Override
            public void onChanged(double v) {
                for (CritterdingBrain b : s.brains) {
                    for (InterNeuron in : b.getInter()) {
                        in.setFiringThreshold(v);
                    }
                }
            }
            
        }, "potential");
        sp.addSlider("Max Absolute Synapse Weight", 0.1f, 2.0f, 1.0f, new SliderListener() {

            @Override
            public void onChanged(double v) {
                for (CritterdingBrain b : s.brains) {
                    for (InterNeuron in : b.getInter()) {
                        in.setMaxAbsoluteSynapseWeight(v);
                    }
                }
            }
            
        }, "weight");
//        sp.addSlider("Adrenaline", 0.0f, 0.2f, 0.05f, new SliderListener() {
//
//            @Override
//            public void onChanged(final double v) {
//                for (CritterdingBrain b : s.brains) {
//                    for (MotorNeuron mn : b.getMotor()) {
//                        if (mn instanceof RotateRevoluteJoint) {
//                            RotateRevoluteJoint rrv = (RotateRevoluteJoint)mn;
//                            rrv.setFactor((float)v);
//                                
//                        }
//                    }
//                }
//            }
//            
//        }, "rot-motor radians / spike");
//        sp.addSlider("Stillness", 0, 1.0f, 0.05f, new SliderListener() {
//
//            @Override
//            public void onChanged(final double v) {
//                for (CritterdingBrain b : s.brains) {
//                    for (MotorNeuron mn : b.getMotor()) {
//                        if (mn instanceof RotateRevoluteJoint) {
//                            RotateRevoluteJoint rrv = (RotateRevoluteJoint)mn;
//                            rrv.setRestitution(1.0f - (float)v);
//                                
//                        }
//                    }
//                }
//            }
//            
//        }, "rot-motor angle restore / cycle");
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
        sp.addSlider("Vision Distance", 1f, 1000f, 250f, new SliderListener() {

            @Override
            public void onChanged(final double v) {
                s.setVisionDistance((float)v);
            }
            
        }, "meters");
        sp.addSlider("Add New Neurons", 0f, 10f, 0f, new SliderListener() {

            @Override
            public void onChanged(final double v) {
                s.setAddNewNeurons((int)v);
            }
            
        }, "neurons per cycle");
        sp.addSlider("Remove Dead Synapses", 0f, 10f, 0f, new SliderListener() {

            @Override
            public void onChanged(final double v) {
                s.setDeadSynapsesRemoved((int)v);
            }
            
        }, "neurons per cycle");
        sp.addSlider("Brain Iterations", 0f, 8f, 1f, new SliderListener() {

            @Override
            public void onChanged(final double v) {
                s.setBrainCyclesPerFrame((int)v);
            }
            
        }, "cycles per frame");
        
        
        /* MORE ONLINE PARAMETERS:
         * 
         * max revolution limits
         * synaptic plasticity rate (strengteh & weaken separate)
         * noise
         */
        
        sp.pack();
        
        Window w = new Window("Critter Parameters", sim.getSkin());
        w.defaults().spaceBottom(10);
        w.row().fill().expandX();
        w.add(sp);
        w.pack();
        
        sim.getStage().addActor(w);
        
        
        
        
    }
    
    @Override
    public void init(App app) {

        World physicsWorld = app.physicsWorld;
        
        physicsWorld.setGravity(new Vector2(0, 9.8f));

        float wallThick = 8;
        
        //create walls to keep the balls in bounds:
        PolygonShape verticalWall = new PolygonShape();
        verticalWall.setAsBox(wallThick, app.getHeight()/2.0f);

        PolygonShape horizontalWall = new PolygonShape();
        horizontalWall.setAsBox(app.getWidth()/2.0f,  wallThick );

        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //left wall:
            Body leftWall = physicsWorld.createBody(wallDef);
            leftWall.createFixture(verticalWall, wallThick);
            leftWall.setTransform(new Vector2(0, app.getHeight()/2.0f), 0);
        }

        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;
            
            //right wall:
            Body rightWall = physicsWorld.createBody(wallDef);
            rightWall.createFixture(verticalWall, wallThick);
            rightWall.setTransform(new Vector2(app.getWidth()-20, app.getHeight()/2.0f), 0);
            //rightWall.setTransform(new Vector2(app.getWidth()-50, 0), 0);
        }
        

        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //floor:
            PolygonShape roofShape = new PolygonShape();
            roofShape.setAsBox(app.getWidth()/2.0f, wallThick);

            Body roof = physicsWorld.createBody(wallDef);
            roof.createFixture(roofShape, 1);
            roof.setTransform(new Vector2(app.getWidth()/2.0f, app.getHeight()-20), 0);
        }
        
        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //ceiling:
            PolygonShape roofShape = new PolygonShape();
            roofShape.setAsBox(app.getWidth()/2.0f, wallThick);

            Body roof = physicsWorld.createBody(wallDef);
            roof.createFixture(roofShape, 1);
            roof.setTransform(new Vector2(app.getWidth()/2.0f, 20), 0);
        }

        for (int i = 0; i < 4; i++) {
            addCircleRock(app, 100+30*i, 100, RandomNumber.getFloat(10, 30), new Color(0.3f, 1.0f- ((float)Math.random())*0.3f, 0.15f, 1.0f));
            addRectRock(app, 100+30*i, 100, RandomNumber.getFloat(30, 50), new Color(1.0f - ((float)Math.random())*0.3f, 0.3f, 0.15f, 1.0f));
        }
        //addCircleRock(sim, 500, 400, 80, new Color(1.0f, 0.3f, 0.5f));

        addCreatures(app);
        //sim.addCritter(new Snake(new Vector2(200, 300), 8));
    }
    
    
    public void addCircleRock(App sim, float x, float y, float r, Color c) {
        sim.createBall(r, x, y, c, 1.0f);
    }
    public void addRectRock(App sim, float x, float y, float r, Color c) {
        sim.createRectangle(r, r*1.6f, x, y, 0, c, 4.0f);
    }
    
    public static void main(String[] args) {
        App.run(new SeHSpiderSimulation(), "Spiders", 640, 480);
    }
}
