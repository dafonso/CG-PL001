package OpenGL;

import Logic.PersistenceManager;
import Logic.Section;
import Logic.Simulator;
import Logic.Source;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.awt.GLCanvas;

/**
 * An example of a listener that reacts to some key events.
 *
 * @author Pedro Mariano
 */
public class AppListener
        extends StaticListener {

    public AppListener(GLCanvas canvas, Simulator simulator) {
        super(canvas, simulator);
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        if (this.simulator.isIsEditorMode()) {
            keyTypedInEdition(ke.getKeyChar());
        } else {
            keyTypedInSimulation(ke.getKeyChar());
        }
        this.canvas.display();
    }

    private void keyTypedInEdition(char chars) {
        switch (chars) {
            case 'm': //adiciona segmento antes
                if (this.simulator.hadSelection()) {
                    ArrayList<Section> road = this.simulator.getRoad();
                    ArrayList<Section> newRoad = new ArrayList();
                    int index = road.indexOf(this.simulator.getSelectedSection());
                    int i = 0;
                    float z = 10;
                    for (Section s : road) {
                        if (i == index) {
                            Section newSection = s;
                            newSection.setOriginZ(z * i);
                            newRoad.add(i, s);
                            i++;
                        }
                        s.setOriginZ(z * i);
                        newRoad.add(i, s);
                        i++;
                    }
                    this.simulator.setRoad(newRoad);
                }
                System.out.println("m pressionada!");
                break;
            case 'n': //adiciona segmento depois
                if (this.simulator.hadSelection()) {
                    ArrayList<Section> road = this.simulator.getRoad();
                    ArrayList<Section> newRoad = new ArrayList();
                    int index = road.indexOf(this.simulator.getSelectedSection());
                    int i = 0;
                    float z = 10;
                    for (Section s : road) {
                        s.setOriginZ(z * i);
                        newRoad.add(i, s);
                        if (i == index) {
                            Section newSection = s;
                            newSection.setOriginZ(z * (i + 1));
                            newRoad.add(i + 1, s);
                            i++;
                        }
                        i++;
                    }
                    this.simulator.setRoad(newRoad);
                }
                System.out.println("n pressionada!");
                break;
            case 'b': //remove segmento
                if (this.simulator.hadSelection()) {
                    ArrayList<Section> road = this.simulator.getRoad();
                    int index = road.indexOf(this.simulator.getSelectedSection());
                    road.remove(index);
                    this.simulator.setRoad(road);
                    this.simulator.removeSelection();
                }
                break;
            case 'k': //insere fonte com periodo por default = 1
                if (this.simulator.hadSelection()) {
                    ArrayList<Section> road = this.simulator.getRoad();
                    int index = road.indexOf(this.simulator.getSelectedSection());
                    Section s = this.simulator.getSelectedSection();
                    if (!s.hasSource()) {
                        Source source = PersistenceManager.createSource(index, 1, s);
                        s.setSource(source);
                        road.add(index, s);
                        this.simulator.setRoad(road);
                    }
                }
                break;
                case 'j': //remove fonte
                if (this.simulator.hadSelection()) {
                    ArrayList<Section> road = this.simulator.getRoad();
                    int index = road.indexOf(this.simulator.getSelectedSection());
                    Section s = this.simulator.getSelectedSection();
                    if (!s.hasSource()) {
                        Source source = PersistenceManager.createSource(index, 1, s);
                        s.setSource(source);
                        road.add(index, s);
                        this.simulator.setRoad(road);
                    }
                }
                break;
                
                
                
                
            case '�'://grava um ficheiro com a configura��o actual
                PersistenceManager.saveSimulator();
                System.out.println("Edi��o actual gravada no ficheiro de texto");
                break;
            case ' ':
                this.simulator.setIsEditorMode(false);
                break;
        }
    }

    private void keyTypedInSimulation(char chars) {

        float viewAngle = getViewAngle();
        switch (chars) {
            case 'a':
                this.eye[0] += 0.1f * Math.sin(viewAngle);
                this.center[0] += 0.1f * Math.sin(viewAngle);
                this.eye[2] -= 0.1f * Math.cos(viewAngle);
                this.center[2] -= 0.1f * Math.cos(viewAngle);
                break;
            case 'd':
                this.eye[0] -= 0.1f * Math.sin(viewAngle);
                this.center[0] -= 0.1f * Math.sin(viewAngle);
                this.eye[2] += 0.1f * Math.cos(viewAngle);
                this.center[2] += 0.1f * Math.cos(viewAngle);
                break;
            case 's':
                this.eye[0] -= 0.1f * Math.cos(viewAngle);
                this.center[0] -= 0.1f * Math.cos(viewAngle);
                this.eye[2] -= 0.1f * Math.sin(viewAngle);
                this.center[2] -= 0.1f * Math.sin(viewAngle);
                break;
            case 'w':
                this.eye[0] += 0.1f * Math.cos(viewAngle);
                this.center[0] += 0.1f * Math.cos(viewAngle);
                this.eye[2] += 0.1f * Math.sin(viewAngle);
                this.center[2] += 0.1f * Math.sin(viewAngle);
                break;
            case 'e':
                this.center[0] = eye[0] + (float) (distanceEyeToCenter() * Math.cos(viewAngle + 0.17));
                this.center[2] = eye[2] + (float) (distanceEyeToCenter() * Math.sin(viewAngle + 0.17));
                break;
            case 'q':
                this.center[0] = eye[0] + (float) (distanceEyeToCenter() * Math.cos(viewAngle - 0.17));
                this.center[2] = eye[2] + (float) (distanceEyeToCenter() * Math.sin(viewAngle - 0.17));
                break;
            case ' ':
                this.simulator.setIsEditorMode(true);
                if (this.simulator.isAnimationRunning()) {
                    this.simulator.toogleAnimation();
                }
                break;
            case 'p':
                toogleAnimation();
                break;
            case 'o':
                if (this.simulator.hadSelection()) {
                    Source source = this.simulator.getSelectedSection().getSource();
                    if (source != null) {
                        source.setOn(!source.isOn());
                    }
                }
                break;
            // TODO: Check this keys
            case 'f':
                this.eye[1] -= 0.1f;
                this.center[1] -= 0.1f;
                break;
            case 'z':
                this.eye[1] -= 0.1f;
                break;
            case 'x':
                this.eye[1] += 0.1f;
                break;
            case 'r':
                this.eye[1] += 0.1f;
                this.center[1] += 0.1f;
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        /*if (simulator.isIsEditorMode()) {

         } else {*/
        switch (ke.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                simulator.selectLeft();
                break;
            case KeyEvent.VK_RIGHT:
                simulator.selectRight();
                break;
            case KeyEvent.VK_DOWN:
                simulator.removeSelection();
                break;
        }
        //}
        this.canvas.display();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        System.out.println("mouse clicked!");
        //System.out.println(me.getX()); //To change body of generated methods, choose Tools | Templates.
        //System.out.println(me.getXOnScreen()); //To change body of generated methods, choose Tools | Templates.
    }

    public void toogleAnimation() {
        if (simulator.isAnimationRunning()) {
            simulator.toogleAnimation();
        } else {
            simulator.toogleAnimation();
            AnimationThread animation = new AnimationThread();
            new Thread(animation).start();

        }
    }

    class AnimationThread implements Runnable {

        @Override
        public void run() {
            while (simulator.isAnimationRunning()) {
                try {
                    simulator.incrementInstant();
                    canvas.display();
                    TimeUnit.MILLISECONDS.sleep(150);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Thread finished!");
        }
    }
}
