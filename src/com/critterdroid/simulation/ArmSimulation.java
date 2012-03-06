/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.simulation;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.critterdroid.bio.Material;
import com.critterdroid.bio.Simulation;
import com.critterdroid.bio.act.ServoRevoluteJoint;
import com.critterdroid.bio.brain.MeshWiring;
import com.critterdroid.bio.brain.RandomWiring;
import com.critterdroid.bio.feel.Orientation;
import com.critterdroid.bio.feel.Retina;
import com.critterdroid.bio.feel.RevoluteJointAngle;
import com.critterdroid.bio.feel.VelocityAngular;
import com.critterdroid.bio.feel.VelocityAxis;
import com.critterdroid.entities.Critter;
import java.util.LinkedList;
import java.util.List;
import jcog.critterding.BrainReport;
import jcog.critterding.CritterdingBrain;

/**
 *
 * @author seh
 */
public class ArmSimulation implements Simulation {

    public class RobotArm extends Critter {

        float baseLength = 0.5f;
        float baseWidth = 0.5f;

        int armSegments = 4;
        
        float armLength = 0.5f;
        float armWidth = 0.1f;
        
        float servoRange = ((float)Math.PI / 2.0f) * 0.9f;
        int servoSteps = 6;
        int numRetinasPerSegment = 32;
        
        float visionDistance = 10.0f;
        
        CritterdingBrain brain = new CritterdingBrain();
        List<Retina> retinas = new LinkedList();
        private final float ix;
        private final float iy;
        MeshWiring brainMesh;

        public RobotArm(float ix, float iy) {
            super();
            this.ix = ix;
            this.iy = iy;
        }
 
        
        @Override
        public void init(App s) {
            Material m = new Material(Color.GRAY, Color.DARK_GRAY, 2);
            
            Body base = s.newRectangle(baseWidth, baseLength, ix, iy, 0, 160.0f, m);

        
            Body[] arm = new Body[armSegments];
            
            float x = base.getWorldCenter().x;
            float y = base.getWorldCenter().y - baseLength;
            
            Body prev = base;
            
            y -= armLength*0.3;
            
            for (int i = 0; i < armSegments; i++) {
                
                Body b = arm[i] = s.newRectangle(armWidth, armLength, x, y, 0, 1.0f, m);
                
                RevoluteJoint j = s.joinRevolute(arm[i], prev, x, y+armLength/2.0f);

                new ServoRevoluteJoint(brain, j, -servoRange, servoRange, servoSteps);
                
                brain.addInput(new RevoluteJointAngle(j));


                brain.addInput(new VelocityAxis(b, true));
                brain.addInput(new VelocityAxis(b, false));
                brain.addInput(new Orientation(b));
                brain.addInput(new VelocityAngular(b));


                int n = numRetinasPerSegment;
                for (float z = 0; z < n; z++) {

                    float a = z * (float)(Math.PI*2.0 / ((float)n));
                    retinas.add(new Retina(brain, b, new Vector2(0, 0), a, visionDistance));
                }
                
                
                y -= armLength*1.3f;
                
                prev = arm[i];
            }
            
            
            //brainMesh = new MeshWiring(brain, mHeight, mWidth);
            //brainMesh.wireBrain(brain);
            
            //new RandomWiring(400, 4, 16, 0.25f, 0.1f, 0.9f).wireBrain(brain);
            new RandomWiring(4096, 4, 16, 0.25f, 0.1f, 0.95f).wireBrain(brain);
            
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
        
    }
    
    
    @Override
    public void init(App app) {

        World world = app.world;
        
        world.setGravity(new Vector2(0, 9.8f));

        addWorldBox(app, world, 10f, 7f, 0.2f);
        
        app.addCritter(new RobotArm(0, 0));
        
        //app.addCritter(new RobotArm(500, 700));

        for (int i = 0; i < 8; i++) {
            Material m = new Material(Color.ORANGE, new Color(1.0f, 0.9f, 0, 0.5f), 5);
            addCircleRock(app, 2f, -2f+i*0.3f, 0.1f + ((float)Math.random()) * 0.2f, m);
        }

        
//        MeshPanel mv = new MeshPanel(ra, ra.brainMesh, 2);
//        mv.pack();
//        
//        Window w = new Window("Brain Mesh", app.getSkin());
//        w.defaults().spaceBottom(50);
//        w.row().fill().expandX();
//        w.add(mv).expand();
//        w.pack();
//        
//        
//        app.getStage().addActor(w);
        
    }
    
    public void addCircleRock(App sim, float x, float y, float r, Material m) {
        sim.newCircle(r, x, y, 1.0f, m);
    }
    
//        public void addRectRock(App sim, float x, float y, float r, Color c) {
//            sim.newRectangle(r, r*1.6f, x, y, 0, c, 4.0f);
//        }
    
    public void addWorldBox(final App app, final World physicsWorld, float w, float h, float wallThick) {
        
        //create walls to keep the balls in bounds:
        PolygonShape verticalWall = new PolygonShape();
        verticalWall.setAsBox(wallThick, h/2f);

        PolygonShape horizontalWall = new PolygonShape();
        horizontalWall.setAsBox(w/2f,  wallThick );

        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //left wall:
            Body leftWall = physicsWorld.createBody(wallDef);
            leftWall.createFixture(verticalWall, 1);
            leftWall.setTransform(new Vector2(-w/2, wallThick), 0);
        }

        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //floor:
            Body bottomWall = physicsWorld.createBody(wallDef);
            bottomWall.createFixture(horizontalWall, 1);
            bottomWall.setTransform(new Vector2(0, h/2), 0);
            
            app.setGroundBody(bottomWall);
        }

        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //right wall:
            Body rightWall = physicsWorld.createBody(wallDef);
            rightWall.createFixture(verticalWall, 1);
            rightWall.setTransform(new Vector2(w/2, wallThick), 0);
        }
        
        {
            BodyDef wallDef = new BodyDef();
            wallDef.type = BodyType.StaticBody;

            //ceiling:
            Body topWall = physicsWorld.createBody(wallDef);
            topWall.createFixture(horizontalWall, 1);
            topWall.setTransform(new Vector2(0, -h/2), 0);
        }
        
//        {
//            BodyDef wallDef = new BodyDef();
//            wallDef.type = BodyType.StaticBody;
//            
//            //right wall:
//            Body rightWall = physicsWorld.createBody(wallDef);
//            rightWall.createFixture(verticalWall, wallThick);
//            rightWall.setTransform(new Vector2(app.getWidth()-20, app.getHeight()/2.0f), 0);
//            //rightWall.setTransform(new Vector2(app.getWidth()-50, 0), 0);
//        }
        

        
//        {
//            BodyDef wallDef = new BodyDef();
//            wallDef.type = BodyType.StaticBody;
//
//            //ceiling:
//            PolygonShape roofShape = new PolygonShape();
//            roofShape.setAsBox(app.getWidth()/2.0f, wallThick);
//
//            Body roof = physicsWorld.createBody(wallDef);
//            roof.createFixture(roofShape, 1);
//            roof.setTransform(new Vector2(app.getWidth()/2.0f, 20), 0);
//        }
        
    }
    
    public static void main(String[] args) {
        App.run(new ArmSimulation(), "Arm", 1280, 720);
    }
}
