package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QualityMeasurementsForHeuristics {

	/**
	 * This method allows the user to fetch the specified column of a file.
	 * 
	 * @param file                Data, user wants to fetch the specified column
	 *                            from.
	 * @param columnIndex         Needed for the specific column, user wants to
	 *                            fetch.
	 * @param delimiter           Here: One space.
	 * @param numberOfLinesToSkip Used for the method 'getTotalArea'.
	 * @return Double values from the specified column in a list.
	 * @throws IOException
	 */
	public static List<Double> loadValuesFromColumn(File file, int columnIndex, String delimiter,
			int numberOfLinesToSkip) throws IOException, NumberFormatException {

		List<Double> valuesList = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		int lineCounter = 0;

		// Skip the first two lines.
		for (int i = 0; i < 2; i++) {
			reader.readLine();
		}

		// This step is used for the computation of the total area.
		for (int i = 0; i < numberOfLinesToSkip; i++) {
			reader.readLine();
		}

		// Read line by line.
		while ((line = reader.readLine()) != null) {
			lineCounter++;
			// Check for completeness regarding existing columns.
			String[] split = line.split(delimiter);
			if (split.length <= columnIndex) {
				reader.close();
				throw new IndexOutOfBoundsException(
						"In Zeile " + lineCounter + " gibt es keine " + columnIndex + " Spalten!");
			}
			double doubleValue = 0.0;
			try {
				doubleValue = Double.parseDouble(split[columnIndex]);
				valuesList.add(doubleValue);
			} catch (NumberFormatException e) {
				reader.close();
				throw new NumberFormatException(e.getMessage());
			}
		}
		reader.close();

		return valuesList;
	}

	/**
	 * This method returns the sum of centers, which is given in the first line of
	 * the input file.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static int getSumOfCenters(File file) throws IOException, NumberFormatException {

		BufferedReader reader = new BufferedReader(new FileReader(file));
		String firstLine = "";

		firstLine = reader.readLine();
		int sumOfCenters = Integer.parseInt(firstLine);

		reader.close();

		return sumOfCenters;
	}

	/**
	 * This method allows the user to fetch the first and the second last
	 * elimination time for "building" the intervals.
	 * 
	 * @param file
	 * @param columnIndex
	 * @param delimiter
	 * @param sumOfCenters
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static List<Double> loadFirstAndSecondLastEliminationTimesFromColumn(File file, int columnIndex,
			String delimiter, int sumOfCenters) throws IOException, NumberFormatException {

		List<Double> valuesList = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		int lineCounter = 1;

		// Skip first two lines.
		for (int i = 0; i < 2; i++) {
			reader.readLine();
		}

		// Read line by line.
		while ((line = reader.readLine()) != null) {
			lineCounter++;
			if (lineCounter == 2 || lineCounter == sumOfCenters) {
				// Check for completeness regarding existing columns.
				String[] split = line.split(delimiter);
				if (split.length <= columnIndex) {
					reader.close();
					throw new IndexOutOfBoundsException(
							"In Zeile " + lineCounter + " gibt es keine " + columnIndex + " Spalten!");
				}
				double doubleValue = 0.0;
				try {
					doubleValue = Double.parseDouble(split[columnIndex]);
					valuesList.add(doubleValue);
				} catch (NumberFormatException e) {
					reader.close();
					throw new NumberFormatException(e.getMessage());
				}
			}
		}
		reader.close();

		return valuesList;
	}

	/**
	 * This method allows the user to compute the number of total alive centers for
	 * a given time.
	 * 
	 * @param file
	 * @param columnIndexForEliminationTime
	 * @param delimiter
	 * @param timeLimit
	 * @param eliminationTimeList
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static int getTotalAlive(File file, int columnIndexForEliminationTime, String delimiter, double timeLimit,
			List<Double> eliminationTimeList) throws NumberFormatException, IOException {

		eliminationTimeList = loadValuesFromColumn(file, columnIndexForEliminationTime, delimiter, 0);

		int totalAlive = 0;
		for (double time : eliminationTimeList) {
			if (time >= timeLimit) {
				totalAlive++;
			}
		}
		return totalAlive;
	}

	/**
	 * This method allows the user to compute the total area of the total alive
	 * centers for a given time.
	 * 
	 * @param file
	 * @param columnIndexForEliminationTime
	 * @param columnIndexForRadius
	 * @param delimiter
	 * @param timeLimit
	 * @param eliminationTimeList
	 * @param radiiList
	 * @param sumOfCenters
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static double getTotalArea(File file, int columnIndexForEliminationTime, int columnIndexForRadius,
			String delimiter, double timeLimit, List<Double> eliminationTimeList, List<Double> radiiList,
			int sumOfCenters) throws NumberFormatException, IOException {

		int totalAlive = getTotalAlive(file, columnIndexForEliminationTime, " ", timeLimit, eliminationTimeList);

		int numbersOfLinesToSkip = sumOfCenters - totalAlive;

		radiiList = loadValuesFromColumn(file, columnIndexForRadius, delimiter, numbersOfLinesToSkip);

		double area = 0.0;
		double PI = Math.PI;

		for (double radius : radiiList) {
			area = area + 2 * PI * Math.pow(radius, 2);
		}
		return area;
	}
}