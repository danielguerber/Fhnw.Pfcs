//  -------------   JOGL SampleProgram  (Fadenkreuz) ------------
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;

public class MyFirstA
       implements WindowListener, GLEventListener, KeyListener
{
	private double xm=-5;
	private double ym=2;
	private double vx=10, r=0.1;
	private double vy=5;
	private double dt=0.01;
	final double g = 9.81;
	private double ay=-g;
     //  ------------------  Methoden  --------------------

     void zeichneAchsen(GL2 gl)                                  // Koordinatenachsen zeichnen
     {  gl.glBegin(gl.GL_LINES);
        gl.glVertex2d(-10,0);        // x-Achse
        gl.glVertex2d(10,0);
        gl.glVertex2d(0,-10);        // y-Achse
        gl.glVertex2d(0,10);
        gl.glEnd();
     }


     void zeichneKreis(GL2 gl, double r, double xm, double ym)                         // Kreis um den Nullpunkt
     {  int nPkte = 40;                                          // Anzahl Punkte
        double dt = 2.0*Math.PI / nPkte;                         // Parameter-Schrittweite
        gl.glBegin(GL2.GL_POLYGON);
        for (int i=0; i < nPkte; i++)
          gl.glVertex2d(xm+r*Math.cos(i*dt),                         // x = r*cos(i*dt)
                        ym+r*Math.sin(i*dt));                        // y = r*sin(i*dt-phi)
        gl.glEnd();
     }


     public MyFirstA()                                             // Konstruktor
     {  Frame f = new Frame("MyFirst");
        f.setSize(800, 600);
        f.addWindowListener(this);
        f.addKeyListener(this);
        GLCanvas canvas = new GLCanvas();                         // OpenGL-Window
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        f.add(canvas);
        f.setVisible(true);
        FPSAnimator anim = new FPSAnimator(canvas, 100, true);
        anim.start();
     }


     public static void main(String[] args)                     // main-Methode der Applikation
     {  new MyFirstA();
     }


     //  ---------  OpenGL-Events  -----------------------

     public void init(GLAutoDrawable drawable)
     {  GL gl0 = drawable.getGL();                               // OpenGL-Objekt
        GL2 gl = gl0.getGL2();
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);                // erasing color
        
     }


     public void display(GLAutoDrawable drawable)
     {  GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);                     // Bild loeschen
        gl.glColor3d(0.5,0.5,0.5);                                    // Zeichenfarbe
        zeichneAchsen(gl);
        gl.glColor3d(1, 1, 1);                                  // Zeichenfarbe
        zeichneKreis(gl,r,xm,ym);
        
        if (ym <= r)
        	vy=-vy;
        
        if (xm > 20) {
        	xm = -20;
        }
        
        xm+=vx*dt;
        ym += vy*dt;
        vy+=ay*dt;
     }


     public void reshape(GLAutoDrawable drawable,               // Window resized
                         int x, int y,
                         int width, int height)
     {  GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glViewport(0, 0, width, height);                     // Window
        double aspect = (double)height/width;
        double left=-10, right=10;
        double bottom=aspect*left, top=aspect*right;
        double near=-100, far=100;
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left,right,bottom,top,near,far);             // ViewingVolume
     }


     public void dispose(GLAutoDrawable drawable)
     { }


     //  ---------  Window-Events  --------------------

     public void windowClosing(WindowEvent e)
     {  System.exit(0);
     }
     public void windowActivated(WindowEvent e) {  }
     public void windowClosed(WindowEvent e) {  }
     public void windowDeactivated(WindowEvent e) {  }
     public void windowDeiconified(WindowEvent e) {  }
     public void windowIconified(WindowEvent e) {  }
     public void windowOpened(WindowEvent e) {  }


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
		switch (key) {
			case KeyEvent.VK_UP:
				System.out.println("keyPresed up");
				break;
			case KeyEvent.VK_DOWN:
				System.out.println("keyPresed down");
				break;
			default:
				break;
		}
	}


	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}


	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Typed: " + e.getKeyChar());
	}

  }
