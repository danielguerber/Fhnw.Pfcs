//  -------------   JOGL SampleProgram  (Rollbahn) ------------
import javax.media.opengl.*;
import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.*;
import static java.lang.Math.*;

public class Rollbahn
       implements WindowListener, GLEventListener
{

     //  ---------------  globale Daten  ------------------
     double left=-2, right=2;                                     // Koordinatenbereich
     double r=0.02;                                               // Kugelradius
     double v0=0.2;                                               // Anfangsgeschwindigkeit der Kugel
     double x1=left, y1=0, v1=v0;                                 // Position und Geschw. Kugel1
     double dt=0.01;                                              // Zeitschritt
     double a=-0.3, b=-2*a, c=a;                                  // Parameter der Bahn (Polynomkoeff.)
     GLUT glut = new GLUT();


     //  ------------------  Methoden  --------------------

     void zeichneAchsen(GL2 gl)                                  // Koordinatenachsen zeichnen
     {  gl.glBegin(gl.GL_LINES);
        gl.glVertex2d(-10,0);        // x-Achse
        gl.glVertex2d(10,0);
        gl.glVertex2d(0,-10);        // y-Achse
        gl.glVertex2d(0,10);
        gl.glEnd();
     }


     double yBahn(double x)                                       // y = a + b*x^2 + c*x^4
     {  if ( abs(x) >= 1)
          return 0;
        else
          return a + b*pow(x,2) + c*pow(x,4);
     }


     void zeichneBahn(GL2 gl)
     {  double x=-2, dx=0.1, xEnd=-x+dx, y;
        gl.glBegin(gl.GL_LINE_STRIP);
        while ( x <= xEnd )
        { y = yBahn(x);
          gl.glVertex2d(x,y);
          x += dx;
        }
        gl.glEnd();
     }


     public Rollbahn()                                            // Konstruktor
     {  Frame f = new Frame("MyFirst");
        f.setSize(800, 600);
        f.addWindowListener(this);
        GLCanvas canvas = new GLCanvas();                         // OpenGL-Window
        canvas.addGLEventListener(this);
        f.add(canvas);
        f.setVisible(true);
        FPSAnimator anim = new FPSAnimator(canvas,                // Animations-Thread
                                           200,true);
        anim.start();
     }


     public static void main(String[] args)                       // main-Methode der Applikation
     {  new Rollbahn();
     }


     //  ---------  OpenGL-Events  -----------------------

     public void init(GLAutoDrawable drawable)
     {  GL gl0 = drawable.getGL();                                // OpenGL-Objekt
        GL2 gl = gl0.getGL2();
        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);                  // erasing color
     }


     public void display(GLAutoDrawable drawable)
     {  GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glClear(gl.GL_COLOR_BUFFER_BIT);                       // Bild loeschen
        gl.glMatrixMode(gl.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glColor3d(0.5,0.5,0.5);                                // Zeichenfarbe
        zeichneAchsen(gl);
        gl.glColor3d(1,1,0.5);
        zeichneBahn(gl);
        gl.glColor3d(1, 0, 0);
        gl.glTranslated(x1,y1+r,0);
        glut.glutSolidSphere(r,10,10);                           // Kugel zeichnen
        x1 += v1*dt;                                             // Kugel verschieben
     }


     public void reshape(GLAutoDrawable drawable,                // Window resized
                         int x, int y,
                         int width, int height)
     {  GL gl0 = drawable.getGL();
        GL2 gl = gl0.getGL2();
        gl.glViewport(0, 0, width, height);                      // Window
        double aspect = (double)height/width;
        double bottom=aspect*left, top=aspect*right;
        double near=-100, far=100;
        gl.glMatrixMode(gl.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left,right,bottom,top,near,far);              // ViewingVolume
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

  }