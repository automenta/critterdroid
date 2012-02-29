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
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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
import com.critterdroid.simulation.ui.MeshPanel;
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

        float baseLength = 60;
        float baseWidth = 150.0f;

        int armSegments = 3;
        
        float armLength = 65.0f;
        float armWidth = 28.0f;
        
        float servoRange = ((float)Math.PI / 2.0f) * 0.9f;
        int servoSteps = 6;
        int numRetinasPerSegment = 8;
        
        float visionDistance = 550f;
        
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
            Body base = s.createRectangle(baseWidth, baseLength, ix, iy, 0, Color.WHITE, 160.0f);

        
            Body[] arm = new Body[armSegments];
            
            float x = base.getWorldCenter().x;
            float y = base.getWorldCenter().y - baseLength;
            
            Body prev = base;
            
            y -= armLength*0.3;
            
            for (int i = 0; i < armSegments; i++) {
                Body b = arm[i] = s.createRectangle(armWidth, armLength, x, y, 0, Color.WHITE, 2.0f);
                
                RevoluteJoint j = s.joinRevolute(arm[i], prev, x, y+armLength/2.0f);

                //brain.addOutput(new RotateRevoluteJoint(rj, range, jointSpeed, false));
                //brain.addOutput(new RotateRevoluteJoint(rj, range, jointSpeed, true));
                
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
            
            
            int mHeight = 128;
            int mWidth = 8;
            
            //brainMesh = new MeshWiring(brain, mHeight, mWidth);
            //brainMesh.wireBrain(brain);
            
            //new RandomWiring(400, 4, 16, 0.25f, 0.1f, 0.9f).wireBrain(brain);
            new RandomWiring(128, 2, 4, 0f, 0f, 0.9f).wireBrain(brain);
            
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
                App.setColor(r.getColor());
                App.setLineWidth(2.0f);
                App.drawLine(r.p1.x, r.p1.y, r.pint.x, r.pint.y);
            }
        }

        @Override
        public void renderOverlay(Graphics g) {
        }
        
    }
    
    
    @Override
    public void init(App app) {

        World physicsWorld = app.physicsWorld;
        
        physicsWorld.setGravity(new Vector2(0, 9.8f));

        addWorldBox(app, physicsWorld);
        
//        for (int i = 0; i < 7; i++) {
//            addCircleRock(app, 100+30*i, 100, RandomNumber.getFloat(10, 30), new Color(0.3f, 1.0f- ((float)Math.random())*0.3f, 0.15f, 1.0f));
//            addRectRock(app, 100+30*i, 100, RandomNumber.getFloat(30, 50), new Color(1.0f - ((float)Math.random())*0.3f, 0.3f, 0.15f, 1.0f));
//        }
        
        //addCircleRock(sim, 500, 400, 80, new Color(1.0f, 0.3f, 0.5f));

        RobotArm ra = new RobotArm(300, 700);
        app.addCritter(ra);
        
        //app.addCritter(new RobotArm(500, 700));

        for (int i = 0; i < 8; i++) {
            addCircleRock(app, 400f, 200f+i*30f, 16f, new Color(1.0f, 0.5f, 0.0f, 1.0f));
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
    
        public void addCircleRock(App sim, float x, float y, float r, Color c) {
            sim.createBall(r, x, y, c, 1.0f);
        }
        public void addRectRock(App sim, float x, float y, float r, Color c) {
            sim.createRectangle(r, r*1.6f, x, y, 0, c, 4.0f);
        }
    
    public void addWorldBox(final App app, final World physicsWorld) {
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
        
    }
    
}
