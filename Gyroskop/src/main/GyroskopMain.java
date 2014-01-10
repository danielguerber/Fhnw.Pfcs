package main;

import javax.media.opengl.*;

import java.awt.*;
import java.awt.event.*;

import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.*;

public class GyroskopMain implements WindowListener, GLEventListener, KeyListener {
	GLCanvas canvas;
	double left = -10, right = 10;
	double bottom, top;
	double near = -100, far = 100;
	GLUT glut;
	double elev = 10;
	double azim = 40; 
	double dist = 4; 
	double phi = 60;
	double omega = 3;
	double dt = 0.01;
	private TextRenderer textRenderer;

	// ------------------ Methoden --------------------
	double[] cross(double[] u, double[] v) {
		double n1 = u[1] * v[2] - u[2] * v[1];
		double n2 = u[2] * v[0] - u[0] * v[2];
		double n3 = u[0] * v[1] - u[1] * v[0];
		double[] n = { n1, n2, n3 };
		return n;
	}

	double[] normale(double[] A, double[] B, double[] C) {
		double[] u = { B[0] - A[0], B[1] - A[1], B[2] - A[2] };
		double[] v = { C[0] - A[0], C[1] - A[1], C[2] - A[2] };
		double[] n = cross(u, v);
		return n;
	}

	public GyroskopMain() // Konstruktor
	{
		Frame f = new Frame("Gyroskop");
		canvas = new GLCanvas(); // OpenGL-Window
		f.setSize(800, 600);
		f.setBackground(Color.gray);
		f.addWindowListener(this);
		f.addKeyListener(this);
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		f.add(canvas);
		f.setVisible(true);
		FPSAnimator anim = new FPSAnimator(canvas, 120, true);
		anim.start();
	}

	public static void main(String[] args) // main-Methode der Applikation
	{
		new GyroskopMain();
	}

	// --------- OpenGL-Events -----------------------

	public void init(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL(); // OpenGL-Objekt
		GL2 gl = gl0.getGL2();
		gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f); // erasing color
		glut = new GLUT();
		gl.glEnable(gl.GL_DEPTH_TEST);// z-Buffer aktivieren
		gl.glEnable(gl.GL_NORMALIZE);
		gl.glEnable(gl.GL_LIGHT0);
		textRenderer = new TextRenderer(new Font("Arial", 0, 10));
	}

	public void display(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		float[] lightpos = { -10, 150, 100, 1 };
		
		

		gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		gl.glMatrixMode(gl.GL_MODELVIEW);
		gl.glLoadIdentity();// Zeichenfarbe
		gl.glTranslated(0, 1, 0);
		gl.glRotated(0,45,0,0);
		gl.glTranslated(0, 0, -1);
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
		phi = (phi + omega * dt) % 360;
		gl.glRotated(-90, 90, 0, 0);

		//construct stand
		gl.glColor3d(0.6, 0.6, 0.6);
		glut.glutSolidCylinder(0.1, -4, 64, 64);
		gl.glTranslated(0, 0, -4);
		glut.glutSolidCone(0.75, 1.5, 64, 64);
		gl.glTranslated(0, 0, 4);
		gl.glRotated(90, -90, 0, 0);
		
		//apply elevation and azimuth
		gl.glRotated(-azim, 0, 1, 0);
		gl.glRotated(elev, 1, 0, 0);
		
		gl.glTranslated(0, 0, -3);
		gl.glRotated(1, 1, phi, 0);
		gl.glTranslated(-0.05, 0, 0);
		glut.glutSolidCylinder(0.1, 6, 64, 64);
		gl.glTranslated(0, 0, 5.25);
		glut.glutSolidCylinder(0.35, 0.25, 64, 64);
		gl.glTranslated(0, 0, -5.25);
		
		gl.glTranslated(0, 0, 4.75);
		glut.glutSolidCylinder(0.25, 0.25, 64, 64);
		gl.glTranslated(0, 0, -4.75);
		gl.glTranslated(0, 0, 3);
		glut.glutSolidSphere(0.2, 64, 64);
		gl.glTranslated(0, 0, -3);
		
		gl.glColor3d(0.3, 0.3, 0.3);
		glut.glutSolidCylinder(1, 0.25, 20, 20);
		gl.glColor3d(0.5, 0.5, 0.5);
		glut.glutSolidCylinder(0.25, 0.5, 64, 64);
		
		
		gl.glRotated(phi * 180 / Math.PI, 0, 0, 1);
		
		gl.glTranslated(0.8, 0, 0);
		gl.glColor3d(0.8549, 0.0, 0.0);
		glut.glutSolidSphere(0.03, 64, 64);
		gl.glTranslated(-0.8, 0, 0);
		gl.glTranslated(0.8, 0, 0.25);
		glut.glutSolidSphere(0.03, 64, 64);
		gl.glTranslated(-0.8, 0, -0.25);
		
		textRenderer.setColor(0.0f, 0.0f, 0.9f, 1.0f);
		
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.draw("Increase Azimuth: Arrow Rigth", 10, 70);
		textRenderer.draw("Decrease Azimuth Speed: Arrow Left", 10, 60);
		textRenderer.draw("Increase Elevation: Arrow Up", 10, 50);
		textRenderer.draw("Decrease Elevation: Arrow Down", 10, 40);
		textRenderer.draw("Inrease Speed: W", 10, 30);
		textRenderer.draw("Decrease Speed: w", 10, 20);
		textRenderer.endRendering();
		
	}

	public void reshape(GLAutoDrawable drawable, // Window resized
			int x, int y, int width, int height) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glViewport(0, 0, width, height);
		double aspect = (float) height / width; // aspect-ratio
		bottom = aspect * left;
		top = aspect * right;
		gl.glMatrixMode(gl.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, near, far); // Viewing-Volume (im
															// Raum)
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	// --------- Window-Events --------------------

	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		char key2 = e.getKeyChar();
		switch (key) {
		case KeyEvent.VK_UP:
			elev = elev - 3;
			break;
		case KeyEvent.VK_DOWN:
			elev = elev + 3;
			break;
		case KeyEvent.VK_LEFT:
			azim = azim - 3;
			break;
		case KeyEvent.VK_RIGHT:
			azim = azim + 3;
			break;
		}
		switch (key2) {
		case 'w':
			omega = omega - 2;
			break;
		case 'W':
			omega = omega + 2;
			break;
		case 's':
			omega = 0;
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

	}

}