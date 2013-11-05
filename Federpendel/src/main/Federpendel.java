package main;

import java.awt.Font;
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
import com.jogamp.opengl.util.awt.TextRenderer;

public class Federpendel implements WindowListener, GLEventListener,
		KeyListener {

	private double xm = 0;
	private double ym = 0;
	private double x = 0;
	private double y = 0;
	private double z = 0;
	private double dt = 0.01;
	private double rBall = 0.2;
	private double rK = 4;
	private double r = rK - rBall;
	private double speedModifier = 1;
	private double w = 0;
	private boolean showCircle=false;
	private TextRenderer textRenderer;
	
	public static void main(String[] args) {
		new Federpendel();
	}

	public Federpendel() {
		Frame f = new Frame("Federpendel");
		f.setSize(1000, 600);
		f.addWindowListener(this);
		f.addKeyListener(this);

		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);

		f.add(canvas);
		f.setVisible(true);

		FPSAnimator anim = new FPSAnimator(canvas, 200, true);
		anim.start();
	}

	public void init(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClearColor(0.3f, 0.01f, 0.8f, 1.0f);
		textRenderer = new TextRenderer(new Font("Arial", 0, 10));
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		drawAxis(gl);

		calculateAndDrawPositions(gl);
		
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.draw("Show circle: r", 10, 60);
		textRenderer.draw("Increase Altitude: A", 10, 50);
		textRenderer.draw("Decrease Altitude : a", 10, 40);
		textRenderer.draw("Increase Speed: Arrow Up", 10, 30);
		textRenderer.draw("Decrease Speed: Arrow Down", 10, 20);
		textRenderer.endRendering();
	}

	private void drawAxis(GL2 gl) {
		gl.glColor3d(0.5, 0.5, 0.5);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(-10, 0);
		gl.glVertex2d(10, 0);
		gl.glVertex2d(0, -10);
		gl.glVertex2d(0, 10);
		gl.glEnd();
	}

	private void calculateAndDrawPositions(GL2 gl) {
		gl.glColor3d(0.8, 0.8, 0.8); 
		
		w -= 1 * speedModifier;
		y = ym + r * Math.sin(w * dt);
		x = r * Math.cos(w * dt);
		drawBall(gl, rBall, xm, y); 
		gl.glColor3d(1, 1, 1);
		drawSpring(gl, rBall-0.05, 0, y + rBall, z);
		if (showCircle) {
			drawCircle(gl, rK - rBall); 
			drawBall(gl, rBall, x, y);
		}
	}
	
	void drawBall(GL2 gl, double r, double xm, double ym)
	{
		int nPkte = 70;
		double dt = 2.0 * Math.PI / nPkte; 
		gl.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < nPkte; i++)
		gl.glVertex2d(xm + r * Math.cos(i * dt), 
		ym + r * Math.sin(i * dt)); 
		gl.glEnd();
	}
	
	void drawSpring(GL2 gl, double r, double xm, double ym, double zm) {
		int coils = 9;
		int points = coils * 70;
		int total = coils * points;
		double anchor = 5.0; 
		double stretch = 0; // spring stretch
		double dt = 2.0 * coils * Math.PI / points; // time step
		gl.glBegin(GL2.GL_LINE_STRIP);
		{
			// top vertices
			gl.glVertex3d(0, (anchor + r * Math.sin(10 * dt)) + 0.5, zm);
			gl.glVertex3d(0, anchor + (0 / total * (coils - ym)) - stretch, zm);
			// loops
			for (int i = 0; i < points; i++) {
				gl.glVertex3d(xm + r * Math.cos(i * dt), anchor
						+ (i / total * (coils - ym)) - stretch,
						r * Math.sin(i * dt));
				stretch += ((anchor - ym) / (points + 40));
			}
			// bottom vertex
			gl.glVertex3d(0, anchor + (points / total * (coils - ym))
					- stretch, zm);
			gl.glVertex3d(0, ym, zm);
		}
		gl.glEnd();
	}

	void drawCircle(GL2 gl, double r)
	{
		int nPkte = 70;
		double dt = 2.0 * Math.PI / nPkte;
		gl.glBegin(GL.GL_LINE_LOOP);
		for (int i = 0; i < nPkte; i++)
			gl.glVertex2d(r * Math.cos(i * dt),
					r * Math.sin(i * dt));
		gl.glEnd();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		if (c == 'r') { // r
			showCircle = showCircle ? false : true;
		} else if (c == 'a' && rK >= 0.2) { // a
			rK = rK - 0.2;
			r = rK - rBall;
		} else if (c == 'A' && rK <= 4.5) { // A
			rK = rK + 0.2;
			r = rK - rBall;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		int key = event.getKeyCode();
		switch (key) {
		case KeyEvent.VK_UP:
			if (speedModifier < 1000)
				speedModifier += 0.2;
			break;
		case KeyEvent.VK_DOWN:
			if (speedModifier >= 0.1)
				speedModifier -= 0.2;
			break;
		}
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glViewport(0, 0, width, height);
		double aspect = (double) height / width;
		double left = -10, right = 10;
		double bottom = aspect * left, top = aspect * right;
		double near = -100, far = 100;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, near, far);
	}

	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
	
	//Unused events
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	public void dispose(GLAutoDrawable drawable) {}

}