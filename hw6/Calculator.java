public class Calculator<T extends Number> {

    private double d(Number n) {
        return n == null ? Double.NaN : n.doubleValue();
    }

    public double sum(T a, T b) {
        if (a == null || b == null) return Double.NaN;
        return a.doubleValue() + b.doubleValue();
    }

    public double subtract(T a, T b) {
        if (a == null || b == null) return Double.NaN;
        return a.doubleValue() - b.doubleValue();
    }

    public double multiply(T a, T b) {
        if (a == null || b == null) return Double.NaN;
        return a.doubleValue() * b.doubleValue();
    }

    public double divide(T a, T b) {
        if (a == null || b == null) return Double.NaN;
        double denom = b.doubleValue();
        if (denom == 0.0) return Double.NaN;
        return a.doubleValue() / denom;
    }
    public static void main(String[] args) {
        // пример использования
        final Calculator<Integer> intCalc = new Calculator<>();
        final double result = intCalc.sum(5, 3); // 8.0

        final Calculator<Double> doubleCalc = new Calculator<>();
        final double div = doubleCalc.divide(10.0, 4.0); // 2.5
    }
}
