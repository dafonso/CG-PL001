package OpenGL;

import Logic.Section;
import Logic.Simulator;
import Logic.Simulator.ViewportSize;
import Logic.Source;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.media.opengl.GL;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2ES1.GL_LIGHT_MODEL_AMBIENT;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_COLOR_MATERIAL;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_EMISSION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SHININESS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import javax.media.opengl.glu.GLU;

/**
 * An OpenGL and key listener that has no action associated to key events.
 *
 * @author Pedro Mariano
 */
public class StaticListener
        implements
        GLEventListener,
        KeyListener {

    /**
     * Use a perspective or a parallel projection.
     */
    protected boolean perspectiveProjection = true;
    protected float left = -4;
    protected float right = 14;
    protected float top = 12;
    protected float bottom = -1f;
    protected float far = 500;
    protected float near = 1;
    /**
     * Camera coordinates.
     */
    protected float[] eye = new float[]{0, 12, -14};
    /**
     * Coordinates of where the camera is pointing.
     */
    protected float[] center = new float[]{0, 11.5f, -12};
    /**
     * Up vector used when setting the camera properties.
     */
    protected float[] up = new float[]{0, 1, 0};
    /**
     * The OpenGL AWT component that this listener is attached to.
     */
    protected final GLCanvas canvas;

    protected volatile Simulator simulator;
    private TextDisplayer textDisplay;

    public static HashMap<String, AppTexture> textureDic;

    StaticListener(GLCanvas canvas, Simulator simulator) {
        this.canvas = canvas;
        this.simulator = simulator;
        if (textureDic == null) {
            textureDic = new HashMap<>();
        }
        this.status();
    }

    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();      // get the OpenGL graphics context
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
        gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting   
        gl.glEnable(GL_DEPTH_TEST); // enables depth testing
        gl.glEnable(GL_LIGHTING);
        float[] floatArray = {1.0f, 1.0f, 1.0f, 1};
        gl.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, floatArray, 0);
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL.GL_FRONT, GL_DIFFUSE);
        gl.glEnable(GL_LIGHT0);
        // Load all textures
        loadTextures(gl);

        textDisplay = new TextDisplayer();

        System.out.println("GLEventListener.init(GLAutoDrawable)");
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        System.out.println("GLEventListener.dispose(GLAutoDrawable)");
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers

        //int currentInstant = simulator.getCurrentInstant();
        float hourDayFactorCos = (float) Math.abs(Math.cos(Math.toRadians((double) simulator.getCurrentInstant())));

        float hourDayFactorSin = (float) Math.abs(Math.sin(Math.toRadians((double) simulator.getCurrentInstant())));

        float red = 0.53f * (hourDayFactorCos + 0.1f);
        float green = 0.81f * (hourDayFactorCos + 0.1f);
        float blue = 0.93f * (hourDayFactorCos + 0.1f);

        float[] lightPosition = {0.0f, 1.0f * hourDayFactorCos, 1.0f * hourDayFactorSin, 0.0f};
        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPosition, 0);

        gl.glClearColor(red, green, blue, 1.0f);
        gl.glLoadIdentity();
        GLU glu = GLU.createGLU(gl);
        glu.gluLookAt(
                this.eye[0], this.eye[1], this.eye[2],
                this.center[0], this.center[1], this.center[2],
                this.up[0], this.up[1], this.up[2]
        );

        float[] rgba = {1.0f, 1.0f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.5f);
        // create grass plane
        AppTexture grass = StaticListener.textureDic.get("grass");
        if (grass != null && grass.isSuccess()) {
            grass.getTexture().enable(gl);
            grass.getTexture().bind(gl);
        }
        float maxSize = 10000;
        float repeatRacio = maxSize / 10.0f;
        gl.glBegin(GL_QUADS); // of the color cube

        gl.glTexCoord2f(repeatRacio, 0);
        gl.glVertex3f(-maxSize, -0.3f, maxSize);
        gl.glTexCoord2f(repeatRacio, repeatRacio);
        gl.glVertex3f(maxSize, -0.3f, maxSize);
        gl.glTexCoord2f(0, repeatRacio);
        gl.glVertex3f(maxSize, -0.3f, -maxSize);
        gl.glTexCoord2f(0, 0);
        gl.glVertex3f(-maxSize, -0.3f, -maxSize);

        gl.glEnd();

        if (grass != null && grass.isSuccess()) {
            grass.getTexture().disable(gl);
        }
        resetMaterial(gl);
        simulator.render(gl);
        updateText();
        textDisplay.render(drawable.getWidth(), drawable.getHeight());
    }

    private void updateText() {
        textDisplay.clearUlString();
        textDisplay.addLineUlString(simulator.isIsEditorMode() ? "Editor" : "Simulator");
        textDisplay.addLineUlString(simulator.isAnimationRunning() ? "Running" : "Stopped");
        textDisplay.addLineUlString("T = " + simulator.getCurrentInstant());
        textDisplay.addLineUlString("Road size = " + (simulator.getRoad().size() - 2));
        textDisplay.clearDlString();
        if (!simulator.isIsEditorMode()) {
            textDisplay.addLineDlString(this.perspectiveProjection ? "Perspective" : "Parallel");
            textDisplay.addLineDlString(String.format("Left Right: %5.1f .. %5.1f", this.left, this.right));
            textDisplay.addLineDlString(String.format("Top Bottom: %5.1f .. %5.1f", this.top, this.bottom));
            textDisplay.addLineDlString(String.format("  Near Far: %5.1f .. %5.1f", this.near, this.far));
            textDisplay.addLineDlString(String.format("   Eye:  ( %5.1f , %5.1f , %5.1f )", this.eye[0], this.eye[1], this.eye[2]));
            textDisplay.addLineDlString(String.format("Center:  ( %5.1f , %5.1f , %5.1f )", this.center[0], this.center[1], this.center[2]));
            textDisplay.addLineDlString(String.format("    Up:  ( %5.1f , %5.1f , %5.1f )", this.up[0], this.up[1], this.up[2]));
            textDisplay.addLineDlString(String.format("View angle:  ( %5.1f �)", Math.toDegrees(getViewAngle())));
        }
        textDisplay.clearUrString();
        if (simulator.hadSelection()) {
            Section section = simulator.getSelectedSection();
            textDisplay.addLineUrString(String.format("Angle:  ( %5.1f �)", section.getAngle()));
            Source source = section.getSource();
            textDisplay.addLineUrString(source != null ? "Period : " + source.getPeriod() : "No source");
        } else {
            textDisplay.addLineUrString("No selection!");
        }
    }

    public float getViewAngle() {
        float x0 = center[0] - eye[0];
        return (float) Math.acos(x0 / distanceEyeToCenter());
    }

    public float distanceEyeToCenter() {
        float x0 = center[0] - eye[0];
        float z0 = center[2] - eye[2];
        return (float) Math.sqrt(Math.pow(z0, 2) + Math.pow(x0, 2));
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (this.simulator.isIsEditorMode()) {
            ViewportSize vp = simulator.getViewport();
            up = new float[]{1, 0, 0};
            eye = new float[]{0, 200, 0};
            center = new float[]{0, 150, 0};
            gl.glOrtho(
                    vp.getzMin(), vp.getzMax(),
                    vp.getxMin(), vp.getxMax(),
                    1, 1000
            );
        } else {
            up = new float[]{0, 1, 0};
            eye = new float[]{0, 12, -14};
            center = new float[]{0, 11.5f, -12};
            GLU glu = GLU.createGLU(gl);
            glu.gluPerspective(60, width / height, near, far);
        }
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }

    /**
     * Print the properties of the projection parameters and of the camera.
     */
    protected void status() {
        System.out.println("\n\n");
        System.out.println(this.perspectiveProjection ? "Perspective" : "Parallel");
        System.out.format("Left Right: %5.1f .. %5.1f\n", this.left, this.right);
        System.out.format("Top Bottom: %5.1f .. %5.1f\n", this.top, this.bottom);
        System.out.format("  Near Far: %5.1f .. %5.1f\n", this.near, this.far);
        System.out.format("   Eye:  ( %5.1f , %5.1f , %5.1f )\n", this.eye[0], this.eye[1], this.eye[2]);
        System.out.format("Center:  ( %5.1f , %5.1f , %5.1f )\n", this.center[0], this.center[1], this.center[2]);
        System.out.format("    Up:  ( %5.1f , %5.1f , %5.1f )\n", this.up[0], this.up[1], this.up[2]);
    }

    private void loadTextures(GL2 gl) {
        if (!textureDic.containsKey("house_back")) {
            textureDic.put("house_back", new AppTexture("resources/house_back_wall.jpg", gl));
        }
        if (!textureDic.containsKey("house_front")) {
            textureDic.put("house_front", new AppTexture("resources/house_front_wall.jpg", gl));
        }
        if (!textureDic.containsKey("house_side")) {
            textureDic.put("house_side", new AppTexture("resources/house_side_wall.jpg", gl));
        }
        if (!textureDic.containsKey("house_roof")) {
            textureDic.put("house_roof", new AppTexture("resources/house_roof.jpg", gl));
        }
        if (!textureDic.containsKey("grass")) {
            AppTexture newTexture = new AppTexture("resources/grass.jpg", gl);
            newTexture.getTexture().setTexParameterf(gl, GL.GL_TEXTURE_WRAP_T, GL_REPEAT);
            newTexture.getTexture().setTexParameterf(gl, GL.GL_TEXTURE_WRAP_S, GL_REPEAT);
            newTexture.getTexture().setTexParameterf(gl, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            textureDic.put("grass", newTexture);
        }
        if (!textureDic.containsKey("road")) {
            AppTexture newTexture = new AppTexture("resources/road.jpg", gl);
            textureDic.put("road", newTexture);
        }

    }

    public static void resetMaterial(GL2 gl) {
        //GL_AMBIENT 	 	
        //The default ambient reflectance for both front- and back-facing materials is (0.2, 0.2, 0.2, 1.0).	
        float[] ambient = {0.2f, 0.2f, 0.2f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL_AMBIENT, ambient, 0);
        //GL_DIFFUSE 	 	
        //The default diffuse reflectance for both front- and back-facing materials is (0.8, 0.8, 0.8, 1.0).	
        float[] diffuse = {0.8f, 0.8f, 0.8f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL_DIFFUSE, diffuse, 0);
        //GL_SPECULAR 	 	
        //The default specular reflectance for both front- and back-facing materials is (0.0, 0.0, 0.0, 1.0).	
        float[] specular = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL_SPECULAR, specular, 0);
        //GL_EMISSION 	 	
        //The default emission intensity for both front- and back-facing materials is (0.0, 0.0, 0.0, 1.0).	
        float[] emission = {0.0f, 0.0f, 0.0f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL_EMISSION, emission, 0);
        //GL_SHININESS 	 	
        //The default specular exponent for both front- and back-facing materials is 0.
        gl.glMaterialf(GL.GL_FRONT, GL_SHININESS, 0.0f);
    }

}
