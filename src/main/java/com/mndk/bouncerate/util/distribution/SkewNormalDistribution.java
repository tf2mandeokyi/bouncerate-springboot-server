package com.mndk.bouncerate.util.distribution;

import com.mndk.bouncerate.util.math.FastOwenT;
import lombok.Getter;
import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

@Getter
public class SkewNormalDistribution extends AbstractRealDistribution {

    private static final double ROOT_2DIVPI = Math.sqrt(2 / Math.PI);

    private final double location;
    private final double scale;
    private final double shape;

    private final NormalDistribution normal;
    private final double delta;

    public SkewNormalDistribution(double location,
                                  double scale,
                                  double shape) {
        this(new Well19937c(), location, scale, shape);
    }

    public SkewNormalDistribution(RandomGenerator rng,
                                  double location,
                                  double scale,
                                  double shape) {
        super(rng);

        if (scale <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, scale);
        }

        this.location = location;
        this.scale = scale;
        this.shape = shape;

        this.normal = new NormalDistribution(rng, location, scale);
        this.delta = shape / Math.sqrt(1 + shape * shape);
    }

    @Override
    public double density(double x) {
        return 2 * normal.density(x) * normal.cumulativeProbability(shape * (x - location) + location);
    }

    @Override
    public double cumulativeProbability(double x) {
        double z = (x - location) / scale;
        return normal.cumulativeProbability(x) - 2 * FastOwenT.owenT(z, shape);
    }

    @Override
    public double getNumericalMean() {
        return location + ROOT_2DIVPI * scale * delta;
    }

    @Override
    public double getNumericalVariance() {
        return scale * scale * (1 - (2 * delta * delta / Math.PI));
    }

    @Override public double getSupportLowerBound() { return Double.NEGATIVE_INFINITY; }
    @Override public double getSupportUpperBound() { return Double.POSITIVE_INFINITY; }
    @Override public boolean isSupportLowerBoundInclusive() { return false; }
    @Override public boolean isSupportUpperBoundInclusive() { return false; }
    @Override public boolean isSupportConnected() { return true; }
}
