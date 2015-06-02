/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Test;

import ISAXIndex.DataHandler;
import ISAXIndex.Distance;
import ISAXIndex.Index;
import ISAXIndex.TSUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 *
 * @author ian
 */
public class Test {

    private static final Logger logger = Logger.getLogger(Test.class.getName());

    static {
        logger.setLevel(Level.ALL);
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        Logger.getLogger("").addHandler(ch);
    }

    public static void main(String[] args) throws Exception {
        String FILE = "../datasets/ecg/ecg100.arff";
        String DATA_VALUE_ATTRIBUTE = "value0";
        int windowSize = 360;
        int DIMENSIONALITY = 4;
        int CARDINALITY = 16;
        int LENGTH = -1;
        Level level = Level.INFO;

        // get the data first
        Instances tsData = ConverterUtils.DataSource.read(FILE);
        Attribute dataAttribute = tsData.attribute(DATA_VALUE_ATTRIBUTE);
        double[] timeseries = toRealSeries(tsData, dataAttribute);

        if (LENGTH > 0) {
            timeseries = TSUtils.getSubSeries(timeseries, 0, LENGTH);
        }

        double mean = TSUtils.mean(timeseries);
        double std = TSUtils.stDev(timeseries);

        DataInMemory dh = new DataInMemory(timeseries, windowSize, mean, std);
        Distance df = new ED();
        Index index = new Index(CARDINALITY, DIMENSIONALITY, df);
        Index.setLoggerLevel(level);

        for (int i = 0; i < timeseries.length - windowSize + 1; i++) {
            index.add(dh.get(i), i);
        }

//        ArrayList<Long> list = new ArrayList();
//        for (long id : index) {
//            int position = Collections.binarySearch(list, id);
//            assert position < 0;
//            list.add(-1 * position - 1, id);
//        }
//        System.out.println(list.size());
//
//        final long exampleID = 5100;
//        final int k = 4;
//        ArrayList<Long> exception = new ArrayList();
//        exception.add(exampleID);
//
//        System.out.println("Find exception aware exact k nearest neighbors of exampleID: " + exampleID);
//        Date start = new Date();
//        ArrayList<Long> knn = index.knn(dh.get(exampleID), k, dh, exception);
//        Date end = new Date();
//        System.out.println("Elapsed time: " + ((double) (end.getTime() - start.getTime()) / 1000));
//
//        for (long id : knn) {
//            double dist = ED.distance(dh.getRaw(id), dh.getRaw(exampleID));
//            System.out.println(id + ":\t" + dist);
//        }
        final long exampleID = 5100;
        final double dist = 0.5;

        System.out.println("Range search within dist of exampleID: " + exampleID);
        Date start = new Date();
        ArrayList<Long> rs = index.rs(dh.get(exampleID), dh.unityPower(dist), windowSize, dh);
        Date end = new Date();
        System.out.println("Elapsed time: " + ((double) (end.getTime() - start.getTime()) / 1000));

        for (long id : rs) {
            double rawDist = df.distance(dh.getRaw(id), dh.getRaw(exampleID));
            System.out.println(id + ":\t" + rawDist);
        }

//        System.out.println("");
//
//        System.out.println("Find exception aware approximated k nearest neighbors of exampleID: " + exampleID);
//        start = new Date();
//        knn = index.knn(dh.get(exampleID), k, exception);
//        end = new Date();
//        System.out.println("Elapsed time: " + ((double) (end.getTime() - start.getTime()) / 1000));
//
//        for (long id : knn) {
//            double dist = ED.distance(dh.getRaw(id), dh.getRaw(exampleID));
//            System.out.println(id + ":\t" + dist);
//        }
        for (int i = 0; i < timeseries.length - windowSize + 1; i++) {
            index.remove(dh.get(i), i);
        }
    }

    /**
     * Converts Instances into double array.
     *
     * @param tsData The instances data.
     * @param dataAttribute The attribute to use in conversion.
     * @return real-valued array.
     */
    public static double[] toRealSeries(Instances tsData, Attribute dataAttribute) {
        double[] vals = new double[tsData.numInstances()];
        for (int i = 0; i < tsData.numInstances(); i++) {
            vals[i] = tsData.instance(i).value(dataAttribute.index());
        }
        return vals;
    }

}

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
