package main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
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
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.FPSAnimator;

public class CarSimulation implements WindowListener, GLEventListener {

	private static double vxs = -40;
	private static double vys = 10;
	private static Car car;
	
	private static double threshold = 20;
	
	private static boolean stop = false;

	public CarSimulation() {
		car = new Car(vxs, vys);
		Frame f = new Frame("Car Simulation");
		f.setSize(800, 600);
		f.addWindowListener(this);
		f.setLayout(new BorderLayout());
		GLCanvas canvas = new GLCanvas(); // OpenGL-Window
		canvas.addGLEventListener(this);
		f.add(canvas, BorderLayout.CENTER);
		f.setVisible(true);
		FPSAnimator anim = new FPSAnimator(canvas, 200, true);
		anim.start();
		initKeys(canvas);
	}

	public static void main(String[] args) {
		new CarSimulation();
	}

	void zeichneStrecke(GL2 gl, double s) {
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(0, 0);
		gl.glVertex2d(s, 0);
		gl.glEnd();
		gl.glTranslated(0, -10, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(0, 0);
		gl.glVertex2d(s, 0);
		gl.glEnd();
		gl.glTranslated(0, 10, 0);
	}
	
	private void zeichneKurve(GL2 gl) {
		gl.glTranslated(0, 15, 0);
		double s = 0.8;
		for (int i = 0; i < 20; i++) {
			zeichneStrecke(gl, s);
			gl.glTranslated(s, 0, 0);
		}
		double phi = 0, dphi = 0.1;
		for (int i = 0; i < 30; i++) {
			zeichneStrecke(gl, s);
			gl.glTranslated(s, 0, 0);
			gl.glRotated(-phi, 0, 0, 1);
			phi += dphi;
		}
		for (int i = 0; i < 30; i++) {
			zeichneStrecke(gl, s);
			gl.glTranslated(s, 0, 0);
			gl.glRotated(-phi, 0, 0, 1);
		}
		for (int i = 0; i < 30; i++) {
			zeichneStrecke(gl, s);
			gl.glTranslated(s, 0, 0);
			gl.glRotated(-phi, 0, 0, 1);
			phi -= dphi;
		}
		gl.glTranslated(22*s, -18.6, 0);
		gl.glRotated(180, 0, 0, 1);
	}
	
	private void zeichneRennbahn(GL2 gl) {
		gl.glColor3d(0, 0, 1);
		
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(-50, 15);
		gl.glVertex2d(0, 15);
		gl.glVertex2d(-50, 5);
		gl.glVertex2d(0, 5);
		gl.glEnd();
		
		zeichneKurve(gl);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT); // Bild loeschen
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (!stop)
			car.update();
		
		double zentripetal = car.getCentripetalForce();
//		zentri.setText("Zentripetalkraft: " + zentripetal);
		if (zentripetal < -threshold || zentripetal > threshold)
			car.isRed=true;
		else
			car.isRed=false;
//		else zentri.setBackground(Color.GREEN);
		car.draw(gl);
		zeichneRennbahn(gl);
	}


	@Override
	public void dispose(GLAutoDrawable arg0) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL(); // OpenGL-Objekt
		GL2 gl = gl0.getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // erasing color
	}

	@Override
	public void reshape(GLAutoDrawable drawable, // Window resized
			int x, int y, int width, int height) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glViewport(0, 0, width, height); // Window
		double aspect = (double) height / width;
		double left = -50, right = 50;
		double bottom = aspect * left, top = aspect * right;
		double near = 0, far = 1;
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, near, far); // ViewingVolume
	}

	private void initKeys(Canvas c) {
		c.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT: if (!stop && car.angleWheel >= -35) car.angleWheel -= 2; break;
				case KeyEvent.VK_LEFT: if (!stop && car.angleWheel <= 35) car.angleWheel += 2; break;
				case KeyEvent.VK_UP:
					if (!stop) car.v += 1;
					break;
				case KeyEvent.VK_DOWN:
					if (!stop) car.v -= 1;
					break;
				case KeyEvent.VK_S: stop = !stop; break;
				case KeyEvent.VK_R: car = new Car(vxs, vys); stop = false; break;
				}
			}
		});
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
