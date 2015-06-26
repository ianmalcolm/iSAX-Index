/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ian.ISAXIndex;

/**
 *
 * @author ian
 */
public class TSUtils  implements java.io.Serializable{

    /**
     * Computes the mean value of timeseries.
     *
     * @param series The timeseries.
     * @return The mean value.
     */
    public static double mean(double[] series) {
        double res = 0D;
        int count = 0;
        for (double tp : series) {
            if (Double.isNaN(tp) || Double.isInfinite(tp)) {
            } else {
                res += tp;
                count += 1;
            }
        }
        if (count > 0) {
            return res / ((Integer) count).doubleValue();
        }
        return Double.NaN;
    }

    /**
     * Extracts sub-series from series.
     *
     * @param data The series.
     * @param start The start position.
     * @param end The end position
     * @return sub-series from start to end.
     */
    public static double[] getSubSeries(double[] data, int start, int end) {
        double[] vals = new double[end - start];
        for (int i = 0; i < end - start; i++) {
            vals[i] = data[start + i];
        }
        return vals;
    }

    /**
     * Computes the standard deviation of timeseries.
     *
     * @param series The timeseries.
     * @return the standard deviation.
     */
    public static double stDev(double[] series) {
        double num0 = 0D;
        double sum = 0D;
        int count = 0;
        for (double tp : series) {
            if (Double.isNaN(tp) || Double.isInfinite(tp)) {
            } else {
                num0 = num0 + tp * tp;
                sum = sum + tp;
                count += 1;
            }
        }
        if (count > 0) {
            double len = ((Integer) count).doubleValue();
            return Math.sqrt((len * num0 - sum * sum) / (len * (len - 1)));
        }
        return Double.NaN;
    }

    /**
     * Z-Normalize timeseries to the mean zero and standard deviation of one.
     *
     * @param series The timeseries.
     * @return Z-normalized time-series.
     * @throws TSException if error occurs.
     */
    public static double[] zNormalize(double[] series) {

        // this is the resulting normalization
        //
        double[] res = new double[series.length];

        // get mean and sdev, NaN's will be handled
        //
        double mean = mean(series);
        double sd = stDev(series);

        // check if we hit special case, where something got NaN
        //
        if (Double.isInfinite(mean) || Double.isNaN(mean) || Double.isInfinite(sd) || Double.isNaN(sd)) {

            // case[1] single value within the timeseries, normalize this value to 1.0 - magic number
            //
            int nanNum = countNaN(series);
            if ((series.length - nanNum) == 1) {
                for (int i = 0; i < res.length; i++) {
                    if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                        res[i] = Double.NaN;
                    } else {
                        res[i] = 1.0D;
                    }
                }
            } // case[2] all values are NaN's
            //
            else if (series.length == nanNum) {
                for (int i = 0; i < res.length; i++) {
                    res[i] = Double.NaN;
                }
            }
        } // another special case, where SD happens to be close to a zero, i.e. they all are the same for
        // example
        //
        else if (sd <= 0.001D) {

            // here I assign another magic value - 0.001D which makes to middle band of the normal
            // Alphabet
            //
            for (int i = 0; i < res.length; i++) {
                if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                    res[i] = series[i];
                } else {
                    res[i] = 0.1D;
                }
            }
        } // normal case, everything seems to be fine
        //
        else {
            // sd and mean here, - go-go-go
            for (int i = 0; i < res.length; i++) {
                res[i] = (series[i] - mean) / sd;
            }
        }
        return res;

    }

    /**
     * Z-Normalize timeseries to the mean zero and standard deviation of one.
     *
     * @param series The timeseries.
     * @param mean The mean values.
     * @param sd The standard deviation.
     * @return Z-normalized time-series.
     * @throws TSException if error occurs.
     */
    public static double[] zNormalize(double[] series, double mean, double sd) {

        // this is the resulting normalization
        //
        double[] res = new double[series.length];

        // check if we hit special case, where something got NaN
        //
        if (Double.isInfinite(mean) || Double.isNaN(mean) || Double.isInfinite(sd) || Double.isNaN(sd)) {

            // case[1] single value within the timeseries, normalize this value to 1.0 - magic number
            //
            int nanNum = countNaN(series);
            if ((series.length - nanNum) == 1) {
                for (int i = 0; i < res.length; i++) {
                    if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                        res[i] = Double.NaN;
                    } else {
                        res[i] = 1.0D;
                    }
                }
            } // case[2] all values are NaN's
            //
            else if (series.length == nanNum) {
                for (int i = 0; i < res.length; i++) {
                    res[i] = Double.NaN;
                }
            }
        } // another special case, where SD happens to be close to a zero, i.e. they all are the same for
        // example
        //
        else if (sd <= 0.001D) {

            // here I assign another magic value - 0.001D which makes to middle band of the normal
            // Alphabet
            //
            for (int i = 0; i < res.length; i++) {
                if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                    res[i] = series[i];
                } else {
                    res[i] = 0.1D;
                }
            }
        } // normal case, everything seems to be fine
        //
        else {
            // sd and mean here, - go-go-go
            for (int i = 0; i < res.length; i++) {
                res[i] = (series[i] - mean) / sd;
            }
        }
        return res;

    }

    /**
     * Z-Normalize timeseries to the mean zero and standard deviation of one.
     *
     * @param val The timeseries.
     * @param mean The mean values.
     * @param sd The standard deviation.
     * @return Z-normalized time-series.
     */
    public static double zNormalize(double val, double mean, double sd) {
        return (val - mean) / sd;
    }

    /**
     * Counts the number of NaNs' in the timeseries.
     *
     * @param series The timeseries.
     * @return The count of NaN values.
     */
    private static int countNaN(double[] series) {
        int res = 0;
        for (double d : series) {
            if (Double.isInfinite(d) || Double.isNaN(d)) {
                res += 1;
            }
        }
        return res;
    }

    /**
     * Approximate the timeseries using PAA. If the timeseries has some NaN's
     * they are handled as follows: 1) if all values of the piece are NaNs - the
     * piece is approximated as NaN, 2) if there are some (more or equal one)
     * values happened to be in the piece - algorithm will handle it as usual -
     * getting the mean.
     *
     * @param ts The timeseries to approximate.
     * @param paaSize The desired length of approximated timeseries.
     * @return PAA-approximated timeseries.
     * @throws TSException if error occurs.
     */
    public static double[] paa(double[] ts, int paaSize) {
        // fix the length
        int len = ts.length;
        // check for the trivial case
        if (len == paaSize) {
            return ts.clone();
        } else {
            // get values and timestamps
            double[][] vals = asMatrix(ts);
            // work out PAA by reshaping arrays
            double[][] res;
            if (len % paaSize == 0) {
                res = reshape(vals, len / paaSize, paaSize);
            } else {
                double[][] tmp = new double[paaSize][len];
                for (int i = 0; i < paaSize; i++) {
                    System.arraycopy(vals[0], 0, tmp[i], 0, len);
                }
                double[][] expandedSS = reshape(tmp, 1, len * paaSize);
                res = reshape(expandedSS, len, paaSize);
            }
            double[] newVals = colMeans(res);

            return newVals;
        }

    }

    /**
     * Converts the vector into one-row matrix.
     *
     * @param vector The vector.
     * @return The matrix.
     */
    private static double[][] asMatrix(double[] vector) {
        double[][] res = new double[1][vector.length];
        System.arraycopy(vector, 0, res[0], 0, vector.length);
        return res;

    }

    /**
     * Computes column means for the matrix.
     *
     * @param a the input matrix.
     * @return result.
     */
    private static double[] colMeans(double[][] a) {
        double[] res = new double[a[0].length];
        for (int j = 0; j < a[0].length; j++) {
            double sum = 0;
            int counter = 0;
            for (int i = 0; i < a.length; i++) {
                if (Double.isNaN(a[i][j]) || Double.isInfinite(a[i][j])) {
                    continue;
                }
                sum += a[i][j];
                counter++;
            }
            if (counter == 0) {
                res[j] = Double.NaN;
            } else {
                res[j] = sum / ((double) counter);
            }
        }
        return res;
    }

    /**
     * Mimics Matlab function for reshape: returns the m-by-n matrix B whose
     * elements are taken column-wise from A. An error results if A does not
     * have m*n elements.
     *
     * @param a the source matrix.
     * @param n number of rows in the new matrix.
     * @param m number of columns in the new matrix.
     *
     * @return reshaped matrix.
     */
    private static double[][] reshape(double[][] a, int n, int m) {
        int cEl = 0;
        int aRows = a.length;

        double[][] res = new double[n][m];

        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                res[i][j] = a[cEl % aRows][cEl / aRows];
                cEl++;
            }
        }

        return res;
    }

}
