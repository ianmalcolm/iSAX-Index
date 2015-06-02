/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import ISAXIndex.Distance;

/**
 *
 * @author ian
 */
public class ED extends Distance {

    /**
     * Calculates the square of the Euclidean distance between two 1D points
     * represented by real values.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The Square of Euclidean distance.
     */
    public double distance2(double p1, double p2) {
        double temp = p1 - p2;
        return temp * temp;
    }

    /**
     * Calculates the square of the Euclidean distance between two
     * multidimensional points represented by the real vectors.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The Euclidean distance.
     * @throws TSException In the case of error.
     */
    public static double distance2(double[] point1, double[] point2) {
        assert point1.length == point2.length : "Exception in Euclidean distance: array lengths are not equal";
        Double sum = 0D;
        for (int i = 0; i < point1.length; i++) {
            double temp = point2[i] - point1[i];
            sum = sum + temp * temp;
        }
        return sum;
    }

    /**
     * Calculates the square of the Euclidean distance between two
     * multidimensional points represented by integer vectors.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The Euclidean distance.
     * @throws TSException In the case of error.
     */
    public double distance2(int[] point1, int[] point2) {
        assert point1.length == point2.length : "Exception in Euclidean distance: array lengths are not equal";
        Double sum = 0D;
        for (int i = 0; i < point1.length; i++) {
            double temp = Integer.valueOf(point2[i]).doubleValue() - Integer.valueOf(point1[i]).doubleValue();
            sum = sum + temp * temp;
        }
        return sum;
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The Euclidean distance.
     */
    public double distance(double p1, double p2) {
        double temp = (p1 - p2);
        double d = temp * temp;
        return Math.sqrt(d);
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The Euclidean distance.
     * @throws TSException In the case of error.
     */
    public double distance(double[] point1, double[] point2) {

        return Math.sqrt(distance2(point1, point2));
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param point1 The first point.
     * @param point2 The second point.
     * @return The Euclidean distance.
     * @throws TSException In the case of error.
     */
    public double distance(int[] point1, int[] point2) {
        return Math.sqrt(distance2(point1, point2));
    }

    /**
     * Calculates euclidean distance between two one-dimensional time-series of
     * equal length.
     *
     * @param series1 The first series.
     * @param series2 The second series.
     * @return The eclidean distance.
     * @throws TSException if error occures.
     */
    public double seriesDistance(double[] series1, double[] series2) {
        assert series1.length == series2.length : "Exception in Euclidean distance: array lengths are not equal";
        Double res = 0D;
        for (int i = 0; i < series1.length; i++) {
            res = res + distance2(series1[i], series2[i]);
        }
        return Math.sqrt(res);
    }

    /**
     * Calculates euclidean distance between two multi-dimensional time-series
     * of equal length.
     *
     * @param series1 The first series.
     * @param series2 The second series.
     * @return The eclidean distance.
     * @throws TSException if error occures.
     */
    public double seriesDistance(double[][] series1, double[][] series2) {
        assert series1.length == series2.length : "Exception in Euclidean distance: array lengths are not equal";
        Double res = 0D;
        for (int i = 0; i < series1.length; i++) {
            res = res + distance2(series1[i], series2[i]);
        }
        return Math.sqrt(res);

    }

    @Override
    public void clearCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public long getCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
