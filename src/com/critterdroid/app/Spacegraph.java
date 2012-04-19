/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.app;

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
import com.critterdroid.simulation.App;

/**
 *
 * @author seh
 */
public class Spacegraph implements Simulation {
    private App sim;

    
    @Override
    public void init(App app) {

        this.sim = app;
        
        World world = app.world;
        
        world.setGravity(new Vector2(0, 0));

        addWorldBox(app, world, 16f, 12f, 0.1f);
        
        for (int i = 0; i < 4; i++) {
            Material m = new Material(Color.GRAY.mul(0.95f), new Color(1.0f, 1.0f, 1.0f, 0.5f), 5);
            m.addText(new Text("Text", -6, 1, 1.0f, 1.0f));
            
            sim.newRectangle(0.8f+i, 0.4f+i, 3f, -2f+i*0.3f, 0, 1.0f, m);            
        }

        
    }
    
    public void addCircleRock(App sim, float x, float y, float r, Material m) {
        sim.newCircle(r, x, y, 1.0f, m);
    }
    
    public static void addWorldBox(final App app, final World physicsWorld, float w, float h, float wallThick) {
        
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
        
    }
    
    public static void main(String[] args) {
        App.run(new Spacegraph(), "Spacegraph", 1280, 720);
    }
}
