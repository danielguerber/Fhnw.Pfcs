//  -------------   JOGL SampleProgram  (Fadenkreuz) ------------
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

public class SchieferWurf implements WindowListener, GLEventListener,
		KeyListener {

	double angle = 30;
	final double xm = 0;
	final double ym = 0.5;
	final double g = 9.81;
	double speed_modifier = 1;
	final double v_initial = 10;
	final double dt = 0.01;
	int counter = 0;
	final double mass = 0.058;
	final double r = 0.2;
	final double radius_ball = 0.034;
	double phi = 1.2041;
	final double flow_coefficient = 0.4;
	final double cross_section_area = Math.PI * radius_ball * radius_ball;
	double force = phi / 2 * flow_coefficient * cross_section_area;
	private TextRenderer textRenderer;

	ArrayList<Ball> ballsList = new ArrayList<Ball>();

	static class Ball {
		double x;
		double y;
		double vy;
		double vx;
		double t = 0;

		public Ball(double x, double y, double velocity, double angle) {
			this.x = x;
			this.y = y;
			this.vy = velocity * Math.cos(Math.toRadians(angle));
			this.vx = velocity * Math.sin(Math.toRadians(angle));
		}
	}

	public static void main(String[] args) {
		new SchieferWurf();
	}

	public SchieferWurf() {
		Frame f = new Frame("Schiefer Wurf");
		f.setSize(800, 600);
		f.addWindowListener(this);
		f.addKeyListener(this);

		GLCanvas canvas = new GLCanvas();
		canvas.addGLEventListener(this);
		canvas.addKeyListener(this);
		f.add(canvas);

		f.setVisible(true);
		FPSAnimator anim = new FPSAnimator(canvas, 80, true);
		anim.start();
	}

	public void init(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClearColor(0f, 0.45f, 0f, 1.0f);
		textRenderer = new TextRenderer(new Font("Arial", 0, 10));
	}

	public void display(GLAutoDrawable drawable) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		drawAxis(gl);

		if (counter == 15) {
			Ball newBall = new Ball(xm, ym, v_initial * speed_modifier, angle);
			ballsList.add(newBall);
			counter = 0;
		}

		for (int i = 0; i < ballsList.size(); i++) {
			Ball curBall = ballsList.get(i);
			if (curBall.y <= r) {
				curBall.vy = -curBall.vy * 0.6;
				curBall.y = ym * 0.5;
			}
			if (Math.abs(curBall.x) >= 20 || curBall.t > 2000) {
				ballsList.remove(i);
			} else {
				double abs = Math.sqrt(curBall.vx * curBall.vx + curBall.vy
						* curBall.vy);
				double oldVx = curBall.vx;
				double oldVy = curBall.vy;

				curBall.vx = oldVx + (-force / mass) * abs * oldVx * dt;
				curBall.vy = oldVy + ((-force / mass) * abs * oldVy - g) * dt;
				curBall.x = curBall.x + ((oldVx + curBall.vx) / 2) * dt;
				curBall.y = curBall.y + ((oldVy + curBall.vy) / 2) * dt;
			}
			gl.glColor3d(1, 1, 0);
			drawCircle(gl, r, curBall.x, curBall.y);
			curBall.t++;
		}
		counter = counter + 1;

		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.draw("Increase Launch Speed: Arrow Rigth", 10, 50);
		textRenderer.draw("Decrease Launch Speed: Arrow Left", 10, 40);
		textRenderer.draw("Increase Angle: Arrow Up", 10, 30);
		textRenderer.draw("Decrease Angle: Arrow Down", 10, 20);
		textRenderer.endRendering();
	}

	void drawAxis(GL2 gl) {
		gl.glColor3d(1, 1, 1);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2d(-7, 0);
		gl.glVertex2d(20, 0);
		gl.glVertex2d(0, -5);
		gl.glVertex2d(0, 20);
		gl.glEnd();
	}

	void drawCircle(GL2 gl, double r, double xm, double ym) {
		int nPkte = 70;
		double dt = 2.0 * Math.PI / nPkte;
		gl.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < nPkte; i++)
			gl.glVertex2d(xm + r * Math.cos(i * dt), ym + r * Math.sin(i * dt));
		gl.glEnd();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_DOWN:
			if (angle <= 86)
				angle = angle + 5;
			break;
		case KeyEvent.VK_UP:
			if (angle >= 4)
				angle = angle - 5;
			break;
		case KeyEvent.VK_RIGHT:
			if (speed_modifier <= 10)
				speed_modifier += 0.1;
			break;
		case KeyEvent.VK_LEFT:
			if (speed_modifier >= 0.05)
				speed_modifier -= 0.1;
			break;
		}

	}

	public void reshape(GLAutoDrawable drawable, // Window resized
			int x, int y, int width, int height) {
		GL gl0 = drawable.getGL();
		GL2 gl = gl0.getGL2();
		gl.glViewport(0, 0, width, height); // Window
		double aspect = (double) height / width;
		double left = -7, right = 20;
		double bottom = aspect * left, top = aspect * right;
		double near = -100, far = 100;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(left, right, bottom, top, near, far); // ViewingVolume
	}

	public void windowClosing(WindowEvent e) {
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
