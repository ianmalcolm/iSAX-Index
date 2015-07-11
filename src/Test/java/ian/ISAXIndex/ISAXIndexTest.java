package ian.ISAXIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.io.BufferedReader;
import java.io.FileReader;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Unit test for ISAXIndex.
 */
public class ISAXIndexTest {

	private String FILE = "../datasets/ecg/ecg100.txt";
	private int windowSize = 360;
	private int DIMENSIONALITY = 4;
	private int CARDINALITY = 16;
	private int LENGTH = -1;
	private int OFFSET = 0;
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
		double[] timeseries = null;

		if (LENGTH > 0) {
			timeseries = new double[LENGTH];
			int i = 0;
			int ofsCnt = 1;
			try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
				String line;
				while (ofsCnt++ < OFFSET) {
					line = br.readLine();
					if (line == null) {
						System.out.println("Not enough lines in " + FILE);
						return;
					}
				}
				while (i < LENGTH) {
					line = br.readLine();
					if (line == null) {
						System.out.println("Not enough lines in " + FILE);
						return;
					}
					timeseries[i++] = Double.parseDouble(line);
				}
				br.close();
			}

		} else {

			try {
				List<String> lines = Files.readAllLines(Paths.get(FILE),
						Charset.defaultCharset());
				if (lines.size() < 1) {
					return;
				}
				timeseries = new double[lines.size()];
				int i = 0;
				for (String line : lines) {
					timeseries[i++] = Double.parseDouble(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

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
		Assert.assertTrue("Error in traversal of the index",
				list.size() == dh.size() - windowSize + 1);

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
			// System.out.println(id + ":\t" + rawDist);
		}
	}

	@Test
	public void testApproxKNNwithException() {
		final long exampleID = 5100;
		ArrayList<Long> exception = new ArrayList<Long>();
		exception.add(exampleID);
		final int k = 1;

		System.out
				.println("Find exception aware approximated k nearest neighbors of exampleID: "
						+ exampleID);
		Date start = new Date();
		ArrayList<Long> knn = index.knn(dh.get(exampleID), k, exception);
		Date end = new Date();
		System.out.println("Elapsed time: "
				+ ((double) (end.getTime() - start.getTime()) / 1000));

		double nnDist = Double.POSITIVE_INFINITY;
		for (long id : knn) {
			double dist = df.distance(dh.get(id), dh.get(exampleID));
			if (nnDist > dist) {
				nnDist = dist;
			}
			// System.out.println(id + ":\t" + dist);
		}
		knn = index.knn(dh.get(exampleID), k, dh, exception);
		double nnDistExact = index.df.distance(dh.get(exampleID),
				dh.get(knn.get(0)));
		System.out.println("Approx nnDist " + nnDist + "\tExact nnDist "
				+ nnDistExact);

	}

	@Test
	public void testISAX2String() {
		final long exampleID = 5100;
		ISAX p = new ISAX(dh.get(exampleID), DIMENSIONALITY, CARDINALITY);
		Assert.assertTrue("The SAX toString of " + exampleID
				+ " should be 6896, but now it is " + p.toString(), p
				.toString().equalsIgnoreCase("6896"));
		System.out.println(p.toString());
	}

	@Test
	public void testString2ISAX() {
		final String word = "6896";
		ISAX p = new ISAX(word, DIMENSIONALITY, CARDINALITY);
		System.out.println(p.toString());
		Assert.assertTrue(
				"String2ISAX error, should be 6896, but it actually is "
						+ p.toString(), p.toString().equalsIgnoreCase("6896"));
	}
}
