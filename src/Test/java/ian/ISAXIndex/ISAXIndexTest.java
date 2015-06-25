package ian.ISAXIndex;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.junit.Test;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/**
 * Unit test for ISAXIndex.
 */
public class ISAXIndexTest {

	private String FILE = "./ecg100.arff";
	private String DATA_VALUE_ATTRIBUTE = "value0";
	private int windowSize = 360;
	private int DIMENSIONALITY = 4;
	private int CARDINALITY = 16;
	private int LENGTH = -1;
	private ISAXIndex index = null;
	private DataInMemory dh = null;
	private Distance df = null;

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 * @throws Exception
	 */
	public ISAXIndexTest() throws Exception {
		// get the data first
		Instances tsData = ConverterUtils.DataSource.read(FILE);
		Attribute dataAttribute = tsData.attribute(DATA_VALUE_ATTRIBUTE);
		double[] timeseries = toRealSeries(tsData, dataAttribute);

		if (LENGTH > 0) {
			timeseries = TSUtils.getSubSeries(timeseries, 0, LENGTH);
		}

		double mean = TSUtils.mean(timeseries);
		double std = TSUtils.stDev(timeseries);

		dh = new DataInMemory(timeseries, windowSize, mean, std);
		df = new ED();
		index = new ISAXIndex(CARDINALITY, DIMENSIONALITY, df);

		for (int i = 0; i < timeseries.length - windowSize + 1; i++) {
			index.add(dh.get(i), i);
		}
	}

	private static double[] toRealSeries(Instances tsData,
			Attribute dataAttribute) {
		double[] vals = new double[tsData.numInstances()];
		for (int i = 0; i < tsData.numInstances(); i++) {
			vals[i] = tsData.instance(i).value(dataAttribute.index());
		}
		return vals;
	}

	/**
	 * Rigourous Test :-)
	 */

	@Test
	public void testIterator() {
		ArrayList<Long> list = new ArrayList<Long>();
		for (long id : index) {
			int position = Collections.binarySearch(list, id);
			assert position < 0;
			list.add(-1 * position - 1, id);
		}
		assertTrue("Error in traversal of the index", list.size() == dh.size()
				- windowSize + 1);

	}

	@Test
	public void testExactKNNwithException() {
		final long exampleID = 5100;
		final int k = 4;
		ArrayList<Long> exception = new ArrayList<Long>();
		exception.add(exampleID);

		System.out
				.println("Find exception aware exact k nearest neighbors of exampleID: "
						+ exampleID);
		Date start = new Date();
		ArrayList<Long> knn = index.knn(dh.get(exampleID), k, dh, exception);
		Date end = new Date();
		System.out.println("Elapsed time: "
				+ ((double) (end.getTime() - start.getTime()) / 1000));

		for (long id : knn) {
			double dist = df.distance(dh.getRaw(id), dh.getRaw(exampleID));
			System.out.println(id + ":\t" + dist);
		}
	}

	@Test
	public void testExactRangeSearch() {
		final long exampleID = 5100;
		final double dist = 0.5;

		System.out.println("Range search within dist of exampleID: "
				+ exampleID);
		Date start = new Date();
		ArrayList<Long> rs = index.rs(dh.get(exampleID), dh.unityPower(dist),
				windowSize, dh);
		Date end = new Date();
		System.out.println("Elapsed time: "
				+ ((double) (end.getTime() - start.getTime()) / 1000));

		for (long id : rs) {
			double rawDist = df.distance(dh.getRaw(id), dh.getRaw(exampleID));
			System.out.println(id + ":\t" + rawDist);
		}
	}

	@Test
	public void testApproxKNNwithException() {
		final long exampleID = 5100;
		ArrayList<Long> exception = new ArrayList<Long>();
		exception.add(exampleID);
		final int k = 4;

		System.out
				.println("Find exception aware approximated k nearest neighbors of exampleID: "
						+ exampleID);
		Date start = new Date();
		ArrayList<Long> knn = index.knn(dh.get(exampleID), k, exception);
		Date end = new Date();
		System.out.println("Elapsed time: "
				+ ((double) (end.getTime() - start.getTime()) / 1000));

		for (long id : knn) {
			double dist = df.distance(dh.getRaw(id), dh.getRaw(exampleID));
			System.out.println(id + ":\t" + dist);
		}
	}
}
