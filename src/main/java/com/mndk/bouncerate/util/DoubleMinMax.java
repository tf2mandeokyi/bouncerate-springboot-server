package com.mndk.bouncerate.util;

public record DoubleMinMax(double min, double max) {

    public double fitToRange(double input) {
        if(input < min) return min;
        if(input > max) return max;
        return input;
    }

}
