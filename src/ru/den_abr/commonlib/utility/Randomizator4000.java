package ru.den_abr.commonlib.utility;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Randomizator4000<T>
{
    private HashMap<T, Double> distribution;
    private double distSum;
    private SecureRandom random;
    
    public Randomizator4000() {
        this.distribution = new HashMap<T, Double>();
        this.random = new SecureRandom();
    }
    
    public void addValue(final T value, final double distribution) {
        if (this.distribution.get(value) != null) {
            this.distSum -= this.distribution.get(value);
        }
        this.distribution.put(value, distribution);
        this.distSum += distribution;
    }
    
    public T getRandomValue() {
        final double rand = this.random.nextDouble();
        final double ratio = 1.0 / this.distSum;
        double tempDist = 0.0;
        for (final Map.Entry<T, Double> entry : this.distribution.entrySet()) {
            final T i = entry.getKey();
            tempDist += entry.getValue();
            if (rand / ratio <= tempDist) {
                return i;
            }
        }
        throw new IllegalStateException("Randomizator is empty");
    }
    
    public void reset() {
        this.distribution.clear();
    }
}
