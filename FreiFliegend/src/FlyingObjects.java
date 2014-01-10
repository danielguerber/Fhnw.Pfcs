
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Font;
import java.util.List;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import javax.swing.JFrame;

public class FlyingObjects extends JFrame implements GLEventListener, KeyListener{
    
    private static enum Type { Quader, Torus }

    private final double viewportWidth = 200;
    
    private final float[] lightPos = { -10, 150, 100, 1 };
    
    private final double dt = 0.05;                      // time steps 
    private static Type objectType = Type.Torus;         // Object Type
    private double elev = 10;                            // Elevation Camera
    private double azim = 40;                            // Azimut Camera
    private double v0 = 50;                              // shooting start speed 
    private double w = 45;                               // shooting angle 
    private final Vec3 aV0 = new Vec3(1, 2, 4);          // starting angle speed
    private final int bulletDelay = 20;                  // delay of the bullets
    private final Vec3 startPos = new Vec3(0,0,0);       // start position
    private final Vec3 quaderSize = new Vec3(5, 5, 5);   // quader dimension
    private final Vec2 torusSize = new Vec2(2, 8);       // torus dimension
    
    private final List<AbstractBullet> bullets = new ArrayList<>();
    private int bulletCount = 0;
    private boolean shift = false;

	private TextRenderer textRenderer;
    

    public static void main(String[] args) { new FlyingObjects(); }
    public FlyingObjects() {
        setName("FlyingObjects");
        setTitle("FlyingObjects");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);

        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addKeyListener(this);
        add(canvas);
        
        setVisible(true);
        
        FPSAnimator anim = new FPSAnimator(canvas, 100, true);
        anim.start();
    }
    
    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
        gl.glClearColor(0.9f, 0.9f, 0.9f, 1.0f); 
        gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glEnable(GL2.GL_LIGHT0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightPos, 0);
		textRenderer = new TextRenderer(new Font("Arial", 0, 10));

    }

    @Override
    public void dispose(GLAutoDrawable glad) { }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl = glad.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
		
		
        rotateCam(gl, elev, azim);
        
        gl.glTranslated(0, -10, 0);
        
        gl.glPushMatrix();
        gl.glColor3d(0, 0.5, 0.2);
        gl.glRotated(45, 0, 1, 0);
        gl.glDisable(GL2.GL_LIGHTING);
        Draw.axes3d(gl);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopMatrix();
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        if (bulletCount++  % bulletDelay == 0) {
            if (objectType.equals(Type.Quader))
                bullets.add(new Quader(startPos.clone(), aV0.clone(), v0, w, quaderSize));
            else
                bullets.add(new Torus(startPos.clone(), aV0.clone(), v0, w, torusSize));
        }
        
        Iterator<AbstractBullet> it = bullets.iterator();
        while(it.hasNext()) {
            AbstractBullet b = it.next();
            b.update(dt);
            b.draw(gl);
            if (b.getLiveTime() == 0) it.remove();
        }
        
        textRenderer.setColor(0.0f, 0.0f, 0.9f, 1.0f);
		
		textRenderer.beginRendering(glad.getWidth(), glad.getHeight());
		textRenderer.draw("Change Quader/Torus: 1/2", 10, 120);
		textRenderer.draw("Change Elevation: Arrow Up/Down", 10, 110);
		textRenderer.draw("Change Azimuth: Arrow Left/Right", 10, 100);
		textRenderer.draw("Change Start Velocity: v/V", 10, 90);
		textRenderer.draw("Change Start Angle: w/W", 10, 80);
		textRenderer.draw("Change Quader Side x: a/A", 10, 70);
		textRenderer.draw("Change Quader Side y: b/B", 10, 60);
		textRenderer.draw("Change Quader Side z: z/Z", 10, 50);
		textRenderer.draw("Change Angular Speed x: x/X", 10, 40);
		textRenderer.draw("Change Angular Speed y: y/Y", 10, 30);
		textRenderer.draw("Change Angular Speed z: z/Z", 10, 20);
		textRenderer.endRendering();
        
    }
    
    private void rotateCam(GL2 gl, double elev, double azim) {
        gl.glRotated(elev, 1, 0, 0);
        gl.glRotated(-azim, 0, 1, 0);
    }

    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {	
        GL2 gl = glad.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
        double aspect = (double) height / width;
        double left = -viewportWidth;
        double right = viewportWidth;
        double bottom = left * aspect;
        double top = right * aspect;
        double near = -(viewportWidth*2), far = viewportWidth*3;
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(left, right, bottom, top, near, far);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                shift = true;
                break;
            case KeyEvent.VK_1:
                objectType = Type.Quader;
                break;
            case KeyEvent.VK_2:
                objectType = Type.Torus;
                break;
            case KeyEvent.VK_UP:
                elev += 2;
                elev %= 360;
                break;
            case KeyEvent.VK_DOWN:
                elev -= 2;
                elev %= 360;
                break;
            case KeyEvent.VK_LEFT:
                azim += 2;
                azim %= 360;
                break;
            case KeyEvent.VK_RIGHT:
                azim -= 2;
                azim %= 360;
                break;
            case KeyEvent.VK_V:
                if (shift) v0 += 0.5;
                else       v0 -= 0.5;
                if (v0 < 0) v0 = 0;
                break;
            case KeyEvent.VK_W:
                if (shift) w += 2;
                else       w -= 2;
                w %= 360;
                break;
            case KeyEvent.VK_A:
                if (shift) quaderSize.x += 1;
                else       quaderSize.x -= 1;
                if (quaderSize.x < 0) quaderSize.x = 0;
                break;
            case KeyEvent.VK_B:
                if (shift) quaderSize.y += 1;
                else       quaderSize.y -= 1;
                if (quaderSize.y < 0) quaderSize.y = 0;
                break;
            case KeyEvent.VK_C:
                if (shift) quaderSize.z += 1;
                else       quaderSize.z -= 1;
                if (quaderSize.z < 0) quaderSize.z = 0;
                break; 
            case KeyEvent.VK_X:
                if (shift) aV0.x += 1;
                else       aV0.x -= 1;
                if (aV0.x < 0) aV0.x = 0;
                break; 
            case KeyEvent.VK_Y:
                if (shift) aV0.y += 1;
                else       aV0.y -= 1;
                if (aV0.y < 0) aV0.y = 0;
                break; 
            case KeyEvent.VK_Z:
                if (shift) aV0.z += 1;
                else       aV0.z -= 1;
                if (aV0.z < 0) aV0.z = 0;
                break; 
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SHIFT)
            shift = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
    
}
