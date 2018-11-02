package rocks.zipcode.calcskin;

public class CalcEngine {

    CalcEngine() {
    }

    public double add(double v, double v1) {
        return v+v1;
    }

    public double subtract(double v, double v1) {
        return v-v1;
    }

    public double multiply(double v, double v1) {
        return v*v1;
    }

    public double divide(double v, double v1) {
        return v/v1;
    }

    public double squared(double v) {return v*v;}

    public double sqrt(double v) {return Math.sqrt(v);}

    public double inverse(double v) {return 1/v;}

    public double exponent(double v, double v1) {return Math.pow(v, v1);}

    public double memoryAdd(double v) {return v;}

    public double memoryRecall(double v) {return v;}

}
