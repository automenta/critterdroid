package com.critterdroid.simulation;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.ValueChangedListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.critterdroid.bio.Material;
import com.critterdroid.bio.Material.Text;
import com.critterdroid.bio.Simulation;
import com.critterdroid.entities.Critter;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
public class App implements ApplicationListener, InputProcessor {


    
    /**
     * handles the physics
     */
    public World world;
    boolean paused = false;
    
    private boolean displayUI = true;
    
    //The suggested iteration count for Box2D is 8 for velocity and 3 for position. You can tune this number to your liking, just keep in mind that this has a trade-off between speed and accuracy. Using fewer iterations increases performance but accuracy suffers. Likewise, using more iterations decreases performance but improves the quality of your simulation. For this simple example, we don't need much iteration. Here are our chosen iteration counts.    
    private int velocityIterations = 8;
    private int positionIterations = 3;
    
    float defaultFriction = 0.95f;
    float simDT;
    
    /**
     * indicates what state to go to next if nextState==getID(), do not change states
     */
    int nextState;
    /*
     * Image brickBackground; Sound clack;
     */
    Set<Critter> critters = new HashSet();
    /**
     * our ground box *
     */
    public static Body groundBody;
    /**
     * our mouse joint *
     */
    private MouseJoint mouseJoint = null;
    private Simulation sim;
    
    int numOvalSegments = 7;

    /** brings any angle to within [0, 2*pi] */
    public static float normalizeAngle(float angle) {
        float a = angle;

        while (a < 0)
            a+=Math.PI*2.0;

        return a;                        
    }
    
    public static void run(Simulation s, String title, int width, int height) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = width;
        config.height = height;
        config.title = title;
        config.useGL20 = false;
        config.forceExit = true;
        config.vSyncEnabled = true;
        config.samples = 1;
        
        
        new LwjglApplication(new App(s), config);
        
    }

//    //http://code.google.com/p/libgdx/wiki/ProjectSetup
//    public static void main(String[] args) {
//        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
//        config.width = 1280;
//        config.height = 720;
//        config.title = "Critterdroid";
//        config.useGL20 = false;
//        config.forceExit = true;
//        config.vSyncEnabled = false;
//
//        new LwjglApplication(new App(new SeHSpiderSimulation()), config);
//        //new LwjglApplication(new App(new ArmSimulation()), config);
//    }
    
    float targetZoom, currentAngle=0,targetAngle = 0;
    Vector3 camPos;
    private OrthographicCamera cam;
    //private Texture texture;
    //private Mesh mesh;
    private Rectangle glViewport;
    //private float rotationSpeed;
    private Stage stage;
    private Skin skin;
    private float defaultRestitution = 0.5f;
    

    public App(Simulation s) {
        this.sim = s;
    }

    static final public float getWidth() {
        return Gdx.graphics.getWidth();
    }

    static final public float getHeight() {
        return Gdx.graphics.getHeight();
    }

    @Override
    public void create() {
        simDT = 1.0f / ((float)LwjglApplicationConfiguration.getDesktopDisplayMode().refreshRate);
                
        //clear the world and physics images
        world = new World(new Vector2(0,0), true);

        final int WIDTH = Gdx.graphics.getWidth();
        final int HEIGHT = Gdx.graphics.getHeight();

        cam = new OrthographicCamera(WIDTH, HEIGHT);
        cam.position.set(0, HEIGHT, 0);

        targetZoom = cam.zoom = 0.01f;
        targetAngle = 0;
        camPos = new Vector3(cam.position);

        glViewport = new Rectangle(0, 0, WIDTH, HEIGHT);


//        {
//            PolygonShape groundPoly = new PolygonShape();
//            groundPoly.setAsBox(1, 1);
//
//            BodyDef groundBodyDef = new BodyDef();
//            groundBodyDef.type = BodyType.StaticBody;
//            groundBody = world.createBody(groundBodyDef);
//            // finally we add a fixture to the body using the polygon
//            // defined above. Note that we have to dispose PolygonShapes
//            // and CircleShapes once they are no longer used. This is the
//            // only time you have to care explicitely for memomry managment.
//            FixtureDef fixtureDef = new FixtureDef();
//            fixtureDef.shape = groundPoly;
//            fixtureDef.filter.groupIndex = 0;
//            groundBody.createFixture(fixtureDef);
//            groundPoly.dispose();
//        }

                fontSpriteBatch = new SpriteBatch();
                atlas = new TextureAtlas();
                font = new BitmapFont(Gdx.files.local("assets/ui/default.fnt"), atlas.findRegion("verdana39"), false);
                renderer = new ShapeRenderer();
                renderer.setProjectionMatrix(fontSpriteBatch.getProjectionMatrix());
        
        createUI();

        sim.init(this);

    }

    public void setGroundBody(Body b) {
        groundBody = b;
    }
    
    //http://dpk.net/2011/03/10/libgdx-and-twl/
    //http://stackoverflow.com/questions/6498826/ui-api-for-libgdx
    protected void createUI() {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), Gdx.files.internal("ui/uiskin.png"));

        //TextureRegion image = new TextureRegion(new Texture(Gdx.files.internal("data/badlogicsmall.jpg")));
        //TextureRegion image2 = new TextureRegion(new Texture(Gdx.files.internal("data/badlogic.jpg")));
        stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, this));


        // Group.debug = true;

        final Button button = new TextButton("Single", skin.getStyle(TextButtonStyle.class), "button-sl");
        final Button buttonMulti = new TextButton("Multi\nLine\nToggle", skin.getStyle("toggle", TextButtonStyle.class),
                "button-ml-tgl");
        //final Button imgButton = new Button(new Image(image), skin.getStyle(ButtonStyle.class));
        //final Button imgToggleButton = new Button(new Image(image), skin.getStyle("toggle", ButtonStyle.class));
        final CheckBox checkBox = new CheckBox("Check me", skin.getStyle(CheckBoxStyle.class), "checkbox");
        final Slider slider = new Slider(0, 10, 1, skin.getStyle(SliderStyle.class), "slider");
        final TextField textfield = new TextField("", "Click here!", skin.getStyle(TextFieldStyle.class), "textfield");
        final SelectBox dropdown = new SelectBox(new String[]{"Android", "Windows", "Linux", "OSX"},
                skin.getStyle(SelectBoxStyle.class), "combo");
        //final Image imageActor = new Image(image2);
        //final FlickScrollPane scrollPane = new FlickScrollPane(imageActor, "flickscroll");

        String[] listEntries = {"This is a list entry", "And another one", "The meaning of life", "Is hard to come by",
            "This is a list entry", "And another one", "The meaning of life", "Is hard to come by", "This is a list entry",
            "And another one", "The meaning of life", "Is hard to come by", "This is a list entry", "And another one",
            "The meaning of life", "Is hard to come by", "This is a list entry", "And another one", "The meaning of life",
            "Is hard to come by"};

        final com.badlogic.gdx.scenes.scene2d.ui.List list = new com.badlogic.gdx.scenes.scene2d.ui.List(listEntries, skin.getStyle(ListStyle.class), "list");
        final ScrollPane scrollPane2 = new ScrollPane(list, skin.getStyle(ScrollPaneStyle.class), "scroll");
        //final SplitPane splitPane = new SplitPane(scrollPane, scrollPane2, false, skin.getStyle("default-horizontal",SplitPaneStyle.class), "split");
        final Label fpsLabel = new Label("fps:", skin.getStyle(LabelStyle.class), "label");

        // window.debug();
//        window = new Window("Brain", skin.getStyle(WindowStyle.class), "window");
//        window.x = window.y = 0;
//        window.defaults().spaceBottom(10);
//        window.row().fill().expandX();
        //window.add(button).fill(0f, 0f);
        //window.add(buttonMulti);

        //window.add(new CanvasPanel(200, 200));

        //window.add(imgButton);
        //window.add(imgToggleButton);
        //window.row();
        //window.add(checkBox);
        //window.add(slider).minWidth(100).fillX().colspan(3);
        //window.row();
        //window.add(dropdown);
        //window.add(textfield).minWidth(100).expandX().fillX().colspan(3);
        //window.row();
        //window.add(splitPane).fill().expand().colspan(4).maxHeight(200);
        //window.row();
        //window.pack();

        stage.addActor(fpsLabel);

        // stage.addActor(new Button("Behind Window", skin));
        //stage.addActor(window);

        textfield.setTextFieldListener(new TextFieldListener() {

            public void keyTyped(TextField textField, char key) {
                if (key == '\n') {
                    textField.getOnscreenKeyboard().show(false);
                }
            }
        });

        slider.setValueChangedListener(new ValueChangedListener() {

            public void changed(Slider slider, float value) {
                Gdx.app.log("UITest", "slider: " + value);
            }
        });
    }

    @Override
    public void render() {
        final float delta = Gdx.graphics.getDeltaTime();

        update(delta);

        GL10 gl = Gdx.graphics.getGL10();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl.glDisable(GL10.GL_TEXTURE_2D);



        // Camera --------------------- /
        gl.glViewport((int) glViewport.x, (int) glViewport.y,
                (int) glViewport.width, (int) glViewport.height);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

        updateCam();
        cam.update();
        cam.apply(gl);

//                // Texturing --------------------- /
//                gl.glActiveTexture(GL10.GL_TEXTURE0);
//                gl.glEnable(GL10.GL_TEXTURE_2D);
//                texture.bind();
//                
//                mesh.render(GL10.GL_TRIANGLES);


        //drawBackground(container);

//            //render the physics objects
//            for (PhysicsImage o:physicsImages){
//                    o.render();
//            }


        Graphics g = Gdx.graphics;

        for (Critter c : critters) {
            c.renderUnderlay(g);
        }


        Iterator<Body> ib = world.getBodies();
        while (ib.hasNext()) {
            Body b = ib.next();
            renderBody(g, b);
        }

        for (Critter c : critters) {
            c.renderOverlay(g);
        }

        //show the current time
//        g.setColor(new Color(0, 0, 0, 0.5f));
//        g.fillRect(0, 0, container.getWidth(), 32);
//        g.setColor(Color.white);
//        g.drawString("Time: " + timePassed / 1000f,
//                (container.getWidth()) / 2 - 48,
//                10);

        
        if (displayUI)
            renderUI(delta);

    }

    protected void renderUI(float dt) {
        ((Label) stage.findActor("label")).setText("fps: " + (1.0f / dt));

        stage.act(dt);
        stage.draw();
        //Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.setViewport(width, height, false);
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
              fontSpriteBatch.dispose();
                renderer.dispose();
                font.dispose();
                atlas.dispose();            
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

//public class AndroidGame extends AndroidApplication {
//        public void onCreate (android.os.Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                initialize(new Game(), false);
//        }
//}        
    public void addCritter(Critter c) {
        c.init(this);
        critters.add(c);
    }
//    public void drawBackground(GameContainer container) {
//        //tile the background image to get a full background
//        for (int x = 0; x < container.getWidth(); x += brickBackground.getWidth()) {
//            for (int y = 0; y < container.getHeight(); y += brickBackground.getHeight()) {
//                brickBackground.draw(x, y);
//            }
//        }
//
//    }
    final Color lc = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    final Vector2 ve = new Vector2();

    public static final void setColor(final Color c) {
        Gdx.gl11.glColor4f(c.r, c.g, c.b, c.a);
    }

    public static final void setLineWidth(final float w) {
        Gdx.gl11.glLineWidth(w);        
    }
    static float[] lineVertices = new float[6];
    static Mesh lineMesh = null; 

    public static final void drawLine(final float x1, final float y1, final float x2, final float y2) {
        if (lineMesh == null) {
            lineMesh = new Mesh(true, 4, 6, new VertexAttribute(VertexAttributes.Usage.Position, 3, "attr_Position"));
            lineMesh.setIndices(new short[]{0, 1});
        }
        //lineMesh.setVertices(new float[] { x1, y1, 0, x2,y2, 0                });
        lineVertices[0] = x1;
        lineVertices[1] = getHeight() - y1;
        lineVertices[3] = x2;
        lineVertices[4] = getHeight() - y2;
        lineMesh.setVertices(lineVertices);
        lineMesh.render(GL10.GL_LINES);
    }
    
    final int MAX_SEGMENTS = 16;
    FloatBuffer fbuffer = BufferUtils.createFloatBuffer(MAX_SEGMENTS * 2);
    int psegments;
    //ByteBuffer cbuffer = BufferUtils.createByteBuffer(MAX_SEGMENTS * 4);
    
    void initPolygon(final float sx, final float sy) {
        fbuffer.rewind();
        fbuffer.put(sx);
        fbuffer.put(sy);
        psegments = 1;
    }
    
    void pushPolygonPoint(final float x, final float y) {
        fbuffer.put(x);
        fbuffer.put(y);
        psegments++;
    }

    void drawPolygon(final boolean filled) {
        fbuffer.rewind();
        
        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        //Gdx.gl11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        
        //Gdx.gl11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, cbuffer);    
        Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, fbuffer);

        Gdx.gl11.glDrawArrays((filled) ? GL11.GL_TRIANGLE_FAN : GL10.GL_LINE_LOOP, 0, psegments);
        //glDrawArrays(GL_TRIANGLE_STRIP, 0, 10);
        
        Gdx.gl11.glDisableClientState(GL11.GL_COLOR_ARRAY);

    }

    void drawEllipse(final float x, final float y, final float w, final float h, final int segments, final float angle, final boolean filled) {
        if (segments >= MAX_SEGMENTS) {
            return;
        }

        float t = angle;
        float dt = (float) (Math.PI * 2.0 / ((float) segments));
        for (int count = 0; count <= segments; count++) {
            final float px = x + (float) (Math.cos(t) * w);
            final float py = getHeight() - (y + (float) (Math.sin(t) * h));
            if (count == 0) {
                initPolygon(px, py);
            }
            else {
                pushPolygonPoint(px, py);
            }
            
            t+=dt;
        }
        
        drawPolygon(filled);
    }
    
    public void drawPolygon(final float cx, final float cy, final PolygonShape ps, final float angle, final boolean filled) {
        final float cf = (float) Math.cos(angle);
        final float sf = (float) Math.sin(angle);
                
            final int segments = ps.getVertexCount();
            
            if (segments >= MAX_SEGMENTS) {
                return;
            }
            
            for (int v = 0; v < segments; v++) {
                ps.getVertex(v, ve);

                //x' = x cos f - y sin f
                //y' = y cos f + x sin f


                float x = ve.x * cf - ve.y * sf;
                float y = ve.y * cf + ve.x * sf;

                x += cx;
                y = getHeight() - (y + cy);

                if (v == 0) {
                    initPolygon(x, y);                    
                }
                else {
                    pushPolygonPoint(x, y);
                }                
            }
            
            drawPolygon(filled);                           
            
    }
    

//    public static void drawOval(float ox, float oy, float r, int numOvalSegments, float angle) {
//        float dt = (float) (Math.PI * 2.0 / ((float) numOvalSegments));
//        float t = angle;
//
//        float px = 0, py = 0;
//
//        for (int i = 0; i <= numOvalSegments; i++) {
//            float x = ox + ((float) Math.cos(t)) * r;
//            float y = oy + ((float) Math.sin(t)) * r;
//            if (i > 0) {
//                drawLine(x, y, px, py);
//            }
//            px = x;
//            py = y;
//            t += dt;
//        }
//    }
    
    public final static Color gray = new Color(0.5f, 0.5f, 0.5f, 0.5f);

        private SpriteBatch fontSpriteBatch;
        private TextureAtlas atlas;
        private BitmapFont font;
        private ShapeRenderer renderer;
    
    private void renderBody(final Graphics g, final Body body/*
             * , float halfWidth, float halfHeight
             */) {
        final Vector2 center = body.getWorldCenter();
        final float angle = body.getAngle();

        for (final Fixture f : body.getFixtureList()) {
            final Object ud = f.getUserData();
            
            Color fillColor = null;
            Color strokeColor = null;
            int strokeWidth = 0;
            Material m = null;
            
            if (ud instanceof Material) {
                m = (Material) ud;
                fillColor = m.fillColor;
                strokeColor = m.strokeColor;
                strokeWidth = m.strokeWidth;
            } else {
                fillColor = gray;
            }

            float radius = 0;
            
            final Shape s = f.getShape();
            if (s instanceof CircleShape) {
                CircleShape cs = (CircleShape) s;
                
                if (fillColor!=null) {
                    setColor(fillColor);
                    drawEllipse(center.x, center.y, cs.getRadius(), cs.getRadius(), numOvalSegments, angle, true);
                }
                if ((strokeColor!=null) && (strokeWidth > 0)) {
                    setColor(strokeColor);
                    setLineWidth(strokeWidth);
                    drawEllipse(center.x, center.y, cs.getRadius(), cs.getRadius(), numOvalSegments, angle, false);
                }
                
                radius = cs.getRadius();
                
                
            } else if (s instanceof PolygonShape) {
                PolygonShape ps = (PolygonShape) s;
                
                if (fillColor!=null) {
                    setColor(fillColor);
                    drawPolygon(center.x, center.y, ps, angle, true);
                }
                if ((strokeColor!=null) && (strokeWidth > 0)) {
                    setColor(strokeColor);
                    setLineWidth(strokeWidth);
                    drawPolygon(center.x, center.y, ps, angle, false);  
                    
                }
                
                radius = getRadius(ps);
                
            } else {
                //System.out.println("unrendered: " + s);
            }
            if (m!=null)
                if (m.texts!=null)
                    drawText(center.x, center.y, radius, angle, m.texts);
        }
    }
 
    public float getRadius(Shape s) {
        if (s instanceof CircleShape) {
            return ((CircleShape)s).getRadius();
        }
        else if (s instanceof PolygonShape) {
            return getRadius((PolygonShape)s);
        }
        System.err.println("Unknown radius calcuation: " + s.getClass());
        return 0;
    }
    
    public float getRadius(PolygonShape ps) {
        float minX=0, maxX=0;
        float minY=0, maxY=0;
        Vector2 v = new Vector2();
        for (int i = 0; i < ps.getVertexCount(); i++) {
            ps.getVertex(i, v);
            if (i == 0) {
                minX = maxX = v.x;
                minY = maxY = v.y;
            }
            else {
                if (v.x < minX) minX = v.x;
                if (v.y < minY) minX = v.y;
                if (v.x > minX) maxX = v.x;
                if (v.y > minY) maxX = v.y;
            }
                        
        }
        return Math.max(Math.abs(minX-maxX), Math.abs(minY-maxY));
    }
    
    Matrix4 m = new Matrix4();                
 
    public void drawText(float cx, float cy, float shapeRadius, float angle, List<Text> texts) {
            Gdx.gl10.glPushMatrix();
                final int viewHeight = Gdx.graphics.getHeight();
                
        for (Text t : texts) {
            
             // red.a = (red.a + Gdx.graphics.getDeltaTime() * 0.1f) % 1;


                
                //Gdx.gl.glClearColor(1, 1, 1, 1);
                //Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                fontSpriteBatch.begin();
                fontSpriteBatch.setProjectionMatrix(cam.projection);
                

                
                int alignmentWidth = 280;
                
                        TextBounds bounds = font.getWrappedBounds(t.text, alignmentWidth);
                        float x = (bounds.width / 2) * t.scale.x - t.position.x / t.scale.x;
                        float y = (bounds.height / 2) * t.scale.y - t.position.y / t.scale.y;
                
                m.setToRotation(0, 0, 1, (float)((currentAngle)*180.0/Math.PI) );
                m.translate(cx-cam.position.x, -cy+(viewHeight - cam.position.y), 0);
                m.rotate(0, 0, 1, (float)((-angle)*180.0/Math.PI)  );
                
                m.scale(t.scale.x/bounds.width*shapeRadius,t.scale.y/bounds.height*shapeRadius, 1f);
                fontSpriteBatch.setTransformMatrix(m);

                font.setColor(Color.WHITE);

//                if (false) {
//                        alignmentWidth = 0;
//                        font.drawMultiLine(spriteBatch, text, x, viewHeight - y, alignmentWidth, HAlignment.RIGHT);
//                }
//
//                if (false) {
//                        TextBounds bounds = font.getMultiLineBounds(text);
//                        alignmentWidth = bounds.width;
//                        font.drawMultiLine(spriteBatch, text, x, viewHeight - y, alignmentWidth, HAlignment.RIGHT);
//                }

                if (true) {
                // font.drawMultiLine(spriteBatch, text, x, viewHeight - y, alignmentWidth, HAlignment.RIGHT);
                        font.drawWrapped(fontSpriteBatch, t.text, -x, y, alignmentWidth, BitmapFont.HAlignment.LEFT);
                        
                }

                
                fontSpriteBatch.end();
        }
            Gdx.gl10.glPopMatrix();
        
//                renderer.begin(ShapeType.Rectangle);
//                renderer.rect(x, viewHeight - y, x + alignmentWidth, 300);
//                renderer.end();
        
    }
    
    private Body initBody(BodyDef b, Shape shape, float x, float y, float density, float friction, float restitution, Color c) {
        return initBody(b, shape, x, y, density, friction, restitution, new Material(c));
    }
    
    private Body initBody(BodyDef b, Shape shape, float x, float y, float density, float friction, float restitution, Material m) {
        //call physicsWorld.createBody to actually make the body
        Body body = world.createBody(b);

        Fixture f = body.createFixture(shape, density);
        f.setFriction(friction);
        f.setRestitution(restitution);
        f.setUserData(m);

        //put the body and image together
        body.setTransform(x, y, 0);
        //ballBody.setLinearVelocity(new Vector2(1, 0));//don't let them perfectly stack
        //ballBody.getFixtureList().get(0).setRestitution(1);//keep all energy


        //a circle shape is moved from it's center, so for the image to follow the
        //shape right, it must be moved to the upper left corner
        //ball.setOffset(-ballImage.getWidth()/2, -ballImage.getWidth()/2);
        return body;
    }
    public Body newRectangle(float w, float h, final float x, final float y, float angle, float density, float friction, float restitution, Material m) {
        PolygonShape s = new PolygonShape();
        s.setAsBox(w / 2.0f, h / 2.0f, new Vector2(0, 0), angle);
        
        //make a bodyDef, and make it dynamic so it actually moves
        BodyDef b = new BodyDef();
        b.type = BodyType.DynamicBody;

        return initBody(b, s, x, y, density, defaultFriction, restitution, m);        
    }

    public Body newRectangle(float w, float h, final float x, final float y, float angle, float density, Material m) {
        return newRectangle(w, h, x, y, angle, density, defaultFriction,defaultRestitution,  m);
    }

    public Body newCircle(float radius, float x, float y, float density, float friction, float restitution, Material m) {
        //create a shape for the ball
        CircleShape circle = new CircleShape();
        circle.setPosition(new Vector2(0, 0));
        circle.setRadius(radius);

        //make a bodyDef, and make it dynamic so it actually moves
        BodyDef ballDef = new BodyDef();
        ballDef.type = BodyType.DynamicBody;        

        return initBody(ballDef, circle, x, y, density, friction, restitution, m);        
    }
    
    public Body newCircle(float radius, float x, float y, float density, Material m) {
        return newCircle(radius, x, y, density, defaultFriction, defaultRestitution, m);
    }

    public RevoluteJoint joinRevolute(Body a, Body b, float x, float y) {
        RevoluteJointDef jd = new RevoluteJointDef();
        jd.initialize(a, b, new Vector2(x, y));
        jd.collideConnected = true;
        jd.enableMotor = true;
        jd.enableLimit = true;
        jd.lowerAngle = -0.001f;
        jd.upperAngle = 0.001f;
        
        RevoluteJoint j = (RevoluteJoint) world.createJoint(jd);
        return j;
    }

    //http://www.box2d.org/manual.html#_Toc258082973
    public DistanceJoint joinDistance(Body a, Body b, Vector2 anchorA, Vector2 anchorB, float length) {
        DistanceJointDef jd = new DistanceJointDef();
        jd.initialize(a, b, anchorA, anchorB);
        jd.collideConnected = true;
        jd.length = length;        
        jd.frequencyHz = 1.0f;
        jd.dampingRatio = 0.0f;        

        DistanceJoint j = (DistanceJoint) world.createJoint(jd);
        
        return j;
    }
    public PrismaticJoint joinPrismatic(Body a, Body b, Vector2 anchor, Vector2 direction) {
        PrismaticJointDef jd = new PrismaticJointDef();
        jd.initialize(a, b, anchor, direction);
        jd.collideConnected = true;
        jd.lowerTranslation = -2;
        jd.upperTranslation = 2;
        jd.enableLimit = true;
                

        PrismaticJoint j = (PrismaticJoint) world.createJoint(jd);
        
        return j;
    }

    public WeldJoint joinWeld(Body a, Body b, Vector2 anchor) {
        WeldJointDef jd = new WeldJointDef();
        jd.initialize(a, b, anchor);


        WeldJoint j = (WeldJoint) world.createJoint(jd);
        return j;
    }

    public void update(float realDT) {

        if (!paused) {

            for (Critter c : critters) {
                c._update(simDT);
            }

            //run the world simulation
            world.step(simDT, velocityIterations, positionIterations);

        }

    }

    public void getScreenToWorld(int px, int py, Vector2 testPoint) {
        Ray r = cam.getPickRay(px, py);
        Vector3 v = r.getEndPoint(1.0f);
        testPoint.set(v.x, getHeight() - v.y);
    }
    
    /**
     * another temporary vector *
     */
    Vector2 target = new Vector2();
    /**
     * we instantiate this vector and the callback here so we don't irritate the
     * GC *
     */
    Vector2 testPoint = new Vector2();
    private Body hitBody;
    boolean mouseButtonPressed[] = new boolean[4];
    QueryCallback callback = new QueryCallback() {

        @Override
        public boolean reportFixture(Fixture fixture) {
            // if the hit fixture's body is the ground body
            // we ignore it
            if (fixture.getBody() == groundBody) {
                return true;
            }

            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(testPoint)) {
                hitBody = fixture.getBody();
                return false;
            } else {
                return true;
            }
        }
    };

    @Override
    public boolean keyDown(int keycode) {
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public void updateHitBody() {
        // translate the mouse coordinates to world coordinates

        // ask the world which bodies are within the given
        // bounding box around the mouse pointer
        hitBody = null;
        world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);
    }
    
    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        mouseButtonPressed[button] = true;

        getScreenToWorld(x, y, testPoint);

        if (posAtDragStart == null) {
            posAtDragStart = new Vector3(cam.position);
            pointerAtDragStart = new Vector2(testPoint);  //TODO is this necessary or can we just use testPoint?
        }

        if (button == 0) {
             updateHitBody();
             if (hitBody!=null) {
                 System.out.println("hit: " + hitBody + " with " + hitBody.getUserData());
                 if (hitBody.getFixtureList().get(0).getUserData()!=null) {
                     Material m = (Material)hitBody.getFixtureList().get(0).getUserData();
                     m.onTouchDown(0, testPoint);
                 }
             }
            
            // if we hit something we create a new mouse joint
            // and attach it to the hit body.
            if (groundBody == null) {
                System.err.println(this + " has undefined groundBody.  Unable to create mouseJoint");
            }
            else if (hitBody != null)  {
                
                MouseJointDef def = new MouseJointDef();
                def.bodyA = groundBody;
                def.bodyB = hitBody;
                def.collideConnected = true;
                def.target.set(testPoint);
                def.maxForce = 15000.0f * hitBody.getMass();


                mouseJoint = (MouseJoint) world.createJoint(def);
                hitBody.setAwake(true);
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {

        mouseButtonPressed[button] = false;
        posAtDragStart = null;

        if (button == 0) {
            // if a mouse joint exists we simply destroy it       
            if (mouseJoint != null) {
                world.destroyJoint(mouseJoint);
                mouseJoint = null;
            }
        } else if (button == 1) {
            getScreenToWorld(x, y, testPoint);
            updateHitBody();
            if (hitBody!=null) {
                camPos.set(hitBody.getPosition().x, getHeight() - hitBody.getPosition().y, 0);
                float rad = getRadius(hitBody.getFixtureList().get(0).getShape());
                targetZoom = (rad/300.0f);
                targetAngle = hitBody.getAngle();
            }
            else {
                camPos.set(testPoint.x, getHeight() - testPoint.y, 0);
            }
        }
        return false;
    }
    Vector2 pointerAtDragStart;
    Vector3 posAtDragStart;

    @Override
    public boolean touchDragged(int newx, int newy, int pointer) {
        if (mouseButtonPressed[0]) {
            // if a mouse joint exists we simply update
            // the target of the joint based on the new
            // mouse coordinates
            if (mouseJoint != null) {
                getScreenToWorld(newx, newy, target);
                mouseJoint.setTarget(target);
            }
        } else if (mouseButtonPressed[1]) {
            //System.out.println("dragging: " + newx);
            //getScreenToWorld(newx, newy, target);
            //cam.position.set(posAtDragStart.x + pointerAtDragStart.x - target.x, posAtDragStart.y + pointerAtDragStart.y - target.y, 0);
            //cam.position.sub((pointerAtDragStart.x - target.x)*0.1f, (pointerAtDragStart.y - target.y)*0.1f, 0);
        }
        return false;
    }

    @Override
    public boolean touchMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if (mouseButtonPressed[1]) {
//            float r = 5.0f * amount;
//            camRotate += r;
//            cam.rotate(r, 0, 0, 1f);
        } else {
            targetZoom *= 1.0f + ((float) amount) / 10.0f;
        }
        return false;
    }

    protected void updateCam() {
        float momentum = 0.1f;
        cam.position.lerp(camPos, momentum);
        cam.zoom = cam.zoom * (1.0f - momentum) + (momentum) * targetZoom;
        
        
        if ((currentAngle < 0) && (targetAngle > 0)) {
            currentAngle += (float)(Math.PI*2.0f);
        }
        else if ((currentAngle > 0) && (targetAngle < 0)) {
            targetAngle += (float)(Math.PI*2.0f);            
        }
//        if (currentAngle - targetAngle > ((float)Math.PI*2.0f)) {
//            currentAngle -= Math.PI*2.0f;
//        }
//        else if (targetAngle - currentAngle > ((float)Math.PI*2.0f)) {
//            targetAngle -= Math.PI*2.0f;
//        }
        currentAngle = (1.0f - momentum) * currentAngle + (momentum) * targetAngle;
        
        cam.up.set((float)Math.sin(currentAngle), (float)Math.cos(currentAngle), 0);
//		cam.tmpMat.setToRotation(tmpVec.set(0, 0, 1), angle);
//		cam.direction.mul(tmpMat).nor();
//		cam.up.mul(tmpMat).nor();
    }

    public boolean needsGL20() {
        return false;
    }

    public Skin getSkin() {
        return skin;
    }

    public Stage getStage() {
        return stage;
    }

    public void addWindow(Window w) {
        getStage().addActor(w);
    }

}