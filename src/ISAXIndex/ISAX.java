/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ISAXIndex;

/**
 *
 * @author ian
 */
public class ISAX implements Comparable<ISAX> {

    private Symbol[] load;

    public ISAX(double[] vals, int dim, int card) {
        int[] temp = getISAXVals(vals, dim, NormalAlphabet.getCuts(card));
        load = new Symbol[dim];
        for (int i = 0; i < dim; i++) {
            load[i] = new Symbol(temp[i], card);
        }
    }

    ISAX(ISAX o) {
        this(o.load);
    }

    ISAX(ISAX o, int _width) {
        this(o);
        for (int i = 0; i < dim(); i++) {
            load[i].setWidth(_width);
        }
    }

    ISAX(Symbol[] workload) {
        load = new Symbol[workload.length];
        for (int i = 0; i < workload.length; i++) {
            load[i] = new Symbol(workload[i]);
        }
    }

    // for root
    ISAX(int dimensionality) {
        load = new Symbol[dimensionality];
        for (int i = 0; i < dimensionality; i++) {
            load[i] = new Symbol(0, 0);
        }
    }

    public boolean covers(ISAX o) {
        assert dim() == o.dim();
        for (int i = 0; i < dim(); i++) {
            if (getWidth(i) > o.getWidth(i)) {
                return false;
            }
        }
        return compareTo(o) == 0;
    }

    public int getLoad(int i) {
        assert i >= 0 && i < load.length;
        return load[i].load;
    }

    private int getWidth(int i) {
        assert i >= 0 && i < load.length;
        return load[i].width;
    }

    public int width() {
        for (int i = 0; i < dim() - 1; i++) {
            assert load[i].width == load[i + 1].width;
        }
        return load[0].width;
    }

    public void setWidth(int maxWidth) {
        for (int i = 0; i < load.length; i++) {
            load[i].setWidth(maxWidth);
        }
    }

    /**
     * Convert real-valued series into symbolic representation.
     *
     * @param vals Real valued timeseries.
     * @param windowSize The PAA window size.
     * @param cuts The cut values array used for SAX transform.
     * @return The symbolic representation of the given real time-series.
     * @throws TSException If error occurs.
     */
    private static int[] getISAXVals(double[] vals, int dimensionality, double[] cuts) {
        int[] l;
        if (vals.length == cuts.length + 1) {
            l = ts2isax(vals, cuts);
        } else {
            l = ts2isax(TSUtils.paa(vals, dimensionality), cuts);
        }
        return l;
    }

    /**
     * Converts the timeseries into string using given cuts intervals. Useful
     * for not-normal distribution cuts.
     *
     * @param vals The timeseries.
     * @param cuts The cut intervals.
     * @return The timeseries SAX representation.
     */
    private static int[] ts2isax(double[] vals, double[] cuts) {
        int[] l = new int[vals.length];
        for (int i = 0; i < vals.length; i++) {
            l[i] = num2sax(vals[i], cuts);
        }
        return l;
    }

    /**
     * Get mapping of a number to char.
     *
     * @param value the value to map.
     * @param cuts the array of intervals.
     * @return character corresponding to numeric value.
     */
    private static int num2sax(double value, double[] cuts) {
        int count = 0;
        while ((count < cuts.length) && (cuts[count] <= value)) {
            count++;
        }
        return count;
    }

    @Override
    public int compareTo(ISAX o) {
        for (int i = 0; i < dim(); i++) {
            if (load[i].compareTo(o.load[i]) > 0) {
                return 1;
            } else if (load[i].compareTo(o.load[i]) < 0) {
                return -1;
            }
        }
        return 0;
    }

//    public double maxDist(ISAX o) {
//        double dist = 0.0;
//        for (int i = 0; i < dim(); i++) {
//            double temp = load[i].maxDist(o.load[i]);
//            if (temp >= Double.MAX_VALUE) {
//                return Double.MAX_VALUE;
//            } else {
//                dist += temp * temp;
//            }
//        }
//        return dist;
//    }

    public boolean equals(ISAX o) {
        for (int i = 0; i < dim(); i++) {
            if (!load[i].equals(o.load[i])) {
                return false;
            }
        }
        return true;
    }

    private int dim() {
        return load.length;
    }

    public double minDist(ISAX o) {
        assert dim() == o.dim();
        double dist = 0;
        for (int i = 0; i < dim(); i++) {
            double temp = load[i].minDist(o.load[i]);
            dist += temp * temp;
        }
        return dist;
    }

    public String disp() {
        String l = "";
        for (int i = 0; i < dim(); i++) {
            l = l + "\t" + getLoad(i) + "(" + getWidth(i) + ")";
        }
        return l;
    }
}

class Symbol implements Comparable<Symbol> {

    public int load;
    public int width;

    Symbol(int workload, int card) {
        load = workload;
        if (card < 2) {
            width = 0;
        } else {
            width = (int) Math.ceil(Math.log(card - 1) / Math.log(2));
        }
    }

    Symbol(Symbol o) {
        load = o.load;
        width = o.width;
    }

    Symbol(Symbol o, int _width) {
        this(o);
        width = _width;
        if (o.width > width) {
            load = o.load >> (o.width - width);

        } else if (o.width < width) {
            load = o.load << (width - o.width);
        }
    }

    public void setWidth(int _width) {
        if (width > _width) {
            load = load >> (width - _width);

        } else if (width < _width) {
            load = load << (_width - width);
        }
        width = _width;
    }

    @Override
    public int compareTo(Symbol o) {
        if (width == o.width) {
            return load - o.load;
        } else if (width > o.width) {
            int widthDiff = width - o.width;
            int rsLoad = load >> widthDiff;
            return rsLoad - o.load;
        } else {
            int widthDiff = o.width - width;
            int rsOLoad = o.load >> widthDiff;
            return load - rsOLoad;
        }
    }

    public boolean equals(Symbol o) {
        return load == o.load && width == o.width;
    }

//    public double maxDist(Symbol o) {
//        if (load >= 1 << width || load <= 0) {
//            return Double.MAX_VALUE;
//        }
//        if (o.load >= 1 << o.width || o.load <= 0) {
//            return Double.MAX_VALUE;
//        }
//        Symbol a, b;
//        if (this.width > o.width) {
//            a = o;
//            b = this;
//        } else {
//            a = this;
//            b = o;
//        }
//        if (a.width == b.width) {
//            double[][] distMat = NormalAlphabet.getDistanceMatrix(1 << a.width);
//            if (a.load > b.load) {
//                return distMat[a.load + 1][b.load - 1];
//            } else {
//                return distMat[a.load - 1][b.load + 1];
//            }
//        } else {
//            int widthDiff = b.width - a.width;
//            int coarse = a.load - (b.load >> widthDiff);
//            double[][] distMat = NormalAlphabet.getDistanceMatrix(1 << b.width);
//            if (coarse > 0) {
//                int lsALoad = (a.load + 1 << widthDiff) & (Integer.MAX_VALUE << widthDiff);
//                return distMat[b.load - 1][lsALoad];
//            } else if (coarse < 0) {
//                int lsALoad = (a.load - 1 << widthDiff) | (Integer.MAX_VALUE >> (Integer.SIZE - widthDiff));
//                return distMat[b.load + 1][lsALoad];
//            } else {
//                int lsALoad1 = (a.load + 1 << widthDiff) & (Integer.MAX_VALUE << widthDiff);
//                double temp1 = distMat[b.load - 1][lsALoad1];
//                int lsALoad2 = (a.load - 1 << widthDiff) | (Integer.MAX_VALUE >> (Integer.SIZE - widthDiff));
//                double temp2 = distMat[b.load + 1][lsALoad2];
//                if (temp1 > temp2) {
//                    return temp1;
//                } else {
//                    return temp2;
//                }
//            }
//        }
//    }

    public double minDist(Symbol o) {
        Symbol a, b;
        if ((this.width < o.width)) {
            a = this;
            b = o;
        } else {
            b = this;
            a = o;
        }

        if (a.width == b.width) {
            double[][] distMat = NormalAlphabet.getDistanceMatrix(1 << a.width);
            return distMat[a.load][b.load];
        } else {
            double[][] distMat = NormalAlphabet.getDistanceMatrix(1 << b.width);
            int widthDiff = b.width - a.width;
            int rsBLoad = b.load >> widthDiff;
            if (a.load > rsBLoad) {
                int lsALoad = (a.load << widthDiff) & (Integer.MAX_VALUE << widthDiff);
                return distMat[b.load][lsALoad];
            } else if (a.load < rsBLoad) {
                int lsALoad = (a.load << widthDiff) | (Integer.MAX_VALUE >> (Integer.SIZE - widthDiff));
                return distMat[b.load][lsALoad];
            } else {
                return 0;
            }
        }
    }

}
