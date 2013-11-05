package main;

import java.awt.BorderLayout;
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
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

public class CarSimulation implements WindowListener, GLEventListener,
		KeyListener {

	private double vxs = -40;
	private double vys = 10;
	private Car car;

	private double threshold = 20;

	private boolean stop = false;
	private TextRenderer textRendererLegend;
	private TextRenderer textRendererCentr;

	public static void main(String[] args) {
		new CarSimulation();
	}

	public CarSimulation() {
		car = new Car(vxs, vys);
		Frame f = new Frame("Car Simulation");
		f.setSize(800, 600);
		f.addWindowListener(this);
		f.addKeyListener(this);

		f.setLayout(new BorderLayout());
		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);

		f.add(canvas, BorderLayout.CENTER);
		f.setVisible(true);
		FPSAnimator anim = new FPSAnimator(canvas, 200, true);
		anim.start();
		textRendererLegend = new TextRenderer(new Font("Arial", 0, 10));
		textRendererCentr = new TextRenderer(new Font("Arial", 0, 20));
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		if (!stop)
			car.update();

		drawTrack(gl);

		double zentripetal = car.getCentripetalForce();
		if (zentripetal < -threshold || zentripetal > threshold) {
			car.isRed = true;
			textRendererCentr.setColor(1, 0, 0, 1);
		} else {
			car.isRed = false;
			textRendererCentr.setColor(1, 1, 1, 1);
		}
		car.draw(gl);

		textRendererCentr.beginRendering(drawable.getWidth(),
				drawable.getHeight());
		textRendererCentr.draw(
				String.format("Zentripedalkraft: %.2f", zentripetal), 10,
				drawable.getHeight() - 30);
		textRendererCentr.endRendering();

		textRendererLegend.beginRendering(drawable.getWidth(),
				drawable.getHeight());
		textRendererLegend.draw("Reset: r", 10, 70);
		textRendererLegend.draw("Stop/Continue Simulation: s", 10, 60);
		textRendererLegend.draw("Turn Rigth: Arrow Rigth", 10, 50);
		textRendererLegend.draw("Turn Left: Arrow Left", 10, 40);
		textRendererLegend.draw("Increase Speed: Arrow Up", 10, 30);
		textRendererLegend.draw("Decrease Speed: Arrow Down", 10, 20);
		textRendererLegend.endRendering();
	}

	private void drawTrack(GL2 gl) {
		gl.glColor3d(0, 0, 1);

		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(-50, 15);
		gl.glVertex2d(0, 15);
		gl.glVertex2d(-50, 5);
		gl.glVertex2d(0, 5);
		gl.glEnd();

		drawCurve(gl);
	}

	private void drawCurve(GL2 gl) {
		gl.glTranslated(0, 15, 0);
		double s = 0.8;
		for (int i = 0; i < 20; i++) {
			drawLine(gl, s);
			gl.glTranslated(s, 0, 0);
		}
		double phi = 0, dphi = 0.1;
		for (int i = 0; i < 30; i++) {
			drawLine(gl, s);
			gl.glTranslated(s, 0, 0);
			gl.glRotated(-phi, 0, 0, 1);
			phi += dphi;
		}
		for (int i = 0; i < 30; i++) {
			drawLine(gl, s);
			gl.glTranslated(s, 0, 0);
			gl.glRotated(-phi, 0, 0, 1);
		}
		for (int i = 0; i < 30; i++) {
			drawLine(gl, s);
			gl.glTranslated(s, 0, 0);
			gl.glRotated(-phi, 0, 0, 1);
			phi -= dphi;
		}
		gl.glTranslated(22 * s, -18.6, 0);
		gl.glRotated(180, 0, 0, 1);
	}

	void drawLine(GL2 gl, double s) {
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

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_RIGHT:
			if (!stop && car.angleWheel >= -35)
				car.angleWheel -= 2;
			break;
		case KeyEvent.VK_LEFT:
			if (!stop && car.angleWheel <= 35)
				car.angleWheel += 2;
			break;
		case KeyEvent.VK_UP:
			if (!stop)
				car.v += 1;
			break;
		case KeyEvent.VK_DOWN:
			if (!stop)
				car.v -= 1;
			break;
		case KeyEvent.VK_S:
			stop = !stop;
			break;
		case KeyEvent.VK_R:
			car = new Car(vxs, vys);
			stop = false;
			break;
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	// Unused events
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
	public void keyReleased(KeyEvent arg0) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}