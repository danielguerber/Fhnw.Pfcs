package main;

import java.awt.geom.Point2D;

import javax.media.opengl.GL2;

public class Car {

	private double x;
	private double y;
	public double v;

	// angles
	public double angleWheel = 0;
	private double angleCar = 0;
	private double angleLeft = 0;
	private double angleRight = 0;

	private double carLenght;
	private double carWidth;
	private double axisOffsetX;
	private double axisLenght;
	private double wheelLenght;
	private double wheelWidth;
	private double radius;
	
	public boolean isRed = false;


	public Car(double x, double y) {

		this.x = x;
		this.y = y;
		this.angleCar = 0;

		// calculate size
		carLenght =6.0 / 2; // the half of the car length for less
								// calculations
		carWidth = carLenght / 2; // the half of the car width for less
									// calculations

		// calculate axis
		axisOffsetX = 6.0 / 3;
		axisLenght = carWidth + carWidth / 3; // the half of the axis width for
												// less calculations

		// calculate wheels
		wheelLenght = 6.0 / 10;
		wheelWidth = wheelLenght / 2;
	}

	public void update() {

		double dt = 0.01;

		double angle = Math.toRadians(angleCar);
		double angleW = Math.toRadians(angleWheel);

		if (angleWheel == 0) {

			// drive straight on
			x += v * Math.cos(angle) * dt;
			y += v * Math.sin(angle) * dt;

		} else {

			double d = 2 * axisOffsetX; // Abstand Achsen
			double b = axisLenght; // Halbe Breite

			if (angleWheel > 0) {

				// turn left
				radius = b + d / Math.tan(angleW);
				angleLeft = angleWheel;
				angleRight = Math.toDegrees(Math.tan(d / (radius - b)));

			} else {

				// turn right
				radius = -b + d / Math.tan(angleW);
				angleLeft = Math.toDegrees(Math.tan(d / (radius - b)));
				angleRight = angleWheel;

			}

			double deltaPhi = v * dt / radius;

			x += radius
					* (Math.sin(angle + deltaPhi) - Math.sin(angle));
			y -= radius
					* (Math.cos(angle + deltaPhi) - Math.cos(angle));

			angleCar = (angleCar + Math.toDegrees(deltaPhi)) % 360;
		}
	}

	public final double getCentripetalForce() {
		double value = Math.abs(v * v / radius);
		return value == java.lang.Double.POSITIVE_INFINITY ? 0 : value;
	}

	@Override
	public String toString() {
		return "angleCar: " + String.format("%.2f", Math.abs(angleCar)) + ", "
				+ "angleWheel: " + String.format("%.2f", angleWheel) + ", "
				+ "speed: " + v + ", " + "centripetal force: "
				+ getCentripetalForce();
	}

	public void draw(GL2 graphics) {
		graphics.glPushMatrix();
		if (isRed)
			graphics.glColor3d(1, 0,0);
		else
			graphics.glColor3d(1, 1,1);

		// translate and rotate
		graphics.glTranslated(x, y, 0);
		graphics.glRotated(angleCar, 0, 0, 1);

		drawRect(graphics, new Point2D.Double(-carLenght, carWidth),
				new Point2D.Double(carLenght, -carWidth), true);

		drawLine(graphics, new Point2D.Double(-axisOffsetX,
				axisLenght), new Point2D.Double(-axisOffsetX, -axisLenght));
		drawLine(graphics,
				new Point2D.Double(axisOffsetX, axisLenght),
				new Point2D.Double(axisOffsetX, -axisLenght));

		drawWeel(graphics, new Point2D.Double(-axisOffsetX, axisLenght + wheelWidth), 0);
		drawWeel(graphics, new Point2D.Double(-axisOffsetX, -axisLenght - wheelWidth), 0);
		drawWeel(graphics, new Point2D.Double(axisOffsetX, axisLenght + wheelWidth),
				angleLeft);
		drawWeel(graphics, new Point2D.Double(axisOffsetX, -axisLenght - wheelWidth),
				angleRight);

		graphics.glPopMatrix();
	}

	private void drawWeel(GL2 graphics, Point2D.Double pos, double angle) {
		graphics.glPushMatrix();

		graphics.glTranslated(pos.x, pos.y, 0);
		graphics.glRotated(angle, 0, 0, 1);

		drawRect(graphics,
						new Point2D.Double(-wheelLenght, wheelWidth),
						new Point2D.Double(wheelLenght, -wheelWidth), true);

		graphics.glPopMatrix();
	}
	
	private static void drawLine(GL2 gl, Point2D.Double start, Point2D.Double end) {
		gl.glBegin(GL2.GL_LINES);
		gl.glVertex2d(start.x, start.y);
		gl.glVertex2d(end.x, end.y);
		gl.glEnd();
	}

	private static void drawRect(GL2 gl, Point2D.Double p1, Point2D.Double p2,
			boolean fill) {
		gl.glBegin(fill ? GL2.GL_POLYGON : GL2.GL_LINE_LOOP);
		gl.glVertex2d(p1.x, p1.y);
		gl.glVertex2d(p2.x, p1.y);
		gl.glVertex2d(p2.x, p2.y);
		gl.glVertex2d(p1.x, p2.y);
		gl.glEnd();
	}

}
