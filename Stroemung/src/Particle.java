	

    import javax.media.opengl.GL2;
     
import com.jogamp.opengl.util.gl2.GLUT;
     
     
    public class Particle extends Dynamics{
           
            private double x = 0;
            private double y = 0;
            private double r = 0;
            private static final int RES = 8;
            private int lifetime = 0;
           
            public Particle (double x, double y, double r, int time) {
                    this.x = x;
                    this.y = y;
                    this.r = r;
                    this.lifetime = time;
            }
           
            public void draw(GL2 gl, GLUT glut) {
                            gl.glPushMatrix();
                            gl.glTranslated(x, y, 0);
                            glut.glutSolidSphere(r, RES, RES);
                            gl.glPopMatrix();
                   
            }
           
            public double getLifeTime() {
                    return lifetime;
            }
           
            public double getX() {
                    return x;
            }
     
            public void setX(double x) {
                    this.x = x;
            }
     
            public double getY() {
                    return y;
            }
     
            public void setY(double y) {
                    this.y = y;
            }
            
            public void update(double dt) {
            	double[] res = runge(new double[] {x,y},dt );
            	x = res[0];
            	y = res[1];
            	lifetime--;
            }

            @Override
            public double[] f(double[] x) {
                   
                    double xy2 = x[0] * x[0] + x[1] * x[1];
                    //TODO: r
                    double r2 = 5 * 5;
                   
                    double v1 = 1 + r2 / xy2 - (2 * r2 * x[0] * x[0]) / (xy2 * xy2);
                    double v2 = - (2 * r2 * x[0] * x[1]) / (xy2 * xy2);
                    double[] res = {v1, v2};
                    return res;
            }
           
    }

