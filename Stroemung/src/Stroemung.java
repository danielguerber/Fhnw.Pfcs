	

    import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
     
    import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
     
    import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
     
     
    public class Stroemung implements WindowListener, GLEventListener, KeyListener {
           
            private final static int RES = 16;
           
            private static double r = 5.0;
            private ArrayList<Particle> particles = new ArrayList<Particle>();      // one stream
            private int nextParticle = 0;
            private int particleV = 50;
           
            private int streamNo = 1;
           
            private double azim = 0.0;
            private double elev = 0.0;

			private TextRenderer textRenderer;
           
           
            public static void main(String[] args) {
                    new Stroemung();
            }
           
            public Stroemung() // Konstruktor
            {
                    Frame f = new Frame("Strömung");
                    f.setSize(800, 600);
                    f.addWindowListener(this);
                    GLCanvas canvas = new GLCanvas(); // OpenGL-Window
                    canvas.addGLEventListener(this);
                    f.add(canvas);
                   
                    f.addKeyListener(this);
                   
                    f.setVisible(true);
                    canvas.addKeyListener(this);
                    FPSAnimator anim = new FPSAnimator(canvas, 200, true);
                    anim.start();
            }
     
            @Override
            public void display(GLAutoDrawable drawable) {
                    GLUT glut = new GLUT();
                    GL gl0 = drawable.getGL();
                    GL2 gl = gl0.getGL2();
                   
                    gl.glClear(GL.GL_COLOR_BUFFER_BIT + GL.GL_DEPTH_BUFFER_BIT); // Bild loeschen
                    gl.glClearDepth(1.0f);
                    gl.glEnable(GL2.GL_DEPTH_TEST);
                    gl.glDepthFunc(GL2.GL_LEQUAL);
                    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
                    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glColor3d(0.1, 0.1, 0.6);
           
                    // draw here
            gl.glRotated(-elev, 1, 0, 0);
            gl.glRotated(azim, 0, 1, 0);
            if (nextParticle == 0) {
                    for (int i = 0; i < 5; i++) {
                    particles.add(new Particle(-25, (i+0.5)*2, 0.25, 1000));
                            }
                    nextParticle = particleV;
            }
            nextParticle--;
            
            Iterator<Particle> it1 = particles.iterator();
            while (it1.hasNext()) {
                    Particle particle = it1.next();
                    particle.update(0.1);
                    if (particle.getLifeTime() == 0) it1.remove();
            }
            
            double omega = 360.0 / streamNo;
            for (int i = 0; i < streamNo; i++) {
				gl.glRotated(omega, 1,0,0);
				for (Particle particle : particles) {
					particle.draw(gl, glut);
				}   
            }
            gl.glColor3d(0, 0, 0);
            glut.glutSolidSphere(r, RES, RES);
            
            textRenderer.setColor(0.0f, 0.0f, 0.9f, 1.0f);
    		
    		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
    		
    		textRenderer.draw("Increase Distance: Q", 10, 90);
    		textRenderer.draw("Decrease Distance: E", 10, 80);
    		textRenderer.draw("Increase Azimuth: D", 10, 70);
    		textRenderer.draw("Decrease Azimuth: A", 10, 60);
    		textRenderer.draw("Increase Elevation: W", 10, 50);
    		textRenderer.draw("Decrease Elevation: S", 10, 40);
    		textRenderer.draw("Inrease Flows: Arrow Up", 10, 30);
    		textRenderer.draw("Decrease Flows: Arrow Down", 10, 20);
    		textRenderer.endRendering();
            }
     
            @Override
            public void dispose(GLAutoDrawable arg0) {}
     
            @Override
            public void init(GLAutoDrawable drawable) {
                    GL gl0 = drawable.getGL(); // OpenGL-Objekt
                    GL2 gl = gl0.getGL2();
                    gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f); // erasing color
                    textRenderer = new TextRenderer(new Font("Arial", 0, 10));
            }
     
            @Override
            public void reshape(GLAutoDrawable drawable, // Window resized
                            int x, int y, int width, int height) {
                    GL gl0 = drawable.getGL();
                    GL2 gl = gl0.getGL2();
                    gl.glViewport(0, 0, width, height); // Window
                    double aspect = (double) height / width;
                    double left = -25, right = 25;
                    double bottom = aspect * left, top = aspect * right;
                    double near = -100, far = 100;
                    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
                    gl.glLoadIdentity();
                    gl.glOrtho(left, right, bottom, top, near, far); // ViewingVolume
            }
     
            @Override
            public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                            System.exit(0);
                            break;
                    case KeyEvent.VK_W:
                            this.elev += 3;
                            break;
                    case KeyEvent.VK_S:
                            this.elev -= 3;
                            break;
                    case KeyEvent.VK_A:
                            this.azim -= 3;
                            break;
                    case KeyEvent.VK_D:
                            this.azim += 3;
                            break;
                    case KeyEvent.VK_UP:
                            this.streamNo++;
                            break;
                    case KeyEvent.VK_DOWN:
                            if (streamNo > 1) {
                                    this.streamNo--;
                            }
                            break;
                    case KeyEvent.VK_Q:
                            this.particleV += 5;
                            break;
                    case KeyEvent.VK_E:
                            if (this.particleV > 5) {
                                    this.particleV -= 5;
                            }
                            break;
                    }
     
            }
     
            @Override
            public void keyReleased(KeyEvent arg0) {}
     
            @Override
            public void keyTyped(KeyEvent arg0) {}
     
            @Override
            public void windowActivated(WindowEvent arg0) {}
     
            @Override
            public void windowClosed(WindowEvent arg0) {}
     
            @Override
            public void windowClosing(WindowEvent arg0) {System.exit(0);}
     
            @Override
            public void windowDeactivated(WindowEvent arg0) {}
     
            @Override
            public void windowDeiconified(WindowEvent arg0) {}
     
            @Override
            public void windowIconified(WindowEvent arg0) {}
     
            @Override
            public void windowOpened(WindowEvent arg0) {}
    }

