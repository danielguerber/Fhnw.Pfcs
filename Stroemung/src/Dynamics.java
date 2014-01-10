public abstract class Dynamics {
 
        public double[] euler(double[] x, double dt) {
                double[] y = this.f(x);
                double[] xx = new double[x.length];
                for (int i = 0; i < x.length; i++) {
                        xx[i] = x[i] + y[i] * dt;               // xx = x+y*dt
                }
 
                return xx;
        }
 
        public double[] runge(double[] x, double dt) {
                double[] xx = new double[x.length];
 
                double[] y1 = this.f(x);                     // erster Hilfsvektor
                for (int i = 0; i < x.length; i++) {
                        xx[i] = x[i] + y1[i] * dt / 2;           // xx = x+y*dt/2
                }
 
                double[] y2 = this.f(xx);                    // zweiter Hilfsvektor
                for (int i = 0; i < x.length; i++) {
                        xx[i] = x[i] + y2[i] * dt / 2;           // xx = x+y*dt/2
                }
 
                double[] y3 = this.f(xx);                    // dritter Hilfsvektor
                for (int i = 0; i < x.length; i++) {
                        xx[i] = x[i] + y3[i] * dt;               // xx = x+y*dt
                }
 
                double[] y4 = this.f(xx);                    // vierter Hilfsvektor
 
                double[] ym = new double[x.length];          // gemittelter Vektor
                for (int i = 0; i < x.length; i++) {
                        ym[i] = (y1[i] + 2 * y2[i] + 2 * y3[i] + y4[i]) / 6;
                }
 
                for (int i = 0; i < x.length; i++) {
                        xx[i] = x[i] + ym[i] * dt;               // xx = x+ym*dt
                }
 
                return xx;
        }
 
        public abstract double[] f(double[] x);
}