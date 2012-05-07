/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.bio.feel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.critterdroid.bio.Material;
import com.critterdroid.bio.brain.Brain;
import com.critterdroid.simulation.App;
import jcog.critterding.InputNeuron;

/**
 *
 * @author seh
 */
public class Retina implements RayCastCallback {

    final public Color c = new Color(0, 0, 0, 1f); //color seen
    private final Body body;
    private final Vector2 offset;
    private final float angle;
    private final Brain brain;
    final public Vector2 p1 = new Vector2();
    final public Vector2 p2 = new Vector2();
    final public Vector2 pint = new Vector2();
    private float maxDistance;
    private Material material;

    public class RetinaSensor extends InputNeuron {
        private final int channel;
        private final float iStart;
        private final float iStop;

        public RetinaSensor(int channel /* 0=R, 1=G, 2=B */, float iStart, float iStop) {
            this.channel = channel;
            this.iStart = iStart;
            this.iStop = iStop;
        }
        
        @Override public double getOutput() {
            float v = 0;
            if (channel == 0)
                v = c.r;
            else if (channel == 1)
                v = c.g;
            else
                v = c.b;

            if (iStart > v)
                return 0.0;
            else if (iStop <= v) {
                return 1.0;
            }
            else  {
                return (v - iStart) / (iStop - iStart);
            }
        }
    
        
    }
    
    public Retina(final Brain brain, Body body, Vector2 offset, float angle, float maxDistance, int levels) {
        super();

        this.body = body;
        this.brain = brain;
        this.offset = offset;
        this.angle = angle;
        this.maxDistance = maxDistance;

        float dl = 1.0f / ((float)levels);
        float l = 0;
        for (int i = 0; i < levels; i++) {
            for (int j = 0; j < 3; j++) {
                brain.addInput(new RetinaSensor(j, l, l + dl));
            }
            l += dl;
        }

    }

    private void seeColor(final float r, final float g, final float b) {
        c.r = r;
        c.g = g;
        c.b = b;
        c.a = (r + g + b) / 3.0f;
        
//        final float mappedR = 2.0f * (c.r - 0.5f);
//        final float mappedG = 2.0f * (c.b - 0.5f);
//        final float mappedB = 2.0f * (c.b - 0.5f);
       // System.out.println(c + " ->  [" + mappedR + "," + mappedG + "," + mappedB + "]");
    }
    
    Vector2 offsetX = new Vector2();
    Fixture intersected;
    float bestDist;

    public void update() {
        clear();

        if (maxDistance<=0)
            return;
        
        p1.set(body.getWorldPoint(offset));
        offsetX.set(offset.x + (float) Math.cos(angle) * maxDistance, offset.y + (float) Math.sin(angle) * maxDistance);
        p2.set(body.getWorldPoint(offsetX));

        intersected = null;

        bestDist = -1;
        body.getWorld().rayCast(this, p1, p2);

        if (intersected == null) {
            pint.set(p1);
        }

    }

    public void clear() {
        c.r = c.g = c.b = 0;
        seeColor(0, 0, 0);
    }

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (fixture.getBody() == body)
            return fraction;
        
        float currDist = point.dst(p1);
        if ((currDist > bestDist) && (bestDist > 0)) {
            return fraction;
        }

        //TODO use exponential or logarithmic scaling???
        float v = (maxDistance - currDist) / maxDistance;

        if (v < 0) {
            v = 0;
        }
        pint.set(point);
        intersected = fixture;

        Object ud = intersected.getUserData();
        if (ud instanceof Material) {
            material = (Material) ud;

           /* c.r = (float) (material.color.r * v);
            c.g = (float) (material.color.g * v);
            c.b = (float) (material.color.b * v);*/
            seeColor((float) (material.fillColor.r * v),(float) (material.fillColor.g * v),(float) (material.fillColor.b * v));
        } else {
            material = null;
           // c.r = c.g = c.b = v;
            seeColor(v/2.0f, v/2.0f, v/2.0f);
        }

        bestDist = currDist;

        return fraction;
    }

    public Color getColor() {
        return c;
    }

    public void setVisionDistance(float v) {
        this.maxDistance = v;
    }

    public void draw() {
        if ((p1.x==pint.x) && (p1.y == pint.y))
            return;
        
        App.setColor(getColor());
        App.setLineWidth(2.0f);
        App.drawLine(p1.x, p1.y, pint.x, pint.y);
    }
}
