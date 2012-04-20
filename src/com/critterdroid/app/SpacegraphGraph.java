/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.critterdroid.app;

import automenta.netention.Self;
import automenta.netention.app.RunSelfBrowser;
import automenta.netention.index.SchemaIndex;
import automenta.netention.index.SchemaIndex.SchemaResult;
import automenta.netention.rdf.AddOWLPatterns;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.critterdroid.bio.Material;
import com.critterdroid.bio.Material.Text;
import com.critterdroid.bio.Simulation;
import com.critterdroid.entities.Critter;
import com.critterdroid.simulation.App;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author seh
 */
public class SpacegraphGraph implements Simulation {
    private App sim;

    public class GraphCritter<N,E> extends Critter {
        private final DirectedSparseGraph<N, E> graph;
        
        private Map<N, Object> nodeComponents = new HashMap();
        private Map<E, Object> edgeComponents = new HashMap();

        public GraphCritter(DirectedSparseGraph<N,E> g) {
            this.graph = g;
            
            update();
        }

        public void update() {
            //remove existing
            
            for (N n : graph.getVertices()) {
                addNode(n);
            }
            for (E e : graph.getEdges()) {
                addEdge(e, graph.getSource(e), graph.getDest(e));
            }
        }
        
        
        @Override
        public void init(App s) {
        }

        @Override
        protected void update(double dt) {
        }

        @Override
        public void renderUnderlay(Graphics g) {
            for (E e : edgeComponents.keySet()) {
                N a = graph.getSource(e);
                N b = graph.getDest(e);
                
                Body ba = (Body)nodeComponents.get(a);
                Body bb = (Body)nodeComponents.get(b);
                
                sim.drawLine(ba.getWorldCenter().x, ba.getWorldCenter().y, bb.getWorldCenter().x, bb.getWorldCenter().y);
            }
        }

        @Override
        public void renderOverlay(Graphics g) {
        }
        
        protected N addNode(N n) {
            nodeComponents.put(n, getNodeComponent(n));
            return n;            
        }
        
        public Object getNodeComponent(N n) {
            Material m = new Material(Color.GRAY.mul(0.95f), new Color(1.0f, 1.0f, 1.0f, 0.5f), 0);
            m.addText(new Text(n.toString(), -6, 1, 1.0f, 1.0f));
            
            Body b = sim.newRectangle(0.8f, 0.4f, 3f, -2f*0.3f, 0, 7.0f, m);            
            b.setAngularDamping(0.8f);
            b.setLinearDamping(0.9f);
            return b;
            
        }
        
        public Object getEdgeComponent(E e, N from, N to) {
            Body bfrom = (Body)nodeComponents.get(from);
            Body bto = (Body)nodeComponents.get(to);
            return sim.joinDistance(bfrom, bto, new Vector2(bfrom.getWorldCenter()), new Vector2(bto.getWorldCenter()), 1.5f);
            //return sim.joinPrismatic(bfrom, bto, new Vector2(bfrom.getWorldCenter()), new Vector2(0, 1));
        }
        
        protected void removeNode(N n) {
            
        }
        
        protected E addEdge(E e, N from, N to) {
            edgeComponents.put(e, getEdgeComponent(e, from, to));
            return e;
        }
        
        protected void removeEdge(E e) {
            
        }
    }
    
    public static class ConceptNode {
        public final String text;
        public final float strength;

        public ConceptNode(String text, float strength) {
            this.text = text;
            this.strength = strength;
        }
                
    }
    
    @Override
    public void init(App app) {

        this.sim = app;
        
        World world = app.world;
        
        world.setGravity(new Vector2(0, 0));
        

        Spacegraph.addWorldBox(app, world, 16f, 12f, 0.1f);

        DirectedSparseGraph<ConceptNode,String> g = new DirectedSparseGraph();
        
        final Self self = RunSelfBrowser.newDefaultSelf();
        {
            AddOWLPatterns.add("../netention/schema/sumodlfull.owl", self);

            self.getPattern("http://stuarthendren.net/resource/sumodlfull.owl#Artifact").addParent("Built");
        }
        
        SchemaIndex si = new SchemaIndex(self);
        
        String query = "built life event";
        
        ConceptNode a = new ConceptNode(query, 2.0f);
        g.addVertex(a);
        
        int i = 0;
        for (SchemaResult sr : si.getSuggestions(query)) {
            ConceptNode b = new ConceptNode(sr.toString(self), (float)sr.score/10.0f);
            g.addVertex(b);
            g.addEdge("e" + i, a, b);
            i++;
        }
        
//        g.addVertex("a");
//        g.addVertex("b");
//        g.addVertex("c");
//        g.addVertex("d");
//        g.addEdge("ab", "a", "b");
//        g.addEdge("bc", "b", "c");
//        g.addEdge("ad", "a", "d");
        
        GraphCritter<ConceptNode,String> gc = new GraphCritter<ConceptNode, String>(g) {

            @Override
            public Object getNodeComponent(ConceptNode n) {
                Material m = new Material(new Color(0.5f + (n.strength/2.0f), 0.5f, 0.5f, 0.75f), new Color(1.0f, 1.0f, 1.0f, 0.5f), 0);
                m.addText(new Text(n.text, -1, 1, 0.8f, 0.8f));

                Body b = sim.newRectangle(0.8f * n.strength, 0.4f * n.strength, 3f, -2f*0.3f, 0, 7.0f, m);            
                b.setAngularDamping(0.8f);
                b.setLinearDamping(0.9f);
                return b;
            }
            
        };
        
        app.addCritter(gc);
        
//        for (int i = 0; i < 4; i++) {
//            Material m = new Material(Color.GRAY.mul(0.95f), new Color(1.0f, 1.0f, 1.0f, 0.5f), 5);
//            m.addText(new Text("Text", -6, 1, 1.0f, 1.0f));
//            
//            sim.newRectangle(0.8f+i, 0.4f+i, 3f, -2f+i*0.3f, 0, 1.0f, m);            
//        }

        
    }
    
    public void addCircleRock(App sim, float x, float y, float r, Material m) {
        sim.newCircle(r, x, y, 1.0f, m);
    }
    
    
    public static void main(String[] args) {
        App.run(new SpacegraphGraph(), "Spacegraph", 1280, 720);
    }
}
