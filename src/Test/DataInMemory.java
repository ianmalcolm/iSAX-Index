/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Test;

import ISAXIndex.DataHandler;
import ISAXIndex.TSUtils;

/**
 *
 * @author ian
 */
class DataInMemory extends DataHandler {

    double[] vals = null;
    int windowSize = 0;
    double mean = 0.0;
    double std = 1.0;

    DataInMemory(double[] _vals, int _windowSize, double _mean, double _std) {
        vals = _vals;
        windowSize = _windowSize;
        mean = _mean;
        std = _std;
    }

    public double unityPower(double in) {
        return in / std;
    }

    @Override
    public long size() {
        return vals.length;
    }

    @Override
    public double[] get(long i) {
        assert i + windowSize <= size();
        double[] subSeries = TSUtils.getSubSeries(vals, ((int) i), ((int) i) + windowSize);
        return TSUtils.zNormalize(subSeries, mean, std);
    }

    public double[] getRaw(long i) {
        assert i + windowSize <= size();
        double[] subSeries = TSUtils.getSubSeries(vals, ((int) i), ((int) i) + windowSize);
        return subSeries;
    }

    @Override
    public int windowSize() {
        return this.windowSize;
    }
}
