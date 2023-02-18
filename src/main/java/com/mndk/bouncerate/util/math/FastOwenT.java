package com.mndk.bouncerate.util.math;

import org.apache.commons.math3.special.Erf;

/**
 * <a href="https://www.researchgate.net/publication/5142787_Fast_and_Accurate_Calculation_of_Owen's_T_Function">
 *     Article link
 * </a>
 * <br/>
 * Use {@link FastOwenT#owenT(double, double)} for the Owen's T function.
 */
public class FastOwenT {

    private static final double RTWOPI = 1 / (2 * Math.PI);
    private static final double RRTPI = Math.sqrt(RTWOPI);
    private static final double RROOT2 = 1 / Math.sqrt(2);

    private static final double[] C2 = new double[] {
            0.99999999999999987510,
            -0.99999999999988796462,    0.99999999998290743652,
            -0.99999999896282500134,    0.99999996660459362918,
            -0.99999933986272476760,    0.99999125611136965852,
            -0.99991777624463387686,    0.99942835555870132569D,
            -0.99697311720723000295,    0.98751448037275303682,
            -0.95915857980572882813,    0.89246305511006708555,
            -0.76893425990463999675,    0.58893528468484693250,
            -0.38380345160440256652,    0.20317601701045299653,
            -0.82813631607004984866e-1, 0.24167984735759576523e-1,
            -0.44676566663971825242e-2, 0.39141169402373836468e-3
    };
    private static final double[] PTS = new double[] {
            0.35082039676451715489e-2,
            0.31279042338030753740e-1, 0.85266826283219451090e-1,
            0.16245071730812277011,    0.25851196049125434828,
            0.36807553840697533536,    0.48501092905604697475,
            0.60277514152618576821,    0.71477884217753226516,
            0.81475510988760098605,    0.89711029755948965867,
            0.95723808085944261843,    0.99178832974629703586
    };
    private static final double[] WTS = new double[] {
            0.18831438115323502887e-1,
            0.18567086243977649478e-1, 0.18042093461223385584e-1,
            0.17263829606398753364e-1, 0.16243219975989856730e-1,
            0.14994592034116704829e-1, 0.13535474469662088392e-1,
            0.11886351605820165233e-1, 0.10070377242777431897e-1,
            0.81130545742299586629e-2, 0.60419009528470238773e-2,
            0.38862217010742057883e-2, 0.16793031084546090448e-2
    };
    private static final int[] METH = new int[] {
            1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 4, 4, 4, 4, 5, 6
    };
    private static final int[] ORD = new int[] {
            2, 3, 4, 5, 7, 10, 12, 18, 10, 20, 30, 20, 4, 7, 8, 20, 13, 0
    };
    private static final double[] HRANGE = new double[] {
            0.02, 0.06, 0.09, 0.125, 0.26, 0.4, 0.6, 1.6, 1.7, 2.33, 2.4, 3.36, 3.4, 4.8
    };
    private static final double[] ARANGE = new double[] {
            0.025, 0.09, 0.15, 0.36, 0.5, 0.9, 0.99999
    };
    /* Get code by SELECT[aCode][hCode] */
    private static final int[][] SELECT = new int[][] {
            {1,  1,  2, 13, 13, 13, 13, 13, 13, 13, 13, 16, 16, 16,  9 },
            {1,  2,  2,  3,  3,  5,  5, 14, 14, 15, 15, 16, 16, 16,  9 },
            {2,  2,  3,  3,  3,  5,  5, 15, 15, 15, 15, 16, 16, 16, 10 },
            {2,  2,  3,  5,  5,  5,  5,  7,  7, 16, 16, 16, 16, 16, 10 },
            {2,  3,  3,  5,  5,  6,  6,  8,  8, 17, 17, 17, 12, 12, 11 },
            {2,  3,  5,  5,  5,  6,  6,  8,  8, 17, 17, 17, 12, 12, 12 },
            {2,  3,  4,  4,  6,  6,  8,  8, 17, 17, 17, 17, 17, 12, 12 },
            {2,  3,  4,  4,  6,  6, 18, 18, 18, 18, 17, 17, 17, 12, 12 }
    };

    public static double owenT(double h, double a) {
        double absA = Math.abs(a), signA = Math.signum(a), absH = Math.abs(h), ah = absH * absA;
        double result;
        if(absA <= 1) {
            result = owenTF(absH, absA);
        }
        else {
            if(absH <= 0.67) {
                result = 0.25 - erfNormal(absH) * erfNormal(ah) - owenTF(ah, 1 / absA);
            }
            else {
                double normH = erfcNormal(absH), normAH = erfcNormal(ah);
                result = 0.5 * (normH + normAH) - normH * normAH - owenTF(ah, 1 / absA);
            }
        }
        if(result <= 0) result = -result;
        return result * signA;
    }

    private static double owenTF(double h, double a) {
        int hCode = 15, aCode = 8;
        for(int i = 1; i <= 14; i++) { if(h <= HRANGE[i - 1]) { hCode = i; break; } }
        for(int i = 1; i <= 7 ; i++) { if(a <= ARANGE[i - 1]) { aCode = i; break; } }

        int code = SELECT[aCode - 1][hCode - 1], m = ORD[code - 1];
        double ah = a * h;

        return switch(METH[code - 1]) {
            case 1 -> {
                double hs = -0.5 * h * h, dhs = Math.exp(hs), as = a * a, aj = RTWOPI * a, dj = dhs - 1, gj = hs * dhs;
                int j = 1, jj = 1;

                double result = RTWOPI * Math.atan(a);
                while(true) {
                    result += dj * aj / (double) jj;
                    if(j >= m) break;
                    j++; jj += 2;
                    aj *= as;
                    dj = gj - dj;
                    gj *= hs / (double) j;
                }
                yield result;
            }
            case 2 -> {
                int ii = 1;
                double vi = RRTPI * a * Math.exp(-0.5 * ah * ah);
                double hs = h * h, as = -a * a, z = erfNormal(ah) / h, y = 1 / hs;

                double result = 0;
                while(true) {
                    result += z;
                    if(ii >= m + m + 1) break;
                    z = y * (vi - ii * z);
                    vi *= as;
                    ii += 2;
                }
                yield result * RRTPI * Math.exp(-0.5 * hs);
            }
            case 3 -> {
                int i = 1, ii = 1;
                double vi = RRTPI * a * Math.exp(-0.5 * ah * ah);
                double hs = h * h, as = a * a, zi = erfNormal(ah) / h, y = 1 / hs;

                double result = 0;
                while(true) {
                    result += zi * C2[i - 1];
                    if(i >= m) break;
                    zi = y * (ii * zi - vi);
                    vi = as * vi;
                    i++;
                    ii += 2;
                }
                yield result * RRTPI * Math.exp(-0.5 * hs);
            }
            case 4 -> {
                int ii = 1;
                double hs = h * h, as = -a * a, yi = 1;
                double ai = RTWOPI * a * Math.exp(-0.5 * hs * (1 - as));

                double result = 0;
                while(true) {
                    result += ai * yi;
                    if(ii >= m + m + 1) break;
                    ii += 2;
                    yi = (1 - hs * yi) / (double) ii;
                    ai *= as;
                }
                yield result;
            }
            case 5 -> {
                double as = a * a, hs = -0.5 * h * h;

                double result = 0;
                for(int i = 1; i <= m; i++) {
                    double r = 1 + as * PTS[i - 1];
                    result += WTS[i - 1] * Math.exp(hs * r) / r;
                }
                yield a * result;
            }
            case 6 -> {
                double normH = erfcNormal(h), y = 1 - a, r = Math.atan(y / (1 + a));
                double result = 0.5 * normH * (1 - normH);
                if(r != 0) result -= RTWOPI * r * Math.exp(-0.5 * y * h * h / r);
                yield result;
            }
            default -> throw new IllegalStateException("Unexpected value: " + METH[code - 1]);
        };
    }

    private static double erfNormal(double x) {
        return 0.5 * Erf.erf(x * RROOT2);
    }

    private static double erfcNormal(double x) {
        return 0.5 * Erf.erfc(x * RROOT2);
    }

}
