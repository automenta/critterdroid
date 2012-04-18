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
import com.critterdroid.bio.Material;
import com.critterdroid.bio.Material.Text;
import com.critterdroid.bio.Simulation;
import com.critterdroid.entities.Critter;

/**
 *
 * @author seh
 */
public class Spacegraph implements Simulation {
    private App sim;

    public static class TextNode extends Critter {

        public TextNode() {
            
        }
        
        @Override
        public void init(App s) {
        }

        @Override
        protected void update(double dt) {
        }

        @Override
        public void renderUnderlay(Graphics g) {
        }

        @Override
        public void renderOverlay(Graphics g) {
        }    
        
        public void dispose() {
        }
    }
    
    @Override
    public void init(App app) {

        this.sim = app;
        
        World world = app.world;
        
        world.setGravity(new Vector2(0, 0));

        //app.addCritter(new TextNode());
        addWorldBox(app, world, 16f, 7f, 0.1f);
        
//        Spider r;
//        //app.addCritter(r = new Spider(3, 9, 0.8f, 0, 0, new Color(0.5f, 1f, 0.1f, 0.8f), new RandomWiring(10000, 2, 12, 0.5f, 0.1f)));
//        app.addCritter(r = new Spider(2, 6, 0.8f, 0, 0, new Color(0.4f, 0.9f, 1.0f, 0.8f), new RandomWiring(45000, 4, 12, 0.25f, 0.1f)));
//        addControls(r);

//        Spider snake = new Spider(1, 12, 0.9f, -4, -1, new Color(0.1f, 0.6f, 0.7f, 0.8f), new RandomWiring(2048, 1, 4, 0.5f, 0.2f));
//        snake.armLength /= 2f;
//        snake.armWidth /= 2f;
//        snake.torsoRadius /= 2f;
//        snake.retinaLevels = 2;
//        snake.numRetinasPerSegment = 7;
//        snake.orientationSteps = 6;
//        app.addCritter(snake);
        
        for (int i = 0; i < 4; i++) {
            Material m = new Material(Color.ORANGE, new Color(1.0f, 0.9f, 0, 0.5f), 3);
            m.addText(new Text("SeH", 0, 0, 1, 1));
            addCircleRock(app, 2f, -2f+i*0.3f, 0.1f + ((float)Math.random()) * 0.15f, m);
        }

        
    }
    
    public void addCircleRock(App sim, float x, float y, float r, Material m) {
        sim.newCircle(r, x, y, 1.0f, m);
    }
//    
////        public void addRectRock(App sim, float x, float y, float r, Color c) {
////            sim.newRectangle(r, r*1.6f, x, y, 0, c, 4.0f);
////        }
//    
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
        App.run(new Spacegraph(), "Spacegraph", 1280, 720);
    }
}
